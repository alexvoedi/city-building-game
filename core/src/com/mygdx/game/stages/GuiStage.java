package com.mygdx.game.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.City;
import com.mygdx.game.Map;
import com.mygdx.game.ZoneColors;
import com.mygdx.game.structures.building.power_supply.PowerSupplyBuildingFactory;
import com.mygdx.game.structures.building.power_supply.PowerSupplyBuildingType;
import com.mygdx.game.tools.building.BulldozerTool;
import com.mygdx.game.tools.building.ParkBuildingTool;
import com.mygdx.game.tools.building.ResidentialZoningTool;
import com.mygdx.game.tools.building.RoadBuildingTool;
import com.mygdx.game.tools.building.WaterSupplyBuildingTool;
import com.mygdx.game.tools.selection.LocationSelectionTool;
import com.mygdx.game.tools.selection.DragLineSelectionTool;
import com.mygdx.game.tools.selection.RectangleSelectionTool;
import com.mygdx.game.zones.ZoneDensity;

public class GuiStage extends Stage {
  AssetManager assetManager;
  Map map;
  City city;
  Stage stage;

  Skin skin;
  Table rootTable;
  VerticalGroup mainMenu;
  VerticalGroup subMenu;
  VerticalGroup subSubMenu;

  public GuiStage(AssetManager assetManager, Map map, City city, SpriteBatch SpriteBatch, Viewport viewport) {
    super(viewport, SpriteBatch);

    this.assetManager = assetManager;
    this.map = map;
    this.city = city;
    this.stage = this;

    skin = new Skin(Gdx.files.internal("./ui-skin.json"));

    rootTable = new Table();
    rootTable.setFillParent(true);
    rootTable.align(Align.topLeft);

    addActor(rootTable);

    mainMenu = new VerticalGroup();
    mainMenu.align(Align.top);
    rootTable.add(mainMenu).fillY();

    subMenu = new VerticalGroup();
    subMenu.align(Align.top);
    rootTable.add(subMenu).fillY();

    subSubMenu = new VerticalGroup();
    subSubMenu.align(Align.top);
    rootTable.add(subSubMenu).fillY();

    initRoadMenu();
    initZoningMenu();
    initUtilityBuildingMenu();
    initBulldozerMenu();
  }

