package com.mygdx.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

public class Terrain {
  AssetManager assetManager;
  Cell cell;
  float height;
  TerrainType terrainType;

  Sprite sprite;

  public Terrain(AssetManager assetManager, Cell cell, float height) {
    this.assetManager = assetManager;
    this.cell = cell;
    this.height = height;

    if      (height < 0.20) this.terrainType = TerrainType.WATER;
    else if (height < 0.25) this.terrainType = TerrainType.SAND;
    else                    this.terrainType = TerrainType.GRASS;

    this.assignTerrainTexture();
  }

  public void assignTerrainTexture() {
    String textureName = "";

    if      (height < 0.10) textureName += "water-2";
    else if (height < 0.20) textureName += "water-1";
    else if (height < 0.25) textureName += "sand-1";
    else if (height < 0.50) textureName += "grass-2";
    else                    textureName += "grass-1";

    Vector2 position = cell.getPosition();

    TextureAtlas terrainAtlas = assetManager.get("terrain.atlas", TextureAtlas.class);
    sprite = terrainAtlas.createSprite(textureName);
    sprite.setPosition(position.x, position.y);
  }

  public void render(SpriteBatch spriteBatch) {
    sprite.draw(spriteBatch);
  }

  public Cell getCell() {
    return cell;
  }

  public void setCell(Cell cell) {
    this.cell = cell;
  }

  public float getHeight() {
    return height;
  }

  public void setHeight(float height) {
    this.height = height;
  }

  public TerrainType getTerrainType() {
    return terrainType;
  }

  public void setTerrainType(TerrainType terrainType) {
    this.terrainType = terrainType;
  }
}
