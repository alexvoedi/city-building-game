package com.mygdx.game.structures.building.power_supply;

import java.util.ArrayList;

import com.mygdx.game.Cell;
import com.mygdx.game.structures.building.PowerSupplyBuilding;

public class WindMill extends PowerSupplyBuilding {
  public static final int cost = 1200;

  public WindMill(ArrayList<Cell> cells) {
    super(cells);
  }
}
