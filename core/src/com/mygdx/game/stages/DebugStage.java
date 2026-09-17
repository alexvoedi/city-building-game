package com.mygdx.game.stages;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.City;
import com.mygdx.game.ViewOverlayMode;

public class DebugStage extends Stage {
  BitmapFont font;
  City city;
  IntSupplier simulationSpeedSupplier;
  Supplier<ViewOverlayMode> overlayModeSupplier;

  Label fpsLabel;
  Label dateLabel;
  Label moneyLabel;
  Label populationLabel;
  Label budgetLabel;
  Label weatherLabel;
  Label speedLabel;
  Label overlayLabel;
  Label parkHintLabel;
  Label roadHintLabel;
  Label waterHintLabel;

  public DebugStage(SpriteBatch spriteBatch, Viewport viewport, City city, IntSupplier simulationSpeedSupplier,
      Supplier<ViewOverlayMode> overlayModeSupplier) {
    super(viewport, spriteBatch);

    this.city = city;
    this.simulationSpeedSupplier = simulationSpeedSupplier;
    this.overlayModeSupplier = overlayModeSupplier;

    FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("./Minimal5x7.ttf"));
    FreeTypeFontParameter fontGeneratorParameters = new FreeTypeFontParameter();

    fontGeneratorParameters.size = 24;

    BitmapFont font = fontGenerator.generateFont(fontGeneratorParameters);
    LabelStyle labelStyle = new LabelStyle(font, Color.PINK);

    Table debugTable = new Table();
    debugTable.setFillParent(true);
    debugTable.top().left();

    this.fpsLabel = new Label("FPS: 0", labelStyle);
    debugTable.add(this.fpsLabel);

    Table hudTable = new Table();
    hudTable.setFillParent(true);
    hudTable.top().right().pad(12);

    this.dateLabel = new Label("Date: 1.1.2000", labelStyle);
    this.moneyLabel = new Label("Money: $0", labelStyle);
    this.populationLabel = new Label("Population: 0", labelStyle);
    this.budgetLabel = new Label("Daily: $0", labelStyle);
    this.weatherLabel = new Label("Weather: Spring / Clear", labelStyle);
    this.speedLabel = new Label("Speed: 1x", labelStyle);
    this.overlayLabel = new Label("Overlay: NONE (O)", labelStyle);
    this.parkHintLabel = new Label("Park: Utilities -> Green Circle ($300)", labelStyle);
    this.roadHintLabel = new Label("Road: Click and drag to draw, release to place", labelStyle);

    hudTable.add(this.dateLabel).right().row();
    hudTable.add(this.moneyLabel).right().padTop(4).row();
    hudTable.add(this.populationLabel).right().padTop(4).row();
    hudTable.add(this.budgetLabel).right().padTop(4).row();
    hudTable.add(this.weatherLabel).right().padTop(4).row();
    hudTable.add(this.overlayLabel).right().padTop(4).row();
    hudTable.add(this.speedLabel).right().padTop(4).row();
    hudTable.add(this.parkHintLabel).right().padTop(4).row();
    hudTable.add(this.roadHintLabel).right().padTop(4);

    addActor(debugTable);
    addActor(hudTable);
  }

  public void resize(int width, int height) {
    getViewport().update(width, height, true);
  }

  public void update() {
    draw();

    fpsLabel.setText(
        String.format("FPS: %d", Gdx.graphics.getFramesPerSecond()));

    dateLabel.setText(String.format("Date: %s", city.getCityDate()));
    moneyLabel.setText(String.format("Money: $%,d", city.getMoney()));
    populationLabel.setText(String.format("Population: %,d", city.getPopulation()));

    int dailyBalance = city.getLastDailyBalance();
    String sign = dailyBalance >= 0 ? "+" : "";
    budgetLabel.setText(String.format("Daily: %s$%,d", sign, dailyBalance));
    weatherLabel.setText(String.format("Weather: %s / %s", city.getSeason(), city.getWeather()));
    overlayLabel.setText(String.format("Overlay: %s (O)", overlayModeSupplier.get().name()));

    int speed = simulationSpeedSupplier.getAsInt();
    if (speed == 0) {
      speedLabel.setText("Speed: Paused");
    } else {
      speedLabel.setText(String.format("Speed: %dx", speed));
    }
  }
}
