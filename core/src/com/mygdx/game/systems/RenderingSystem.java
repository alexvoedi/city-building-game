package com.mygdx.game.systems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.function.Supplier;

import com.artemis.BaseSystem;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.mygdx.game.Cell;
import com.mygdx.game.Map;
import com.mygdx.game.ViewOverlayMode;
import com.mygdx.game.structures.Structure;
import com.mygdx.game.structures.building.Park;
import com.mygdx.game.structures.network.Road;
import com.mygdx.game.tools.selection.SelectionTool;

import static com.mygdx.game.Map.GAME_WORLD_SIZE;
import static com.mygdx.game.Cell.CELL_SIZE;

public class RenderingSystem extends BaseSystem {
  AssetManager assetManager;
  SpriteBatch spriteBatch;
  Viewport viewport;
  Map map;
  Supplier<ViewOverlayMode> overlayModeSupplier;
  TiledDrawable grid;
  Sprite overlaySprite;
  boolean showGrid;

  ArrayList<Structure> visibleStructures;
  HashSet<Structure> uniqueVisibleStructures;
  float visualAcc;

  public RenderingSystem(AssetManager assetManager, SpriteBatch spriteBatch, Viewport viewport, Map map,
      Supplier<ViewOverlayMode> overlayModeSupplier) {
    this.assetManager = assetManager;
    this.spriteBatch = spriteBatch;
    this.viewport = viewport;
    this.map = map;
    this.overlayModeSupplier = overlayModeSupplier;

    this.visibleStructures = new ArrayList<>();
    this.uniqueVisibleStructures = new HashSet<>();
    this.visualAcc = 0f;
    this.showGrid = false;

    this.generateGrid();
  }

  private void generateGrid() {
    TextureAtlas commonAtlas = assetManager.get("common.atlas", TextureAtlas.class);
    TextureRegion cursorTexture = commonAtlas.findRegion("cursor");
    this.overlaySprite = commonAtlas.createSprite("cursor");

    Color tintColor = new Color(0, 0, 0, 0.4f);
    grid = new TiledDrawable(cursorTexture).tint(tintColor);
  }

  @Override
  protected void processSystem() {
    visualAcc += world.getDelta();

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
    ViewOverlayMode overlayMode = overlayModeSupplier.get();

    visibleStructures.clear();
    uniqueVisibleStructures.clear();

    int i = 0;
    for (int x4 = xStart, y4 = yStart; x4 < xEnd || y4 > yEnd;) {
      for (int x = xStart, y = yStart; x < x1 || y < y1; x++, y++, x4 = x, y4 = y) {
        if (x >= 0 && y >= 0 && x < GAME_WORLD_SIZE && y < GAME_WORLD_SIZE) {
          Cell cell = cells.get(y * GAME_WORLD_SIZE + x);

          cell.render(spriteBatch);
          renderOverlay(cell, overlayMode);

          Structure structure = cell.getStructure();
          if (structure != null && uniqueVisibleStructures.add(structure))
            visibleStructures.add(structure);
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

    if (showGrid) {
      grid.draw(
          spriteBatch,
          (int) Math.floor(start.x / CELL_SIZE.x) * CELL_SIZE.x,
          (int) Math.floor(end.y / CELL_SIZE.y) * CELL_SIZE.y,
          Math.abs(end.x - start.x) + CELL_SIZE.x,
          Math.abs(end.y - start.y) + CELL_SIZE.y);
    }

    for (Structure structure : visibleStructures)
      structure.render(spriteBatch);

    renderParkAuras();

    SelectionTool selectionTool = map.getSelectionTool();
    if (selectionTool != null)
      selectionTool.render(spriteBatch);
  }

  private void renderParkAuras() {
    spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

    for (Structure structure : visibleStructures) {
      if (!(structure instanceof Park)) {
        continue;
      }

      Sprite sprite = structure.getSprite();
      if (sprite == null) {
        continue;
      }

      float pulse = 0.65f + 0.35f * MathUtils.sin(visualAcc * 2.3f + (structure.getZIndex() % 13));
      float glowW = 42f + 12f * pulse;
      float glowH = 20f + 6f * pulse;
      float x = sprite.getX() + sprite.getWidth() * 0.5f - glowW * 0.5f;
      float y = sprite.getY() - 2f;

      overlaySprite.setColor(0.22f, 0.95f, 0.30f, 0.15f * pulse);
      overlaySprite.setPosition(x, y);
      overlaySprite.setSize(glowW, glowH);
      overlaySprite.draw(spriteBatch);
    }

    spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    overlaySprite.setSize(CELL_SIZE.x, CELL_SIZE.y);
  }

  private void renderOverlay(Cell cell, ViewOverlayMode overlayMode) {
    switch (overlayMode) {
      case POWER:
        if (cell.isHasPower()) {
          drawOverlay(cell, 0.2f, 0.8f, 1f, 0.45f);
        } else if (cell.getZone() != null || cell.getStructure() != null) {
          drawOverlay(cell, 1f, 0.25f, 0.25f, 0.30f);
        }
        break;

      case ZONING:
        if (cell.getZone() != null) {
          drawOverlay(cell, 0.3f, 1f, 0.4f, 0.35f);
        }
        break;

      case TRAFFIC:
        if (cell.getStructure() instanceof Road) {
          drawOverlay(cell, 1f, 0.85f, 0.2f, 0.40f);
        }
        break;

      default:
        break;
    }
  }

  private void drawOverlay(Cell cell, float r, float g, float b, float a) {
    overlaySprite.setColor(r, g, b, a);
    overlaySprite.setPosition(cell.getPosition().x, cell.getPosition().y);
    overlaySprite.draw(spriteBatch);
  }

  public void toggleGrid() {
    showGrid = !showGrid;
  }

  public boolean isShowGrid() {
    return showGrid;
  }
}
