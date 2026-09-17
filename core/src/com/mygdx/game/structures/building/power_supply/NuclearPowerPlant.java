package com.mygdx.game.structures.building.power_supply;

import java.util.ArrayList;

import com.mygdx.game.Cell;
import com.mygdx.game.structures.building.PowerSupplyBuilding;

public class NuclearPowerPlant extends PowerSupplyBuilding {
  public static final int cost = 15000;

  public NuclearPowerPlant(ArrayList<Cell> cells) {
    super(cells);
  }
}
