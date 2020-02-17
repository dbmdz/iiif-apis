package de.digitalcollections.iiif.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.CaseFormat;
import java.net.URI;

/**
 * A hint to the client as to the most appropriate method of displaying the resource.
 *
 * <p>Try to use one of the pre-defined constants. If a custom viewing hint is desired, pass its URL
 * identifier as the sole constructor argument.
 */
public class ViewingHint {

  public enum Type {
    INDIVIDUALS,
    PAGED,
    CONTINUOUS,
    MULTI_PART,
    NON_PAGED,
    TOP,
    FACING_PAGES,
    OTHER
  }

  /**
   * Valid on collection, manifest, sequence and range. When used as the viewingHint of a
   * collection, the client should treat each of the manifests as distinct individual objects. For
   * manifest, sequence and range, the canvases referenced are all distinct individual views, and
   * should not be presented in a page-turning interface. Examples include a gallery of paintings, a
   * set of views of a 3 dimensional object, or a set of the front sides of photographs in a
   * collection.
   */
  public static ViewingHint INDIVIDUALS = new ViewingHint(Type.INDIVIDUALS);

  /**
   * Valid on manifest, sequence and range. Canvases with this viewingHint represent pages in a
   * bound volume, and should be presented in a page-turning interface if one is available. The
   * first canvas is a single view (the first recto) and thus the second canvas represents the back
   * of the object in the first canvas.
   */
  public static ViewingHint PAGED = new ViewingHint(Type.PAGED);

  /**
   * Valid on manifest, sequence and range. A canvas with this viewingHint is a partial view and an
   * appropriate rendering might display either the canvases individually, or all of the canvases
   * virtually stitched together in the display. Examples when this would be appropriate include
   * long scrolls, rolls, or objects designed to be displayed adjacent to each other. If this
   * viewingHint is present, then the resource must also have a viewingDirection which will
   * determine the arrangement of the canvases. Note that this does not allow for both sides of a
   * scroll to be included in the same manifest with this viewingHint. To accomplish that, the
   * manifest should be “individuals” and have two ranges, one for each side, which are
   * “continuous”.
   */
  public static ViewingHint CONTINUOUS = new ViewingHint(Type.CONTINUOUS);

  /**
   * Valid only for collections. Collections with this viewingHint consist of multiple manifests
   * that each form part of a logical whole. Clients might render the collection as a table of
   * contents, rather than with thumbnails. Examples include multi-volume books or a set of journal
   * issues or other serials.
   */
  public static ViewingHint MULTI_PART = new ViewingHint(Type.MULTI_PART);

  /**
   * Valid only for canvases. Canvases with this viewingHint must not be presented in a page turning
   * interface, and must be skipped over when determining the page sequence. This viewing hint must
   * be ignored if the current sequence or manifest does not have the ‘paged’ viewing hint.
   */
  public static ViewingHint NON_PAGED = new ViewingHint(Type.NON_PAGED);

  /**
   * Valid only for ranges. A Range with this viewingHint is the top-most node in a hierarchy of
   * ranges that represents a structure to be rendered by the client to assist in navigation. For
   * example, a table of contents within a paged object, major sections of a 3d object, the textual
   * areas within a single scroll, and so forth. Other ranges that are descendants of the “top”
   * range are the entries to be rendered in the navigation structure. There may be multiple ranges
   * marked with this hint. If so, the client should display a choice of multiple structures to
   * navigate through.
   */
  public static ViewingHint TOP = new ViewingHint(Type.TOP);

  /**
   * Valid only for canvases. Canvases with this viewingHint, in a sequence or manifest with the
   * “paged” viewing hint, must be displayed by themselves, as they depict both parts of the
   * opening. If all of the canvases are like this, then page turning is not possible, so simply use
   * “individuals” instead.
   */
  public static ViewingHint FACING_PAGES = new ViewingHint(Type.FACING_PAGES);

  private Type type;
  private URI uri;

  @JsonCreator
  public ViewingHint(String value) {
    String typeName = CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE, value);
    try {
      this.type = Type.valueOf(typeName);
    } catch (IllegalArgumentException e) {
      this.type = Type.OTHER;
      this.uri = URI.create(value);
    }
  }

  public ViewingHint(Type type) {
    this.type = type;
  }

  public ViewingHint(URI uri) {
    this.type = Type.OTHER;
    this.uri = uri;
  }

  @JsonIgnore
  public Type getType() {
    return type;
  }

  @JsonIgnore
  public URI getUri() {
    return uri;
  }

  @Override
  @JsonValue
  public String toString() {
    if (this.type == Type.OTHER) {
      return this.uri.toString();
    } else {
      return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, this.type.name());
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ViewingHint that = (ViewingHint) o;
    return this.type == that.type && (uri != null ? uri.equals(that.uri) : that.uri == null);
  }

  @Override
  public int hashCode() {
    int result = type.hashCode();
    result = 31 * result + (uri != null ? uri.hashCode() : 0);
    return result;
  }
}
