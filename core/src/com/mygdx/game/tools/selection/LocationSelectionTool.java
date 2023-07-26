package com.mygdx.game.tools.selection;

import java.util.ArrayList;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Cell;
import com.mygdx.game.Map;

public class LocationSelectionTool extends SelectionTool {
  int size;

  GridPoint2 position;

  public LocationSelectionTool(AssetManager assetManager, Map map, int size) {
    super(assetManager, map);

    this.size = size;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    Vector2 position = map.screenToWorldCoordinates(screenX, screenY);

    this.position = map.getGridPoint(position.x, position.y);

    updateCells();

    return true;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    if (button == Input.Buttons.LEFT) {
      updateCells();

      if (cells.size() > 0) {
        build();
      }


      return true;
    }

    return false;
  }

  private void updateCells() {
    this.cells = new ArrayList<Cell>();

    for (int x = position.x; x < position.x + size; x++) {
      for (int y = position.y; y > position.y - size; y--) {
        Cell cell = map.getCell(x, y);

        if (cell != null) {
          this.cells.add(cell);
        }
      }
    }
  }
}
