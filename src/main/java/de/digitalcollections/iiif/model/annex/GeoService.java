package de.digitalcollections.iiif.model.annex;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.digitalcollections.iiif.model.Service;
import java.net.URI;

/**
 * A GeoJSON service (http://iiif.io/api/annex/services/#geojson).
 *
 * Initialize it either with an URL to a GeoJSON resource that identifies the linked location, or pass a
 * {@link org.geojson.Feature} object that defines the location.
 */
public class GeoService extends Service {
  public static final String CONTEXT = "http://geojson.org/geojson-ld/geojson-context.jsonld";

  @JsonUnwrapped
  private org.geojson.Feature feature;

  public GeoService() {
    super(URI.create(CONTEXT));
  };

  public GeoService(String identifier) {
    this();
    this.setIdentifier(URI.create(identifier));
  }

  public GeoService(org.geojson.Feature feature) {
    this(null, feature);
  }

  public GeoService(String identifier, org.geojson.Feature feature) {
    this(identifier);
    // Don't set null-ish Features
    if (!isFeatureEmpty(feature)) {
      this.feature = feature;
    }
  }

  public String getType() {
    // Skip type if feature is empty
    if (!isFeatureEmpty(feature)) {
      return "Feature";
    } else {
      return null;
    }
  }

  public org.geojson.Feature getFeature() {
    return feature;
  }

  private boolean isFeatureEmpty(org.geojson.Feature feature) {
    return feature == null || (feature.getGeometry() == null && feature.getProperties().isEmpty());
  }
}
