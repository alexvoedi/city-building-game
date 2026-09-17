package com.mygdx.game.tools.selection;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Cell;
import com.mygdx.game.Map;

import java.util.ArrayList;

public class PathSelectionTool extends SelectionTool {

  public PathSelectionTool(AssetManager assetManager, Map map) {
    super(assetManager, map);
    this.cells = new ArrayList<>();
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    if (button == Input.Buttons.LEFT) {
      // Left click: add cell to path
      addCellToPath(screenX, screenY);
      return true;
    } else if (button == Input.Buttons.RIGHT) {
      // Right click: remove last cell from path
      if (!cells.isEmpty()) {
        cells.remove(cells.size() - 1);
      }
      return true;
    }

    return false;
  }

  @Override
  public boolean keyDown(int keycode) {
    if (keycode == Input.Keys.ENTER) {
      // Confirm path
      build();
      return true;
    } else if (keycode == Input.Keys.BACKSPACE) {
      // Remove last cell
      if (!cells.isEmpty()) {
        cells.remove(cells.size() - 1);
      }
      return true;
    } else if (keycode == Input.Keys.ESCAPE) {
      // Cancel path
      cells = new ArrayList<>();
      return true;
    }

    return false;
  }

  private void addCellToPath(int screenX, int screenY) {
    Vector2 worldPosition = map.screenToWorldCoordinates(screenX, screenY);
    GridPoint2 gridPoint = map.getGridPoint(worldPosition.x, worldPosition.y);

    if (!map.inBounds(gridPoint.x, gridPoint.y)) {
      return;
    }

    Cell newCell = map.getCell(gridPoint.x, gridPoint.y);

    // If this is the first cell, just add it
    if (cells.isEmpty()) {
      cells.add(newCell);
      return;
    }

    // For subsequent cells, only allow adjacent cells
    Cell lastCell = cells.get(cells.size() - 1);
    GridPoint2 lastGridPoint = lastCell.getGridPosition();
    GridPoint2 newGridPoint = newCell.getGridPosition();

    int dx = Math.abs(newGridPoint.x - lastGridPoint.x);
    int dy = Math.abs(newGridPoint.y - lastGridPoint.y);

    // Only allow adjacent cells (Manhattan distance of 1)
    if ((dx == 1 && dy == 0) || (dx == 0 && dy == 1)) {
      // Avoid adding the same cell twice in a row
      if (!newCell.equals(lastCell)) {
        cells.add(newCell);
      }
    }
  }
}
