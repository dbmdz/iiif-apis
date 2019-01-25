package de.digitalcollections.iiif.model.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.impl.IndexedListSerializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import de.digitalcollections.iiif.model.Profile;
import de.digitalcollections.iiif.model.jackson.serialization.IiifIndexedListSerializer;
import de.digitalcollections.iiif.model.jackson.serialization.ProfileSerializer;
import de.digitalcollections.iiif.model.jackson.serialization.ResourceSerializer;
import de.digitalcollections.iiif.model.sharedcanvas.Resource;
import java.util.ArrayList;

/**
 * Modifies the serializer to support the following functions:
 *
 *  - Add the JSON-LD '@context' property with the IIIF context to the top-level object
 *  - Serialize empty Resources as null, Resources with only an @id as strings
 *  - Remove redundant `@type` from Annotation.on and certain image resources
 *  - Add custom logic for when to unwrap single values
 */
public class SerializerModifier extends BeanSerializerModifier {

  @Override
  public JsonSerializer<?> modifyCollectionSerializer(SerializationConfig config, CollectionType valueType,
                                                      BeanDescription beanDesc, JsonSerializer<?> serializer) {
    if (valueType.getRawClass() == ArrayList.class) {
      return new IiifIndexedListSerializer((IndexedListSerializer) serializer, config.getTypeFactory());
    }
    return super.modifyCollectionSerializer(config, valueType, beanDesc, serializer);
  }

  @Override
  public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc,
                                            JsonSerializer<?> serializer) {
    if (Resource.class.isAssignableFrom(beanDesc.getBeanClass())) {
      return new ResourceSerializer((JsonSerializer<Object>) serializer);
    } else if (Profile.class.isAssignableFrom(beanDesc.getBeanClass())) {
      return new ProfileSerializer((JsonSerializer<Object>) serializer);
    }
    return super.modifySerializer(config, beanDesc, serializer);
  }
}
