package de.digitalcollections.iiif.model.sharedcanvas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import de.digitalcollections.iiif.model.ImageContent;
import de.digitalcollections.iiif.model.Motivation;
import de.digitalcollections.iiif.model.enums.ViewingHint.Type;
import de.digitalcollections.iiif.model.image.ImageApiProfile;
import de.digitalcollections.iiif.model.image.ImageService;
import de.digitalcollections.iiif.model.openannotation.Annotation;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A virtual container that represents a page or view and has content resources associated with it or with parts of it.
 *
 * The canvas provides a frame of reference for the layout of the content. The concept of a canvas is borrowed from
 * standards like PDF and HTML, or applications like Photoshop and Powerpoint, where the display starts from a blank
 * canvas and images, text and other resources are “painted” on to it.
 *
 * See http://iiif.io/api/presentation/2.1/#canvas
 */
public class Canvas extends Resource<Canvas> {

  public static final String TYPE = "sc:Canvas";

  private List<Annotation> images;
  private List<AnnotationList> otherContent;
  private Integer width;
  private Integer height;

  @JsonCreator
  public Canvas(@JsonProperty("@id") String identifier) {
    super(identifier);
  }

  public Canvas(String identifier, String label) {
    super(identifier);
    this.addLabel(label);
  }

  @Override
  public String getType() {
    return TYPE;
  }

  public List<Annotation> getImages() {
    return images;
  }

  /**
   * Sets the image annotations on this canvas. Must all be instances of {@link ImageContent}
   *
   * @param images image annotations on this canvas
   * @throws IllegalArgumentException if at least one of the image annotations is not an {@link ImageContent}
   */
  public void setImages(List<Annotation> images) throws IllegalArgumentException {
    this.images = images;
  }

  public Canvas addIIIFImage(String serviceUrl, ImageApiProfile profile) {
    if (this.images == null) {
      this.images = new ArrayList<>();
    }
    Annotation imgAnno = new Annotation(Motivation.PAINTING);
    // We don't want a typed resource since it would be too verbose
    imgAnno.setOn(new Canvas(this.getIdentifier().toString()));
    ImageContent imgRes = new ImageContent(String.format("%s/full/full/0/default.jpg", serviceUrl));
    imgRes.setFormat(MimeType.MIME_IMAGE_JPEG);
    imgRes.setWidth(width);
    imgRes.setHeight(height);
    imgRes.addService(new ImageService(serviceUrl, profile));
    imgAnno.setResource(imgRes);
    this.images.add(imgAnno);
    return this;
  }

  private Annotation wrapImageInAnnotation(ImageContent img) {
    Annotation imgAnno = new Annotation(Motivation.PAINTING);
    // We don't want a typed resource since it would be too verbose
    imgAnno.setOn(new Canvas(this.getIdentifier().toString()));
    imgAnno.setResource(img);
    return imgAnno;
  }

  public Canvas addImage(ImageContent first, ImageContent... rest) {
    if (this.images == null) {
      this.images = new ArrayList<>();
    }
    this.images.add(wrapImageInAnnotation(first));
    this.images.addAll(Arrays.stream(rest)
            .map(this::wrapImageInAnnotation)
            .collect(Collectors.toList()));
    // If we only have one image, set width/height from the image content by default
    if (this.images.size() == 1 && this.getWidth() == null && this.getHeight() == null) {
      this.setWidthFromImage(this.images.get(0));
    }
    return this;
  }

  public void setWidthFromImage(Annotation imageAnno) {
    this.setWidth(((ImageContent) imageAnno.getResource()).getWidth());
    this.setHeight(((ImageContent) imageAnno.getResource()).getHeight());
  }

  public Integer getWidth() {
    return width;
  }

  public Integer getHeight() {
    return height;
  }

  public void setWidth(Integer width) {
    this.width = width;
  }

  public void setHeight(Integer height) {
    this.height = height;
  }

  public List<AnnotationList> getOtherContent() {
    return otherContent;
  }

  public void setOtherContent(List<AnnotationList> otherContent) {
    this.otherContent = otherContent;
  }

  public Canvas addOtherContent(AnnotationList first, AnnotationList... rest) {
    if (this.otherContent == null) {
      this.otherContent = new ArrayList<>();
    }
    this.otherContent.addAll(Lists.asList(first, rest));
    return this;
  }

  @JsonIgnore
  @Override
  public Set<Type> getSupportedViewingHintTypes() {
    return ImmutableSet.of(Type.NON_PAGED, Type.FACING_PAGES);
  }
}
