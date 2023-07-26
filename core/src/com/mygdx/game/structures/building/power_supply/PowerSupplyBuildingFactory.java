package com.mygdx.game.structures.building.power_supply;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.mygdx.game.Cell;
import com.mygdx.game.Map;
import com.mygdx.game.structures.BuildingFactory;
import com.mygdx.game.structures.building.PowerSupplyBuilding;

public class PowerSupplyBuildingFactory extends BuildingFactory<PowerSupplyBuilding, PowerSupplyBuildingType> {

  public PowerSupplyBuildingFactory(Map map) {
    super(map);
  }

  @Override
  public PowerSupplyBuilding build(
    PowerSupplyBuildingType buildingType,
    ArrayList<Cell> cells
  ) {
    PowerSupplyBuilding building = this.getBuilding(buildingType, cells);

    Set<Cell> newCells = new HashSet<>();

    for (Cell cell : cells) {
      cell.setHasPower(true);

      ArrayList<Cell> surroundingCells = map.getCellsInCircle(cell.getGridPosition(), 5);
      newCells.addAll(surroundingCells);
    }

    for (Cell cell : newCells) {
      cell.setHasPower(true);
    }

    return building;
  }

  private PowerSupplyBuilding getBuilding(
    PowerSupplyBuildingType buildingType,
    ArrayList<Cell> cells
  ) {
    switch (buildingType) {
      case COAL_POWER_PLANT: {
        return new CoalPowerPlant(cells);
      }

      case FUSION_POWER_PLANT: {
        return new CoalPowerPlant(cells);
      }

      case GAS_POWER_PLANT: {
        return new CoalPowerPlant(cells);
      }

      case MICROWAVE_POWER_PLANT: {
        return new CoalPowerPlant(cells);
      }

      case NUCLEAR_POWER_PLANT: {
        return new CoalPowerPlant(cells);
      }

      case OIL_POWER_PLANT: {
        return new CoalPowerPlant(cells);
      }

      case SOLAR_POWER_COLLECTOR: {
        return new CoalPowerPlant(cells);
      }

      case WIND_MILL: {
        return new CoalPowerPlant(cells);
      }

      default: {
        return new CoalPowerPlant(cells);
      }
    }
  }
}
