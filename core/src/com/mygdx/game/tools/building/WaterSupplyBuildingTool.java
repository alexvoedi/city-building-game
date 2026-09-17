package com.mygdx.game.tools.building;

import java.util.ArrayList;

import com.badlogic.gdx.assets.AssetManager;
import com.mygdx.game.Cell;
import com.mygdx.game.Map;
import com.mygdx.game.City;
import com.mygdx.game.structures.Structure;
import com.mygdx.game.structures.building.water_supply.WaterTreatmentPlant;

public class WaterSupplyBuildingTool extends BuildingTool {

  private static final int WATER_TREATMENT_PLANT_COST = WaterTreatmentPlant.COST;

  City city;
  Map map;

  public WaterSupplyBuildingTool(AssetManager assetManager, Map map, City city) {
    super(assetManager, map);

    this.map = map;
    this.city = city;
  }

  @Override
  public void build(ArrayList<Cell> cells) {
    if (cells.isEmpty()) {
      return;
    }

    Cell cell = cells.get(0);

    if (cell.getTerrain().getTerrainType().name().equals("WATER")) {
      return;
    }

    Structure structure = cell.getStructure();
    if (structure != null) {
      return;
    }

    if (city.getMoney() < WATER_TREATMENT_PLANT_COST) {
      return;
    }

    ArrayList<Cell> occupiedCells = new ArrayList<>();
    occupiedCells.add(cell);

    WaterTreatmentPlant waterTreatmentPlant = new WaterTreatmentPlant(assetManager, occupiedCells);

    cell.setStructure(waterTreatmentPlant);

    city.setMoney(city.getMoney() - WATER_TREATMENT_PLANT_COST);
  }
}
