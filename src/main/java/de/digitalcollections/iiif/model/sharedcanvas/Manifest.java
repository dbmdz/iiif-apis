package de.digitalcollections.iiif.model.sharedcanvas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import de.digitalcollections.iiif.model.enums.ViewingDirection;
import de.digitalcollections.iiif.model.enums.ViewingHint.Type;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The overall description of the structure and properties of the digital representation of an object.
 *
 * It carries information needed for the viewer to present the digitized content to the user, such as a title and other
 * descriptive information about the object or the intellectual work that it conveys. Each manifest describes how to
 * present a single object such as a book, a photograph, or a statue.
 *
 * See http://iiif.io/api/presentation/2.1/#manifest
 */
@JsonPropertyOrder({"@context", "@type", "@id", "label", "metadata", "description", "navDate",
                    "license", "attribution", "service", "seeAlso", "rendering", "within",
                    "sequences", "structures"})
public class Manifest extends Resource {
  public static final String TYPE = "sc:Manifest";

  private ViewingDirection viewingDirection;
  private OffsetDateTime navDate;
  private List<Sequence> sequences;

  @JsonProperty("structures")
  private List<Range> ranges;

  @JsonCreator
  public Manifest(@JsonProperty("@id") String identifier) {
    super(identifier);
  }

  public Manifest(String identifier, String label) {
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

  public OffsetDateTime getNavDate() {
    return navDate;
  }

  public void setNavDate(OffsetDateTime navDate) {
    this.navDate = navDate;
  }

  public List<Sequence> getSequences() {
    return sequences;
  }

  public void setSequences(List<Sequence> sequences) {
    this.sequences = sequences;
  }

  @JsonIgnore
  public Sequence getDefaultSequence() {
    if (sequences == null || sequences.isEmpty()) {
      return null;
    } else {
      return sequences.get(0);
    }
  }

  public Manifest addSequence(Sequence first, Sequence... rest) {
    if (this.sequences == null) {
      this.sequences = new ArrayList<>();
    }
    this.sequences.addAll(Lists.asList(first, rest));
    return this;
  }

  public List<Range> getRanges() {
    return ranges;
  }

  public void setRanges(List<Range> ranges) {
    this.ranges = ranges;
  }

  public Manifest addRange(Range first, Range... rest) {
    if (this.ranges == null) {
      this.ranges = new ArrayList<>();
    }
    this.ranges.addAll(Lists.asList(first, rest));
    return this;
  }
}
