package de.digitalcollections.iiif.model.sharedcanvas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import de.digitalcollections.iiif.model.ModelUtilities;
import de.digitalcollections.iiif.model.ModelUtilities.Completeness;
import de.digitalcollections.iiif.model.enums.ViewingDirection;
import de.digitalcollections.iiif.model.enums.ViewingHint.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.stream;

public class Range extends Resource {
  public static final String TYPE = "sc:Range";

  private ViewingDirection viewingDirection;
  private URI startCanvas;
  private Layer contentLayer;

  List<Range> ranges;
  List<Canvas> canvases;
  List<Resource> members;

  @JsonCreator
  public Range(@JsonProperty("@id") String identifier) {
    super(identifier);
  }

  public Range(String identifier, String label) {
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
    return ImmutableSet.of(Type.INDIVIDUALS, Type.PAGED, Type.CONTINUOUS, Type.TOP);
  }

  public URI getStartCanvas() {
    return startCanvas;
  }

  public void setStartCanvas(URI startCanvas) {
    this.startCanvas = startCanvas;
  }

  public Layer getContentLayer() {
    return contentLayer;
  }

  public void setContentLayer(Layer contentLayer) {
    this.contentLayer = contentLayer;
  }

  public void setContentLayer(String identifier) {
    this.setContentLayer(new Layer(identifier));
  }

  public List<Canvas> getCanvases() {
    return canvases;
  }

  private void checkIdOnly(Resource res) {
    Completeness completeness = ModelUtilities.getCompleteness(res, res.getClass());
    if (completeness != Completeness.ID_AND_TYPE && completeness != Completeness.ID_ONLY) {
      throw new IllegalArgumentException(
          "Member resource must only have an identifier and no other field." +
          " Use add<Resource>(URI first, URI... rest) for convenience.");
    }
  }

  public void setCanvases(List<Canvas> canvases) {
    canvases.forEach(this::checkIdOnly);
    this.canvases = canvases;
  }

  public void addCanvas(Canvas first, Canvas... rest) {
    if (this.canvases == null) {
      this.canvases = new ArrayList<>();
    }
    checkIdOnly(first);
    stream(rest).forEach(this::checkIdOnly);
    this.canvases.addAll(Lists.asList(first, rest));
  }

  public void addCanvas(String idOfFirst, String... idsOfRest) {
    this.addCanvas(new Canvas(idOfFirst),
                   Arrays.stream(idsOfRest).map(Canvas::new).toArray(Canvas[]::new));
  }

  public List<Range> getRanges() {
    return ranges;
  }

  public void setRanges(List<Range> ranges) {
    ranges.forEach(this::checkIdOnly);
    this.ranges = ranges;
  }

  public void addRange(Range first, Range... rest) {
    if (this.ranges == null) {
      this.ranges = new ArrayList<>();
    }
    checkIdOnly(first);
    stream(rest).forEach(this::checkIdOnly);
    this.ranges.addAll(Lists.asList(first, rest));
  }

  public void addRange(String first, String... rest) {
    this.addRange(new Range(first),
                  Arrays.stream(rest).map(Range::new).toArray(Range[]::new));
  }

  public List<Resource> getMembers() {
    return members;
  }

  private void checkMember(Resource res) {
    if (!(res instanceof Range) && !(res instanceof Canvas)) {
      throw new IllegalArgumentException("Member resources must be either of type Range or Canvas");
    }
    if (res.getIdentifier() == null || res.getLabel() == null || res.getLabel().getValues().isEmpty()) {
      throw new IllegalArgumentException("Member resources must have identifier, type and label set.");
    }
  }

  public void setMembers(List<Resource> members) {
    members.forEach(this::checkMember);
    this.members = members;
  }

  public void addMember(Resource first, Resource... rest) {
    if (this.members == null) {
      this.members = new ArrayList<>();
    }
    this.members.addAll(Lists.asList(first, rest));
  }
}
