
package org.edmcouncil.spec.fibo.weasel.model.graph.vis;

import org.edmcouncil.spec.fibo.weasel.model.graph.GraphNode;
import org.edmcouncil.spec.fibo.weasel.model.graph.GraphNodeType;
import org.edmcouncil.spec.fibo.weasel.model.graph.viewer.ViewerRelation;

/**
 * @author Patrycja Miazek (patrycja.miazek@makolab.com) 
 */

public class VisRelation extends ViewerRelation{
  private VisNode start;
  private VisNode end;

  private GraphNodeType endNodeType;

  public VisRelation() {
  }
}
