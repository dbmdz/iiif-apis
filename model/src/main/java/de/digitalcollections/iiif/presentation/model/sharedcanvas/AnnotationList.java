package de.digitalcollections.iiif.presentation.model.sharedcanvas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import de.digitalcollections.iiif.presentation.model.api.Pageable;

@JsonTypeName(AnnotationList.TYPE)
public class AnnotationList extends Content implements Pageable<AnnotationList> {
  public final static String TYPE = "sc:AnnotationList";

  @JsonProperty("next")
  private AnnotationList nextPage;

  @JsonProperty("prev")
  private AnnotationList previousPage;

  private Integer startIndex;

  @JsonCreator
  public AnnotationList(@JsonProperty("@id") String identifier) {
    super(identifier);
  }

  @Override
  public String getType() {
    return TYPE;
  }

  @Override
  public AnnotationList getNext() {
    return nextPage;
  }

  @Override
  public void setNext(AnnotationList next) {
    this.nextPage = next;
  }

  @Override
  public AnnotationList getPrevious() {
    return previousPage;
  }

  @Override
  public void setPrevious(AnnotationList previous) {
    this.previousPage = previous;
  }

  @Override
  public Integer getStartIndex() {
    return startIndex;
  }

  @Override
  public void setStartIndex(int startIndex) {
    this.startIndex = startIndex;
  }
}
