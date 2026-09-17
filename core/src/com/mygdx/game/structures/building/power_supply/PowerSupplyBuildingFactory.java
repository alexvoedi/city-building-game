package com.mygdx.game.structures.building.power_supply;

import java.util.ArrayList;

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
      ArrayList<Cell> cells) {
    return this.getBuilding(buildingType, cells);
  }

  private PowerSupplyBuilding getBuilding(
      PowerSupplyBuildingType buildingType,
      ArrayList<Cell> cells) {
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
        return new NuclearPowerPlant(cells);
      }

      case OIL_POWER_PLANT: {
        return new CoalPowerPlant(cells);
      }

      case SOLAR_POWER_COLLECTOR: {
        return new CoalPowerPlant(cells);
      }

      case WIND_MILL: {
        return new WindMill(cells);
      }

      default: {
        return new CoalPowerPlant(cells);
      }
    }
  }
}
