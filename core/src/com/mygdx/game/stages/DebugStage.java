package com.mygdx.game.stages;

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

public class DebugStage extends Stage {
  BitmapFont font;

  Label fpsLabel;

  public DebugStage(SpriteBatch spriteBatch, Viewport viewport) {
    super(viewport, spriteBatch);

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

    addActor(debugTable);
  }

  public void resize(int width, int height) {
    getViewport().update(width, height, true);
  }

  public void update() {
    draw();

    fpsLabel.setText(
      String.format("FPS: %d", Gdx.graphics.getFramesPerSecond())
    );
  }
}
