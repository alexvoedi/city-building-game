package com.mygdx.game.structures.building.water_supply;

import java.util.ArrayList;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.game.Cell;
import com.mygdx.game.structures.building.WaterSupplyBuilding;

public class WaterTreatmentPlant extends WaterSupplyBuilding {
  public static final int COST = 800;

  public WaterTreatmentPlant(ArrayList<Cell> cells) {
    super(cells);
  }

  public WaterTreatmentPlant(AssetManager assetManager, ArrayList<Cell> occupiedCells) {
    this(occupiedCells);

    TextureAtlas commonAtlas = assetManager.get("common.atlas", TextureAtlas.class);
    setSprite(commonAtlas.createSprite("cell"));

    for (Cell cell : occupiedCells) {
      getSprite().setPosition(cell.getPosition().x, cell.getPosition().y);
    }
  }
}
