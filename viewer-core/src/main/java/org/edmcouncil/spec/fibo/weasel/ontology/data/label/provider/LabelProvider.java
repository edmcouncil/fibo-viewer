package org.edmcouncil.spec.fibo.weasel.ontology.data.label.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.edmcouncil.spec.fibo.config.configuration.model.AppConfiguration;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.element.MissingLanguageItem;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.ViewerCoreConfiguration;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.element.DefaultLabelItem;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.element.LabelPriority.Priority;
import org.edmcouncil.spec.fibo.weasel.ontology.OntologyManager;
import org.edmcouncil.spec.fibo.weasel.ontology.data.label.vocabulary.DefaultAppLabels;
import org.edmcouncil.spec.fibo.weasel.ontology.factory.DefaultLabelsFactory;
import org.edmcouncil.spec.fibo.weasel.utils.StringUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

/**
 *
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
@Component
public class LabelProvider {

  private static final Logger LOG = LoggerFactory.getLogger(LabelProvider.class);

  private Map<IRI, String> previouslyUsedLabels = new HashMap<>();

  @Autowired
  private OntologyManager ontology;
  private final Boolean forceLabelLang;
  private final String labelLang;
  private final Boolean useLabels;
  private final MissingLanguageItem.Action missingLanguageAction;
  private final Priority groupLabelPriority;
  private final Set<DefaultLabelItem> defaultUserLabels;

  @Inject
  public LabelProvider(AppConfiguration config) {
    ViewerCoreConfiguration weaselConfig = (ViewerCoreConfiguration) config.getWeaselConfig();
    this.forceLabelLang = weaselConfig.isForceLabelLang();
    this.labelLang = weaselConfig.getLabelLang();
    this.useLabels = weaselConfig.useLabels();
    this.missingLanguageAction = weaselConfig.getMissingLanguageAction();
    this.groupLabelPriority = weaselConfig.getGroupLabelPriority();
    this.defaultUserLabels = weaselConfig.getDefaultLabels();

    loadDefaultLabels();

  }

  /**
   * Load default labels from configuration and default labels defined in application
   */
  private void loadDefaultLabels() {
    DefaultAppLabels defAppLabels = DefaultLabelsFactory.createDefaultAppLabels();

    for (Map.Entry<IRI, String> entry : defAppLabels.getLabels().entrySet()) {
      previouslyUsedLabels.put(entry.getKey(), entry.getValue());
    }
    if (useLabels && groupLabelPriority == Priority.USER_DEFINED) {
      defaultUserLabels.forEach((defaultLabel) -> {
        previouslyUsedLabels.put(IRI.create(defaultLabel.getIri()), defaultLabel.getLabel());
      });
    }
  }

  public String getLabelOrDefaultFragment(OWLEntity entity) {
    if (entity == null) {
      return null;
    }
    if (previouslyUsedLabels.containsKey(entity.getIRI())) {
      String label = previouslyUsedLabels.get(entity.getIRI());
      LOG.debug("[Label Extractor]: Previously used label : '{}', for entity : '{}'", label, entity.getIRI().toString());
      return label;
    }

    OWLDataFactory factory = new OWLDataFactoryImpl();
    Map<String, String> labels = new HashMap<>();
    if (useLabels) {
      EntitySearcher.getAnnotations(entity, ontology.getOntology(), factory.getRDFSLabel())
          .collect(Collectors.toSet())
          .stream()
          .filter((annotation) -> (annotation.getValue().isLiteral()))
          .forEachOrdered((annotation) -> {

            String label = annotation.annotationValue().asLiteral().get().getLiteral();

            String lang = annotation.annotationValue().asLiteral().get().getLang();

            labelProcessing(lang, labels, label, entity.getIRI());

          });
    }
    String labelResult = null;
    if (labels.isEmpty()) {
      if (groupLabelPriority == Priority.EXTRACTED) {
        for (DefaultLabelItem defaultUserLabel : defaultUserLabels) {
          if (defaultUserLabel.getIri().equals(entity.getIRI().toString())) {
            labelResult = defaultUserLabel.getLabel();
            break;
          }
        }
      } else {
        labelResult = StringUtils.getFragment(entity.getIRI());
      }
    } else if (labels.size() > 1) {
      labelResult = getTheRightLabel(labels, entity.getIRI());
    } else {
      labelResult = labels.entrySet()
          .stream()
          .findFirst()
          .get().getKey();
    }
    previouslyUsedLabels.put(entity.getIRI(), labelResult);
    return labelResult;
  }

  private String getTheRightLabel(Map<String, String> labels, IRI entityIri) {
    Optional<String> optionalLab = labels.entrySet()
        .stream()
        .filter(p -> p.getValue().equals(labelLang))
        .map(m -> {
          return m.getKey();
        })
        .findFirst();
    if (!optionalLab.isPresent()) {
      LOG.debug("[Label Extractor]: Entity has more than one label but noone have a language");

      if (missingLanguageAction == MissingLanguageItem.Action.FIRST) {
        String missingLab = labels.entrySet()
            .stream()
            .findFirst()
            .get().getKey();
        LOG.debug("[Label Extractor]: Return an first element of label list: {}", missingLab);
        return missingLab;

      } else if (missingLanguageAction == MissingLanguageItem.Action.FRAGMENT) {

        return StringUtils.getFragment(entityIri);
      }

    }
    return optionalLab.get();
  }

  private void labelProcessing(String lang, Map<String, String> labels, String label, IRI entityIri) {
    if (forceLabelLang) {
      if (lang.equals(labelLang)) {

        labels.put(label, lang);
        LOG.debug("[Label Extractor]: Extract label: '{}' @ '{}' for element with IRI: '{}'",
            label, lang.isEmpty() ? "no-lang" : lang, entityIri.toString());

      } else {
        LOG.debug("[Label Extractor]: REJECTED label: '{}' @ '{}' for element with IRI: '{}', "
            + "Reason: Language is not present.",
            label, lang.isEmpty() ? "no-lang" : lang, entityIri.toString());
      }

    } else {
      labels.put(label, lang);
      LOG.debug("[Label Extractor]: Extract label: '{}' @ '{}' for element with IRI: '{}'",
          label, lang.isEmpty() ? "no-lang" : lang, entityIri.toString());
    }
  }

  public String getLabelOrDefaultFragment(IRI iri) {

    if (previouslyUsedLabels.containsKey(iri)) {
      String label = previouslyUsedLabels.get(iri);
      LOG.debug("[Label Extractor]: Previously used label : '{}', for entity : '{}'", label, iri.toString());
      return label;
    }

    OWLEntity entity = ontology.getOntology().entitiesInSignature(iri).findFirst().orElse(
        ontology.getOntology().getOWLOntologyManager().getOWLDataFactory().getOWLEntity(EntityType.CLASS, iri));
    if (iri.toString().endsWith("/")) {
      //it's ontology, we have to get the label from another way
      return getOntologyLabelOrDefaultFragment(iri);
    }
    return getLabelOrDefaultFragment(entity);
  }

  private String getOntologyLabelOrDefaultFragment(IRI iri) {
    Map<String, String> labels = new HashMap<>();
    OWLOntologyManager manager = ontology.getOntology().getOWLOntologyManager();
    OWLDataFactory df = OWLManager.getOWLDataFactory();
    for (OWLOntology onto : manager.ontologies().collect(Collectors.toSet())) {
      if (onto.getOntologyID().getOntologyIRI().get().equals(iri)) {
        onto.annotations(df.getRDFSLabel()).collect(Collectors.toSet()).forEach((annotation) -> {
          String label = annotation.annotationValue().asLiteral().get().getLiteral();

          String lang = annotation.annotationValue().asLiteral().get().getLang();
          labelProcessing(lang, labels, label, iri);
        });
        break;
      }
    }
    String labelResult = null;
    if (labels.isEmpty()) {
      labelResult = StringUtils.getFragment(iri);
    } else if (labels.size() > 1) {
      labelResult = getTheRightLabel(labels, iri);
    } else {
      labelResult = labels.entrySet()
          .stream()
          .findFirst()
          .get().getKey();
    }
    return labelResult;
  }
}
