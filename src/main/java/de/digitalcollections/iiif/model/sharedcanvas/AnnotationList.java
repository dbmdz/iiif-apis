package de.digitalcollections.iiif.model.sharedcanvas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import de.digitalcollections.iiif.model.interfaces.Pageable;
import de.digitalcollections.iiif.model.openannotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * An ordered list of annotation lists.
 *
 * Layers allow higher level groupings of annotations to be recorded. For example, all of the English translation
 * annotations of a medieval French document could be kept separate from the transcription or an edition in modern
 * French.
 *
 * May be paged, see http://iiif.io/api/presentation/2.1/#paging
 *
 * See http://iiif.io/api/presentation/2.1/#annotation-list
 */
public class AnnotationList extends Resource<Annotation> implements Pageable<AnnotationList> {
  public static final String TYPE = "sc:AnnotationList";

  private List<Annotation> resources;

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

  public List<Annotation> getResources() {
    return resources;
  }

  public void setResources(List<Annotation> resources) {
    this.resources = resources;
  }

  public AnnotationList addResource(Annotation first, Annotation... rest) {
    if (this.resources == null) {
      this.resources = new ArrayList<>();
    }
    this.resources.addAll(Lists.asList(first, rest));
    return this;
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
