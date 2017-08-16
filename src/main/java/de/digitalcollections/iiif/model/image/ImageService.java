package de.digitalcollections.iiif.model.image;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import de.digitalcollections.iiif.model.service.Service;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ImageService extends Service {
  @JsonProperty("protocol")
  public static final String PROTOCOL = "http://iiif.io/api/image";
  @JsonProperty("@context")
  public static final String CONTEXT = "http://iiif.io/api/image/2/context.json";

  private Integer width;
  private Integer height;
  private List<TileInfo> tiles;
  private List<Size> sizes;
  @JsonProperty("service")
  private List<Service> services;

  @JsonCreator
  public ImageService(@JsonProperty("@id") String identifier) {
    super(CONTEXT);
    this.setIdentifier(URI.create(identifier));
  }

  public ImageService(String identifier, ImageApiProfile profile) {
    this(identifier);
    this.addProfile(profile);
  }

  public Integer getWidth() {
    return width;
  }

  public void setWidth(Integer width) {
    this.width = width;
  }

  public Integer getHeight() {
    return height;
  }

  public void setHeight(Integer height) {
    this.height = height;
  }

  public List<TileInfo> getTiles() {
    return tiles;
  }

  public void setTiles(List<TileInfo> tiles) {
    this.tiles = tiles;
  }

  public void addTile(TileInfo first, TileInfo... rest) {
    if (this.tiles == null) {
      this.tiles = new ArrayList<>();
    }
    this.tiles.addAll(Lists.asList(first, rest));
  }

  public List<Size> getSizes() {
    return sizes;
  }

  public void setSizes(List<Size> sizes) {
    this.sizes = sizes;
  }

  public void addSize(Size first, Size... rest) {
    if (this.sizes == null) {
      this.sizes = new ArrayList<>();
    }
    this.sizes.addAll(Lists.asList(first, rest));
  }

  public List<Service> getServices() {
    return services;
  }

  public void setServices(List<Service> services) {
    this.services = services;
  }

  public void addService(Service first, Service... rest) {
    if (this.services == null) {
      this.services = new ArrayList<>();
    }
    this.services.addAll(Lists.asList(first, rest));
  }
}
