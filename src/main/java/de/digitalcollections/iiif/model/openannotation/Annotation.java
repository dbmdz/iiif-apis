package de.digitalcollections.iiif.model.openannotation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.digitalcollections.iiif.model.Motivation;
import de.digitalcollections.iiif.model.sharedcanvas.Resource;

@JsonPropertyOrder({"@context", "@id", "@type", "motivation", "resource", "on"})
public class Annotation extends Resource {
  public final static String TYPE = "oa:Annotation";

  private Resource resource;
  private Motivation motivation;
  private Resource on;
  private CssStyle stylesheet;

  @JsonCreator
  public Annotation(@JsonProperty("motivation") Motivation motivation) {
    super();
    this.motivation = motivation;
  }

  public Annotation(String identifier) {
    super(identifier);
  }

  public Annotation(String identifier, Motivation motivation) {
    this(identifier);
    this.motivation = motivation;
  }

  public Resource getResource() {
    return resource;
  }

  public void setResource(Resource resource) {
    this.resource = resource;
  }

  @Override
  public String getType() {
    return "oa:Annotation";
  }

  public Motivation getMotivation() {
    return motivation;
  }

  public Resource getOn() {
    return on;
  }

  public void setOn(Resource on) {
    this.on = on;
  }


  public CssStyle getStylesheet() {
    return stylesheet;
  }

  public void setStylesheet(CssStyle stylesheet) {
    this.stylesheet = stylesheet;
  }
}
