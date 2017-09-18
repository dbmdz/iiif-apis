package de.digitalcollections.iiif.model.sharedcanvas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import de.digitalcollections.iiif.model.ImageContent;
import de.digitalcollections.iiif.model.MetadataEntry;
import de.digitalcollections.iiif.model.OtherContent;
import de.digitalcollections.iiif.model.PropertyValue;
import de.digitalcollections.iiif.model.enums.ViewingHint;
import de.digitalcollections.iiif.model.jackson.SerializerModifier;
import de.digitalcollections.iiif.model.Service;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.asList;


/**
 * Abstract IIIF resource, most other resources are based on this.
 */
@JsonPropertyOrder({"@context", "@id", "@type", "label", "description", "metadata", "thumbnail", "service"})
public abstract class Resource {
  public final static String CONTEXT =  "http://iiif.io/api/presentation/2/context.json";

  /** Only used during serialization,
   *  @see SerializerModifier **/
  @JsonProperty("@context")
  public String _context;

  @JsonProperty("@id")
  private URI identifier;

  private PropertyValue label;

  private PropertyValue description;

  @JsonProperty("service")
  private List<Service> services;

  @JsonProperty("thumbnail")
  private List<ImageContent> thumbnails;

  @JsonProperty("attribution")
  private PropertyValue attribution;

  @JsonProperty("license")
  private List<URI> licenses;

  @JsonProperty("logo")
  private List<ImageContent> logos;

  private List<MetadataEntry> metadata;

  @JsonProperty("viewingHint")
  private List<ViewingHint> viewingHints;

  @JsonProperty("related")
  private List<OtherContent> related;

  @JsonProperty("rendering")
  private List<OtherContent> renderings;

  @JsonProperty("seeAlso")
  private List<OtherContent> seeAlsoContents;

  @JsonProperty("within")
  private List<Resource> within;

  public Resource() {
    this.identifier = null;
  }

  @JsonCreator
  public Resource(@JsonProperty("@id") String identifier) {
    if (identifier != null) {
      this.identifier = URI.create(identifier);
    } else {
      this.identifier = null;
    }
  }

  @JsonProperty("@type")
  public String getType() {
    return null;  // Does not have a type
  }

  public URI getIdentifier() {
    return identifier;
  }

  protected void setIdentifier(URI identifier) {
    this.identifier = identifier;
  }

  public List<Service> getServices() {
    return this.services;
  }

  public void setServices(List<Service> services) {
    this.services = services;
  }

  public Resource addService(Service first, Service... rest) {
    if (this.services == null) {
      this.services = new ArrayList<>();
    }
    this.services.addAll(Lists.asList(first, rest));
    return this;
  }

  public List<ImageContent> getThumbnails() {
    return thumbnails;
  }

  @JsonIgnore
  public ImageContent getThumbnail() {
    return thumbnails.get(0);
  }

  public void setThumbnails(List<ImageContent> thumbnails) {
    this.thumbnails = thumbnails;
  }

  public Resource addThumbnail(ImageContent... thumbnails) {
    if (this.thumbnails == null) {
      this.thumbnails = new ArrayList<>();
    }
    this.thumbnails.addAll(Arrays.asList(thumbnails));
    return this;
  }

  public List<MetadataEntry> getMetadata() {
    return metadata;
  }

  public Resource addMetadata(MetadataEntry... meta) {
    if (this.metadata == null) {
      this.metadata = new ArrayList<>();
    }
    this.metadata.addAll(Arrays.asList(meta));
    return this;
  }

  public Resource addMetadata(String label, String value) {
    return this.addMetadata(new MetadataEntry(label, value));
  }

  public PropertyValue getLabel() {
    return label;
  }

  @JsonIgnore
  public String getLabelString() {
    return label.getFirstValue();
  }

  public void setLabel(PropertyValue label) {
    this.label = label;
  }

  public Resource addLabel(String first, String... rest) {
    if (this.label == null) {
      this.label = new PropertyValue();
    }
    this.label.addValue(first, rest);
    return this;
  }

    public PropertyValue getDescription() {
    return description;
  }

  @JsonIgnore
  public String getDescriptionString() {
    return description.getFirstValue();
  }

  public void setDescription(PropertyValue description) {
    this.description = description;
  }

  public Resource addDescription(String first, String... rest) {
    if (this.description == null) {
      this.description = new PropertyValue();
    }
    this.description.addValue(first, rest);
    return this;
  }

  public PropertyValue getAttribution() {
    return attribution;
  }

  @JsonIgnore
  public String getAttributionString() {
    return attribution.getFirstValue();
  }

