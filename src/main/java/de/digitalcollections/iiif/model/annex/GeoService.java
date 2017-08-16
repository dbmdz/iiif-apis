package de.digitalcollections.iiif.model.geojson;

import de.digitalcollections.iiif.model.service.Service;
import java.net.URI;

public class GeoService extends Service {
  private static final String CONTEXT = "http://geojson.org/geojson-ld/geojson-context.jsonld";

  public GeoService() {
    super(URI.create(CONTEXT));
  }
}
