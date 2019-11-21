package org.edmcouncil.spec.fibo.weasel.model.graph;

/**
 *
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
public class ViewerGraphJson {

  private String nodes;
  private String edges;

  public ViewerGraphJson(OntologyGraph vg) {
    this.nodes = vg.toJsonListNodes();
    this.edges = vg.toJsonListEdges();
  }

  public String getNodes() {
    return nodes;
  }

  public void setNodes(String nodes) {
    this.nodes = nodes;
  }

  public String getEdges() {
    return edges;
  }

  public void setEdges(String edges) {
    this.edges = edges;
  }

}
