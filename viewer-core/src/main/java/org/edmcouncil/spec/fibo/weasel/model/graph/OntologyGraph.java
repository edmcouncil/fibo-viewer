package org.edmcouncil.spec.fibo.weasel.model.graph;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
public class OntologyGraph {

  private Set<GraphNode> nodes;
  private Set<GraphRelation> relations;
  private GraphNode root;
  private int lastId = 0;

  public Set<GraphNode> getNodes() {
    return nodes;
  }

  public int nextId() {
    lastId++;
    return lastId;
  }

  public void setNodes(Set<GraphNode> nodes) {
    this.nodes = nodes;
  }

  public GraphNode getRoot() {
    return root;
  }

  public void setRoot(GraphNode root) {
    this.root = root;
  }

  public Set<GraphRelation> getRelations() {
    return relations;
  }

  public void setRelations(Set<GraphRelation> relations) {
    this.relations = relations;
  }

  public void addNode(GraphNode node) {
    if (nodes == null) {
      nodes = new HashSet<>();
    }
    nodes.add(node);
  }

  public void addRelation(GraphRelation rel) {
    if (relations == null) {
      relations = new HashSet<>();
    }
    relations.add(rel);
  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();
    sb.append("ViewerGraph \n");
    if (relations != null) {
      sb.append("Relations: \n");
      relations.forEach((rel) -> {
        sb.append("\t").append(rel.toString()).append("\n");
      });
    }

    if (nodes != null) {
      sb.append("Nodes: \n");
      nodes.forEach((node) -> {
        sb.append("\t").append(node.toString()).append("\n");
      });
    }
    if (root != null) {
      sb.append("Root Node: \n");
      sb.append(root.toString());
    }

    return sb.toString();
  }

  public String toJsVars() {
    StringBuilder sb = new StringBuilder();
    sb.append("var nodes = new vis.DataSet([");
    if (nodes != null) {
      int size = nodes.size();
      int i = 0;
      for (GraphNode node : nodes) {
        sb.append(node.toSimpleJson());
        if (i < size) {
          sb.append(", ");
        }
        i++;
      }
    }
    sb.append("]);\n");
    sb.append("var edges = new vis.DataSet([");
    if (relations != null) {
      int size = relations.size();
      int i = 0;
      for (GraphRelation rel : relations) {
        sb.append(rel.toSimpleJson());
        if (i < size) {
          sb.append(", ");
        }
        i++;
      }
    }
    sb.append("]);");
    return sb.toString();
  }

  public String toJsonListNodes() {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    if (nodes != null) {
      int size = nodes.size();
      int i = 0;
      for (GraphNode node : nodes) {
        sb.append(node.toSimpleJson());
        if (i < size - 1) {
          sb.append(", ");
        }
        i++;
      }
    }
    sb.append("]");
    return sb.toString();
  }

  public String toJsonListEdges() {
     StringBuilder sb = new StringBuilder();
     sb.append("[");
    if (relations != null) {
      int size = relations.size();
      int i = 0;
      for (GraphRelation rel : relations) {
        sb.append(rel.toSimpleJson());
        if (i < size-1) {
          sb.append(", ");
        }
        i++;
      }
    }
    sb.append("]");
    return sb.toString();
  }

  public boolean isEmpty() {
    return nodes.isEmpty() && relations.isEmpty();
  }

}
