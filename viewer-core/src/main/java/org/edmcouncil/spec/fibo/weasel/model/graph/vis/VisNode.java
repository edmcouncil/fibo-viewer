
package org.edmcouncil.spec.fibo.weasel.model.graph.vis;

import org.edmcouncil.spec.fibo.weasel.model.graph.GraphNodeType;
import org.edmcouncil.spec.fibo.weasel.model.graph.viewer.ViewerNode;

/**
 * @author Patrycja Miazek (patrycja.miazek@makolab.com) 
 */

public class VisNode {

   
  private String iri;
  private String nodeStyle;
  private String nodeShape;

  public String getIri() {
    return iri;
  }

  public void setIri(String iri) {
    this.iri = iri;
  }

  public String getNodeStyle() {
    return nodeStyle;
  }

  public void setNodeStyle(String nodeStyle) {
    this.nodeStyle = nodeStyle;
  }

  public String getNodeShape() {
    return nodeShape;
  }

  public void setNodeShape(String nodeShape) {
    this.nodeShape = nodeShape;
  }

  public VisNode(String iri, String nodeStyle, String nodeShape) {
    this.iri = iri;
    this.nodeStyle = nodeStyle;
    this.nodeShape = nodeShape;
  }
  
  

  
  
}