  private void initRoadMenu() {
    ImageButton roadMenuButton = createMenuButton("button-road");
    mainMenu.addActor(roadMenuButton);

    roadMenuButton.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        subMenu.clear();
        subSubMenu.clear();

        ImageButton roadBuildingToolButton = createMenuButton("button-road");
        subMenu.addActor(roadBuildingToolButton);
        roadBuildingToolButton.addListener(new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            map.setSelectionTool(new DragLineSelectionTool(assetManager, map));
            map.setBuildingTool(new RoadBuildingTool(assetManager, map));
          }
        });
      }
    });
  }

  private void initZoningMenu() {
    ImageButton zoningMenuButton = createMenuButton("button-zone");
    mainMenu.addActor(zoningMenuButton);

    zoningMenuButton.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        subMenu.clear();
        subSubMenu.clear();

        ImageButtonStyle imageButtonStyle = new ImageButtonStyle();
        imageButtonStyle.up = skin.getDrawable("button-circle");
        imageButtonStyle.imageUp = skin.getDrawable("button-circle-zone");

        ImageButton residentialZoneToolButton = createMenuButton("button-zone");
        subMenu.addActor(residentialZoneToolButton);
        residentialZoneToolButton.addListener(new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            subSubMenu.clear();

            ImageButton lowResidentialToolButton = new ImageButton(imageButtonStyle);
            lowResidentialToolButton.setColor(ZoneColors.RESIDENTIAL_LOW);
            subSubMenu.addActor(lowResidentialToolButton);

            lowResidentialToolButton.addListener(new ClickListener() {
              @Override
              public void clicked(InputEvent event, float x, float y) {
                map.setSelectionTool(new RectangleSelectionTool(assetManager, map));
                map.setBuildingTool(new ResidentialZoningTool(assetManager, map, ZoneDensity.LOW));
              }
            });

            ImageButton mediumResidentialToolButton = new ImageButton(imageButtonStyle);
            mediumResidentialToolButton.setColor(ZoneColors.RESIDENTIAL_MEDIUM);
            subSubMenu.addActor(mediumResidentialToolButton);

            mediumResidentialToolButton.addListener(new ClickListener() {
              @Override
              public void clicked(InputEvent event, float x, float y) {
                map.setSelectionTool(new RectangleSelectionTool(assetManager, map));
                map.setBuildingTool(new ResidentialZoningTool(assetManager, map, ZoneDensity.MEDIUM));
              }
            });

            ImageButton highResidentialToolButton = new ImageButton(imageButtonStyle);
            highResidentialToolButton.setColor(ZoneColors.RESIDENTIAL_HIGH);
            subSubMenu.addActor(highResidentialToolButton);

            highResidentialToolButton.addListener(new ClickListener() {
              @Override
              public void clicked(InputEvent event, float x, float y) {
                map.setSelectionTool(new RectangleSelectionTool(assetManager, map));
                map.setBuildingTool(new ResidentialZoningTool(assetManager, map, ZoneDensity.HIGH));
              }
            });
          }
        });

        ImageButton commercialZoneToolButton = createMenuButton("button-zone");
        subMenu.addActor(commercialZoneToolButton);
        commercialZoneToolButton.addListener(new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            subSubMenu.clear();

            ImageButton lowResidentialToolButton = new ImageButton(imageButtonStyle);
            lowResidentialToolButton.setColor(ZoneColors.COMMERCIAL_LOW);
            subSubMenu.addActor(lowResidentialToolButton);

            ImageButton mediumResidentialToolButton = new ImageButton(imageButtonStyle);
            mediumResidentialToolButton.setColor(ZoneColors.COMMERCIAL_MEDIUM);
            subSubMenu.addActor(mediumResidentialToolButton);

            ImageButton highResidentialToolButton = new ImageButton(imageButtonStyle);
            highResidentialToolButton.setColor(ZoneColors.COMMERCIAL_HIGH);
            subSubMenu.addActor(highResidentialToolButton);
          }
        });

        ImageButton industrialZoneToolButton = createMenuButton("button-zone");
        subMenu.addActor(industrialZoneToolButton);
        industrialZoneToolButton.addListener(new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            subSubMenu.clear();

            ImageButton lowResidentialToolButton = new ImageButton(imageButtonStyle);
            lowResidentialToolButton.setColor(ZoneColors.INDUSTRIAL_LOW);
            subSubMenu.addActor(lowResidentialToolButton);

            ImageButton mediumResidentialToolButton = new ImageButton(imageButtonStyle);
            mediumResidentialToolButton.setColor(ZoneColors.INDUSTRIAL_MEDIUM);
            subSubMenu.addActor(mediumResidentialToolButton);

            ImageButton highResidentialToolButton = new ImageButton(imageButtonStyle);
            highResidentialToolButton.setColor(ZoneColors.INDUSTRIAL_HIGH);
            subSubMenu.addActor(highResidentialToolButton);
          }
        });
      }
    });
  }

  private void initUtilityBuildingMenu() {
    ImageButton utilityBuildingMenuButton = createMenuButton("button", new Color(0.6f, 0.9f, 1f, 1f));
    mainMenu.addActor(utilityBuildingMenuButton);

    utilityBuildingMenuButton.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        subMenu.clear();
        subSubMenu.clear();

        ImageButton coalPlantButton = createMenuButton("button", new Color(0.35f, 0.35f, 0.35f, 1f));
        subMenu.addActor(coalPlantButton);
        coalPlantButton.addListener(new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            map.setSelectionTool(new LocationSelectionTool(assetManager, map, 2));
            map.setBuildingTool(new PowerSupplyBuildingFactory(map), PowerSupplyBuildingType.COAL_POWER_PLANT);
          }
        });

        ImageButton windPlantButton = createMenuButton("button", new Color(0.70f, 0.92f, 0.70f, 1f));
        subMenu.addActor(windPlantButton);
        windPlantButton.addListener(new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            map.setSelectionTool(new LocationSelectionTool(assetManager, map, 1));
            map.setBuildingTool(new PowerSupplyBuildingFactory(map), PowerSupplyBuildingType.WIND_MILL);
          }
        });

        ImageButton nuclearPlantButton = createMenuButton("button", new Color(0.8f, 1f, 0.2f, 1f));
        subMenu.addActor(nuclearPlantButton);
        nuclearPlantButton.addListener(new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            map.setSelectionTool(new LocationSelectionTool(assetManager, map, 3));
            map.setBuildingTool(new PowerSupplyBuildingFactory(map), PowerSupplyBuildingType.NUCLEAR_POWER_PLANT);
          }
        });

        ImageButton parkButton = createMenuButton("button-circle", new Color(0.40f, 1f, 0.50f, 1f));
        subMenu.addActor(parkButton);
        parkButton.addListener(new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            map.setSelectionTool(new LocationSelectionTool(assetManager, map, 1));
            map.setBuildingTool(new ParkBuildingTool(assetManager, map, city));
          }
        });

        ImageButton waterButton = createMenuButton("button", new Color(0.2f, 0.6f, 1f, 1f));
        subMenu.addActor(waterButton);
        waterButton.addListener(new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            map.setSelectionTool(new LocationSelectionTool(assetManager, map, 1));
            map.setBuildingTool(new WaterSupplyBuildingTool(assetManager, map, city));
          }
        });
      }
    });
  }

  private void initBulldozerMenu() {
    ImageButton bulldozerMenuButton = createMenuButton("button-road", new Color(0.95f, 0.45f, 0.2f, 1f));
    mainMenu.addActor(bulldozerMenuButton);

    bulldozerMenuButton.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        subMenu.clear();
        subSubMenu.clear();

        map.setSelectionTool(new RectangleSelectionTool(assetManager, map));
        map.setBuildingTool(new BulldozerTool(assetManager, map));
      }
    });
  }

  public void resize(int width, int height) {
    getViewport().update(width, height, true);
  }

  public void update(float delta) {
    act(delta);
    draw();
  }

  private ImageButton createMenuButton(String iconName) {
    return createMenuButton(iconName, Color.WHITE);
  }

  private ImageButton createMenuButton(String iconName, Color iconTint) {
    ImageButtonStyle style = new ImageButtonStyle(skin.get(ImageButtonStyle.class));
    style.imageUp = skin.getDrawable(iconName);

    ImageButton button = new ImageButton(style);
    button.getImage().setColor(iconTint);
    return button;
  }
}
