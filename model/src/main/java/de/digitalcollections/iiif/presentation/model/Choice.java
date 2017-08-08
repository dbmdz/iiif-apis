package de.digitalcollections.iiif.presentation.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import de.digitalcollections.iiif.presentation.model.sharedcanvas.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JsonTypeName(Choice.TYPE)
public class Choice<T extends Resource> extends Resource {
  public static final String TYPE = "oa:Choice";

  @JsonProperty("default")
  private final T defaultResource;

  @JsonProperty("item")
  private List<T> alternatives;


  @SafeVarargs
  public Choice(T defaultResource, T firstAlternative, T... otherAlternatives) {
    this.defaultResource = defaultResource;
    this.alternatives = new ArrayList<>();
    this.alternatives.add(firstAlternative);
    this.alternatives.addAll(Arrays.asList(otherAlternatives));
  }

  @Override
  public String getType() {
    return TYPE;
  }

  public T getDefault() {
    return defaultResource;
  }

  public List<T> getAlternatives() {
    return alternatives;
  }

  @SafeVarargs
  public final void addAlternatives(T first, T... rest) {
    this.alternatives.add(first);
    this.alternatives.addAll(Arrays.asList(rest));
  }
}
