package de.digitalcollections.iiif.presentation.model.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.impl.IndexedListSerializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import de.digitalcollections.iiif.presentation.model.sharedcanvas.Resource;
import java.util.ArrayList;

/**
 * Modifies the serializer to support the following functionalities:
 *  - Add the JSON-LD '@context' property with the IIIF context to the top-level object
 *  - Serialize empty Resources as null, Resources with only an @id as strings
 *  - Remove redundant `@type` from Annotation.on and certain image resources
 */
public class SerializerModifier extends BeanSerializerModifier {

  ;

  @Override
  public JsonSerializer<?> modifyCollectionSerializer(SerializationConfig config, CollectionType valueType,
      BeanDescription beanDesc, JsonSerializer<?> serializer) {
    CollectionType arrayListType = config.getTypeFactory().constructCollectionType(ArrayList.class, Object.class);
    if (valueType.equals(arrayListType)) {
      return new IiifIndexedListSerializer((IndexedListSerializer) serializer, config.getTypeFactory());
    }
    return super.modifyCollectionSerializer(config, valueType, beanDesc, serializer);
  }

  @Override
  public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc,
      JsonSerializer<?> serializer) {
    if (Resource.class.isAssignableFrom(beanDesc.getBeanClass())) {
      return new ResourceSerializer((JsonSerializer<Object>) serializer);
    }
    return super.modifySerializer(config, beanDesc, serializer);
  }

}
