package de.digitalcollections.iiif.model.jackson.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import de.digitalcollections.iiif.model.PropertyValue;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;

public class PropertyValueSerializer extends StdSerializer<PropertyValue> {
  public PropertyValueSerializer() {
    this(null);
  }

  public PropertyValueSerializer(Class<PropertyValue> t) {
    super(t);
  }

  private void writeSingleLocalization(JsonGenerator jgen, Locale language, String value) throws IOException {
    jgen.writeStartObject();
    jgen.writeStringField("@language", language.toLanguageTag());
    jgen.writeStringField("@value", value);
    jgen.writeEndObject();
  }

  @Override
  public void serialize(PropertyValue value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
    if (value.getLocalizations().size() == 1 && value.getLocalizations().contains(Locale.ROOT)) {
      // Simple property value
      if (value.getValues().size() == 1) {
        jgen.writeString(value.getValues().get(0));
      } else {
        jgen.writeStartArray();
        for (String val : value.getValues()) {
          jgen.writeString(val);
        }
        jgen.writeEndArray();
      }
    } else {
      // Localized property value
      Set<Locale> localizations = value.getLocalizations();
      if (localizations.size() == 1 && value.getValues().size() == 1) {
        Locale lang = localizations.iterator().next();
        this.writeSingleLocalization(jgen, lang, value.getFirstValue());
      } else if (localizations.size() > 1 || (value.getValues() != null && value.getValues().size() > 1)) {
        jgen.writeStartArray();
        for (Locale language : localizations) {
          for (String v : value.getValues(language)) {
            this.writeSingleLocalization(jgen, language, v);
          }
        }
        jgen.writeEndArray();
      } else {
        jgen.writeNull();
      }
    }
  }
}
