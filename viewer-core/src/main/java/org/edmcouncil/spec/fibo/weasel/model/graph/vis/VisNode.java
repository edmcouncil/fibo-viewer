
package org.edmcouncil.spec.fibo.weasel.model.graph.vis;

import java.util.List;
import org.edmcouncil.spec.fibo.config.configuration.model.ConfigItemType;
import org.edmcouncil.spec.fibo.weasel.model.graph.GraphNodeType;
import org.edmcouncil.spec.fibo.weasel.model.graph.viewer.ViewerNode;
import org.semanticweb.owlapi.model.OWLAnnotation;

/**
 * @author Patrycja Miazek (patrycja.miazek@makolab.com) 
 */

public class VisNode {

   
  private String iri;
  private String label;
  private String  color = "rgb(255,168,7)";
  //private String nodeStyle  ;
  private String nodeShape = "box";

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  
  public String getIri() {
    return iri;
  }

  public void setIri(String iri) {
    this.iri = iri;
  }


  public String getNodeShape() {
    return nodeShape;
  }

  public void setNodeShape(String nodeShape) {
    this.nodeShape = nodeShape;
  }


  
//  public ConfigItemType (OWLAnnotation next){
//    List <ViewerNode> nodes;
////    if(next.getValue().isIRI()){
////    ConfigItemType.valueOf(nodeShape);
////    return null;
////    }
//    if(GraphNodeType){  
//    }    
//     }
  
}
