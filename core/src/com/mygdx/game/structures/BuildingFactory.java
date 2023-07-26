package com.mygdx.game.structures;

import java.util.ArrayList;

import com.mygdx.game.Cell;
import com.mygdx.game.Map;

public abstract class BuildingFactory<StructureType extends Building, BuildingType> {
  protected Map map;

  public BuildingFactory(Map map) {
    this.map = map;
  }

  public abstract StructureType build(
    BuildingType buildingType,
    ArrayList<Cell> cells
  );
}
