package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.screens.GameScreen;

public class CityBuildingGame extends Game {
  AssetManager assetManager;
  SpriteBatch spriteBatch;

  @Override
  public void create () {
    assetManager = new AssetManager();
    spriteBatch = new SpriteBatch();

    assetManager.load("ui.atlas", TextureAtlas.class);
    assetManager.load("terrain.atlas", TextureAtlas.class);
    assetManager.load("common.atlas", TextureAtlas.class);
    assetManager.load("scenery.atlas", TextureAtlas.class);
    assetManager.load("scenery.atlas", TextureAtlas.class);
    assetManager.load("roads.atlas", TextureAtlas.class);
    assetManager.load("buildings.atlas", TextureAtlas.class);

    assetManager.finishLoading();

    GameScreen gameScreen = new GameScreen(assetManager, spriteBatch);
    setScreen(gameScreen);
  }

  @Override
  public void render () {
    ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);

    super.render();
  }

  @Override
  public void dispose () {}
}
