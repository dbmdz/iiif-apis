package de.digitalcollections.iiif.model.search;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import de.digitalcollections.iiif.model.sharedcanvas.Layer;
import java.util.LinkedHashSet;
import java.util.Set;

public class SearchLayer extends Layer {
  private Set<String> ignored;

  public SearchLayer() {
    super(null);
  }

  @JsonSerialize(contentAs = String.class)
  public Set<String> getIgnored() {
    return ignored;
  }

  public void setIgnored(Set<String> ignored) {
    this.ignored = ignored;
  }

  public void addIgnored(String first, String... rest) {
    if (this.ignored == null) {
      this.ignored = new LinkedHashSet<>();
    }
    this.ignored.addAll(Lists.asList(first, rest));
  }
}
