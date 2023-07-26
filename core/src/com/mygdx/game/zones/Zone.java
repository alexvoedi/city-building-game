package com.mygdx.game.zones;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Cell;

public abstract class Zone {
  AssetManager assetManager;
  Cell cell;
  Sprite sprite;

  public Zone(AssetManager assetManager, Cell cell) {
    this.assetManager = assetManager;
    this.cell = cell;

    Vector2 cellPosition = cell.getPosition();

    TextureAtlas commonAtlas = assetManager.get("common.atlas", TextureAtlas.class);
    this.sprite = commonAtlas.createSprite("cell");
    this.sprite.setPosition(cellPosition.x, cellPosition.y);
  }

  public void render(SpriteBatch spriteBatch) {
    sprite.draw(spriteBatch);
  }
}
