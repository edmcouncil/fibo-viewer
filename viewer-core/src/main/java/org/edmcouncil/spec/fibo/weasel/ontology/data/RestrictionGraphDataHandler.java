package org.edmcouncil.spec.fibo.weasel.ontology.data;

import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import org.edmcouncil.spec.fibo.weasel.model.graph.GraphNode;
import org.edmcouncil.spec.fibo.weasel.model.graph.GraphNodeType;
import org.edmcouncil.spec.fibo.weasel.model.graph.ViewerGraph;
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
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.InferenceDepth;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
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

  public ViewerGraph handleGraph(
      OWLNamedIndividual obj,
      OWLOntology ontology) {

    Iterator<OWLIndividualAxiom> axiomsIterator = ontology.axioms(obj).iterator();
    return handleGraph(axiomsIterator, obj.getIRI());
  }

  public ViewerGraph handleGraph(
      OWLObjectProperty obj,
      OWLOntology ontology) {

    Iterator<OWLObjectPropertyAxiom> axiomsIterator = ontology.axioms(obj).iterator();
    return handleGraph(axiomsIterator, obj.getIRI());
  }

  public ViewerGraph handleGraph(
      OWLDataProperty obj,
      OWLOntology ontology) {

    Iterator<OWLDataPropertyAxiom> axiomsIterator = ontology.axioms(obj).iterator();
    return handleGraph(axiomsIterator, obj.getIRI());
  }

  public ViewerGraph handleGraph(
      OWLClass obj,
      OWLOntology ontology) {

    Iterator<OWLClassAxiom> axiomsIterator = ontology.axioms(obj).iterator();

    ViewerGraph vg = handleGraph(axiomsIterator, obj.getIRI());
    vg = handleInheritedAxiomsGraph(obj, vg, ontology);
    return vg;
  }

  private <T extends OWLAxiom> ViewerGraph handleGraph(
      Iterator<T> axiomsIterator,
      IRI elementIri) {
    return handleGraph(axiomsIterator, elementIri, null, null, GraphNodeType.INTERNAL);
  }

  private <T extends OWLAxiom> ViewerGraph handleGraph(
      Iterator<T> axiomsIterator,
      IRI elementIri,
      GraphNode root,
      ViewerGraph vg,
      GraphNodeType type) {

    if (vg == null) {
      vg = new ViewerGraph();
    }

    if (root == null) {
      root = new GraphNode(vg.nextId());
      root.setIri(elementIri.toString());
      root.setType(GraphNodeType.MAIN);
      String label = labelExtractor.getLabelOrDefaultFragment(elementIri);
      root.setLabel(label);
      //root.setLabel(label.substring(0, 1).toLowerCase() + label.substring(1));
      vg.addNode(root);
    }

    vg.setRoot(root);

    while (axiomsIterator.hasNext()) {
      T axiom = axiomsIterator.next();

      Boolean isRestriction = owlUtils.isRestriction(axiom);

      if (isRestriction && axiom.getAxiomType().equals(AxiomType.SUBCLASS_OF)) {
        OWLSubClassOfAxiom axiomEl = axiom.accept(ontologyVisitors.getAxiomElement(elementIri));

        Map<GraphNode, OWLClassExpression> qrestrictions = axiomEl.getSuperClass()
            .accept(ontologyVisitors.superClassAxiom(vg, root, type));

        if (qrestrictions != null && !qrestrictions.isEmpty()) {
          for (Map.Entry<GraphNode, OWLClassExpression> entry : qrestrictions.entrySet()) {
            handleRecursivelyRestrictions(entry.getValue(), vg, entry.getKey(), type);
          }
        }
      }
    }
    vg.setRoot(root);
    return vg;
  }

  private void handleRecursivelyRestrictions(
      OWLClassExpression expression,
      ViewerGraph vg,
      GraphNode root,
      GraphNodeType type) {

    LOG.debug("[Expression] Process expression: {}", expression.toString());

    if (expression == null) {
      return;
    }

    Map<GraphNode, OWLClassExpression> expressionsMap = expression
        .accept(ontologyVisitors.superClassAxiom(vg, root, type));

    if (expressionsMap != null && !expressionsMap.isEmpty()) {
      expressionsMap.entrySet().forEach((entry) -> {
        handleRecursivelyRestrictions(entry.getValue(), vg, entry.getKey(), type);
      });
    }
  }

  private ViewerGraph handleInheritedAxiomsGraph(OWLClass clazz, ViewerGraph vg, OWLOntology ontology) {

    OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
    OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ontology);

    NodeSet<OWLClass> rset = reasoner.getSuperClasses(clazz, InferenceDepth.ALL);

    for (Node<OWLClass> node : rset) {
      for (OWLClass owlClass : node.entities().collect(Collectors.toSet())) {
        Iterator<OWLClassAxiom> axiomsIterator = ontology.axioms(owlClass).iterator();
        handleGraph(axiomsIterator, owlClass.getIRI(), vg.getRoot(), vg, GraphNodeType.EXTERNAL);
      }
    }
    return vg;
  }

}
