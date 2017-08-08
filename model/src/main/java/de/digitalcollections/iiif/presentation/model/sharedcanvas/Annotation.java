package de.digitalcollections.iiif.presentation.model.sharedcanvas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(Annotation.TYPE)
@JsonPropertyOrder({"@context", "@id", "@type", "motivation", "resource", "on"})
public class Annotation extends Resource {
  public final static String TYPE = "oa:Annotation";

  @JsonProperty("resource")
  private Content contentResource;
  private String motivation;
  private Resource on;

  @JsonCreator
  public Annotation(@JsonProperty("motivation") String motivation) {
    super();
    this.motivation = motivation;
  }

  public Annotation(String identifier, String motivation) {
    super(identifier);
    this.motivation = motivation;
  }

  public Content getResource() {
    return contentResource;
  }

  public void setResource(Content resource) {
    this.contentResource = resource;
  }

  @Override
  public String getType() {
    return "oa:Annotation";
  }

  public String getMotivation() {
    return motivation;
  }

  public Resource getOn() {
    return on;
  }

  public void setOn(Resource on) {
    this.on = on;
  }
}
