package org.edmcouncil.spec.fibo.weasel.ontology.data;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.edmcouncil.spec.fibo.weasel.model.graph.GraphNode;
import org.edmcouncil.spec.fibo.weasel.model.graph.GraphNodeType;
import org.edmcouncil.spec.fibo.weasel.model.graph.OntologyGraph;
import org.edmcouncil.spec.fibo.weasel.ontology.data.label.provider.LabelProvider;
import org.edmcouncil.spec.fibo.weasel.ontology.visitor.OntologyVisitors;
import org.edmcouncil.spec.fibo.weasel.utils.OwlUtils;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
@Component
public class RestrictionGraphDataHandler {

  private static final Logger LOG = LoggerFactory.getLogger(RestrictionGraphDataHandler.class);

  @Autowired
  private LabelProvider labelExtractor;
  @Autowired
  private OwlUtils owlUtils;
  @Autowired
  private OntologyVisitors ontologyVisitors;

  public OntologyGraph handleGraph(
          OWLNamedIndividual obj,
          OWLOntology ontology) {

    Iterator<OWLIndividualAxiom> axiomsIterator = ontology.axioms(obj).iterator();

    IRI elementIri = obj.getIRI();
    GraphNode root = null;
    OntologyGraph vg = null;
    GraphNodeType type = GraphNodeType.INTERNAL;

    if (vg == null) {
      vg = new OntologyGraph();
    }

    if (root == null) {
      root = new GraphNode(vg.nextId());
      root.setIri(elementIri.toString());
      root.setType(GraphNodeType.MAIN);
      String label = labelExtractor.getLabelOrDefaultFragment(elementIri);
      root.setLabel(label);
      vg.addNode(root);
    }

    vg.setRoot(root);

    while (axiomsIterator.hasNext()) {
      OWLIndividualAxiom axiom = axiomsIterator.next();
      axiom.accept(ontologyVisitors.individualCompleteGraphNode(vg, root, type));
      LOG.debug(axiom.getAxiomType().getName());
      for (OWLEntity oWLEntity : axiom.signature().collect(Collectors.toList())) {
        LOG.debug(oWLEntity.toString());
      }

    }

    return vg;
  }

  public OntologyGraph handleGraph(
          OWLObjectProperty obj,
          OWLOntology ontology) {

    Iterator<OWLObjectPropertyAxiom> axiomsIterator = ontology.axioms(obj).iterator();
    return handleGraph(axiomsIterator, obj.getIRI());
  }

  public OntologyGraph handleGraph(
          OWLDataProperty obj,
          OWLOntology ontology) {

    Iterator<OWLDataPropertyAxiom> axiomsIterator = ontology.axioms(obj).iterator();
    return handleGraph(axiomsIterator, obj.getIRI());
  }

  public OntologyGraph handleGraph(
          OWLClass obj,
          OWLOntology ontology) {

    Iterator<OWLClassAxiom> axiomsIterator = ontology.axioms(obj).iterator();

    OntologyGraph vg = handleGraph(axiomsIterator, obj.getIRI());
    vg = handleInheritedAxiomsGraph(obj, vg, ontology);
    return vg;
  }

  private <T extends OWLAxiom> OntologyGraph handleGraph(
          Iterator<T> axiomsIterator,
          IRI elementIri) {
    return handleGraph(axiomsIterator, elementIri, null, null, GraphNodeType.INTERNAL);
  }

  private <T extends OWLAxiom> OntologyGraph handleGraph(
          Iterator<T> axiomsIterator,
          IRI elementIri,
          GraphNode root,
          OntologyGraph vg,
          GraphNodeType type) {

    if (vg == null) {
      vg = new OntologyGraph();
    }

    if (root == null) {
      root = new GraphNode(vg.nextId());
      root.setIri(elementIri.toString());
      root.setType(GraphNodeType.MAIN);
      String label = labelExtractor.getLabelOrDefaultFragment(elementIri);
      root.setLabel(label);
      vg.addNode(root);
    }

    vg.setRoot(root);

    while (axiomsIterator.hasNext()) {
      T axiom = axiomsIterator.next();

      Boolean isRestriction = owlUtils.isRestriction(axiom);

      if (isRestriction && axiom.getAxiomType().equals(AxiomType.SUBCLASS_OF)) {
        OWLSubClassOfAxiom axiomEl = axiom.accept(ontologyVisitors.getAxiomElement(elementIri));

        Map<GraphNode, Set<OWLClassExpression>> qrestrictions = axiomEl.getSuperClass()
                .accept(ontologyVisitors.superClassAxiom(vg, root, type));

        if (qrestrictions != null && !qrestrictions.isEmpty()) {
          for (Map.Entry<GraphNode, Set<OWLClassExpression>> entry : qrestrictions.entrySet()) {
            for (OWLClassExpression classExpression : entry.getValue()) {
              handleRecursivelyRestrictions(classExpression, vg, entry.getKey(), type);
            }
          }
        }
      }
    }
    vg.setRoot(root);
    return vg;
  }

  private void handleRecursivelyRestrictions(
          OWLClassExpression expression,
          OntologyGraph vg,
          GraphNode root,
          GraphNodeType type) {

    LOG.debug("[Expression] Process expression: {}", expression.toString());

    if (expression == null) {
      return;
    }

    Map<GraphNode, Set<OWLClassExpression>> expressionsMap = expression
            .accept(ontologyVisitors.superClassAxiom(vg, root, type));

    if (expressionsMap != null && !expressionsMap.isEmpty()) {
      expressionsMap.entrySet().forEach((entry) -> {
        for (OWLClassExpression classExpression : entry.getValue()) {
          handleRecursivelyRestrictions(classExpression, vg, entry.getKey(), type);
        }
        
      });
    }
  }

  private OntologyGraph handleInheritedAxiomsGraph(OWLClass clazz, OntologyGraph vg, OWLOntology ontology) {

    owlUtils.getSuperClasses(clazz, ontology).forEach((owlClass) -> {
      Iterator<OWLClassAxiom> axiomsIterator = ontology.axioms(owlClass).iterator();
      handleGraph(axiomsIterator, owlClass.getIRI(), vg.getRoot(), vg, GraphNodeType.EXTERNAL);
    });
    return vg;
  }

}
