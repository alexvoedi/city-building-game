package com.mygdx.game.structures;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Cell;
import com.mygdx.game.Renderable;

import java.util.ArrayList;

import static com.mygdx.game.Cell.CELL_SIZE_HALF;
import static com.mygdx.game.Map.GAME_WORLD_SIZE;

public abstract class Structure implements Renderable {
  ArrayList<Cell> cells;
  int zIndex;

  Sprite sprite;

  public Structure(ArrayList<Cell> cells) {
    this.cells = cells;

    this.cells.forEach((cell) -> cell.setStructure(this));

    this.updateZIndex();
  }

  private void updateZIndex() {
    this.zIndex = 0;

    for (Cell cell : this.cells) {
      GridPoint2 gridPosition = cell.getGridPosition();

      this.zIndex += gridPosition.x + (GAME_WORLD_SIZE - gridPosition.y);
    }

    this.zIndex /= cells.size();
  }

  public Cell getSpriteCell() {
    return cells.stream().reduce(
      cells.get(0),
      (Cell spriteCell, Cell currentCell) -> {
        if (
          currentCell.getGridPosition().x - currentCell.getGridPosition().y >
          spriteCell.getGridPosition().x - spriteCell.getGridPosition().y
        ) {
          return currentCell;
        } else {
          return spriteCell;
        }
      }
    );
  }

  public void render(SpriteBatch spriteBatch) {
    sprite.draw(spriteBatch);
  }

  public ArrayList<Cell> getCells() {
    return cells;
  }

  public void setCells(ArrayList<Cell> cells) {
    this.cells = cells;
  }

  public Sprite getSprite() {
    return sprite;
  }

  public void setSprite(Sprite sprite) {
    this.sprite = sprite;

    Vector2 position = getSpriteCell().getPosition();
    this.sprite.setPosition(position.x, position.y);
    this.sprite.translateX((1 - (float) Math.round(this.sprite.getWidth() / 32)) * CELL_SIZE_HALF.x);
  }

  public int getZIndex() {
    return zIndex;
  }

  public void setZIndex(int zIndex) {
    this.zIndex = zIndex;
  }
}
