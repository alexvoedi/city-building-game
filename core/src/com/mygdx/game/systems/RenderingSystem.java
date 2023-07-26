package com.mygdx.game.systems;

import java.util.ArrayList;
import java.util.Collections;

import com.artemis.BaseSystem;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.mygdx.game.Cell;
import com.mygdx.game.Map;
import com.mygdx.game.structures.Structure;
import com.mygdx.game.tools.selection.SelectionTool;

import static com.mygdx.game.Map.GAME_WORLD_SIZE;
import static com.mygdx.game.Cell.CELL_SIZE;

public class RenderingSystem extends BaseSystem {
  AssetManager assetManager;
  SpriteBatch spriteBatch;
  Viewport viewport;
  Map map;
  TiledDrawable grid;

  ArrayList<Structure> visibleStructures;

  public RenderingSystem(AssetManager assetManager, SpriteBatch spriteBatch, Viewport viewport, Map map) {
    this.assetManager = assetManager;
    this.spriteBatch = spriteBatch;
    this.viewport = viewport;
    this.map = map;

    this.visibleStructures = new ArrayList<>();

    this.generateGrid();
  }

  private void generateGrid() {
    TextureAtlas commonAtlas = assetManager.get("common.atlas", TextureAtlas.class);
    TextureRegion cursorTexture = commonAtlas.findRegion("cursor");

    Color tintColor = new Color(0, 0, 0, 0.4f);
    grid = new TiledDrawable(cursorTexture).tint(tintColor);
  }

  @Override
  protected void processSystem() {
    Vector2 upperLeftCorner = new Vector2(0, 0);
    Vector2 lowerRightCorner = new Vector2(viewport.getScreenWidth(), viewport.getScreenHeight());

    Vector2 start = viewport.unproject(upperLeftCorner);
    Vector2 end = viewport.unproject(lowerRightCorner);

    int xStart = (int) Math.round((start.x / CELL_SIZE.x) - (start.y / CELL_SIZE.y)) - 4;
    int yStart = (int) Math.round((start.x / CELL_SIZE.x) + (start.y / CELL_SIZE.y));

    int xEnd = (int) Math.round((end.x / CELL_SIZE.x) - (end.y / CELL_SIZE.y));
    int yEnd = (int) Math.round((end.x / CELL_SIZE.x) + (end.y / CELL_SIZE.y)) - 8;

    int x1 = (int) Math.round((end.x / CELL_SIZE.x) - (start.y / CELL_SIZE.y));
    int y1 = (int) Math.round((end.x / CELL_SIZE.x) + (start.y / CELL_SIZE.y)) - 8;

    ArrayList<Cell> cells = map.getCells();

    visibleStructures = new ArrayList<>();

    int i = 0;
    for (int x4 = xStart, y4 = yStart; x4 < xEnd || y4 > yEnd;) {
      for (int x = xStart, y = yStart; x < x1 || y < y1; x++, y++, x4 = x, y4 = y) {
        if (x >= 0 && y >= 0 && x < GAME_WORLD_SIZE && y < GAME_WORLD_SIZE) {
          Cell cell = cells.get(y * GAME_WORLD_SIZE + x);

          cell.render(spriteBatch);

          Structure structure = cell.getStructure();
          if (structure != null) visibleStructures.add(structure);
        }
      }

      if (++i % 2 == 0) {
        --yStart;
        --y1;
      } else {
        ++xStart;
        ++x1;
      }
    }

    Collections.sort(visibleStructures, (a, b) -> {
      if (a.getZIndex() > b.getZIndex()) {
        return +1;
      } else if (a.getZIndex() < b.getZIndex()) {
        return -1;
      } else {
        return 0;
      }
    });

    grid.draw(
      spriteBatch,
      (int) Math.floor(start.x / CELL_SIZE.x) * CELL_SIZE.x,
      (int) Math.floor(end.y / CELL_SIZE.y) * CELL_SIZE.y,
      Math.abs(end.x - start.x) + CELL_SIZE.x,
      Math.abs(end.y - start.y) + CELL_SIZE.y
    );

    for (Structure structure : visibleStructures) structure.render(spriteBatch);

    SelectionTool selectionTool = map.getSelectionTool();
    if (selectionTool != null) selectionTool.render(spriteBatch);
  }
}
