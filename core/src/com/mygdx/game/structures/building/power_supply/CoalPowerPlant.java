package com.mygdx.game.structures.building.power_supply;

import java.util.ArrayList;

import com.mygdx.game.Cell;
import com.mygdx.game.structures.building.PowerSupplyBuilding;

public class CoalPowerPlant extends PowerSupplyBuilding {
  public static final int cost = 5000;

  public CoalPowerPlant(ArrayList<Cell> cells) {
    super(cells);
  }
}
