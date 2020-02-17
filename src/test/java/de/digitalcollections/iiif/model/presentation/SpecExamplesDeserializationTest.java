package de.digitalcollections.iiif.model.presentation;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import de.digitalcollections.iiif.model.ImageContent;
import de.digitalcollections.iiif.model.Motivation;
import de.digitalcollections.iiif.model.OtherContent;
import de.digitalcollections.iiif.model.Profile;
import de.digitalcollections.iiif.model.enums.ViewingDirection;
import de.digitalcollections.iiif.model.enums.ViewingHint;
import de.digitalcollections.iiif.model.image.ImageApiProfile;
import de.digitalcollections.iiif.model.image.ImageApiSelector;
import de.digitalcollections.iiif.model.image.ImageService;
import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import de.digitalcollections.iiif.model.openannotation.Annotation;
import de.digitalcollections.iiif.model.openannotation.ContentAsText;
import de.digitalcollections.iiif.model.openannotation.SpecificResource;
import de.digitalcollections.iiif.model.openannotation.SvgSelector;
import de.digitalcollections.iiif.model.sharedcanvas.AnnotationList;
import de.digitalcollections.iiif.model.sharedcanvas.Canvas;
import de.digitalcollections.iiif.model.sharedcanvas.Collection;
import de.digitalcollections.iiif.model.sharedcanvas.Layer;
import de.digitalcollections.iiif.model.sharedcanvas.Manifest;
import de.digitalcollections.iiif.model.sharedcanvas.Range;
import de.digitalcollections.iiif.model.sharedcanvas.Sequence;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import java.io.IOException;
import java.net.URI;
import java.util.Locale;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SpecExamplesDeserializationTest {

  private ObjectMapper mapper;

  @BeforeEach
  public void setup() {
    mapper = new IiifObjectMapper();
  }

  private <T> T readFromResources(String filename, Class<T> clz) throws IOException {
    return mapper.readValue(Resources.getResource("spec/presentation/" + filename), clz);
  }

  @Test
  public void testFullResponse() throws IOException {
    Manifest manifest = readFromResources("full_response.json", Manifest.class);
    assertThat(manifest).isNotNull();
    assertThat(manifest.getIdentifier().toString())
        .isEqualTo("http://example.org/iiif/book1/manifest");
    assertThat(manifest.getLabel().getValues()).hasSize(1);
    assertThat(manifest.getLabelString()).isEqualTo("Book 1");
    assertThat(manifest.getMetadata()).hasSize(2);
    assertThat(manifest.getMetadata().get(1).getValue().getLocalizations())
        .containsExactlyInAnyOrder(Locale.ENGLISH, Locale.FRENCH);
    assertThat(manifest.getNavDate()).isEqualTo("1856-01-01T00:00:00Z");

    assertThat(manifest.getServices().get(0).getContext().toString())
        .isEqualTo("http://example.org/ns/jsonld/context.json");
    assertThat(manifest.getServices().get(0))
        .hasFieldOrPropertyWithValue(
            "identifier", URI.create("http://example.org/service/example"));
    assertThat(manifest.getServices().get(0).getProfiles())
        .containsExactly(new Profile(URI.create("http://example.org/docs/example-service.html")));

    assertThat(manifest.getSeeAlso().get(0).getFormat())
        .isEqualTo(MimeType.fromTypename("application/marc"));
    assertThat(manifest.getRenderings().get(0).getFormat())
        .isEqualTo(MimeType.fromTypename("application/pdf"));
    assertThat(manifest.getWithin().get(0))
        .isInstanceOf(Collection.class)
        .hasFieldOrPropertyWithValue(
            "identifier", URI.create("http://example.org/collections/books/"));

    assertThat(manifest.getDefaultSequence().getCanvases()).hasSize(3);
    Canvas firstCanvas = manifest.getDefaultSequence().getCanvases().get(0);
    assertThat(firstCanvas.getHeight()).isEqualTo(1000);
    assertThat(firstCanvas.getImages().get(0).getOn()).isInstanceOf(Canvas.class);
    assertThat(firstCanvas.getImages().get(0).getResource())
        .isInstanceOf(ImageContent.class)
        .hasFieldOrPropertyWithValue("format", MimeType.MIME_IMAGE_JPEG)
        .hasFieldOrPropertyWithValue("height", 2000);
    Assertions.assertThat(firstCanvas.getImages().get(0).getResource().getServices().get(0))
        .isInstanceOf(ImageService.class);

    assertThat(firstCanvas.getOtherContent().get(0).getWithin().get(0))
        .isInstanceOf(Layer.class)
        .hasFieldOrPropertyWithValue(
            "identifier", URI.create("http://example.org/iiif/book1/layer/l1"));

    assertThat(
            manifest
                .getDefaultSequence()
                .getCanvases()
                .get(1)
                .getOtherContent()
                .get(0)
                .getWithin()
                .get(0))
        .isInstanceOf(Layer.class)
        .hasFieldOrPropertyWithValue(
            "identifier", URI.create("http://example.org/iiif/book1/layer/l1"));

    assertThat(manifest.getRanges()).hasSize(1);
    assertThat(manifest.getRanges().get(0).getLabelString()).isEqualTo("Introduction");
    assertThat(manifest.getRanges().get(0).getCanvases()).hasSize(3);
    assertThat(manifest.getRanges().get(0).getCanvases())
        .allMatch(
            c -> c.getIdentifier().toString().startsWith("http://example.org/iiif/book1/canvas/p"));
  }

  @Test
  public void testAnnotationList() throws IOException {
    AnnotationList annoList = readFromResources("annotationList.json", AnnotationList.class);
    assertThat(annoList).isNotNull();
    assertThat(annoList.getIdentifier().toString())
        .isEqualTo("http://example.org/iiif/book1/list/p1");
    assertThat(annoList.getResources()).hasSize(2);
    assertThat(annoList.getResources().get(0).getOn()).isInstanceOf(Canvas.class);
    assertThat(annoList.getResources().get(0).getResource())
        .isInstanceOf(OtherContent.class)
        .hasFieldOrPropertyWithValue("type", "dctypes:Sound")
        .hasFieldOrPropertyWithValue("format", MimeType.fromTypename("audio/mpeg"));
  }

  @Test
  public void testAnnotationListWithTranscription() throws IOException {
    AnnotationList annoList =
        readFromResources("annotationListWithTranscription.json", AnnotationList.class);
    assertThat(annoList).isNotNull();
    assertThat(annoList.getResources().get(0).getIdentifier().toString())
        .isEqualTo("http://example.org/iiif/book1/annotation/anno1");
    assertThat(annoList.getResources().get(0).getOn().getIdentifier().toString())
        .isEqualTo("http://example.org/iiif/book1/canvas/p1#xywh=100,100,300,300");
    assertThat(annoList.getResources().get(0).getResource()).isInstanceOf(SpecificResource.class);

    SpecificResource specificResource =
        (SpecificResource) annoList.getResources().get(0).getResource();
    ContentAsText full = (ContentAsText) specificResource.getFull();
    assertThat(full.getChars()).isEqualTo("Here starts book one...");
    assertThat(full.getFormat().getTypeName()).isEqualTo("text/plain");
    assertThat(full.getLanguage().toString()).isEqualTo("en");
    SvgSelector selector = (SvgSelector) specificResource.getSelector();
    assertThat(selector.getChars()).isEqualTo("<svg xmlns=\"...\"><path d=\"...\"/></svg>");
  }

  @Test
  public void testAnnotationListWithinLayer() throws IOException {
    AnnotationList annoList =
        readFromResources("annotationListWithinLayer.json", AnnotationList.class);
    assertThat(annoList).isNotNull();
    assertThat(annoList.getWithin()).hasSize(1);
    assertThat(annoList.getWithin().get(0)).isInstanceOf(Layer.class);
    assertThat(annoList.getWithin().get(0).getIdentifier().toString())
        .isEqualTo("http://example.org/iiif/book1/layer/transcription");
    assertThat(annoList.getWithin().get(0).getLabelString()).isEqualTo("Diplomatic Transcription");
  }

  @Test
  public void testCanvas() throws IOException {
    Canvas canvas = readFromResources("canvas.json", Canvas.class);
    assertThat(canvas).isNotNull();
    assertThat(canvas.getOtherContent()).hasSize(1);
    assertThat(canvas.getOtherContent().get(0)).isInstanceOf(AnnotationList.class);
  }

  @Test
  public void testAnnotationWithChoice() throws IOException {
    Annotation anno = readFromResources("annotationWithChoice.json", Annotation.class);
    assertThat(anno).isNotNull();
    assertThat(anno.getResource()).isInstanceOf(ImageContent.class);
    assertThat(anno.getResource().getAlternatives()).isNotEmpty();
    assertThat(anno.getResource().getAlternatives()).allMatch(ImageContent.class::isInstance);
    assertThat(anno.getOn()).isInstanceOf(Canvas.class);
  }

  @Test
  public void testAnnotationListPage() throws IOException {
    AnnotationList annoList = readFromResources("annotationListPage.json", AnnotationList.class);
    assertThat(annoList).isNotNull();
    assertThat(annoList.getStartIndex()).isEqualTo(0);
    assertThat(annoList.getWithin()).hasSize(1);
    assertThat(annoList.getWithin().get(0)).isInstanceOf(Layer.class);
    assertThat(annoList.getNext()).isInstanceOf(AnnotationList.class);
    assertThat(annoList.getNext().getIdentifier().toString())
        .isEqualTo("http://example.org/iiif/book1/list/l2");
  }

  @Test
  public void testCollection() throws IOException {
    Collection coll = readFromResources("collection.json", Collection.class);
    assertThat(coll).isNotNull();
    assertThat(coll.getViewingHints()).containsExactly(ViewingHint.TOP);
    assertThat(coll.getLabelString()).isEqualTo("Top Level Collection for Example Organization");
    assertThat(coll.getDescriptionString()).isEqualTo("Description of Collection");
    assertThat(coll.getAttributionString()).isEqualTo("Provided by Example Organization");
    assertThat(coll.getCollections()).hasSize(2);
    Collection childColl = coll.getCollections().get(0);
    assertThat(childColl.getMembers()).hasSize(3);
  }

  @Test
  public void testCollectionPage() throws IOException {
    Collection coll = readFromResources("collectionPage.json", Collection.class);
    assertThat(coll).isNotNull();
    assertThat(coll.getWithin()).hasSize(1);
    assertThat(coll.getWithin().get(0)).isInstanceOf(Collection.class);
    assertThat(coll.getStartIndex()).isEqualTo(0);
    assertThat(coll.getNext()).isInstanceOf(Collection.class);
    assertThat(coll.getManifests()).isEmpty();
  }

  @Test
  public void testImageResource() throws IOException {
    Annotation anno = readFromResources("imageResource.json", Annotation.class);
    assertThat(anno).isNotNull();
    assertThat(anno.getMotivation()).isEqualTo(Motivation.PAINTING);
    assertThat(anno.getOn()).isInstanceOf(Canvas.class);
    assertThat(anno.getResource()).isInstanceOf(ImageContent.class);
    ImageContent img = (ImageContent) anno.getResource();
    assertThat(img)
        .hasFieldOrPropertyWithValue("format", MimeType.MIME_IMAGE_JPEG)
        .hasFieldOrPropertyWithValue("width", 1500)
        .hasFieldOrPropertyWithValue("height", 2000);
    assertThat(img.getServices().get(0)).isInstanceOf(ImageService.class);
    ImageService imgService = (ImageService) img.getServices().get(0);
    assertThat(imgService.getProfiles()).containsExactly(ImageApiProfile.LEVEL_TWO);
  }

  @Test
  public void testImageSelector() throws IOException {
    ImageApiSelector selector = readFromResources("imageSelector.json", ImageApiSelector.class);
    assertThat(selector).isNotNull();
    assertThat(selector.getRegion().toString()).isEqualTo("50,50,1250,1850");
    assertThat(selector.asImageApiUri(URI.create("http://example.com/iiif/foobar")).toString())
        .isEqualTo("http://example.com/iiif/foobar/50,50,1250,1850/full/0/default.jpg");
  }

  @Test
  public void testLayer() throws IOException {
    Layer layer = readFromResources("layer.json", Layer.class);
    assertThat(layer).isNotNull();
    assertThat(layer.getLabelString()).isEqualTo("Diplomatic Transcription");
    assertThat(layer.getOtherContent()).hasSize(4);
  }

  @Test
  public void testManifest() throws Exception {
    Manifest manifest = readFromResources("manifest.json", Manifest.class);
    assertThat(manifest).isNotNull();
    // NOTE: Only testing stuff not yet tested in testFullResponse
    ImageContent thumb = manifest.getThumbnail();
    assertThat(thumb.getIdentifier().toString())
        .isEqualTo("http://example.org/images/book1-page1/full/80,100/0/default.jpg");
    assertThat(thumb.getServices().get(0)).isInstanceOf(ImageService.class);
    assertThat(thumb.getServices().get(0).getProfiles()).containsExactly(ImageApiProfile.LEVEL_ONE);
    assertThat(manifest.getViewingDirection()).isEqualTo(ViewingDirection.RIGHT_TO_LEFT);
    assertThat(manifest.getViewingHints()).containsExactly(ViewingHint.PAGED);
    assertThat(manifest.getLogos()).hasSize(1);
    assertThat(manifest.getRelated().get(0).getFormat())
        .isEqualTo(MimeType.fromTypename("video/mpeg"));
    assertThat(manifest.getSeeAlso().get(0))
        .hasFieldOrPropertyWithValue("format", MimeType.fromTypename("text/xml"));
    assertThat(manifest.getSeeAlso().get(0).getProfile().getIdentifier())
        .isEqualTo(URI.create("http://example.org/profiles/bibliographic"));
    assertThat(manifest.getRenderings().get(0).getFormat())
        .isEqualTo(MimeType.fromTypename("application/pdf"));
    assertThat(manifest.getRenderings().get(0).getLabelString()).isEqualTo("Download as PDF");
    assertThat(manifest.getWithin().get(0)).isInstanceOf(Collection.class);
    assertThat(manifest.getDefaultSequence().getLabelString()).isEqualTo("Current Page Order");
  }

  @Test
  public void testManifestWithSimpleLogo() throws Exception {
    Manifest manifest = readFromResources("manifestWithSimpleLogo.json", Manifest.class);
    assertThat(manifest).isNotNull();
    // NOTE: Only testing stuff not yet tested in testManifest
    assertThat(manifest.getLogos()).hasSize(1);
  }

  @Test
  public void testPagedCollection() throws Exception {
    Collection coll = readFromResources("pagedCollection.json", Collection.class);
    assertThat(coll.getTotal()).isEqualTo(9316290);
    assertThat(coll.getFirst()).isInstanceOf(Collection.class);
  }

  @Test
  public void testPagedLayer() throws Exception {
    Layer layer = readFromResources("pagedLayer.json", Layer.class);
    assertThat(layer.getTotal()).isEqualTo(496923);
    assertThat(layer.getFirst()).isInstanceOf(AnnotationList.class);
  }

  @Test
  public void testManifestWithRanges() throws Exception {
    Manifest manifest = readFromResources("manifestWithRanges.json", Manifest.class);
    assertThat(manifest.getSequences()).isEmpty();
    assertThat(manifest.getRanges()).hasSize(3);
    Range nestedRange = manifest.getRanges().get(0);
    assertThat(nestedRange.getViewingHints()).containsExactly(ViewingHint.TOP);
    assertThat(nestedRange.getMembers()).hasSize(3);
    assertThat(nestedRange.getMembers().get(0)).isInstanceOf(Canvas.class);
    assertThat(nestedRange.getMembers().get(1)).isInstanceOf(Range.class);
    Range otherRange = manifest.getRanges().get(1);
    assertThat(otherRange.getRanges()).hasSize(1);
    assertThat(otherRange.getCanvases()).hasSize(3);
  }

  @Test
  public void testSequence() throws Exception {
    Sequence seq = readFromResources("sequence.json", Sequence.class);
    assertThat(seq.getCanvases()).hasSize(3);
    assertThat(seq.getViewingDirection()).isEqualTo(ViewingDirection.LEFT_TO_RIGHT);
    assertThat(seq.getViewingHints()).containsExactly(ViewingHint.PAGED);
  }

  @Test
  public void testSpecificResourceAnnotation() throws Exception {
    Annotation anno = readFromResources("specificResourceAnnotation.json", Annotation.class);
    assertThat(anno.getResource()).isInstanceOf(SpecificResource.class);
    SpecificResource res = (SpecificResource) anno.getResource();
    assertThat(res.getFull()).isInstanceOf(ImageContent.class);
    assertThat(res.getSelector()).isInstanceOf(ImageApiSelector.class);
  }

  @Test
  public void testEmbeddedContentAnnotation() throws Exception {
    Annotation anno = readFromResources("embeddedContent.json", Annotation.class);
    assertThat(anno.getResource()).isInstanceOf(ContentAsText.class);
    ContentAsText content = (ContentAsText) anno.getResource();
    assertThat(content.getChars()).isEqualTo("Here starts book one...");
    assertThat(content.getFormat()).isEqualTo(MimeType.fromTypename("text/plain"));
    assertThat(content.getLanguage()).isEqualTo(Locale.ENGLISH);
  }

  @Test
  public void testSvgSelector() throws Exception {
    Annotation anno = readFromResources("svgSelector.json", Annotation.class);
    assertThat(anno.getResource()).isInstanceOf(SpecificResource.class);
    SpecificResource res = (SpecificResource) anno.getResource();
    assertThat(res.getSelector()).isInstanceOf(SvgSelector.class);
    assertThat(((SvgSelector) res.getSelector()).getChars())
        .isEqualTo("<svg xmlns=\"...\"><path d=\"...\"/></svg>");
  }

  @Test
  public void testStylesheet() throws Exception {
    Annotation anno = readFromResources("stylesheet.json", Annotation.class);
    assertThat(anno.getStylesheet().getChars()).isEqualTo(".red {color: red;}");
    assertThat(((SpecificResource) anno.getResource()).getStyle()).isEqualTo("red");
  }
}
