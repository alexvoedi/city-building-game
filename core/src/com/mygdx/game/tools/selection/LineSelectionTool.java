package com.mygdx.game.tools.selection;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Cell;
import com.mygdx.game.Map;

import java.util.ArrayList;

public class LineSelectionTool extends SelectionTool {
  int directions;

  Vector2 startPosition;
  Vector2 endPosition;

  Cell startCell;
  Cell endCell;

  public LineSelectionTool(AssetManager assetManager, Map map, int directions) {
    super(assetManager, map);

    this.directions = 8;
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
    if (
      startPosition == null ||
      endPosition == null ||
      startCell == null ||
      endCell == null
    ) return;

    GridPoint2 startCellGridPosition = startCell.getGridPosition();
    GridPoint2 endCellGridPosition = endCell.getGridPosition();

    int dx = endCellGridPosition.x - startCellGridPosition.x;
    int dy = endCellGridPosition.y - startCellGridPosition.y;

    double angle = Math.atan2(dy, dx);
    double arcLength = 2 * Math.PI / directions;

    if (angle % arcLength != 0)
      return;

    int nx = Math.abs(dx);
    int ny = Math.abs(dy);

    int sx = dx > 0 ? +1 : -1;
    int sy = dy > 0 ? +1 : -1;

    Vector2 p = new Vector2(startCellGridPosition.x, startCellGridPosition.y);

    cells = new ArrayList<>();

    cells.add(map.getCell((int) p.x, (int) p.y));

    for (int ix = 0, iy = 0; ix < nx || iy < ny;) {
      int ox = (1 + 2 * ix) * ny;
      int oy = (1 + 2 * iy) * nx;

      if (ox < oy) {
        p.x += sx;
        ix++;
      } else if (ox > oy) {
        p.y += sy;
        iy++;
      } else {
        if (
          startPosition.x < endPosition.x &&
          startPosition.y > endPosition.y ||
          startPosition.x > endPosition.x &&
          startPosition.y < endPosition.y
        ) {
          p.x += sx;
          ix++;
        } else {
          p.y += sy;
          iy++;
        }
      }

      Cell cell = map.getCell((int) p.x, (int) p.y);
      cells.add(cell);
    }
  }
}
