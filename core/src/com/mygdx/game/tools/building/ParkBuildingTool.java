package com.mygdx.game.tools.building;

import java.util.ArrayList;

import com.badlogic.gdx.assets.AssetManager;
import com.mygdx.game.Cell;
import com.mygdx.game.City;
import com.mygdx.game.Map;
import com.mygdx.game.TerrainType;
import com.mygdx.game.structures.Structure;
import com.mygdx.game.structures.Tree;
import com.mygdx.game.structures.building.Park;

public class ParkBuildingTool extends BuildingTool {
  public static final int PARK_COST = 300;

  City city;

  public ParkBuildingTool(AssetManager assetManager, Map map, City city) {
    super(assetManager, map);
    this.city = city;
  }

  @Override
  public void build(ArrayList<Cell> cells) {
    if (cells == null || cells.isEmpty()) {
      return;
    }

    Cell cell = cells.get(0);
    if (cell == null) {
      return;
    }

    if (city.getMoney() < PARK_COST) {
      return;
    }

    if (cell.getTerrain().getTerrainType() == TerrainType.WATER) {
      return;
    }

    Structure structure = cell.getStructure();
    if (structure != null && !(structure instanceof Tree)) {
      return;
    }

    if (cell.getZone() != null) {
      return;
    }

    if (structure instanceof Tree) {
      cell.setStructure(null);
    }

    ArrayList<Cell> occupiedCells = new ArrayList<>();
    occupiedCells.add(cell);

    Park park = new Park(assetManager, occupiedCells);
    city.addMoney(-PARK_COST);
  }
}
