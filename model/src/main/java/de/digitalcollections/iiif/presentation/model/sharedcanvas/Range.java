package de.digitalcollections.iiif.presentation.model.sharedcanvas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;
import de.digitalcollections.iiif.presentation.model.enums.ViewingDirection;
import de.digitalcollections.iiif.presentation.model.enums.ViewingHint.Type;
import java.net.URI;
import java.util.Set;

public class Range extends Resource {
  private ViewingDirection viewingDirection;
  private URI startCanvas;
  private URI contentLayer;

  public ViewingDirection getViewingDirection() {
    return viewingDirection;
  }

  public void setViewingDirection(ViewingDirection viewingDirection) {
    this.viewingDirection = viewingDirection;
  }

  @JsonIgnore
  @Override
  public Set<Type> getSupportedViewingHintTypes() {
    return ImmutableSet.of(Type.INDIVIDUALS, Type.PAGED, Type.CONTINUOUS, Type.TOP);
  }

  public URI getStartCanvas() {
    return startCanvas;
  }

  public void setStartCanvas(URI startCanvas) {
    this.startCanvas = startCanvas;
  }

  public URI getContentLayer() {
    return contentLayer;
  }

  public void setContentLayer(URI contentLayer) {
    this.contentLayer = contentLayer;
  }
}
