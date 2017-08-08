package de.digitalcollections.iiif.presentation.model.sharedcanvas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableSet;
import de.digitalcollections.core.model.api.MimeType;
import de.digitalcollections.iiif.presentation.model.ImageContent;
import de.digitalcollections.iiif.presentation.model.enums.ImageAPIProfile;
import de.digitalcollections.iiif.presentation.model.enums.ViewingHint.Type;
import de.digitalcollections.iiif.presentation.model.service.ImageService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@JsonTypeName(Canvas.TYPE)
public class Canvas extends Resource {
  public final static String TYPE = "sc:Canvas";

  private List<Resource> within;
  private List<Annotation> images;
  private Integer width;
  private Integer height;

  public static Canvas fromId(String id) {
    return new Canvas(id);
  }

  @JsonCreator
  public Canvas(@JsonProperty("@id") String identifier) {
    super(identifier);
  }

  public Canvas(String identifier, String label) {
    super(identifier);
    setLabel(label);
  }


  @Override
  public String getType() {
    return TYPE;
  }

  public void setWithin(List<Resource> within) {
    this.within = within;
  }

  public void addWithin(Resource... withins) {
    if (this.within == null) {
      this.within = new ArrayList<>();
    }
    this.within.addAll(Arrays.asList(withins));
  }

  public List<Resource> getWithin() {
    return within;
  }

  public List<Annotation> getImages() {
    return images;
  }

  public void setImages(List<Annotation> images) {
    validateImages(images);
    this.images = images;
  }

  private void validateImages(List<Annotation> images) {
    if (!images.stream().allMatch(a -> a.getResource() instanceof ImageContent)) {
      throw new IllegalArgumentException("All annotations must **only** have ImageContent resources. Use otherContent" +
                                         " for other types of content.");
    }
  }

  public void addIIIFImage(String serviceUrl, ImageAPIProfile profile) {
    if (this.images == null) {
      this.images = new ArrayList<>();
    }
    Annotation imgAnno = new Annotation("sc:painting");
    // We don't want a typed resource since it would be too verbose
    imgAnno.setOn(new Canvas(this.getIdentifier().toString()));
    ImageContent imgRes = new ImageContent(String.format("%s/full/full/0/default.jpg", serviceUrl));
    imgRes.setFormat(MimeType.MIME_IMAGE_JPEG);
    imgRes.setWidth(width);
    imgRes.setHeight(height);
    imgRes.addService(new ImageService(serviceUrl, profile));
    imgAnno.setResource(imgRes);
    this.images.add(imgAnno);
  }

  public void addImages(ImageContent... images) {
    if (this.images == null) {
      this.images = new ArrayList<>();
    }
    this.images.addAll(Arrays.stream(images)
        .map(i -> {
          Annotation imgAnno = new Annotation("sc:painting");
          // We don't want a typed resource since it would be too verbose
          imgAnno.setOn(new Canvas(this.getIdentifier().toString()));
          imgAnno.setResource(i);
          return imgAnno;
        }).collect(Collectors.toList()));
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

  @JsonIgnore
  @Override
  public Set<Type> getSupportedViewingHintTypes() {
    return ImmutableSet.of(Type.NON_PAGED, Type.FACING_PAGES);
  }
}
