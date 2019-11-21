package org.edmcouncil.spec.fibo.weasel.model.graph.vis;

import java.util.Set;
import org.edmcouncil.spec.fibo.weasel.model.graph.GraphNode;
import org.edmcouncil.spec.fibo.weasel.model.graph.GraphRelation;
import org.edmcouncil.spec.fibo.weasel.model.graph.viewer.ViewerGraph;

/**
 * @author Patrycja Miazek (patrycja.miazek@makolab.com)
 */
public class VisGraph extends ViewerGraph{

  private Set<VisNode> nodes;
  private Set<VisRelation> relations;
  private VisNode root;
  private int lastId = 0;


  public Set<VisNode> getNodes() {
    return nodes;
  }

  public void setNodes(Set<VisNode> nodes) {
    this.nodes = nodes;
  }

  public Set<VisRelation> getRelations() {
    return relations;
  }

  public void setRelations(Set<VisRelation> relations) {
    this.relations = relations;
  }

  public VisNode getRoot() {
    return root;
  }

  public void setRoot(VisNode root) {
    this.root = root;
  }

  public int getLastId() {
    return lastId;
  }

  public void setLastId(int lastId) {
    this.lastId = lastId;
  }


  
  
}
