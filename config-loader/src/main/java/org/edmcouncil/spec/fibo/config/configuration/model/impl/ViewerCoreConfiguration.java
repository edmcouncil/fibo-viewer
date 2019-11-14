package org.edmcouncil.spec.fibo.config.configuration.model.impl;

import org.edmcouncil.spec.fibo.config.configuration.model.impl.element.RenameItem;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.element.BooleanItem;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.element.MissingLanguageItem;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.element.StringItem;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.element.LabelPriority;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.edmcouncil.spec.fibo.config.configuration.model.Configuration;
import org.edmcouncil.spec.fibo.config.configuration.model.ConfigKeys;
import org.edmcouncil.spec.fibo.config.configuration.model.ConfigItem;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.element.DefaultLabelItem;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 * @author Patrycja Miazek (patrycja.miazek@makolab.com)
 */
public class ViewerCoreConfiguration implements Configuration<Set<ConfigItem>> {

  private Map<String, Set<ConfigItem>> configuration;
  private static final String DEFAULT_LANG = "en";

  public ViewerCoreConfiguration() {
    configuration = new HashMap<>();
  }

  @Override
  public Map<String, Set<ConfigItem>> getConfiguration() {
    return this.configuration;
  }

  @Override
  public Set<ConfigItem> getConfigVal(String cfName) {
    return configuration != null ? configuration.get(cfName) : null;
  }

  public void addCongigElement(String key, ConfigItem val) {
    if (configuration == null) {
      configuration = new HashMap<>();
    }

    Set valList = configuration.get(key);
    valList = valList == null ? new LinkedHashSet() : valList;
    valList.add(val);
    configuration.put(key, valList);
  }

  @Override
  public boolean isEmpty() {
    return configuration == null || configuration.isEmpty();
  }

  public boolean isGrouped() {
    return !isEmpty() && configuration.get(ConfigKeys.GROUPS) != null;
  }

  public boolean isOntologyLocationSet() {
    return configuration.containsKey(ConfigKeys.ONTOLOGY_URL)
        || configuration.containsKey(ConfigKeys.ONTOLOGY_PATH);
  }

  public boolean isOntologyLocationURL() {
    return configuration.containsKey(ConfigKeys.ONTOLOGY_URL);
  }

  public String getURLOntology() {
    Set<ConfigItem> elements = configuration.get(ConfigKeys.ONTOLOGY_URL);
    for (ConfigItem element : elements) {
      return element.toString();
    }
    return null;
  }

  public String getPathOntology() {
    Set<ConfigItem> elements = configuration.get(ConfigKeys.ONTOLOGY_PATH);
    for (ConfigItem element : elements) {
      return element.toString();
    }
    return null;
  }

  //TODO: Change this method name..
  /**
   *
   * @param uri - String representation of URI
   * @return True if it finds representation in the configuration, otherwise false.
   */
  public Boolean isUriIri(String uri) {
    Set<ConfigItem> scopeIri = configuration.getOrDefault(ConfigKeys.SCOPE_IRI, new HashSet<>());
    for (ConfigItem configElement : scopeIri) {
      StringItem element = (StringItem) configElement;
      if (uri.contains(element.toString())) {
        return Boolean.TRUE;
      }
    }
    return Boolean.FALSE;
  }

  public Boolean useLabels() {
    Set<ConfigItem> values = configuration.getOrDefault(ConfigKeys.DISPLAY_LABEL, new HashSet<>());

    for (ConfigItem value : values) {
      BooleanItem cbe = (BooleanItem) value;
      return cbe.getValue();
    }

    return Boolean.TRUE;
  }

  public Boolean isForceLabelLang() {
    Set<ConfigItem> values = configuration.getOrDefault(ConfigKeys.FORCE_LABEL_LANG, new HashSet<>());

    for (ConfigItem value : values) {
      BooleanItem cbe = (BooleanItem) value;
      return cbe.getValue();
    }

    return Boolean.FALSE;
  }

  public LabelPriority.Priority getGroupLabelPriority() {
    Set<ConfigItem> values = configuration.getOrDefault(ConfigKeys.LABEL_PRIORITY, new HashSet<>());

    for (ConfigItem value : values) {
      LabelPriority cpe = (LabelPriority) value;
      return cpe.getValue();
    }

    return LabelPriority.Priority.USER_DEFINED;
  }

  public String getLabelLang() {
    Set<ConfigItem> values = configuration.getOrDefault(ConfigKeys.LABEL_LANG, new HashSet<>());

    for (ConfigItem value : values) {
      StringItem cpe = (StringItem) value;
      return cpe.toString();
    }

    return DEFAULT_LANG;
  }

  public MissingLanguageItem.Action getMissingLanguageAction() {
    Set<ConfigItem> values = configuration.getOrDefault(ConfigKeys.MISSING_LANGUAGE_ACTION, new HashSet<>());

    for (ConfigItem value : values) {
      MissingLanguageItem cpe = (MissingLanguageItem) value;
      return cpe.getValue();
    }

    return MissingLanguageItem.Action.FIRST;
  }

  public Set<String> getIgnoredElements() {

    Set<ConfigItem> values = configuration.getOrDefault(ConfigKeys.IGNORE_TO_DISPLAYING, new HashSet<>());
    Set<String> result = new HashSet<>();
    values.stream()
        .map((value) -> (StringItem) value)
        .forEachOrdered((cse) -> {
          result.add(cse.toString());
        });

    return result;
  }

  public Set<DefaultLabelItem> getDefaultLabels() {

    Set<ConfigItem> values = configuration.getOrDefault(ConfigKeys.USER_DEFAULT_NAME_LIST, new HashSet<>());
    Set<DefaultLabelItem> result = new HashSet<>();
    values.stream()
        .map((value) -> (DefaultLabelItem) value)
        .forEachOrdered((cse) -> {
          result.add(cse);
        });

    return result;
  }
  
  public LabelPriority.Priority getLabelPriority(){
    Set<ConfigItem> values = configuration.getOrDefault(ConfigKeys.LABEL_PRIORITY, new HashSet<>());
    
    for (ConfigItem value : values) {
      LabelPriority item = (LabelPriority) value;
      return item.getValue();
    }
    return LabelPriority.Priority.USER_DEFINED;
  }

}
