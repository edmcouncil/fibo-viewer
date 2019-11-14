package org.edmcouncil.spec.fibo.weasel.model.taxonomy;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.edmcouncil.spec.fibo.weasel.model.OwlTaxonomy;
import org.edmcouncil.spec.fibo.config.configuration.model.Pair;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
public class OwlTaxonomyImpl implements OwlTaxonomy<OwlTaxonomyElementImpl> {

  private List<List<OwlTaxonomyElementImpl>> value;

  public OwlTaxonomyImpl() {
    value = new LinkedList<>();
  }

  @Override
  public List<List<OwlTaxonomyElementImpl>> getValue() {
    return value;
  }

  public void setValue(List<List<OwlTaxonomyElementImpl>> value) {
    this.value = value;
  }

  public void addTaxonomy(List<OwlTaxonomyElementImpl> tax) {
    this.value.add(tax);
  }

  public void addTaxonomy(OwlTaxonomyImpl subCLassTax, OwlTaxonomyElementImpl taxEl) {

    for (List<OwlTaxonomyElementImpl> list : subCLassTax.getValue()) {
      list.add(taxEl);
      value.add(list);
    }
  }

  public void sort() {
    Collections.sort(value, Comparator.comparing(List::size));
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Taxonomy: \n");
    for (List<OwlTaxonomyElementImpl> list : value) {
      sb.append("\t");
      for (OwlTaxonomyElementImpl el : list) {
        sb.append(el.getValueA().getValue()).append(" > ");
      }
      sb.append("\n");
    }

    return sb.toString();
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 89 * hash + Objects.hashCode(this.value);
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
    final OwlTaxonomyImpl other = (OwlTaxonomyImpl) obj;
    if (!Objects.equals(this.value, other.value)) {
      return false;
    }
    return true;
  }

}
