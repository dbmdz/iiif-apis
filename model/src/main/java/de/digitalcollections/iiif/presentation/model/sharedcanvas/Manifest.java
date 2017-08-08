package de.digitalcollections.iiif.presentation.model.sharedcanvas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableSet;
import de.digitalcollections.iiif.presentation.model.enums.ViewingDirection;
import de.digitalcollections.iiif.presentation.model.enums.ViewingHint.Type;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@JsonTypeName(Manifest.TYPE)
public class Manifest extends Resource {
  public static final String TYPE = "sc:Manifest";

  private ViewingDirection viewingDirection;
  private OffsetDateTime navDate;
  private List<Sequence> sequences = new ArrayList<>();

  @JsonCreator
  public Manifest(@JsonProperty("@id") String identifier) {
    super(identifier);
  }

  public Manifest(String identifier, String label) {
    this(identifier);
    setLabel(label);
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

  public Sequence getDefaultSequence() {
    if (sequences.isEmpty()) {
      return null;
    } else {
      return sequences.get(0);
    }
  }

  public void addSequences(Sequence first, Sequence... rest) {
    sequences.add(first);
    sequences.addAll(Arrays.asList(rest));
  }
}
