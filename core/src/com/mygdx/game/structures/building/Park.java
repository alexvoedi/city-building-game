package com.mygdx.game.structures.building;

import java.util.ArrayList;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.game.Cell;
import com.mygdx.game.structures.Building;

public class Park extends Building {
  public Park(AssetManager assetManager, ArrayList<Cell> cells) {
    super(cells);

    TextureAtlas sceneryAtlas = assetManager.get("scenery.atlas", TextureAtlas.class);
    Sprite sprite = sceneryAtlas.createSprite("tree");
    setSprite(sprite);
    getSprite().setColor(0.82f, 1f, 0.82f, 1f);
  }
}
