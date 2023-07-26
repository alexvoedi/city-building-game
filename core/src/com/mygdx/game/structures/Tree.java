package com.mygdx.game.structures;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Cell;

import java.util.ArrayList;

public class Tree extends Structure {
  public Tree(AssetManager assetManager, ArrayList<Cell> cells) {
    super(cells);

    TextureAtlas sceneryAtlas = assetManager.get("scenery.atlas", TextureAtlas.class);
    this.sprite = sceneryAtlas.createSprite("tree");

    Vector2 position = getSpriteCell().getPosition();
    this.sprite.setPosition(position.x, position.y);
  }
}
