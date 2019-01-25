package de.digitalcollections.iiif.model.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import de.digitalcollections.iiif.model.openannotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Describes a search hit on a single annotation or across multiple annotations.
 *
 * See http://iiif.io/api/search/1.0/#search-api-specific-responses
 */
public class SearchHit {

  @SuppressWarnings("checkstyle:membername")
  @JsonProperty("@type")
  public final String TYPE = "search:Hit";

  private List<Annotation> annotations;
  private List<TextQuoteSelector> selectors;
  private String match;
  private String before;
  private String after;

  public List<Annotation> getAnnotations() {
    return annotations;
  }

  public void setAnnotations(List<Annotation> annotations) {
    this.annotations = annotations;
  }

  public SearchHit addAnnotation(Annotation first, Annotation... rest) {
    if (this.annotations == null) {
      this.annotations = new ArrayList<>();
    }
    this.annotations.addAll(Lists.asList(first, rest));
    return this;
  }

  public List<TextQuoteSelector> getSelectors() {
    return selectors;
  }

  public void setSelectors(List<TextQuoteSelector> selectors) {
    this.selectors = selectors;
  }

  public SearchHit addSelector(TextQuoteSelector first, TextQuoteSelector... rest) {
    if (this.selectors == null) {
      this.selectors = new ArrayList<>();
    }
    this.selectors.addAll(Lists.asList(first, rest));
    return this;
  }

  public String getMatch() {
    return match;
  }

  public void setMatch(String match) {
    this.match = match;
  }

  public String getBefore() {
    return before;
  }

  public void setBefore(String before) {
    this.before = before;
  }

  public String getAfter() {
    return after;
  }

  public void setAfter(String after) {
    this.after = after;
  }
}
