package de.digitalcollections.iiif.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;

/**
 * A generic service with an identifier and a profile.
 *
 * Only for unknown services, please check if one of these matches your use case instead:
 * - {@link de.digitalcollections.iiif.model.image.ImageService}
 * - {@link de.digitalcollections.iiif.model.search.ContentSearchService}
 * - {@link de.digitalcollections.iiif.model.search.AutocompleteService}
 * - {@link de.digitalcollections.iiif.model.annex.PhysicalDimensionsService}
 * - {@link de.digitalcollections.iiif.model.annex.GeoService}
 */
public class GenericService extends Service {

  @JsonCreator
  public GenericService(@JsonProperty("@context") URI context,
    @JsonProperty("@id") String identifier) {
    super(context, identifier);
  }

  public GenericService(String context, String identifier, String profile) {
    super(URI.create(context), identifier);
    super.addProfile(profile);
  }
}
