package de.digitalcollections.iiif.model.jackson.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.CaseFormat;
import java.io.IOException;

public class EnumDeserializer extends JsonDeserializer<Enum> {

  private final Class<? extends Enum> enumType;

  public EnumDeserializer(Class<? extends Enum> enumType) {
    this.enumType = enumType;
  }

  @Override
  public Enum deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    return Enum.valueOf(
        enumType, CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, p.getValueAsString()));
  }
}
