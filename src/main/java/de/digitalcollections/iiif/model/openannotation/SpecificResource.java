package de.digitalcollections.iiif.model.openannotation;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.digitalcollections.iiif.model.interfaces.Selector;
import de.digitalcollections.iiif.model.jackson.serialization.SelectorDeserializer;
import de.digitalcollections.iiif.model.sharedcanvas.Resource;

/**
 * A resource that applies complex styles and/or selectors to a given resource.
 *
 * See:
 * - http://iiif.io/api/presentation/2.1/#non-rectangular-segments
 * - http://iiif.io/api/presentation/2.1/#style
 * - http://iiif.io/api/presentation/2.1/#rotation
 */
public class SpecificResource extends Resource<SpecificResource> {

  public static final String TYPE = "oa:SpecificResource";

  private Resource full;
  private String style;

  @JsonDeserialize(using = SelectorDeserializer.class)
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

  public Resource<?> getFull() {
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
