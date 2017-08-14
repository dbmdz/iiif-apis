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
import de.digitalcollections.iiif.model.service.Service;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.asList;


@JsonPropertyOrder({"@context", "@id", "@type", "label", "description", "metadata", "thumbnail", "service"})
public abstract class Resource {
  /** Only used during serialization,
   *  @see SerializerModifier **/
  @JsonProperty("@context")
  public String _context;

  @JsonProperty("@id")
  private URI identifier;

  @JsonProperty("label")
  private List<PropertyValue> labels;

  @JsonProperty("description")
  private List<PropertyValue> descriptions;

  @JsonProperty("service")
  private List<Service> services;

  @JsonProperty("thumbnail")
  private List<ImageContent> thumbnails;

  @JsonProperty("attribution")
  private List<PropertyValue> attributions;

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

  public void addService(Service first, Service... rest) {
    if (this.services == null) {
      this.services = new ArrayList<>();
    }
    this.services.addAll(Lists.asList(first, rest));
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

  public void addThumbnail(ImageContent... thumbnails) {
    if (this.thumbnails == null) {
      this.thumbnails = new ArrayList<>();
    }
    this.thumbnails.addAll(Arrays.asList(thumbnails));
  }

  public List<MetadataEntry> getMetadata() {
    return metadata;
  }

  public void addMetadata(MetadataEntry... meta) {
    if (this.metadata == null) {
      this.metadata = new ArrayList<>();
    }
    this.metadata.addAll(Arrays.asList(meta));
  }

  public void addMetadata(String label, String value) {
    this.addMetadata(new MetadataEntry(label, value));
  }

  public List<PropertyValue> getLabels() {
    return labels;
  }

  @JsonIgnore
  public String getLabelString() {
    return labels.get(0).getFirstValue();
  }

  public void setLabel(String label) {
    this.addLabel(new PropertyValue(label));
  }

  public void setLabels(List<PropertyValue> labels) {
    this.labels = labels;
  }

  public void addLabel(PropertyValue first, PropertyValue... rest) {
    if (this.labels == null) {
      this.labels = new ArrayList<>();
    }
    this.labels.addAll(Lists.asList(first, rest));
  }

    public List<PropertyValue> getDescriptions() {
    return descriptions;
  }

  @JsonIgnore
  public String getDescriptionString() {
    return descriptions.get(0).getFirstValue();
  }

  public void setDescription(String description) {
    this.descriptions = Collections.singletonList(new PropertyValue(description));
  }

  public void setDescriptions(List<PropertyValue> descriptions) {
    this.descriptions = descriptions;
  }

  public void addDescription(String first, String... rest) {
    this.addDescription(
        new PropertyValue(first),
        Arrays.stream(rest).map(PropertyValue::new).toArray(PropertyValue[]::new));
  }

  public void addDescription(PropertyValue first, PropertyValue... rest) {
    if (this.descriptions == null) {
      this.descriptions = new ArrayList<>();
    }
    this.descriptions.addAll(Lists.asList(first, rest));
  }

  public List<PropertyValue> getAttributions() {
    return attributions;
  }

  @JsonIgnore
  public String getAttributionString() {
    return attributions.get(0).getFirstValue();
  }

  public void setAttributions(List<PropertyValue> attributions) {
    this.attributions = attributions;
  }

  public void addAttribution(String first, String... rest) {
    this.addAttribution(new PropertyValue(first),
                        Arrays.stream(rest).map(PropertyValue::new).toArray(PropertyValue[]::new));
  }

  public void addAttribution(PropertyValue first, PropertyValue... rest) {
    if (this.attributions == null) {
      this.attributions = new ArrayList<>();
    }
    this.attributions.addAll(Lists.asList(first, rest));
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

  public void addLicense(String first, String... rest) {
    if (this.licenses == null) {
      this.licenses = new ArrayList<>();
    }
    this.licenses.add(URI.create(first));
    Arrays.stream(rest).map(URI::create).forEach(licenses::add);
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

  public void addLogo(String first, String... rest) {
    if (this.logos == null) {
      this.logos = new ArrayList<>();
    }
    this.logos.add(new ImageContent(first));
    Arrays.stream(rest).map(ImageContent::new).forEach(logos::add);
  }

  public void addLogo(ImageContent first, ImageContent... rest) {
    if (this.logos == null) {
      this.logos = new ArrayList<>();
    }
    this.logos.addAll(Lists.asList(first, rest));
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

  public void addViewingHint(ViewingHint first, ViewingHint... rest) {
    List<ViewingHint> hints = this.viewingHints;
    if (hints == null) {
      hints = new ArrayList<>();
    }
    hints.add(first);
    hints.addAll(Arrays.asList(rest));
    this.setViewingHints(hints);
  }

  public List<OtherContent> getRelated() {
    return related;
  }

  public void setRelated(List<OtherContent> related) {
    this.related = related;
  }

  public void addRelated(OtherContent first, OtherContent... rest) {
    if (related == null) {
      this.related = new ArrayList<>();
    }
    this.related.addAll(asList(first, rest));
  }

  public List<OtherContent> getRenderings() {
    return renderings;
  }

  public void setRenderings(List<OtherContent> renderings) {
    this.renderings = renderings;
  }

  public void addRendering(OtherContent first, OtherContent... rest) {
    if (renderings == null) {
      this.renderings = new ArrayList<>();
    }
    this.renderings.addAll(Lists.asList(first, rest));
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

  public void addSeeAlso(OtherContent first, OtherContent... rest) {
    if (seeAlsoContents == null) {
      this.seeAlsoContents = new ArrayList<>();
    }
    this.seeAlsoContents.addAll(asList(first, rest));
  }

  public List<Resource> getWithin() {
    return within;
  }

  public void setWithin(List<Resource> within) {
    this.within = within;
  }

  public void addWithin(Resource first, Resource... rest) {
    if (within == null) {
      this.within = new ArrayList<>();
    }
    this.within.addAll(asList(first, rest));
  }

  @Override
  public String toString() {
    return String.format("Resource(type='%s',id='%s')", getType(), getIdentifier());
  }
}
