package org.edmcouncil.spec.fibo.weasel.model.graph;

/**
 *
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
public class GraphNode extends GraphElement {

  private int cardinality;
  private boolean optional;
  private GraphNodeType type;

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

  public GraphNode(int id) {
    super(id);
  }

  public int getCardinality() {
    return cardinality;
  }

  public void setCardinality(int cardinality) {
    this.cardinality = cardinality;
  }

  @Override
  public String toString() {
    return "{" + "cardinality=" + cardinality + " " + super.toString();
  }

  //
  @Override
  public String toSimpleJson() {
    //TODO: String.format in this case
    String iriDto = super.getIri();
    iriDto = iriDto == null || iriDto.isEmpty() ? "http://www.w3.org/2002/07/owl#Thing" : iriDto;
    String outIri = ", iri:\"" + iriDto.replaceAll("#", "%23") + "\"";
    String shape = super.getLabel().isEmpty() ? "" : ", shape: 'box'";
    String optionalVar = this.optional ? ", shapeProperties:{borderDashes:true}" : "";
    String nodeStyle = "";
    if (type == GraphNodeType.MAIN) {
      nodeStyle = ", color: 'rgb(255,168,7)'";
    } else if (type == GraphNodeType.INTERNAL) {
      nodeStyle = ", color:'#C2FABC'";
    }
    String jLabel = super.getLabel();
    jLabel = jLabel.replaceAll("'", "\u0027");
    StringBuilder sb = new StringBuilder();
    sb.append("{id: ").append(super.getId())
        .append(", label: \"").append(jLabel).append("\"")
        .append(",font:{size:15}")
        .append(shape)
        .append(nodeStyle)
        .append(optionalVar)
        .append(outIri)
        .append("}");
        
    
    return sb.toString();
  }

}
