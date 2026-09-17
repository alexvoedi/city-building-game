package com.mygdx.game.tools.building;

import java.util.ArrayList;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.game.Cell;
import com.mygdx.game.Map;
import com.mygdx.game.TerrainType;
import com.mygdx.game.structures.Building;
import com.mygdx.game.structures.BuildingFactory;
import com.mygdx.game.structures.building.power_supply.PowerSupplyBuildingType;

public class BuildingConstructionTool<S extends Building, T> extends BuildingTool {
  BuildingFactory<S, T> buildingFactory;
  T buildingType;

  public BuildingConstructionTool(
      Map map,
      AssetManager assetManager,
      BuildingFactory<S, T> buildingFactory,
      T buildingType) {
    super(assetManager, map);

    this.buildingFactory = buildingFactory;
    this.buildingType = buildingType;
  }

  @Override
  public void build(ArrayList<Cell> cells) {
    if (cells == null || cells.isEmpty()) {
      return;
    }

    for (Cell cell : cells) {
      if (cell == null || cell.getStructure() != null || cell.getTerrain().getTerrainType() == TerrainType.WATER) {
        return;
      }
    }

    S building = this.buildingFactory.build(buildingType, cells);

    TextureAtlas buildingAtlas = assetManager.get("buildings.atlas", TextureAtlas.class);
    Sprite sprite = buildingAtlas.createSprite(getSpriteName());

    building.setSprite(sprite);

    for (Cell cell : cells) {
      cell.setZone(null);
    }
  }

  private String getSpriteName() {
    if (buildingType instanceof PowerSupplyBuildingType) {
      PowerSupplyBuildingType powerType = (PowerSupplyBuildingType) buildingType;

      switch (powerType) {
        case WIND_MILL:
          return "dummy-building-1x1";
        case NUCLEAR_POWER_PLANT:
          return "dummy-building-3x3";
        default:
          return "dummy-building-2x2";
      }
    }

    return "dummy-building-1x1";
  }
}
