package de.digitalcollections.iiif.model.sharedcanvas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import de.digitalcollections.iiif.model.enums.ViewingDirection;
import de.digitalcollections.iiif.model.enums.ViewingHint.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Sequence extends Resource {
  public static final String TYPE = "sc:Sequence";

  private ViewingDirection viewingDirection;
  private URI startCanvas;
  private List<Canvas> canvases;

  @JsonCreator
  public Sequence(@JsonProperty("@id") String identifier) {
    super(identifier);
  }

  public Sequence(String identifier, String label) {
    this(identifier);
    this.addLabel(label);
  }

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

  public void addCanvas(Canvas first, Canvas... rest) {
    if (this.canvases == null) {
      this.canvases = new ArrayList<>();
    }
    this.canvases.addAll(Lists.asList(first, rest));
  }

  public URI getStartCanvas() {
    return startCanvas;
  }

  public void setStartCanvas(URI startCanvas) {
    this.startCanvas = startCanvas;
  }
}
