package com.mygdx.game.tools.selection;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Cell;
import com.mygdx.game.Map;

import java.util.ArrayList;

public class DragLineSelectionTool extends SelectionTool {
  Vector2 startPosition;
  Vector2 endPosition;

  Cell startCell;
  Cell endCell;

  ArrayList<Integer> pressedButtons;

  public DragLineSelectionTool(AssetManager assetManager, Map map) {
    super(assetManager, map);
    this.pressedButtons = new ArrayList<>();
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    pressedButtons.add(button);

    if (button == Input.Buttons.LEFT) {
      updateStart(screenX, screenY);
      return true;
    }

    return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    pressedButtons.remove(Integer.valueOf(button));

    if (button == Input.Buttons.LEFT && pressedButtons.contains(Input.Buttons.LEFT) == false) {
      updateEnd(screenX, screenY);
      updateCells();
      build();
      return true;
    }

    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    // Always update the end position so preview renders at correct location
    updateEnd(screenX, screenY);

    if (pressedButtons.contains(Input.Buttons.LEFT)) {
      // Recalculate the path while dragging left mouse
      updateCells();
    }

    return false; // Return false to allow other processors (camera) to handle input
  }

  private void updateStart(int screenX, int screenY) {
    startPosition = map.screenToWorldCoordinates(screenX, screenY);

    GridPoint2 startGridPoint = map.getGridPoint(startPosition.x, startPosition.y);

    if (map.inBounds(startGridPoint.x, startGridPoint.y)) {
      startCell = map.getCell(startGridPoint.x, startGridPoint.y);
    }
  }

  private void updateEnd(int screenX, int screenY) {
    endPosition = map.screenToWorldCoordinates(screenX, screenY);

    GridPoint2 endGridPoint = map.getGridPoint(endPosition.x, endPosition.y);

    if (map.inBounds(endGridPoint.x, endGridPoint.y)) {
      endCell = map.getCell(endGridPoint.x, endGridPoint.y);
    }
  }

  private void updateCells() {
    if (startPosition == null ||
        endPosition == null ||
        startCell == null ||
        endCell == null)
      return;

    GridPoint2 startCellGridPosition = startCell.getGridPosition();
    GridPoint2 endCellGridPosition = endCell.getGridPosition();

    cells = bresenhamLine(startCellGridPosition.x, startCellGridPosition.y,
        endCellGridPosition.x, endCellGridPosition.y);
  }

  private ArrayList<Cell> bresenhamLine(int x0, int y0, int x1, int y1) {
    ArrayList<Cell> line = new ArrayList<>();

    int dx = x1 - x0;
    int dy = y1 - y0;

    int sx = dx > 0 ? 1 : (dx < 0 ? -1 : 0);
    int sy = dy > 0 ? 1 : (dy < 0 ? -1 : 0);

    int x = x0;
    int y = y0;

    int adx = Math.abs(dx);
    int ady = Math.abs(dy);

    // Add starting cell
    if (map.inBounds(x, y)) {
      line.add(map.getCell(x, y));
    }

    // Optimize: move in the direction with the larger distance first
    if (adx >= ady) {
      // Move horizontally first, then vertically
      while (x != x1) {
        x += sx;
        if (map.inBounds(x, y)) {
          line.add(map.getCell(x, y));
        }
      }
      while (y != y1) {
        y += sy;
        if (map.inBounds(x, y)) {
          line.add(map.getCell(x, y));
        }
      }
    } else {
      // Move vertically first, then horizontally
      while (y != y1) {
        y += sy;
        if (map.inBounds(x, y)) {
          line.add(map.getCell(x, y));
        }
      }
      while (x != x1) {
        x += sx;
        if (map.inBounds(x, y)) {
          line.add(map.getCell(x, y));
        }
      }
    }

    return line;
  }
}
