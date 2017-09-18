package de.digitalcollections.iiif.model.sharedcanvas.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import de.digitalcollections.iiif.model.Motivation;
import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import de.digitalcollections.iiif.model.openannotation.Annotation;
import de.digitalcollections.iiif.model.openannotation.ContentAsText;
import de.digitalcollections.iiif.model.search.ContentSearchService;
import de.digitalcollections.iiif.model.search.SearchHit;
import de.digitalcollections.iiif.model.search.SearchLayer;
import de.digitalcollections.iiif.model.search.SearchResult;
import de.digitalcollections.iiif.model.search.Term;
import de.digitalcollections.iiif.model.search.TermList;
import de.digitalcollections.iiif.model.search.TextQuoteSelector;
import de.digitalcollections.iiif.model.sharedcanvas.AnnotationList;
import de.digitalcollections.iiif.model.sharedcanvas.Canvas;
import de.digitalcollections.iiif.model.sharedcanvas.Manifest;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class SpecExamplesSerializationTest {
  private ObjectMapper mapper;

  @Before
  public void setup() {
    mapper = new IiifObjectMapper();
  }

  private String readFromResources(String filename) throws IOException {
    return Resources.toString(
        Resources.getResource("spec/search/" + filename), Charset.defaultCharset());
  }

  private void assertSerializationEqualsSpec(Object obj, String specFilename) throws IOException, JSONException {
    String specJson = readFromResources(specFilename);
    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    JSONAssert.assertEquals(specJson, json, true);
  }

  @Test
  public void testAutocomplete() throws Exception {
    ContentSearchService service = new ContentSearchService("http://example.org/services/identifier/search");
    service.setAutocompleteServiceFromId("http://example.org/services/identifier/autocomplete");
    assertSerializationEqualsSpec(service, "autocomplete.json");
  }

  @Test
  public void testBasicSearch() throws Exception {
    SearchResult result = new SearchResult("http://example.org/service/manifest/search?q=bird&page=1");
    result.addResource(new Annotation("http://example.org/identifier/annotation/anno1"));
    SearchHit hit = new SearchHit();
    hit.addAnnotation(new Annotation("http://example.org/identifier/annotation/anno1"));
    result.addHit(hit);
    assertSerializationEqualsSpec(result, "basicSearch.json");
  }

  @Test
  public void testFullResponse() throws Exception {
    TermList terms = new TermList("http://example.org/service/identifier/autocomplete?q=bir&motivation=painting");
    terms.addIgnored("user");
    terms.addTerm(
        new Term("http://example.org/service/identifier/search?motivation=painting&q=bird", "bird", 15),
        new Term("http://example.org/service/identifier/search?motivation=painting&q=biro", "biro", 3),
        new Term("http://example.org/service/identifier/search?motivation=painting&q=birth", "birth", 9),
        new Term("http://example.org/service/identifier/search?motivation=painting&q=birthday", "birthday", 21));
    assertSerializationEqualsSpec(terms, "fullResponse.json");
  }

  @Test
  public void testFullResponseWithLabels() throws Exception {
    TermList terms = new TermList("http://example.org/service/identifier/autocomplete?q=http%3A%2F%2Fsemtag.example.org%2Ftag%2Fb&motivation=tagging");
    terms.addIgnored("user");
    terms.addTerm(
        new Term("http://example.org/service/identifier/autocomplete?motivation=tagging&q=http%3A%2F%2Fsemtag.example.org%2Ftag%2Fbird", "http://semtag.example.org/tag/bird", 15, "bird"),
        new Term("http://example.org/service/identifier/autocomplete?motivation=tagging&q=http%3A%2F%2Fsemtag.example.org%2Ftag%2Fbiro", "http://semtag.example.org/tag/biro", 3, "biro"));
    assertSerializationEqualsSpec(terms, "fullResponseWithLabels.json");
  }

  @Test
  public void testHighlighting() throws Exception {
    SearchResult result = new SearchResult("http://example.org/service/manifest/search?q=b*&page=1");
    Annotation anno = new Annotation("http://example.org/identifier/annotation/anno-line",
                                     Motivation.PAINTING);
    anno.setOn(new Canvas("http://example.org/identifier/canvas1#xywh=200,100,40,20"));
    anno.setResource(new ContentAsText("There are two birds in the bush."));
    result.addResource(anno);

    SearchHit hit = new SearchHit();
    hit.addAnnotation(new Annotation(anno.getIdentifier().toString()));
    hit.addSelector(
        new TextQuoteSelector("birds", "There are two ", " in the bush"),
        new TextQuoteSelector("bush", "two birds in the ", "."));
    result.addHit(hit);
    assertSerializationEqualsSpec(result, "highlighting.json");
  }

  @Test
  public void testMultiAnnotationHits() throws Exception {
    SearchResult result = new SearchResult("http://example.org/service/manifest/search?q=hand+is");
    Annotation anno1 = new Annotation("http://example.org/identifier/annotation/anno-bird",
                                     Motivation.PAINTING);
    anno1.setResource(new ContentAsText("A bird in the hand"));
    anno1.setOn(new Canvas("http://example.org/identifier/canvas1#xywh=200,100,150,30"));
    Annotation anno2 = new Annotation("http://example.org/identifier/annotation/anno-are",
        Motivation.PAINTING);
    anno2.setResource(new ContentAsText("is worth two in the bush"));
    anno2.setOn(new Canvas("http://example.org/identifier/canvas1#xywh=200,140,170,30"));
    result.addResource(anno1, anno2);

    SearchHit hit = new SearchHit();
    hit.addAnnotation(new Annotation(anno1.getIdentifier().toString()),
                      new Annotation(anno2.getIdentifier().toString()));
    hit.setMatch("hand is");
    hit.setBefore("A bird in the ");
    hit.setAfter(" worth two in the bush");
    result.addHit(hit);

    assertSerializationEqualsSpec(result, "multiAnnotationHits.json");
  }

  @Test
  public void testComplexTargetResource() throws Exception {
    AnnotationList annos = new AnnotationList("http://example.org/service/manifest/search?q=bird&motivation=painting");
    Annotation anno = new Annotation("http://example.org/identifier/annotation/anno-line",
                                     Motivation.PAINTING);
    anno.setResource(new ContentAsText("A bird in the hand is worth two in the bush"));

    Canvas target = new Canvas("http://example.org/identifier/canvas1#xywh=100,100,250,20");
    target.addWithin(new Manifest("http://example.org/identifier/manifest", "Example Manifest"));
    anno.setOn(target);
    annos.addResource(anno);
    assertSerializationEqualsSpec(annos, "targetResource.json");
  }

  @Test
  public void testWithIgnored() throws Exception {
    SearchResult result = new SearchResult("http://example.org/service/manifest/search?q=bird&page=1");
    SearchLayer layer = new SearchLayer();
    layer.setTotal(125);
    layer.addIgnored("user");
    result.addWithin(layer);
    result.setNext(new SearchResult("http://example.org/service/identifier/search?q=bird&page=2"));
    result.setStartIndex(0);
    result.setResources(Collections.emptyList());
    assertSerializationEqualsSpec(result, "withIgnored.json");
  }
}
