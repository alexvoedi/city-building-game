package com.mygdx.game.tools.selection;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Cell;
import com.mygdx.game.Map;

import java.util.ArrayList;

public class RectangleSelectionTool extends SelectionTool {
  Vector2 startPosition;
  Vector2 endPosition;

  Cell startCell;
  Cell endCell;

  public RectangleSelectionTool(AssetManager assetManager, Map map) {
    super(assetManager, map);
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    if (button == Input.Buttons.LEFT) {
      updateStart(screenX, screenY);
    }

    return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    if (button == Input.Buttons.LEFT) {
      updateEnd(screenX, screenY);
      build();

      cells = new ArrayList<>();
    }

    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    updateEnd(screenX, screenY);

    updateCells();

    return false;
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
    if (startCell == null || endCell == null) return;

    GridPoint2 startCellGridPosition = startCell.getGridPosition();
    GridPoint2 endCellGridPosition = endCell.getGridPosition();

    int sx = (endCellGridPosition.x - startCellGridPosition.x) > 0 ? +1 : -1;
    int sy = (endCellGridPosition.y - startCellGridPosition.y) > 0 ? +1 : -1;

    cells = new ArrayList<>();

    for (int x = startCellGridPosition.x; x != endCellGridPosition.x + sx; x += sx) {
      for (int y = startCellGridPosition.y; y != endCellGridPosition.y + sy; y += sy) {
        Cell cell = map.getCell(x, y);

        cells.add(cell);
      }
    }
  }
}
