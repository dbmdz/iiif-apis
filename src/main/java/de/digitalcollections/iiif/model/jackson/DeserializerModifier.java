package de.digitalcollections.iiif.model.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import de.digitalcollections.iiif.model.Profile;
import de.digitalcollections.iiif.model.api.Selector;
import de.digitalcollections.iiif.model.image.ImageApiProfile.Format;
import de.digitalcollections.iiif.model.image.ImageApiProfile.Quality;
import de.digitalcollections.iiif.model.service.Service;
import de.digitalcollections.iiif.model.sharedcanvas.Resource;
import java.util.Arrays;

public class DeserializerModifier extends BeanDeserializerModifier {

  @Override
  public JsonDeserializer<?> modifyEnumDeserializer(DeserializationConfig config, JavaType type,
      BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
    if (Arrays.asList(Quality.class, Format.class).contains(type.getRawClass())) {
      return new EnumDeserializer((Class<? extends Enum>) type.getRawClass());

    }
    return super.modifyEnumDeserializer(config, type, beanDesc, deserializer);
  }

  @Override
  public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc,
      JsonDeserializer<?> deserializer) {
    // We don't use the @JsonDeserialize annotation since we only want the
    // custom deserializer for the abstract type and not for the actual types.
    if (Service.class == beanDesc.getBeanClass()) {
      return new ServiceDeserializer();
    } else if (Resource.class == beanDesc.getBeanClass()) {
      return new ResourceDeserializer();
    } else if (Selector.class == beanDesc.getBeanClass()) {
      // Selectors and CssStyles can be ContentAsText, if they're not we just delegate
      // to the default deserializer.
      return new SelectorDeserializer((JsonDeserializer<Object>) deserializer);
    } else if (Profile.class == beanDesc.getBeanClass()) {
      return new ProfileDeserializer((JsonDeserializer<Object>) deserializer);
    }
    return super.modifyDeserializer(config, beanDesc, deserializer);
  }
}
