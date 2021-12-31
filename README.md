# iiif-apis

[![Image API 2.1.1 compliant](https://img.shields.io/badge/iiif--image--api-≤2.1.1-blue.svg)](https://iiif.io/api/image/2.1/)
[![Presentation API 2.1.1 compliant](https://img.shields.io/badge/iiif--presentation--api-≤2.1.1-blue.svg)](https://iiif.io/api/presentation/2.1/)
[![Authentication API 1.0 compliant](https://img.shields.io/badge/iiif--authentication--api-1.0-blue.svg)](https://iiif.io/api/auth/1.0/)
[![Search API 1.0 compliant](https://img.shields.io/badge/iiif--search--api-1.0-blue.svg)](https://iiif.io/api/search/1.0/)

[![Javadocs](https://javadoc.io/badge/de.digitalcollections.iiif/iiif-apis.svg)](https://javadoc.io/doc/de.digitalcollections.iiif/iiif-apis)
[![License](https://img.shields.io/github/license/dbmdz/iiif-apis.svg)](LICENSE)
[![GitHub release](https://img.shields.io/github/release/dbmdz/iiif-apis.svg)](https://github.com/dbmdz/iiif-apis/releases)
[![Maven Central](https://img.shields.io/maven-central/v/de.digitalcollections.iiif/iiif-apis.svg)](https://search.maven.org/search?q=a:iiif-apis)

This module contains model classes for all currently available IIIF API entities, namely for the
[Presentation](http://iiif.io/api/presentation/2.1/), [Image](http://iiif.io/api/image/2.1/),
[Content Search](http://iiif.io/api/search/1.0/) and [Authentication](http://iiif.io/api/auth/1.0/) APIs, as well as
the additional services defined in the [Service Annex](http://iiif.io/api/annex/services/). It also includes all the
necessary components to parse and create valid JSON-LD representations of these entities, using
[Jackson](https://github.com/FasterXML/jackson).

The library contains a comprehensive test suite for all entities, based on all of the examples
provided in the IIIF specifications. Therefore, it should be able to parse and create any IIIF
documents that are compliant with the official specifications. If you find that you cannot parse
a IIIF entity that you believe to be correct, please [submit an issue](https://github.com/dbmdz/iiif-apis/issues) including a link to
the JSON-LD representation of the entity. Likewise, if you find that you cannot express
a certain IIIF construct with the Java API, let us know.

It is intended to replace both [iiif-presentation-api](https://github.com/dbmdz/iiif-presentation-api)
and [iiif-image-api](https://github.com/dbmdz/iiif-image-api) once it is stable enough, with the non-model
parts of these libraries being moved into [hymir](https://github.com/dbmdz/iiif-server-hymir).

## Usage
To use the module, first add it to your project's Maven or Gradle configuration:

```xml
<dependency>
  <groupId>de.digitalcollections.iiif</groupId>
  <artifactId>iiif-apis</artifactId>
  <version>0.3.8</version>
</dependency>
```

```scala
dependencies {
    compile 'de.digitalcollections.iiif.iiif-apis:0.3.8'
}
```

For both reading and writing IIIF JSON-LD documents, it is neccessary to instantiate the Jackson
object mapper:

```java
import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;

ObjectMapper iiifMapper = new IiifObjectMapper();
```

The mapper can then be used to read IIIF JSON-LD documents into their corresponding Java object:

```java
import de.digitalcollections.iiif.model.sharedcanvas.Manifest;

// Reading a manifest
Manifest manifest = iiifMapper.readValue(
    "http://iiif.biblissima.fr/manifests/ark:/12148/btv1b8304502m/manifest.json",
    Manifest.class);

// Reading an annotation list
AnnotationList annoList = iiifMapper.readValue(
    "http://dams.llgc.org.uk/iiif/4342443/annotation/list/ART8.json",
    AnnotationList.class);
```

To create your own IIIF entities, just model them using the available classes and use the
object mapper to obtain their JSON-LD representation:

```java
import de.digitalcollections.iiif.model.sharedcanvas.Canvas;
import de.digitalcollections.iiif.model.ImageContent;
import de.digitalcollections.iiif.model.image.ImageApiProfile;
import de.digitalcollections.iiif.model.image.ImageService;
import de.digitalcollections.iiif.model.OtherContent;
import de.digitalcollections.iiif.model.search.ContentSearchService;
import de.digitalcollections.iiif.model.PropertyValue;

import java.util.Locale;


Canvas canvas = new Canvas("http://some.uri");
canvas.addLabel("A label");
canvas.addDescription("This is a slightly longer text about this canvas.");
canvas.setWidth(800);
canvas.setHeight(600);

// Image
canvas.addIIIFImage("http://some.uri/iiif/foo", ImageApiProfile.LEVEL_ONE);

// Thumbnail
ImageContent thumbnail = new ImageContent("http://some.uri/iiif/foo/full/250,/0/default.jpg");
thumbnail.addService(new ImageService("http://some.uri/iiif/foo", ImageApiProfile.LEVEL_ONE));
canvas.addThumbnail(thumbnail);

// Other Content
canvas.addSeeAlso(new OtherContent("http://some.uri/ocr/foo.hocr", "text/html"));

// Search Service
ContentSearchService searchService = new ContentSearchService("http://some.uri/search/foo");
searchService.addAutocompleteService("http://some.uri/autocomplete/foo");
canvas.addService(searchService);

// Metadata
canvas.addMetadata("Author", "Ignatius Jacques Reilly");
canvas.addMetadata("Location", "New Orleans");
PropertyValue key = new PropertyValue();
key.addValue(Locale.ENGLISH, "Key");
key.addValue(Locale.GERMAN, "Schlüssel");
key.addValue(Locale.CHINESE, "钥");
PropertyValue value = new PropertyValue();
value.addValue(Locale.ENGLISH, "A value", "Another value");
value.addValue(Locale.GERMAN, "Ein Wert", "Noch ein Wert");
value.addValue(Locale.CHINESE, "值", "另值");
canvas.addMetadata(new MetadataEntry(key, value));

// Other stuff
canvas.addViewingHint(ViewingHint.NON_PAGED);

// Licensing/Attribution
canvas.addLicense("http://rightsstatements.org/vocab/NoC-NC/1.0/");
canvas.addAttribution("Some fictional institution");
canvas.addLogo("http://some.uri/logo.jpg");
canvas.addLogo(new ImageContent(new ImageService(
    "http://some.uri/iiif/logo", ImageApiProfile.LEVEL_ONE)));

String json = iiifMapper.writerWithDefaultPrettyPrinter().writeValueAsString(canvas);
```

For more information on how to use the API, consult the
[API documentation](http://javadoc.io/doc/de.digitalcollections.iiif/iiif-apis)
and the [comprehensive test suite](https://github.com/dbmdz/iiif-apis/tree/master/src/test/java/de/digitalcollections/iiif/model).
Since the large majority of the tests are based on the examples from the  specifications, they should be very easy to
follow along.

## Building

To build a Jar file containing all classes you need to run `mvn package`.

### Possible errors

Note that additional Maven arguments (`-D`) an be chained, to ignore multiple problems at once (like in `mvn package -Dfmt.skip -DskipTests`).

#### `fmt-maven-plugin` not working

```
[ERROR] Failed to execute goal com.coveo:fmt-maven-plugin:2.13:format (default) on project iiif-apis: Execution default of goal com.coveo:fmt-maven-plugin:2.13:format failed: An API incompatibility was encountered while executing com.coveo:fmt-maven-plugin:2.13:format: java.lang.IllegalAccessError: null
```

If you get the error message above (on newer Java versions), consider skipping the `fmt-maven-plugin` by passing `-Dfmt.skip`, like in `mvn package -Dfmt.skip`.

#### Tests fail

Sometimes the tests won't pass, especially when you're using a development version, just pass `-DskipTests` to Maven, like in `mvn package -DskipTests`.
