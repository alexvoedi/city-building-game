package com.mygdx.game.tools.building;

import com.badlogic.gdx.assets.AssetManager;
import com.mygdx.game.Cell;
import com.mygdx.game.Map;

import java.util.ArrayList;

public abstract class BuildingTool {
  AssetManager assetManager;
  Map map;

  public BuildingTool(AssetManager assetManager, Map map) {
    this.assetManager = assetManager;
    this.map = map;
  }

  abstract public void build(ArrayList<Cell> cells);
}
