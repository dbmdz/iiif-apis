package de.digitalcollections.iiif.model.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Lists;
import de.digitalcollections.iiif.model.sharedcanvas.AnnotationList;
import de.digitalcollections.iiif.model.sharedcanvas.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An AnnotationList that contains hits for a given search query.
 *
 * See http://iiif.io/api/search/1.0/#simple-lists
 */
public class SearchResult extends AnnotationList {

  public static final String CONTEXT = "http://iiif.io/api/search/1/context.json";

  private List<SearchHit> hits;

  @JsonCreator
  public SearchResult(@JsonProperty("@id") String identifier) {
    super(identifier);
  }

  @JsonProperty("@context")
  public List<String> getContext() {
    return Arrays.asList(Resource.CONTEXT, CONTEXT);
  }

  private void setContext(List<String> contexts) {
    // NOP, is just here for jackson
  }

  public List<SearchHit> getHits() {
    return hits;
  }

  public void setHits(List<SearchHit> hits) {
    this.hits = hits;
  }

  public SearchResult addHit(SearchHit first, SearchHit... rest) {
    if (this.hits == null) {
      this.hits = new ArrayList<>();
    }
    this.hits.addAll(Lists.asList(first, rest));
    return this;
  }

  /**
   * Set the list of containing resources. Must all be instances of {@link SearchLayer}
   *
   * @throws IllegalArgumentException if at least one of the resources is not a {@link SearchLayer}
   */
  @JsonDeserialize(contentAs = SearchLayer.class)
  @Override
  public void setWithin(List<Resource> within) throws IllegalArgumentException {
    if (within.stream().anyMatch(r -> !(r instanceof SearchLayer))) {
      throw new IllegalArgumentException("SearchResult can only be within a SearchLayer.");
    }
    super.setWithin(within);
  }

  /**
   * Add a new containing resource. Must be an instance of {@link SearchLayer}
   *
   * @throws IllegalArgumentException if the resource is not a {@link SearchLayer}
   */
  @Override
  public SearchResult addWithin(Resource first, Resource... rest) throws IllegalArgumentException {
    if (!(first instanceof SearchLayer) || Arrays.stream(rest).anyMatch(r -> !(r instanceof SearchLayer))) {
      throw new IllegalArgumentException("SearchResult can only be within a SearchLayer.");
    }
    super.addWithin(first, rest);
    return this;
  }

  @Override
  public SearchResult getNext() {
    return (SearchResult) super.getNext();
  }

  public void setNext(SearchResult next) {
    super.setNext(next);
  }

  @Override
  public SearchResult getPrevious() {
    return (SearchResult) super.getPrevious();
  }

  public void setPrevious(SearchResult previous) {
    super.setPrevious(previous);
  }
}
