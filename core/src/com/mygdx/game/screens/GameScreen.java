package com.mygdx.game.screens;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.City;
import com.mygdx.game.Map;
import com.mygdx.game.stages.DebugStage;
import com.mygdx.game.stages.GuiStage;
import com.mygdx.game.systems.CameraSystem;
import com.mygdx.game.systems.CitySystem;
import com.mygdx.game.systems.RenderingSystem;

public class GameScreen implements Screen {
  AssetManager assetManager;
  SpriteBatch spriteBatch;

  int simulationSpeed;

  InputMultiplexer inputMultiplexer;
  OrthographicCamera camera;
  ScreenViewport viewport;
  Map map;
  World world;
  City city;

  GuiStage guiStage;
  DebugStage debugStage;

  public GameScreen(AssetManager assetManager, SpriteBatch spriteBatch) {
    this.spriteBatch = spriteBatch;
    this.assetManager = assetManager;

    this.simulationSpeed = 1;

    this.camera = new OrthographicCamera();

    this.viewport = new ScreenViewport(this.camera);
    this.viewport.setUnitsPerPixel(0.25f);

    this.inputMultiplexer = new InputMultiplexer();
    Gdx.input.setInputProcessor(inputMultiplexer);

    this.map = new Map(assetManager, viewport, inputMultiplexer);
    this.city = new City(map);

    this.initGuiStage();
    this.initDebugStage();
    this.initWorld();
  }

  private void initGuiStage() {
    OrthographicCamera guiCamera = new OrthographicCamera();
    ScreenViewport guiViewport = new ScreenViewport(guiCamera);
    this.guiStage = new GuiStage(assetManager, map, spriteBatch, guiViewport);
    this.inputMultiplexer.addProcessor(this.guiStage);
  }

  private void initDebugStage() {
    OrthographicCamera debugCamera = new OrthographicCamera();
    ScreenViewport debugViewport = new ScreenViewport(debugCamera);
    this.debugStage = new DebugStage(spriteBatch, debugViewport);
    this.inputMultiplexer.addProcessor(this.debugStage);
  }

  private void initWorld() {
    CameraSystem cameraSystem = new CameraSystem(camera);
    inputMultiplexer.addProcessor(cameraSystem);

    RenderingSystem renderingSystem = new RenderingSystem(assetManager, spriteBatch, viewport, map);

    CitySystem citySystem = new CitySystem(city);

    WorldConfiguration worldConfig = new WorldConfigurationBuilder()
      .with(cameraSystem)
      .with(renderingSystem)
      .with(citySystem)
      .build();

    world = new World(worldConfig);
  }

  public Vector2 screenToWorldCoordinates(int screenX, int screenY) {
    return viewport.unproject(new Vector2(screenX, screenY));
  }

  @Override
  public void show() {}

  @Override
  public void render(float delta) {
    camera.update();

    spriteBatch.setProjectionMatrix(camera.combined);
    spriteBatch.begin();
    world.setDelta(simulationSpeed * delta);
    world.process();
    spriteBatch.end();

    this.guiStage.update();
    this.debugStage.update();
  }

  @Override
  public void resize(int width, int height) {
    viewport.update(width, height, true);

    guiStage.resize(width, height);
    debugStage.resize(width, height);
  }

  @Override
  public void pause() {}

  @Override
  public void resume() {}

  @Override
  public void hide() {}

  @Override
  public void dispose() {}

  public AssetManager getAssetManager() {
    return assetManager;
  }

  public OrthographicCamera getCamera() {
    return camera;
  }

  public ScreenViewport getViewport() {
    return viewport;
  }

  public Map getMap() {
    return map;
  }

  public void setMap(Map map) {
    this.map = map;
  }

  public World getWorld() {
    return world;
  }

  public void setWorld(World world) {
    this.world = world;
  }

  public SpriteBatch getSpriteBatch() {
    return spriteBatch;
  }

  public InputMultiplexer getInputMultiplexer() {
    return inputMultiplexer;
  }
}
