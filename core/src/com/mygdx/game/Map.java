package com.mygdx.game;

import static com.mygdx.game.Cell.CELL_SIZE;

import java.util.ArrayList;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.structures.Tree;
import com.mygdx.game.structures.building.power_supply.PowerSupplyBuildingFactory;
import com.mygdx.game.structures.building.power_supply.PowerSupplyBuildingType;
import com.mygdx.game.structures.Building;
import com.mygdx.game.structures.BuildingFactory;
import com.mygdx.game.structures.Structure;
import com.mygdx.game.tools.building.BuildingConstructionTool;
import com.mygdx.game.tools.building.BuildingTool;
import com.mygdx.game.tools.selection.LocationSelectionTool;
import com.mygdx.game.tools.selection.SelectionTool;
import com.mygdx.game.util.SimplexNoise;

public class Map {
  public static int GAME_WORLD_SIZE = 256;

  AssetManager assetManager;
  Viewport viewport;
  InputMultiplexer inputMultiplexer;

  ArrayList<Cell> cells;
  ArrayList<Structure> structures;

  BuildingTool buildingTool;
  SelectionTool selectionTool;

  public Map(
    AssetManager assetManager,
    Viewport viewport,
    InputMultiplexer inputMultiplexer
  ) {
    this.assetManager = assetManager;
    this.viewport = viewport;
    this.inputMultiplexer = inputMultiplexer;

    this.cells = new ArrayList<>();
    this.structures = new ArrayList<>();

    this.generateCells();

    this.setSelectionTool(
      new LocationSelectionTool(assetManager, this, 1)
    );
    this.setBuildingTool(
      new PowerSupplyBuildingFactory(this),
      PowerSupplyBuildingType.COAL_POWER_PLANT
    );
  }

  private void generateCells() {
    for (int x = 0; x < GAME_WORLD_SIZE; x++) {
      for (int y = 0; y < GAME_WORLD_SIZE; y++) {

        float sx = x / 64f;
        float sy = y / 64f;

        float z = (float) (0.5 * (1 + SimplexNoise.noise(sx, sy)));

        Cell cell = new Cell(assetManager, y, x, z);

        double treeThreshold = 0.5 * (1 + SimplexNoise.noise(2 * sx, 2 * sy));

        if (treeThreshold > 0.6) addScenery(cell);

        cells.add(cell);
      }
    }
  }

  private void addScenery(Cell cell) {
    if (cell.getTerrain().getHeight() < 0.25 || Math.random() < 0.5) return;

    ArrayList<Cell> cells = new ArrayList<>();

    cells.add(cell);

    Tree scenery = new Tree(assetManager, cells);
    structures.add(scenery);
  }

  public GridPoint2 getGridPoint(float vx, float vy) {
    int x = (int) Math.floor((vx / CELL_SIZE.x) - (vy / CELL_SIZE.y) + 0.5);
    int y = (int) Math.floor((vx / CELL_SIZE.x) + (vy / CELL_SIZE.y) - 0.5);

    return new GridPoint2(x, y);
  }

  public Cell getCell(int x, int y) {
    boolean isInBounds = inBounds(x, y);

    if (isInBounds) {
      return cells.get(y * GAME_WORLD_SIZE + x);
    } else {
      return null;
    }

  }

  public ArrayList<Cell> getCells() {
    return cells;
  }

  public void setCells(ArrayList<Cell> cells) {
    this.cells = cells;
  }

  public ArrayList<Cell> getSurroundingCells(int x, int y) {
    ArrayList<Cell> cells = new ArrayList<>();

    for (int ox = x - 1; ox <= x + 1; ox++) {
      for (int oy = y - 1; oy <= y + 1; oy++) {
        boolean inBounds = inBounds(ox, oy);

        if (inBounds && (ox != x || oy != y)) {
          Cell cell = getCell(ox, oy);

          cells.add(cell);
        }
      }
    }

    return cells;
  }

  public ArrayList<Cell> getAdjacentCells(int x, int y) {
    ArrayList<Cell> cells = new ArrayList<>();

    if (inBounds(x    , y + 1)) cells.add(getCell(x    , y + 1));
    if (inBounds(x + 1, y    )) cells.add(getCell(x + 1, y    ));
    if (inBounds(x    , y - 1)) cells.add(getCell(x    , y - 1));
    if (inBounds(x - 1, y    )) cells.add(getCell(x - 1, y    ));

    return cells;
  }

  public boolean inBounds(int x, int y) {
    return x >= 0 && y >= 0 && x < GAME_WORLD_SIZE && y < GAME_WORLD_SIZE;
  }

  public ArrayList<Structure> getStructures() {
    return structures;
  }

  public void setStructures(ArrayList<Structure> structures) {
    this.structures = structures;
  }

  public BuildingTool getBuildingTool() {
    return buildingTool;
  }

  public <StructureType extends Building, BuildingType> void setBuildingTool(
    BuildingFactory<StructureType, BuildingType> buildingFactory,
    BuildingType buildingType
  ) {
    this.buildingTool = new BuildingConstructionTool<StructureType, BuildingType>(
      this,
      assetManager,
      buildingFactory,
      buildingType
    );
  }

  public void setBuildingTool(BuildingTool buildingTool) {
    this.buildingTool = buildingTool;
  }

  public SelectionTool getSelectionTool() {
    return selectionTool;
  }

  public void setSelectionTool(SelectionTool selectionTool) {
    inputMultiplexer.removeProcessor(this.selectionTool);
    this.selectionTool = selectionTool;
    inputMultiplexer.addProcessor(this.selectionTool);
  }

  public Vector2 screenToWorldCoordinates(int screenX, int screenY) {
    return viewport.unproject(new Vector2(screenX, screenY));
  }

  public ArrayList<Cell> getCellsInSquare(GridPoint2 center, int radius) {
    ArrayList<Cell> cells = new ArrayList<>();

    for (int dx = -radius; dx <= radius; dx++) {
      for (int dy = -radius; dy <= radius; dy++) {
        int x = center.x + dx;
        int y = center.y + dx;

        if (inBounds(x, y)) {
          Cell cell = getCell(x, y);
          cells.add(cell);
        }
      }
    }

    return cells;
  }

  public ArrayList<Cell> getCellsInCircle(GridPoint2 center, int radius) {
    ArrayList<Cell> cells = new ArrayList<>();

    for (int dx = -radius; dx <= radius; dx++) {
      for (int dy = -radius; dy <= radius; dy++) {
        int x = center.x + dx;
        int y = center.y + dy;

        if (inBounds(x, y)) {
          boolean inCircle = dx * dx + dy * dy <= radius * radius;

          if (inCircle) {
            Cell cell = getCell(x, y);
            cells.add(cell);
          }
        }
      }
    }

    return cells;
  }
}
