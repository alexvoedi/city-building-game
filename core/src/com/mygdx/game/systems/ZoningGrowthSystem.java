package com.mygdx.game.systems;

import java.util.ArrayList;

import com.artemis.BaseSystem;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.game.Cell;
import com.mygdx.game.City;
import com.mygdx.game.Map;
import com.mygdx.game.TerrainType;
import com.mygdx.game.structures.network.Road;
import com.mygdx.game.structures.building.residential.ResidentialBuilding;
import com.mygdx.game.zones.ResidentialZone;
import com.mygdx.game.zones.ZoneDensity;

public class ZoningGrowthSystem extends BaseSystem {
  private static final int PARK_INFLUENCE_RADIUS = 6;

  AssetManager assetManager;
  Map map;
  City city;

  float acc;

  public ZoningGrowthSystem(AssetManager assetManager, Map map, City city) {
    this.assetManager = assetManager;
    this.map = map;
    this.city = city;
  }

  @Override
  protected void processSystem() {
    this.acc += this.world.getDelta();

    if (acc > 0.75f) {
      this.acc -= 0.75f;
      runGrowthTick();
    }
  }

  private void runGrowthTick() {
    TextureAtlas buildingsAtlas = assetManager.get("buildings.atlas", TextureAtlas.class);

    for (Cell cell : map.getCells()) {
      if (!isBuildableResidentialTarget(cell)) {
        continue;
      }

      ResidentialZone residentialZone = (ResidentialZone) cell.getZone();
      ZoneDensity density = residentialZone.getZoneDensity();

      float growthChance = getGrowthChance(density) * city.getGrowthModifier();
      int nearbyParks = map.getNearbyParkCount(cell, PARK_INFLUENCE_RADIUS);
      growthChance *= 1f + Math.min(0.60f, nearbyParks * 0.12f);
      growthChance = Math.max(0f, Math.min(0.95f, growthChance));

      if (Math.random() > growthChance) {
        continue;
      }

      ArrayList<Cell> occupiedCells = new ArrayList<>();
      occupiedCells.add(cell);

      ResidentialBuilding building = new ResidentialBuilding(occupiedCells, density);
      Sprite sprite = buildingsAtlas.createSprite(getSpriteName(density));
      building.setSprite(sprite);

      city.setMoney(city.getMoney() + getGrowthIncome(density));
    }
  }

  private boolean isBuildableResidentialTarget(Cell cell) {
    if (!(cell.getZone() instanceof ResidentialZone)) {
      return false;
    }

    if (cell.getStructure() != null) {
      return false;
    }

    if (!cell.isHasPower()) {
      return false;
    }

    if (cell.getTerrain().getTerrainType() == TerrainType.WATER) {
      return false;
    }

    return hasAdjacentRoad(cell);
  }

  private boolean hasAdjacentRoad(Cell cell) {
    for (Cell roadCell : map.getCellsInCircle(cell.getGridPosition(), 4)) {
      if (roadCell.getStructure() instanceof Road) {
        return true;
      }
    }

    return false;
  }

  private float getGrowthChance(ZoneDensity density) {
    switch (density) {
      case LOW:
        return 0.03f;
      case MEDIUM:
        return 0.06f;
      case HIGH:
        return 0.10f;
      default:
        return 0.03f;
    }
  }

  private int getGrowthIncome(ZoneDensity density) {
    switch (density) {
      case LOW:
        return 50;
      case MEDIUM:
        return 90;
      case HIGH:
        return 150;
      default:
        return 50;
    }
  }

  private String getSpriteName(ZoneDensity density) {
    switch (density) {
      case LOW:
        return "dummy-building-1x1";
      case MEDIUM:
        return "dummy-building-2x2";
      case HIGH:
        return "dummy-building-3x3";
      default:
        return "dummy-building-1x1";
    }
  }
}
