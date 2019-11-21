package org.edmcouncil.spec.fibo.weasel.model.graph.viewer;

import org.edmcouncil.spec.fibo.weasel.model.graph.GraphNodeType;
import org.edmcouncil.spec.fibo.weasel.model.graph.vis.VisNode;

/**
 * @author Patrycja Miazek (patrycja.miazek@makolab.com)
 */
public class ViewerNode {

  private int cardinality;
  private boolean optional;
  private GraphNodeType type;
  private String iri;

  public int getCardinality() {
    return cardinality;
  }

  public void setCardinality(int cardinality) {
    this.cardinality = cardinality;
  }

  public boolean isOptional() {
    return optional;
  }

  public void setOptional(boolean optional) {
    this.optional = optional;
  }

  public GraphNodeType getType() {
    return type;
  }

  public void setType(GraphNodeType type) {
    this.type = type;
  }

  public String getIri() {
    return iri;
  }

  public void setIri(String iri) {
    this.iri = iri;
  }

  public ViewerNode(int cardinality, boolean optional, GraphNodeType type, String iri) {
    this.cardinality = cardinality;
    this.optional = optional;
    this.type = type;
    this.iri = iri;
  }

  
}
