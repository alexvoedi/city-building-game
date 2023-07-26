package com.mygdx.game.tools.selection;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Cell;
import com.mygdx.game.Map;
import com.mygdx.game.Renderable;
import com.mygdx.game.tools.building.BuildingTool;

import java.util.ArrayList;

public abstract class SelectionTool implements InputProcessor, Renderable {
  AssetManager assetManager;
  Map map;

  ArrayList<Cell> cells;

  public SelectionTool(AssetManager assetManager, Map map) {
    this.assetManager = assetManager;
    this.map = map;

    this.cells = new ArrayList<>();
  }

  public void render(SpriteBatch spriteBatch) {
    TextureAtlas commonAtlas = assetManager.get("common.atlas", TextureAtlas.class);

    for (Cell cell : cells) {
      Vector2 cellPosition = cell.getPosition();
      Sprite sprite = commonAtlas.createSprite("cursor");
      sprite.setPosition(cellPosition.x, cellPosition.y);
      sprite.draw(spriteBatch);
    }
  }

  public void build() {
    BuildingTool buildingTool = map.getBuildingTool();

    if (buildingTool != null) {
      buildingTool.build(cells);
    }

    cells = new ArrayList<>();
  }

  @Override
  public boolean keyDown(int keycode) {
    return false;
  }

  @Override
  public boolean keyUp(int keycode) {
    return false;
  }

  @Override
  public boolean keyTyped(char character) {
    return false;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return false;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    return false;
  }

  @Override
  public boolean scrolled(float amountX, float amountY) {
    return false;
  }
}
