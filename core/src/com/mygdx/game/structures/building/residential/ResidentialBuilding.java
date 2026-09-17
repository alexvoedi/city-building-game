package com.mygdx.game.structures.building.residential;

import java.util.ArrayList;

import com.mygdx.game.Cell;
import com.mygdx.game.structures.Building;
import com.mygdx.game.zones.ZoneDensity;

public class ResidentialBuilding extends Building {
  ZoneDensity density;

  public ResidentialBuilding(ArrayList<Cell> cells, ZoneDensity density) {
    super(cells);
    this.density = density;
  }

  public ZoneDensity getDensity() {
    return density;
  }
}
