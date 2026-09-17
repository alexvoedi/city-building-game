package com.mygdx.game;

import static com.mygdx.game.Cell.CELL_SIZE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.structures.Tree;
import com.mygdx.game.structures.building.Park;
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

  long worldSeed;
  float terrainOffsetX;
  float terrainOffsetY;
  float detailOffsetX;
  float detailOffsetY;
  float valleyOffsetX;
  float valleyOffsetY;
  float riverOffsetX;
  float riverOffsetY;
  float riverWarpOffsetX;
  float riverWarpOffsetY;
  float moistureOffsetX;
  float moistureOffsetY;
  float canopyOffsetX;
  float canopyOffsetY;
  float coastOffsetX;
  float coastOffsetY;
  float continentShiftX;
  float continentShiftY;

  BuildingTool buildingTool;
  SelectionTool selectionTool;

  public Map(
      AssetManager assetManager,
      Viewport viewport,
      InputMultiplexer inputMultiplexer) {
    this.assetManager = assetManager;
    this.viewport = viewport;
    this.inputMultiplexer = inputMultiplexer;

    this.cells = new ArrayList<>();
    this.structures = new ArrayList<>();

    initializeSeed();

    this.generateCells();

    this.setSelectionTool(
        new LocationSelectionTool(assetManager, this, 1));
    this.setBuildingTool(
        new PowerSupplyBuildingFactory(this),
        PowerSupplyBuildingType.COAL_POWER_PLANT);
  }

  private void initializeSeed() {
    Random seedRandom = new Random();
    this.worldSeed = seedRandom.nextLong();

    Random random = new Random(worldSeed);

    terrainOffsetX = random.nextFloat() * 2000f - 1000f;
    terrainOffsetY = random.nextFloat() * 2000f - 1000f;
    detailOffsetX = random.nextFloat() * 2000f - 1000f;
    detailOffsetY = random.nextFloat() * 2000f - 1000f;
    valleyOffsetX = random.nextFloat() * 2000f - 1000f;
    valleyOffsetY = random.nextFloat() * 2000f - 1000f;
    riverOffsetX = random.nextFloat() * 2000f - 1000f;
    riverOffsetY = random.nextFloat() * 2000f - 1000f;
    riverWarpOffsetX = random.nextFloat() * 2000f - 1000f;
    riverWarpOffsetY = random.nextFloat() * 2000f - 1000f;
    moistureOffsetX = random.nextFloat() * 2000f - 1000f;
    moistureOffsetY = random.nextFloat() * 2000f - 1000f;
    canopyOffsetX = random.nextFloat() * 2000f - 1000f;
    canopyOffsetY = random.nextFloat() * 2000f - 1000f;
    coastOffsetX = random.nextFloat() * 2000f - 1000f;
    coastOffsetY = random.nextFloat() * 2000f - 1000f;

    continentShiftX = random.nextFloat() * 0.55f - 0.275f;
    continentShiftY = random.nextFloat() * 0.55f - 0.275f;
  }

  private void generateCells() {
    for (int x = 0; x < GAME_WORLD_SIZE; x++) {
      for (int y = 0; y < GAME_WORLD_SIZE; y++) {
        float z = getTerrainHeight(x, y);

        Cell cell = new Cell(assetManager, y, x, z);

        if (shouldSpawnTree(x, y, z)) {
          addScenery(cell);
        }

        cells.add(cell);
      }
    }
  }

  private float getTerrainHeight(int x, int y) {
    float normalizedX = (x / (float) (GAME_WORLD_SIZE - 1)) * 2f - 1f - continentShiftX;
    float normalizedY = (y / (float) (GAME_WORLD_SIZE - 1)) * 2f - 1f - continentShiftY;

    float distanceFromCenter = (float) Math.sqrt(normalizedX * normalizedX + normalizedY * normalizedY);
    float continentalShape = clamp(1f - distanceFromCenter * 1.12f, 0f, 1f);

    float macroRelief = fbmNoise((x + terrainOffsetX) / 140f, (y + terrainOffsetY) / 140f, 3, 0.55f, 2f);
    float detailRelief = fbmNoise((x + detailOffsetX) / 48f, (y + detailOffsetY) / 48f, 4, 0.5f, 2f);
    float valleyNoise = fbmNoise((x + valleyOffsetX) / 96f, (y + valleyOffsetY) / 96f, 2, 0.5f, 2f);

    float riverBase = Math.abs((float) SimplexNoise.noise((x + riverOffsetX) / 115f, (y + riverOffsetY) / 115f));
    float riverWarp = Math.abs((float) SimplexNoise.noise((x + riverWarpOffsetX) / 57f, (y + riverWarpOffsetY) / 57f));
    float riverSignal = riverBase * 0.72f + riverWarp * 0.28f;

    float terrain = 0.42f * continentalShape + 0.33f * macroRelief + 0.25f * detailRelief;
    terrain -= (0.12f * (1f - valleyNoise));

    float inlandFactor = smoothstep(0.12f, 0.62f, continentalShape);
    float riverMask = 1f - smoothstep(0.035f, 0.095f, riverSignal);
    terrain -= riverMask * inlandFactor * 0.18f;

    float coastMask = 1f - smoothstep(0.08f, 0.35f, continentalShape);
    float beachTarget = 0.235f + 0.015f * fbmNoise((x + coastOffsetX) / 65f, (y + coastOffsetY) / 65f, 2, 0.5f, 2f);
    terrain = terrain * (1f - coastMask * 0.45f) + beachTarget * (coastMask * 0.45f);

    return clamp(terrain, 0f, 1f);
  }

  private boolean shouldSpawnTree(int x, int y, float height) {
    if (height < 0.30f || height > 0.82f) {
      return false;
    }

    float moisture = fbmNoise((x + moistureOffsetX) / 72f, (y + moistureOffsetY) / 72f, 3, 0.5f, 2f);
    float canopyNoise = fbmNoise((x + canopyOffsetX) / 26f, (y + canopyOffsetY) / 26f, 2, 0.5f, 2f);

    float latitude = Math.abs((y / (float) (GAME_WORLD_SIZE - 1)) * 2f - 1f);
    float climateFactor = 1f - 0.35f * latitude;

    float density = 0.18f + 0.55f * moisture * climateFactor;

    return canopyNoise < density && Math.random() < 0.85;
  }

  private float fbmNoise(float sx, float sy, int octaves, float persistence, float lacunarity) {
    float amplitude = 1f;
    float frequency = 1f;
    float value = 0f;
    float amplitudeSum = 0f;

    for (int i = 0; i < octaves; i++) {
      value += amplitude * (float) SimplexNoise.noise(sx * frequency, sy * frequency);
      amplitudeSum += amplitude;

      amplitude *= persistence;
      frequency *= lacunarity;
    }

    float normalized = value / amplitudeSum;
    return 0.5f * (1f + normalized);
  }

  private float clamp(float value, float min, float max) {
    return Math.max(min, Math.min(max, value));
  }

  private float smoothstep(float edge0, float edge1, float x) {
    float t = clamp((x - edge0) / (edge1 - edge0), 0f, 1f);
    return t * t * (3f - 2f * t);
  }

  private void addScenery(Cell cell) {
    if (cell.getTerrain().getHeight() < 0.25 || Math.random() < 0.5)
      return;

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

    if (inBounds(x, y + 1))
      cells.add(getCell(x, y + 1));
    if (inBounds(x + 1, y))
      cells.add(getCell(x + 1, y));
    if (inBounds(x, y - 1))
      cells.add(getCell(x, y - 1));
    if (inBounds(x - 1, y))
      cells.add(getCell(x - 1, y));

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
      BuildingType buildingType) {
    this.buildingTool = new BuildingConstructionTool<StructureType, BuildingType>(
        this,
        assetManager,
        buildingFactory,
        buildingType);
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
        int y = center.y + dy;

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

  public int getNearbyParkCount(Cell centerCell, int radius) {
    HashSet<Structure> uniqueParks = new HashSet<>();

    for (Cell cell : getCellsInCircle(centerCell.getGridPosition(), radius)) {
      Structure structure = cell.getStructure();

      if (structure instanceof Park) {
        uniqueParks.add(structure);
      }
    }

    return uniqueParks.size();
  }
}
