package de.digitalcollections.iiif.presentation.model.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NONE)
public class GenericService extends Service {
  @JsonCreator
  public GenericService(@JsonProperty("@context") String context) {
    super(context);
  }
}