  public void setAttribution(PropertyValue attribution) {
    this.attribution = attribution;
  }

  public Resource addAttribution(String first, String... rest) {
    if (this.attribution == null) {
      this.attribution = new PropertyValue();
    }
    this.attribution.addValue(first, rest);
    return this;
  }

  public List<URI> getLicenses() {
    return licenses;
  }

  @JsonIgnore
  public URI getFirstLicense() {
    return licenses.get(0);
  }

  public void setLicenses(List<URI> licenses) {
    this.licenses = licenses;
  }

  public Resource addLicense(String first, String... rest) {
    if (this.licenses == null) {
      this.licenses = new ArrayList<>();
    }
    this.licenses.add(URI.create(first));
    Arrays.stream(rest).map(URI::create).forEach(licenses::add);
    return this;
  }

  public List<ImageContent> getLogos() {
    return logos;
  }

  @JsonIgnore
  public URI getLogoUri() {
    return logos.get(0).getIdentifier();
  }

  public void setLogos(List<ImageContent> logos) {
    this.logos = logos;
  }

  public Resource addLogo(String first, String... rest) {
    if (this.logos == null) {
      this.logos = new ArrayList<>();
    }
    this.logos.add(new ImageContent(first));
    Arrays.stream(rest).map(ImageContent::new).forEach(logos::add);
    return this;
  }

  public Resource addLogo(ImageContent first, ImageContent... rest) {
    if (this.logos == null) {
      this.logos = new ArrayList<>();
    }
    this.logos.addAll(Lists.asList(first, rest));
    return this;
  }

  @JsonIgnore
  public Set<ViewingHint.Type> getSupportedViewingHintTypes() {
    return ImmutableSet.of();
  }

  public List<ViewingHint> getViewingHints() {
    return viewingHints;
  }

  public void setViewingHints(List<ViewingHint> viewingHints) {
    for (ViewingHint hint : viewingHints) {
      boolean supportsHint = (hint.getType() == ViewingHint.Type.OTHER ||
                              this.getSupportedViewingHintTypes().contains(hint.getType()));
      if (!supportsHint) {
        throw new IllegalArgumentException(String.format(
            "Resources of type '%s' do not support the '%s' viewing hint.",
            this.getType(), hint.toString()));
      }
    }
    this.viewingHints = viewingHints;
  }

  public Resource addViewingHint(ViewingHint first, ViewingHint... rest) {
    List<ViewingHint> hints = this.viewingHints;
    if (hints == null) {
      hints = new ArrayList<>();
    }
    hints.addAll(Lists.asList(first, rest));
    this.setViewingHints(hints);
    return this;
  }

  public List<OtherContent> getRelated() {
    return related;
  }

  public void setRelated(List<OtherContent> related) {
    this.related = related;
  }

  public Resource addRelated(OtherContent first, OtherContent... rest) {
    if (related == null) {
      this.related = new ArrayList<>();
    }
    this.related.addAll(asList(first, rest));
    return this;
  }

  public List<OtherContent> getRenderings() {
    return renderings;
  }

  public void setRenderings(List<OtherContent> renderings) {
    this.renderings = renderings;
  }

  public Resource addRendering(OtherContent first, OtherContent... rest) {
    if (renderings == null) {
      this.renderings = new ArrayList<>();
    }
    this.renderings.addAll(Lists.asList(first, rest));
    return this;
  }

  public void verifyRendering(OtherContent content) {
    if (content.getProfile() == null || content.getFormat() == null) {
      throw new IllegalArgumentException("Rendering resources must have a profile and format set.");
    }
  }

  public List<OtherContent> getSeeAlso() {
    return seeAlsoContents;
  }

  public void setSeeAlso(List<OtherContent> seeAlso) {
    this.seeAlsoContents = seeAlso;
  }

  public Resource addSeeAlso(OtherContent first, OtherContent... rest) {
    if (seeAlsoContents == null) {
      this.seeAlsoContents = new ArrayList<>();
    }
    this.seeAlsoContents.addAll(asList(first, rest));
    return this;
  }

  public List<Resource> getWithin() {
    return within;
  }

  public void setWithin(List<Resource> within) {
    this.within = within;
  }

  public Resource addWithin(Resource first, Resource... rest) {
    if (within == null) {
      this.within = new ArrayList<>();
    }
    this.within.addAll(asList(first, rest));
    return this;
  }

  @Override
  public String toString() {
    return String.format("Resource(type='%s',id='%s')", getType(), getIdentifier());
  }
}
