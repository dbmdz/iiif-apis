package de.digitalcollections.iiif.presentation.model.sharedcanvas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableSet;
import de.digitalcollections.iiif.presentation.model.api.PageContainer;
import de.digitalcollections.iiif.presentation.model.api.Pageable;
import de.digitalcollections.iiif.presentation.model.enums.ViewingHint.Type;
import java.time.OffsetDateTime;
import java.util.Set;

@JsonTypeName(Collection.TYPE)
public class Collection extends Resource implements Pageable<Collection>, PageContainer<Collection> {
  public final static String TYPE = "sc:Collection";

  private OffsetDateTime navDate;

  @JsonProperty("first")
  private Collection firstPage;

  @JsonProperty("last")
  private Collection lastPage;

  @JsonProperty("total")
  private Integer totalPages;

  @JsonProperty("next")
  private Collection nextPage;

  @JsonProperty("previous")
  private Collection previousPage;

  @JsonProperty("startIndex")
  private Integer startIndex;

  @JsonCreator
  public Collection(@JsonProperty("@id") String identifier) {
    super(identifier);
  }

  public Collection(String identifier, String label) {
    super(identifier);
    this.setLabel(label);
  }

  @Override
  public String getType() {
    return TYPE;
  }

  @JsonIgnore
  @Override
  public Set<Type> getSupportedViewingHintTypes() {
    return ImmutableSet.of(Type.INDIVIDUALS, Type.MULTI_PART);
  }

  public OffsetDateTime getNavDate() {
    return navDate;
  }

  public void setNavDate(OffsetDateTime navDate) {
    this.navDate = navDate;
  }

  @Override
  public Collection getFirst() {
    return firstPage;
  }

  @Override
  public void setFirst(Collection first) {
    this.firstPage = first;
  }

  @Override
  public Collection getLast() {
    return this.lastPage;
  }

  @Override
  public void setLast(Collection last) {
    this.lastPage = last;

  }

  @Override
  public Integer getTotal() {
    return totalPages;
  }

  @Override
  public void setTotal(int total) {
    this.totalPages = total;
  }

  @Override
  public Collection getNext() {
    return nextPage;
  }

  @Override
  public void setNext(Collection next) {
    this.nextPage = next;
  }

  @Override
  public Collection getPrevious() {
    return this.previousPage;
  }

  @Override
  public void setPrevious(Collection previous) {
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
