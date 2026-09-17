package com.mygdx.game.tools.building;

import java.util.ArrayList;

import com.badlogic.gdx.assets.AssetManager;
import com.mygdx.game.Cell;
import com.mygdx.game.Map;
import com.mygdx.game.structures.Structure;
import com.mygdx.game.structures.Tree;
import com.mygdx.game.zones.ResidentialZone;
import com.mygdx.game.zones.Zone;
import com.mygdx.game.zones.ZoneDensity;

public class ResidentialZoningTool extends BuildingTool {
  ZoneDensity zoneDensity;

  public ResidentialZoningTool(AssetManager assetManager, Map map, ZoneDensity zoneDensity) {
    super(assetManager, map);

    this.zoneDensity = zoneDensity;
  }

  @Override
  public void build(ArrayList<Cell> cells) {
    for (Cell cell : cells) {
      if (cell == null) {
        continue;
      }

      Structure structure = cell.getStructure();
      if (structure != null && !(structure instanceof Tree)) {
        continue;
      }

      if (structure instanceof Tree) {
        cell.setStructure(null);
      }

      Zone zone = new ResidentialZone(assetManager, cell, zoneDensity);
      cell.setZone(zone);
    }
  }
}
