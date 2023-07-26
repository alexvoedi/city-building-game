package com.mygdx.game.tools.building;

import java.util.ArrayList;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.game.Cell;
import com.mygdx.game.Map;
import com.mygdx.game.structures.Building;
import com.mygdx.game.structures.BuildingFactory;

public class BuildingConstructionTool<S extends Building, T> extends BuildingTool {
  BuildingFactory<S, T> buildingFactory;
  T buildingType;

  public BuildingConstructionTool(
    Map map,
    AssetManager assetManager,
    BuildingFactory<S, T> buildingFactory,
    T buildingType
  ) {
    super(assetManager, map);

    this.buildingFactory = buildingFactory;
    this.buildingType = buildingType;
  }

  @Override
  public void build(ArrayList<Cell> cells) {
    S building = this.buildingFactory.build(buildingType, cells);

    TextureAtlas buildingAtlas = assetManager.get("buildings.atlas", TextureAtlas.class);
    Sprite sprite = buildingAtlas.createSprite("dummy-building-1x1");

    building.setSprite(sprite);
  }
}
