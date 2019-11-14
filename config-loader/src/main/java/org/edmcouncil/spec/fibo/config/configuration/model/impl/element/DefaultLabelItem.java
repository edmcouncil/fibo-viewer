package org.edmcouncil.spec.fibo.config.configuration.model.impl.element;

import java.util.Objects;
import org.edmcouncil.spec.fibo.config.configuration.model.ConfigItem;
import org.edmcouncil.spec.fibo.config.configuration.model.ConfigItemType;

/**
 * @author Patrycja Miazek (patrycja.miazek@makolab.com)
 */
public class DefaultLabelItem implements ConfigItem {

  private String iri;
  private String label;

  public String getIri() {
    return iri;
  }

  public void setIri(String iri) {
    this.iri = iri;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  @Override
  public int hashCode() {
    int hash = 17;
    hash = 67 * hash + Objects.hashCode(this.iri);
    hash = 67 * hash + Objects.hashCode(this.label);
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
    final DefaultLabelItem other = (DefaultLabelItem) obj;
    if (!Objects.equals(this.iri, other.iri)) {
      return false;
    }
    if (!Objects.equals(this.label, other.label)) {
      return false;
    }
    return true;
  }

  @Override
  public ConfigItemType getType() {
    return ConfigItemType.DEFAULT_LABEL;
  }

  @Override
  public String toString() {
    return "DefaultLabelItem{" + "iri=" + iri + ", label=" + label + '}';
  }

}
