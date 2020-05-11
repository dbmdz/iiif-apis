package de.digitalcollections.iiif.model.jackson;

import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.util.StdConverter;
import de.digitalcollections.iiif.model.MimeType;
import java.util.function.Function;

public class IiifModule extends SimpleModule {

  public IiifModule() {
    super("iiif-module");

    // This will set the ResourceSerializer. We can't set it directly, since it needs to be passed
    // the default serializer.
    this.setSerializerModifier(new SerializerModifier());
    this.setDeserializerModifier(new DeserializerModifier());

    // Just use MimeType's getTypeName and String constructor for serializing/deserializing it
    this.addSerializer(
        new StdDelegatingSerializer(MimeType.class, toString(MimeType::getTypeName)));
    this.addDeserializer(
        MimeType.class, new StdDelegatingDeserializer<>(fromString(MimeType::fromTypename)));
  }

  /** Helper function to create Converter from lambda * */
  private <T> Converter<String, T> fromString(Function<String, ? extends T> fun) {
    return new StdConverter<String, T>() {
      @Override
      public T convert(String value) {
        return fun.apply(value);
      }
    };
  }

  /** Helper function to create Converter from lambda * */
  private <T> Converter<T, String> toString(Function<T, String> fun) {
    return new StdConverter<T, String>() {
      @Override
      public String convert(T value) {
        return fun.apply(value);
      }
    };
  }
}
