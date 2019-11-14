package org.edmcouncil.spec.fibo.weasel.model.graph;

/**
 *
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
public class GraphRelation extends GraphElement {

  private GraphNode start;
  private GraphNode end;
  private boolean optional;
  private GraphNodeType endNodeType;

  public GraphRelation(int id) {
    super(id);
  }

  public GraphNode getStart() {
    return start;
  }

  public GraphNodeType getEndNodeType() {
    return endNodeType;
  }

  public void setEndNodeType(GraphNodeType endNodeType) {
    this.endNodeType = endNodeType;
  }

  public void setStart(GraphNode start) {
    this.start = start;
  }

  public GraphNode getEnd() {
    return end;
  }

  public void setEnd(GraphNode end) {
    this.end = end;
  }

  @Override
  public String toString() {
    return "{" + "start=" + start + ", end=" + end + " " + super.toString();
  }

  @Override
  public String toSimpleJson() {
    String iriDto = super.getIri();
    iriDto = iriDto == null || iriDto.isEmpty() ? "http://www.w3.org/2002/07/owl#Thing" : iriDto;
    String outIri = ", iri:'" + iriDto.replaceAll("#", "%23") + "'";
    String optionalStyle = optional ? ", dashes:true" : "";
    String optionalVariable = ", optional:" + (isOptional() ? "'optional'" : "'non_optional'");
    String typeVariable = ", type:" + (endNodeType == GraphNodeType.INTERNAL ? "'internal'" : "'external'");
    String jLabel = super.getLabel();
    jLabel = jLabel.replaceAll("'", "\u0027");

    StringBuilder sb = new StringBuilder();
    sb.append("{from: ").append(start.getId()).append(", arrows:'to', ")
        .append("to:").append(end.getId())
        .append(", label: '").append(jLabel).append("'")
        .append(", color:{color:'black'}")
        .append(optionalStyle)
        .append(optionalVariable)
        .append(typeVariable)
        .append(outIri)
        .append("}");

    return sb.toString();
  }

  public void setOptional(boolean b) {
    optional = b;
  }

  public boolean isOptional() {
    return optional;
  }

}
