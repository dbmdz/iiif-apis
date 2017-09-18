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
    return Arrays.asList(Resource.CONTEXT, this.CONTEXT);
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

  @JsonDeserialize(contentAs = SearchLayer.class)
  @Override
  public void setWithin(List<Resource> within) {
    if (within.stream().anyMatch(r -> !(r instanceof SearchLayer))) {
      throw new IllegalArgumentException("SearchResult can only be within a SearchLayer.");
    }
    super.setWithin(within);
  }

  @Override
  public Resource addWithin(Resource first, Resource... rest) {
    if (!(first instanceof SearchLayer) || Arrays.stream(rest).anyMatch(r -> !(r instanceof SearchLayer))) {
      throw new IllegalArgumentException("SearchResult can only be within a SearchLayer.");
    }
    return super.addWithin(first, rest);
  }

  @Override
  public SearchResult getNext() {
    return (SearchResult) super.getNext();
  }

  public void setNext(SearchResult next) {
    super.setNext(next);
  }

  @Override
  public AnnotationList getPrevious() {
    return super.getPrevious();
  }

  @Override
  public void setPrevious(AnnotationList previous) {
    super.setPrevious(previous);
  }
}
