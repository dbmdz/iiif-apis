package de.digitalcollections.iiif.presentation.model.sharedcanvas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.google.common.collect.ImmutableSet;
import de.digitalcollections.iiif.presentation.model.ImageContent;
import de.digitalcollections.iiif.presentation.model.MetadataEntry;
import de.digitalcollections.iiif.presentation.model.PropertyValue;
import de.digitalcollections.iiif.presentation.model.enums.ViewingHint;
import de.digitalcollections.iiif.presentation.model.jackson.SerializerModifier;
import de.digitalcollections.iiif.presentation.model.service.ImageService;
import de.digitalcollections.iiif.presentation.model.service.Service;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


// We use an existing property for @type (i.e. we don't let Jackson set it), since this is the only way to ensure @type
// comes after @id, as recommended by the specification
@JsonPropertyOrder({"@context", "@id", "@type", "label", "description", "metadata", "thumbnail", "service"})
@JsonTypeInfo(use = Id.NAME,
              property = "@type",
              include = As.EXISTING_PROPERTY)
@JsonSubTypes({
    @Type(value = Canvas.class, name="sc:Canvas"),
    @Type(value = Layer.class, name="sc:Layer"),
    @Type(value = Collection.class, name="sc:Collection")
})
public abstract class Resource {
  /** Only used during serialization,
   *  @see SerializerModifier **/
  @JsonProperty("@context")
  public String _context;

  @JsonProperty("@id")
  private final URI identifier;

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

  private List<Content> otherContent;

  private List<MetadataEntry> metadata;

  @JsonProperty("viewingHint")
  private List<ViewingHint> viewingHints;

  @JsonProperty("related")
  private List<Content> relatedContents;

  @JsonProperty("rendering")
  private List<Content> renderingContents;

  @JsonProperty("seeAlso")
  private List<Content> seeAlsoContents;

  @JsonProperty("within")
  private List<Resource> withinResources;

  public Resource() {
    this.identifier = null;
  }

  @JsonCreator
  public Resource(@JsonProperty("@id") String identifier) {
    this.identifier = URI.create(identifier);
  }

  @JsonProperty("@type")
  public String getType() {
    return null;  // Does not have a type
  }

  public URI getIdentifier() {
    return identifier;
  }

  public List<Service> getServices() {
    return this.services;
  }

  public void setServices(List<Service> services) {
    this.services = services;
  }

  public void addService(Service... services) {
    if (this.services == null) {
      this.services = new ArrayList<>();
    }
    this.services.addAll(Arrays.asList(services));
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

  public void addThumbnails(ImageContent... thumbnails) {
    if (this.thumbnails == null) {
      this.thumbnails = new ArrayList<>();
    }
    this.thumbnails.addAll(Arrays.asList(thumbnails));
  }

  public List<Content> getOtherContent() {
    return otherContent;
  }

  public void setOtherContent(List<Content> otherContent) {
    this.otherContent = otherContent;
  }

  public void addOtherContent(Content... contents) {
    if (this.otherContent == null) {
      this.otherContent = new ArrayList<>();
    }
    this.otherContent.addAll(Arrays.asList(contents));
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
    this.labels = Collections.singletonList(new PropertyValue(label));
  }

  public void setLabels(List<PropertyValue> labels) {
    this.labels = labels;
  }

  public void addLabels(PropertyValue... labels) {
    if (this.labels == null) {
      this.labels = new ArrayList<>();
    }
    this.labels.addAll(Arrays.asList(labels));
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

  public void addDescriptions(PropertyValue... descriptions) {
    if (this.descriptions == null) {
      this.descriptions = new ArrayList<>();
    }
    this.descriptions.addAll(Arrays.asList(descriptions));
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
    if (this.attributions == null) {
      this.attributions = new ArrayList<>();
    }
    this.attributions.add(new PropertyValue(first));
    this.attributions.addAll(Arrays.stream(rest).map(PropertyValue::new).collect(Collectors.toList()));
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

  public void addLogo(ImageService first, ImageService... rest) {
    if (this.logos == null) {
      this.logos = new ArrayList<>();
    }
    this.logos.add(new ImageContent(first));
    Arrays.stream(rest).map(ImageContent::new).forEach(logos::add);
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

  public void addViewingHints(ViewingHint first, ViewingHint... rest) {
    List<ViewingHint> hints = this.viewingHints;
    if (hints == null) {
      hints = new ArrayList<>();
    }
    hints.add(first);
    hints.addAll(Arrays.asList(rest));
    this.setViewingHints(hints);
  }

  public List<Content> getRelatedContents() {
    return relatedContents;
  }

  public void setRelatedContents(List<Content> relatedContents) {
    this.relatedContents = relatedContents;
  }

  public void addRelatedResources(Content first, Content... rest) {
    if (relatedContents == null) {
      this.relatedContents = new ArrayList<>();
    }
    this.relatedContents.add(first);
    this.relatedContents.addAll(Arrays.asList(rest));
  }

  public List<Content> getRenderingContents() {
    return renderingContents;
  }

  public void setRenderingContents(
      List<Content> renderingContents) {
    this.renderingContents = renderingContents;
  }

  public void addRendering(Content first, Content... rest) {
    if (renderingContents == null) {
      this.renderingContents = new ArrayList<>();
    }
    this.renderingContents.add(first);
    this.renderingContents.addAll(Arrays.asList(rest));
  }

  public List<Content> getSeeAlsoContents() {
    return seeAlsoContents;
  }

  public void setSeeAlsoContents(
      List<Content> seeAlsoContents) {
    this.seeAlsoContents = seeAlsoContents;
  }

  public void addSeeAlso(Content first, Content... rest) {
    if (seeAlsoContents == null) {
      this.seeAlsoContents = new ArrayList<>();
    }
    this.seeAlsoContents.add(first);
    this.seeAlsoContents.addAll(Arrays.asList(rest));
  }

  public List<Resource> getWithinResources() {
    return withinResources;
  }

  public void setWithinResources(
      List<Resource> withinResources) {
    this.withinResources = withinResources;
  }

  public void addWithin(Resource first, Resource... rest) {
    if (withinResources == null) {
      this.withinResources = new ArrayList<>();
    }
    this.withinResources.add(first);
    this.withinResources.addAll(Arrays.asList(rest));
  }
}
