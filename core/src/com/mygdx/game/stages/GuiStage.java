package com.mygdx.game.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Map;
import com.mygdx.game.ZoneColors;
import com.mygdx.game.tools.building.ResidentialZoningTool;
import com.mygdx.game.tools.building.RoadBuildingTool;
import com.mygdx.game.tools.selection.LineSelectionTool;
import com.mygdx.game.tools.selection.RectangleSelectionTool;
import com.mygdx.game.zones.ZoneDensity;

public class GuiStage extends Stage {
  AssetManager assetManager;
  Map map;
  Stage stage;

  Skin skin;
  Table rootTable;
  VerticalGroup mainMenu;
  VerticalGroup subMenu;
  VerticalGroup subSubMenu;

  Window window;

  public GuiStage(AssetManager assetManager, Map map, SpriteBatch SpriteBatch, Viewport viewport) {
    super(viewport, SpriteBatch);

    this.assetManager = assetManager;
    this.map = map;
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
  }

  private void initRoadMenu() {
    ImageButton roadMenuButton = new ImageButton(skin);
    mainMenu.addActor(roadMenuButton);

    roadMenuButton.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        subMenu.clear();
        subSubMenu.clear();

        ImageButton roadBuildingToolButton = new ImageButton(skin);
        subMenu.addActor(roadBuildingToolButton);
        roadBuildingToolButton.addListener(new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            map.setSelectionTool(new LineSelectionTool(assetManager, map, 4));
            map.setBuildingTool(new RoadBuildingTool(assetManager, map));
          }
        });
      }
    });
  }

  private void initZoningMenu() {
    ImageButton zoningMenuButton = new ImageButton(skin);
    mainMenu.addActor(zoningMenuButton);

    zoningMenuButton.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        subMenu.clear();
        subSubMenu.clear();

        ImageButtonStyle imageButtonStyle = new ImageButtonStyle();
        imageButtonStyle.up = skin.getDrawable("button-circle");
        imageButtonStyle.imageUp = skin.getDrawable("button-circle-zone");

        ImageButton residentialZoneToolButton = new ImageButton(skin);
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

        ImageButton commercialZoneToolButton = new ImageButton(skin);
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

        ImageButton industrialZoneToolButton = new ImageButton(skin);
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
    ImageButton utilityBuildingMenuButton = new ImageButton(skin);
    mainMenu.addActor(utilityBuildingMenuButton);

    utilityBuildingMenuButton.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        subMenu.clear();
        subSubMenu.clear();

        ImageButton powerSupplyBuildingButton = new ImageButton(skin);
        subMenu.addActor(powerSupplyBuildingButton);
        powerSupplyBuildingButton.addListener(new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            window = new Window("Test", skin);
            window.pad(50, 20, 20, 20);
            window.setWidth(400);
            window.setHeight(400);

            Table windowTable = new Table();
            window.addActor(windowTable);

            ImageButton roadBuildingToolButton = new ImageButton(skin);
            windowTable.addActor(roadBuildingToolButton);

            stage.addActor(window);
          }
        });
      }
    });
  }

  public void resize(int width, int height) {
    getViewport().update(width, height, true);
  }

  public void update() {
    draw();
  }
}
