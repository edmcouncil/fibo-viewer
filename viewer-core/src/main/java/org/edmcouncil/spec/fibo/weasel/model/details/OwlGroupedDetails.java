package org.edmcouncil.spec.fibo.weasel.model.details;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.element.StringItem;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.ViewerCoreConfiguration;
import org.edmcouncil.spec.fibo.weasel.model.PropertyValue;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlGroupedDetailsProperties;
import org.edmcouncil.spec.fibo.config.configuration.model.ConfigItem;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
public class OwlGroupedDetails extends OwlDetails {

  private OwlGroupedDetailsProperties<PropertyValue> properties;

  public OwlGroupedDetails() {
    if (properties == null) {
      properties = new OwlGroupedDetailsProperties<>();
    }
  }

  public Map<String, Map<String, List<PropertyValue>>> getProperties() {
    return properties.getProperties();
  }
  
  public void setProperties(OwlGroupedDetailsProperties<PropertyValue> prop){
    this.properties = prop;
  }

  public void addProperty(String groupKey, String propertyKey, PropertyValue property) {
    properties.addProperty(groupKey, propertyKey, property);
  }

  public void sortProperties(List<StringItem> priorityList) {
    properties.sort(priorityList);
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 71 * hash * super.hashCode();
    hash = 71 * hash + Objects.hashCode(this.properties);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final OwlGroupedDetails other = (OwlGroupedDetails) obj;
    if (!Objects.equals(this.properties, other.properties)) {
      return false;
    }

    return true;
  }

  public void sortProperties(Set<ConfigItem> groups) {
    properties.sort(groups);

  }

  public void sortProperties(Set<ConfigItem> groups, ViewerCoreConfiguration cfg) {
    properties.sort(groups, cfg);
  }

}
