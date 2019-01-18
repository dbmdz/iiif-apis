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

/**
 * An ordered list of canvases, and/or further ranges.
 *
 * Ranges allow canvases, or parts thereof, to be grouped together in some way. This could be for textual reasons, such
 * as to distinguish books, chapters, verses, sections, non-content-bearing pages, the table of contents or similar.
 * Equally, physical features might be important such as quires or gatherings, sections that have been added later and
 * so forth.
 *
 * See http://iiif.io/api/presentation/2.1/#range
 */
public class Range extends Resource<Range> {

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
        "Member resource must only have an identifier and no other field."
        + " Use add<Resource>(URI first, URI... rest) for convenience.");
    }
  }

  public void setCanvases(List<Canvas> canvases) {
    canvases.forEach(this::checkIdOnly);
    this.canvases = canvases;
  }

  public Range addCanvas(Canvas first, Canvas... rest) {
    if (this.canvases == null) {
      this.canvases = new ArrayList<>();
    }
    checkIdOnly(first);
    stream(rest).forEach(this::checkIdOnly);
    this.canvases.addAll(Lists.asList(first, rest));
    return this;
  }

  public Range addCanvas(String idOfFirst, String... idsOfRest) {
    return this.addCanvas(new Canvas(idOfFirst),
      Arrays.stream(idsOfRest).map(Canvas::new).toArray(Canvas[]::new));
  }

  public List<Range> getRanges() {
    return ranges;
  }

  public void setRanges(List<Range> ranges) {
    ranges.forEach(this::checkIdOnly);
    this.ranges = ranges;
  }

  public Range addRange(Range first, Range... rest) {
    if (this.ranges == null) {
      this.ranges = new ArrayList<>();
    }
    checkIdOnly(first);
    stream(rest).forEach(this::checkIdOnly);
    this.ranges.addAll(Lists.asList(first, rest));
    return this;
  }

  public Range addRange(String first, String... rest) {
    return this.addRange(new Range(first),
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

  /**
   * Sets the member resources.
   * Must be either instances of {@link Range} or {@link Canvas}.
   * All members must have an identifier and a label.
   *
   * @param members member resources
   * @throws IllegalArgumentException if at least one member is not a {@link Range} or {@link Canvas} or does not have
   *         an identifier and a label;
   */
  public void setMembers(List<Resource> members) {
    members.forEach(this::checkMember);
    this.members = members;
  }

  /**
   * Adds one or more member resources.
   * Must be either instances of {@link Range} or {@link Canvas}.
   * All members must have an identifier and a label.
   *
   * @param first first member
   * @param rest other members
   * @return this instance with added members
   * @throws IllegalArgumentException if at least one member is not a {@link Range} or {@link Canvas} or does not have
   *         an identifier and a label;
   */
  public Range addMember(Resource first, Resource... rest) throws IllegalArgumentException {
    if (this.members == null) {
      this.members = new ArrayList<>();
    }
    List<Resource> membersToAdd = Lists.asList(first, rest);
    membersToAdd.forEach(this::checkMember);
    this.members.addAll(membersToAdd);
    return this;
  }
}
