package com.mygdx.game.tools.building;

import java.util.ArrayList;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.mygdx.game.Cell;
import com.mygdx.game.Map;
import com.mygdx.game.structures.Structure;
import com.mygdx.game.structures.network.Road;

public class RoadBuildingTool extends BuildingTool {
  ArrayList<Cell> adjacentCells;

  public RoadBuildingTool(AssetManager assetManager, Map map) {
    super(assetManager, map);
  }

  @Override
  public void build(ArrayList<Cell> cells) {
    TextureAtlas roadAtlas = assetManager.get("roads.atlas", TextureAtlas.class);

    for (Cell cell : cells) {
      Road road = new Road(cell);

      road.setSprite(roadAtlas.createSprite(this.getSpriteName(cell)));
      road.getSprite().setPosition(cell.getPosition().x, cell.getPosition().y);

      updateAdjacentCells(cell);
    }
  }

  private String getSpriteName(Cell cell) {
    String spriteName = "road";

    GridPoint2 cellGridPosition = cell.getGridPosition();
    int x = cellGridPosition.x;
    int y = cellGridPosition.y;

    // Check 4 orthogonal directions only (edge-to-edge connections)
    if (hasAdjacentRoad(x, y + 1))
      spriteName += "-n";
    if (hasAdjacentRoad(x + 1, y))
      spriteName += "-e";
    if (hasAdjacentRoad(x, y - 1))
      spriteName += "-s";
    if (hasAdjacentRoad(x - 1, y))
      spriteName += "-w";

    return spriteName;
  }

  private boolean hasAdjacentRoad(int x, int y) {
    if (!map.inBounds(x, y))
      return false;
    Cell cell = map.getCell(x, y);
    return cell.getStructure() instanceof Road;
  }

  private void updateAdjacentCells(Cell cell) {
    TextureAtlas roadAtlas = assetManager.get("roads.atlas", TextureAtlas.class);

    GridPoint2 cellGridPosition = cell.getGridPosition();
    int x = cellGridPosition.x;
    int y = cellGridPosition.y;

    // Check 4 orthogonal directions only
    int[][] directions = {
        { x, y + 1 }, // N
        { x + 1, y }, // E
        { x, y - 1 }, // S
        { x - 1, y } // W
    };

    for (int[] dir : directions) {
      if (map.inBounds(dir[0], dir[1])) {
        Cell adjacentCell = map.getCell(dir[0], dir[1]);
        Structure structure = adjacentCell.getStructure();

        if (structure instanceof Road) {
          String spriteName = this.getSpriteName(adjacentCell);
          AtlasRegion roadRegion = roadAtlas.findRegion(spriteName);

          if (roadRegion != null) {
            adjacentCell.getStructure().getSprite().setRegion(roadRegion);
          }
        }
      }
    }
  }
}
