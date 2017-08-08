package de.digitalcollections.iiif.presentation.model.sharedcanvas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import de.digitalcollections.iiif.presentation.model.api.PageContainer;
import de.digitalcollections.iiif.presentation.model.enums.ViewingDirection;

@JsonTypeName(Layer.TYPE)
public class Layer extends Resource implements PageContainer<AnnotationList> {
  public static final String TYPE = "sc:Layer";

  private ViewingDirection viewingDirection;

  @JsonProperty("first")
  private AnnotationList firstAnnotationPage;

  @JsonProperty("last")
  private AnnotationList lastAnnotationPage;

  @JsonProperty("total")
  private Integer totalAnnotations;

  @JsonCreator
  public Layer(@JsonProperty("@id") String identifier) {
    super(identifier);
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

  @Override
  public AnnotationList getFirst() {
    return firstAnnotationPage;
  }

  @Override
  public void setFirst(AnnotationList first) {
    this.firstAnnotationPage = first;
  }

  @Override
  public AnnotationList getLast() {
    return lastAnnotationPage;
  }

  @Override
  public void setLast(AnnotationList last) {
    this.lastAnnotationPage = last;

  }

  @Override
  public Integer getTotal() {
    return totalAnnotations;
  }

  @Override
  public void setTotal(int total) {
    this.totalAnnotations = total;
  }
}
