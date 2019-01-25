package de.digitalcollections.iiif.model.sharedcanvas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import de.digitalcollections.iiif.model.enums.ViewingDirection;
import de.digitalcollections.iiif.model.interfaces.PageContainer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An ordered list of annotation lists.
 *
 * Layers allow higher level groupings of annotations to be recorded. For example, all of the English translation
 * annotations of a medieval French document could be kept separate from the transcription or an edition in modern
 * French.
 *
 * See http://iiif.io/api/presentation/2.1/#layer
 */
public class Layer extends Resource<Layer> implements PageContainer<AnnotationList> {

  public static final String TYPE = "sc:Layer";

  private ViewingDirection viewingDirection;

  @JsonProperty("first")
  private AnnotationList firstAnnotationPage;

  @JsonProperty("last")
  private AnnotationList lastAnnotationPage;

  @JsonProperty("total")
  private Integer totalAnnotations;
  private List<AnnotationList> otherContent;

  @JsonCreator
  public Layer(@JsonProperty("@id") String identifier) {
    super(identifier);
  }

  public Layer(String identifier, String label) {
    super(identifier);
    this.addLabel(label);
  }

  public Layer() {
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

  public List<AnnotationList> getOtherContent() {
    return otherContent;
  }

  public void setOtherContent(List<AnnotationList> otherContent) {
    this.otherContent = otherContent;
  }

  public Layer addOtherContent(String first, String... rest) {
    return this.addOtherContent(new AnnotationList(first),
                                Arrays.stream(rest).map(AnnotationList::new).toArray(AnnotationList[]::new));
  }

  public Layer addOtherContent(AnnotationList first, AnnotationList... rest) {
    if (this.otherContent == null) {
      this.otherContent = new ArrayList<>();
    }
    this.otherContent.addAll(Lists.asList(first, rest));
    return this;
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
