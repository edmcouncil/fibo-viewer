package org.edmcouncil.spec.fibo.weasel.model.graph;

/**
 *
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
public class GraphElement {
  
  private int id;
  private String iri;
  private String label="";
  
  public GraphElement(int id) {
    this.id = id;
  }
  
  public String getLabel() {
    return label;
  }
  
  public void setLabel(String label) {
    this.label = label;
  }
  
  public String getIri() {
    return iri;
  }
  
  public void setIri(String iri) {
    this.iri = iri;
  }
  
  public int getId() {
    return id;
  }
  
  public void setId(int id) {
    this.id = id;
  }
  
  @Override
  public String toString() {
    return "" + "id=" + id + ", iri=" + iri + ", label=" + label + '}';
  }
  
  public String toSimpleJson() {
    //TODO: String.format in this case
    String format = "{id: %s, label: '%s'}";
    return String.format(format, id, label);
  }
  
}
