package com.mygdx.game.screens;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.City;
import com.mygdx.game.Map;
import com.mygdx.game.ViewOverlayMode;
import com.mygdx.game.stages.DebugStage;
import com.mygdx.game.stages.GuiStage;
import com.mygdx.game.systems.CameraSystem;
import com.mygdx.game.systems.CitySystem;
import com.mygdx.game.systems.EconomySystem;
import com.mygdx.game.systems.PowerSupplySystem;
import com.mygdx.game.systems.RenderingSystem;
import com.mygdx.game.systems.TrafficSystem;
import com.mygdx.game.systems.WaterSupplySystem;
import com.mygdx.game.systems.WeatherSystem;
import com.mygdx.game.systems.ZoningGrowthSystem;

public class GameScreen implements Screen {
  AssetManager assetManager;
  SpriteBatch spriteBatch;

  int simulationSpeed;
  int previousSimulationSpeed;
  ViewOverlayMode overlayMode;

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
    this.previousSimulationSpeed = 1;
    this.overlayMode = ViewOverlayMode.NONE;

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
    this.guiStage = new GuiStage(assetManager, map, city, spriteBatch, guiViewport);
    this.inputMultiplexer.addProcessor(0, this.guiStage);
  }

  private void initDebugStage() {
    OrthographicCamera debugCamera = new OrthographicCamera();
    ScreenViewport debugViewport = new ScreenViewport(debugCamera);
    this.debugStage = new DebugStage(spriteBatch, debugViewport, city, this::getSimulationSpeed, this::getOverlayMode);
    this.inputMultiplexer.addProcessor(this.debugStage);
  }

  private void initWorld() {
    CameraSystem cameraSystem = new CameraSystem(camera);
    inputMultiplexer.addProcessor(cameraSystem);

    RenderingSystem renderingSystem = new RenderingSystem(assetManager, spriteBatch, viewport, map,
        this::getOverlayMode);
    TrafficSystem trafficSystem = new TrafficSystem(assetManager, spriteBatch, map);
    PowerSupplySystem powerSupplySystem = new PowerSupplySystem(map);
    WaterSupplySystem waterSupplySystem = new WaterSupplySystem(map);

    CitySystem citySystem = new CitySystem(city);
    WeatherSystem weatherSystem = new WeatherSystem(city);
    EconomySystem economySystem = new EconomySystem(map, city);
    ZoningGrowthSystem zoningGrowthSystem = new ZoningGrowthSystem(assetManager, map, city);

    WorldConfiguration worldConfig = new WorldConfigurationBuilder()
        .with(cameraSystem)
        .with(renderingSystem)
        .with(trafficSystem)
        .with(powerSupplySystem)
        .with(waterSupplySystem)
        .with(citySystem)
        .with(weatherSystem)
        .with(economySystem)
        .with(zoningGrowthSystem)
        .build();

    world = new World(worldConfig);
  }

  public Vector2 screenToWorldCoordinates(int screenX, int screenY) {
    return viewport.unproject(new Vector2(screenX, screenY));
  }

  @Override
  public void show() {
  }

  @Override
  public void render(float delta) {
    handleSimulationControls();

    camera.update();

    spriteBatch.setProjectionMatrix(camera.combined);
    spriteBatch.begin();
    world.setDelta(simulationSpeed * delta);
    world.process();
    spriteBatch.end();

    this.guiStage.update(delta);
    this.debugStage.update();
  }

  private void handleSimulationControls() {
    if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
      simulationSpeed = 1;
      previousSimulationSpeed = 1;
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
      simulationSpeed = 2;
      previousSimulationSpeed = 2;
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
      simulationSpeed = 4;
      previousSimulationSpeed = 4;
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
      if (simulationSpeed == 0) {
        simulationSpeed = previousSimulationSpeed;
      } else {
        previousSimulationSpeed = simulationSpeed;
        simulationSpeed = 0;
      }
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
      switch (overlayMode) {
        case NONE:
          overlayMode = ViewOverlayMode.POWER;
          break;
        case POWER:
          overlayMode = ViewOverlayMode.ZONING;
          break;
        case ZONING:
          overlayMode = ViewOverlayMode.TRAFFIC;
          break;
        default:
          overlayMode = ViewOverlayMode.NONE;
          break;
      }
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
      world.getSystem(RenderingSystem.class).toggleGrid();
    }
  }

  @Override
  public void resize(int width, int height) {
    viewport.update(width, height, true);

    guiStage.resize(width, height);
    debugStage.resize(width, height);
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void hide() {
  }

  @Override
  public void dispose() {
  }

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

  public int getSimulationSpeed() {
    return simulationSpeed;
  }

  public ViewOverlayMode getOverlayMode() {
    return overlayMode;
  }
}
