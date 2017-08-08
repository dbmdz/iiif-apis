package de.digitalcollections.iiif.presentation.model.sharedcanvas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import de.digitalcollections.core.model.api.MimeType;
import de.digitalcollections.iiif.presentation.model.GenericContent;
import de.digitalcollections.iiif.presentation.model.ImageContent;
import java.net.URI;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


@JsonTypeInfo(use = Id.NAME,
    property = "@type",
    include = As.EXISTING_PROPERTY,
    defaultImpl = GenericContent.class
    //defaultImpl = Content.class
)
@JsonSubTypes({
    @JsonSubTypes.Type(value= ImageContent.class, name="dctypes:Image"),
    //@JsonSubTypes.Type(value= GenericContent.class, name=""),
})
@JsonPropertyOrder({"@id", "@type"})
public class Content extends Resource {
  private MimeType format;
  private URI profile;
  private Integer width;
  private Integer height;

  @JsonCreator
  public Content(@JsonProperty("@id") String identifier) {
    super(identifier);
  }

  @Override
  public String getType() {
    return null;  // Does not have a type
  }

  public MimeType getFormat() {
    return format;
  }

  public void setFormat(MimeType format) {
    this.format = format;
  }

  public void setFormat(String format) {
    checkNotNull(format);
    this.format = MimeType.fromTypename(format);
  }

  @Override
  public List<Content> getOtherContent() {
    return null;
  }

  @Override
  public void setOtherContent(List<Content> otherContent) {
    throw new UnsupportedOperationException("Content does not have otherContent");
  }

  @Override
  public void addOtherContent(Content... contents) {
    throw new UnsupportedOperationException("Content does not have otherContent");
  }

  public Integer getWidth() {
    return width;
  }

  public void setWidth(Integer width) {
    this.width = width;
  }

  public Integer getHeight() {
    return height;
  }

  public void setHeight(Integer height) {
    this.height = height;
  }

  public URI getProfile() {
    return profile;
  }

  public void setProfile(URI profile) {
    this.profile = profile;
  }
}
