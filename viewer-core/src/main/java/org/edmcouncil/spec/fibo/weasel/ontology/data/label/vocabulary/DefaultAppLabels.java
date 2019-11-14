package org.edmcouncil.spec.fibo.weasel.ontology.data.label.vocabulary;

import java.util.HashMap;
import java.util.Map;
import org.semanticweb.owlapi.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
public class DefaultAppLabels {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultAppLabels.class);
  public static final String DEF_EXT_CLASS = "external classes";
  public static final String DEF_INT_CLASS = "internal classes";
  public static final String DEF_EXT_INSTANCES = "external instances";
  public static final String DEF_INT_INSTANCES = "internal instances";
  public static final String DEF_EXT_DATA_PROPERTIES = "external data properties";
  public static final String DEF_INT_DATA_PROPERTIES = "internal data properties";
  public static final String DEF_EXT_OBJECT_PROPERTIES = "external object properties";
  public static final String DEF_INT_OBJECT_PROPERTIES = "internal object properties";
  public static final String DEF_DIRECT_SUBCLASSES = "Direct subclasses";
  public static final String DEF_INSTANCES = "Instances/individuals";
  public static final String DEF_ANONYMOUS_ANCESTOR = "IS-A restrictions inherited from superclasses";

  private final Map<IRI, String> labels = new HashMap<>();

  public void addLabel(String iri, String label) {
    LOG.debug("[Default Label] Add default app label {} for {}", label, iri);
    labels.put(IRI.create(iri), label);
  }

  public void addLabel(IRI iri, String label) {
    LOG.debug("[Default Label] Add default app label {} for {}", label, iri);
    labels.put(iri, label);
  }

  public Map<IRI, String> getLabels() {
    return labels;
  }

}
