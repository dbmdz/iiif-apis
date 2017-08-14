package de.digitalcollections.iiif.model.openannotation;

import de.digitalcollections.iiif.model.api.Selector;
import de.digitalcollections.iiif.model.sharedcanvas.Resource;

public class SpecificResource extends Resource {
  public static final String TYPE = "oa:SpecificResource";

  private Resource full;
  private String style;
  private Selector selector;

  public SpecificResource() {
  }

  public SpecificResource(String identifier) {
    super(identifier);
  }

  @Override
  public String getType() {
    return TYPE;
  }

  public Resource getFull() {
    return full;
  }

  public void setFull(Resource full) {
    this.full = full;
  }

  public String getStyle() {
    return style;
  }

  public void setStyle(String style) {
    this.style = style;
  }

  public Selector getSelector() {
    return selector;
  }

  public void setSelector(Selector selector) {
    this.selector = selector;
  }
}
