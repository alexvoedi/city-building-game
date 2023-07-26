package com.mygdx.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.structures.Structure;
import com.mygdx.game.zones.Zone;

public class Cell {
  public static Vector2 CELL_SIZE = new Vector2(32, 16);
  public static Vector2 CELL_SIZE_HALF = new Vector2(16, 8);

  GridPoint2 gridPosition;
  Vector2 position;

  Terrain terrain;
  Zone zone;
  Structure structure;

  Sprite powerIndicatorSprite;
  boolean hasPower;

  public Cell(AssetManager assetManager, int x, int y, float height) {
    this.gridPosition = new GridPoint2(x, y);

    this.position = new Vector2(
      (y + x) * CELL_SIZE_HALF.x,
      (y - x) * CELL_SIZE_HALF.y
    );

    this.terrain = new Terrain(assetManager, this, height);

    TextureAtlas commonAtlas = assetManager.get("common.atlas", TextureAtlas.class);
    this.powerIndicatorSprite = commonAtlas.createSprite("cell");
    this.powerIndicatorSprite.setPosition(this.position.x, this.position.y);
    this.hasPower = false;
  }

  public void update(float delta) {
  }

  public void render(SpriteBatch spriteBatch) {
    terrain.render(spriteBatch);

    if (zone != null) {
      zone.render(spriteBatch);
    }

    if (hasPower) {
      powerIndicatorSprite.draw(spriteBatch);
    }
  }

  public GridPoint2 getGridPosition() {
    return gridPosition;
  }

  public void setGridPosition(GridPoint2 gridPosition) {
    this.gridPosition = gridPosition;
  }

  public Vector2 getPosition() {
    return position;
  }

  public void setPosition(Vector2 position) {
    this.position = position;
  }

  public Structure getStructure() {
    return structure;
  }

  public void setStructure(Structure structure) {
    this.structure = structure;
  }

  public Zone getZone() {
    return zone;
  }

  public void setZone(Zone zone) {
    this.zone = zone;
  }

  public Terrain getTerrain() {
    return terrain;
  }

  public void setTerrain(Terrain terrain) {
    this.terrain = terrain;
  }

  public boolean isHasPower() {
    return hasPower;
  }

  public void setHasPower(boolean hasPower) {
    this.hasPower = hasPower;
  }


}
