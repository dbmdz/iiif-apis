package de.digitalcollections.iiif.model.openannotation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import de.digitalcollections.iiif.model.sharedcanvas.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A choice between two or more IIIF resources.
 *
 * See http://iiif.io/api/presentation/2.1/#choice-of-alternative-resources
 */
public class Choice extends Resource {
  public static final String TYPE = "oa:Choice";

  @JsonProperty("default")
  private final Resource defaultResource;

  @JsonProperty("item")
  private List<Resource> alternatives;


  @JsonCreator
  public Choice(@JsonProperty("default") Resource defaultResource,
                @JsonProperty("item") List<Resource> alternatives) {
    if (defaultResource.getIdentifier().toString().equals("rdf:nil")) {
      this.defaultResource = null;
    } else {
      this.defaultResource = defaultResource;
    }
    this.alternatives = alternatives.stream()
        .map(r -> r.getIdentifier().toString().equals("rdf:nil") ? null : r)
        .collect(Collectors.toList());
  }


  public Choice(Resource defaultResource, Resource firstAlternative, Resource... otherAlternatives) {
    this.defaultResource = defaultResource;
    this.alternatives = new ArrayList<>();
    this.alternatives.addAll(Lists.asList(firstAlternative, otherAlternatives));
  }

  @Override
  public String getType() {
    return TYPE;
  }

  public Resource getDefault() {
    return defaultResource;
  }

  public List<Resource> getAlternatives() {
    return alternatives;
  }

  public final void addAlternative(Resource first, Resource... rest) {
    if (this.alternatives == null) {
      this.alternatives = new ArrayList<>();
    }
    this.alternatives.addAll(Lists.asList(first, rest));
  }
}
