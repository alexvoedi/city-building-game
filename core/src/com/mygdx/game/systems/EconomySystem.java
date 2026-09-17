package com.mygdx.game.systems;

import java.util.HashSet;
import java.util.Set;

import com.artemis.BaseSystem;
import com.mygdx.game.Cell;
import com.mygdx.game.City;
import com.mygdx.game.Map;
import com.mygdx.game.structures.Structure;
import com.mygdx.game.structures.building.PowerSupplyBuilding;
import com.mygdx.game.structures.building.power_supply.CoalPowerPlant;
import com.mygdx.game.structures.building.power_supply.NuclearPowerPlant;
import com.mygdx.game.structures.building.power_supply.WindMill;
import com.mygdx.game.structures.building.residential.ResidentialBuilding;
import com.mygdx.game.structures.network.Road;
import com.mygdx.game.zones.ZoneDensity;

public class EconomySystem extends BaseSystem {
  private static final float DAY_TICK_SECONDS = 1f;
  private static final int TAX_PER_CITIZEN = 2;
  private static final int PARK_INFLUENCE_RADIUS = 6;

  Map map;
  City city;

  float acc;

  public EconomySystem(Map map, City city) {
    this.map = map;
    this.city = city;
  }

  @Override
  protected void processSystem() {
    acc += world.getDelta();

    if (acc >= DAY_TICK_SECONDS) {
      acc -= DAY_TICK_SECONDS;
      applyDailyEconomy();
    }
  }

  private void applyDailyEconomy() {
    Set<Structure> uniqueStructures = new HashSet<>();
    int roadTileCount = 0;

    for (Cell cell : map.getCells()) {
      Structure structure = cell.getStructure();
      if (structure == null) {
        continue;
      }

      uniqueStructures.add(structure);
      if (structure instanceof Road) {
        roadTileCount++;
      }
    }

    int population = 0;
    int maintenance = roadTileCount;

    for (Structure structure : uniqueStructures) {
      if (structure instanceof ResidentialBuilding) {
        population += getAdjustedPopulation((ResidentialBuilding) structure);
        maintenance += 2;
      }

      if (structure instanceof PowerSupplyBuilding) {
        maintenance += getPlantMaintenance((PowerSupplyBuilding) structure);
      }
    }

    int income = Math.round(population * TAX_PER_CITIZEN * city.getIncomeModifier());
    int net = income - maintenance;

    city.setPopulation(population);
    city.setLastDailyBalance(net);
    city.addMoney(net);
  }

  private int getPopulation(ResidentialBuilding residentialBuilding) {
    ZoneDensity density = residentialBuilding.getDensity();

    switch (density) {
      case LOW:
        return 8;
      case MEDIUM:
        return 20;
      case HIGH:
        return 45;
      default:
        return 8;
    }
  }

  private int getAdjustedPopulation(ResidentialBuilding residentialBuilding) {
    int basePopulation = getPopulation(residentialBuilding);
    Cell anchorCell = residentialBuilding.getCells().get(0);
    int nearbyParks = map.getNearbyParkCount(anchorCell, PARK_INFLUENCE_RADIUS);
    float desirabilityBonus = Math.min(0.25f, nearbyParks * 0.05f);

    return Math.round(basePopulation * (1f + desirabilityBonus));
  }

  private int getPlantMaintenance(PowerSupplyBuilding powerBuilding) {
    if (powerBuilding instanceof WindMill) {
      return 8;
    }

    if (powerBuilding instanceof CoalPowerPlant) {
      return 20;
    }

    if (powerBuilding instanceof NuclearPowerPlant) {
      return 60;
    }

    return 15;
  }
}
