package de.digitalcollections.iiif.model.sharedcanvas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import de.digitalcollections.iiif.model.PropertyValue;
import de.digitalcollections.iiif.model.enums.ViewingHint.Type;
import de.digitalcollections.iiif.model.interfaces.PageContainer;
import de.digitalcollections.iiif.model.interfaces.Pageable;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.stream;

/**
 * An ordered list of manifests, and/or further collections.
 *
 * Collections allow easy advertising and browsing of the manifests in a hierarchical structure, potentially with its
 * own descriptive information. They can also provide clients with a means to locate all of the manifests known to the
 * publishing institution.
 *
 * See http://iiif.io/api/presentation/2.1/#collection
 */
public class Collection extends Resource<Collection> implements Pageable<Collection>, PageContainer<Collection> {

  public static final String TYPE = "sc:Collection";

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

  private List<Collection> collections;
  private List<Manifest> manifests;
  private List<Resource> members;

  @JsonCreator
  public Collection(@JsonProperty("@id") String identifier) {
    super(identifier);
  }

  public Collection(String identifier, String label) {
    super(identifier);
    this.addLabel(label);
  }

  public Collection(URI identifier, PropertyValue label) {
    this.setIdentifier(identifier);
    this.setLabel(label);
  }

  @Override
  public String getType() {
    return TYPE;
  }

  @JsonIgnore
  @Override
  public Set<Type> getSupportedViewingHintTypes() {
    return ImmutableSet.of(Type.TOP, Type.INDIVIDUALS, Type.MULTI_PART);
  }

  public List<Collection> getCollections() {
    return collections;
  }

  public void setCollections(List<Collection> collections) {
    this.collections = collections;
  }

  public Collection addCollection(Collection first, Collection... rest) {
    if (this.collections == null) {
      this.collections = new ArrayList<>();
    }
    this.collections.addAll(Lists.asList(first, rest));
    return this;
  }

  public List<Manifest> getManifests() {
    return manifests;
  }

  public void setManifests(List<Manifest> manifests) {
    this.manifests = manifests;
  }

  public Collection addManifest(Manifest first, Manifest... rest) {
    if (this.manifests == null) {
      this.manifests = new ArrayList<>();
    }
    this.manifests.addAll(Lists.asList(first, rest));
    return this;
  }

  public List<Resource> getMembers() {
    return members;
  }

  private void checkMember(Resource member) {
    if (member instanceof Collection) {
      Collection coll = (Collection) member;
      if (coll.getViewingHints().isEmpty()) {
        throw new IllegalArgumentException("Collection members must have a viewingHint.");
      }
    } else if (!(member instanceof Manifest)) {
      throw new IllegalArgumentException("Members must be either Manifest or Collection resources.");
    }
  }

  /**
   * Set the list of member resources.
   * Must be either instances of {@link Manifest} or {@link Collection}.
   * All {@link Collection} members must have at least one {@link de.digitalcollections.iiif.model.enums.ViewingHint}.
   *
   * @param members member resources
   * @throws IllegalArgumentException if at least one member is not a {@link Manifest} or {@link Collection} or is a
   *         {@link Collection} and does not have at least one {@link de.digitalcollections.iiif.model.enums.ViewingHint}
   */
  public void setMembers(List<Resource> members) {
    members.forEach(this::checkMember);
    this.members = members;
  }

  /**
   * Adds one or more member resources.
   * Must be either instances of {@link Manifest} or {@link Collection}.
   * All {@link Collection} members must have at least one {@link de.digitalcollections.iiif.model.enums.ViewingHint}.
   *
   * @param first first member
   * @param rest other members
   * @return this collection with added members
   * @throws IllegalArgumentException if at least one member is not a {@link Manifest} or {@link Collection} or is a
   *         {@link Collection} and does not have at least one {@link de.digitalcollections.iiif.model.enums.ViewingHint}
   */
  public Collection addMember(Resource first, Resource... rest) {
    if (this.members == null) {
      this.members = new ArrayList<>();
      checkMember(first);
      stream(rest).forEach(this::checkMember);
    }
    this.members.addAll(Lists.asList(first, rest));
    return this;
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
