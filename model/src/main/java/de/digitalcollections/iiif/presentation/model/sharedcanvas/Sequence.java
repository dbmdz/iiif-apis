package de.digitalcollections.iiif.presentation.model.sharedcanvas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableSet;
import de.digitalcollections.iiif.presentation.model.enums.ViewingDirection;
import de.digitalcollections.iiif.presentation.model.enums.ViewingHint.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@JsonTypeName(Sequence.TYPE)
public class Sequence extends Resource {
  public static final String TYPE = "sc:Sequence";

  private ViewingDirection viewingDirection;
  private URI startCanvas;
  private List<Canvas> canvases = new ArrayList<>();

  @Override
  public String getType() {
    return TYPE;
  }

  public ViewingDirection getViewingDirection() {
    return viewingDirection;
  }

  public void setViewingDirection(ViewingDirection viewingDirection) {
    this.viewingDirection = viewingDirection;
  }

  @JsonIgnore
  @Override
  public Set<Type> getSupportedViewingHintTypes() {
    return ImmutableSet.of(Type.INDIVIDUALS, Type.PAGED, Type.CONTINUOUS);
  }

  public List<Canvas> getCanvases() {
    return canvases;
  }

  public void setCanvases(List<Canvas> canvases) {
    this.canvases = canvases;
  }

  public void addCanvases(Canvas first, Canvas... rest) {
    this.canvases.add(first);
    this.canvases.addAll(Arrays.asList(rest));
  }

  public URI getStartCanvas() {
    return startCanvas;
  }

  public void setStartCanvas(URI startCanvas) {
    this.startCanvas = startCanvas;
  }
}
