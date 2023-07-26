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

    ArrayList<Cell> adjacentCells = map.getAdjacentCells(cellGridPosition.x, cellGridPosition.y);

    for (Cell adjacentCell : adjacentCells) {
      GridPoint2 adjCellGridPosition = adjacentCell.getGridPosition();

      if (adjacentCell.getStructure() instanceof Road) {
        if (cellGridPosition.x     == adjCellGridPosition.x && cellGridPosition.y + 1 == adjCellGridPosition.y)
          spriteName += "-n";
        if (cellGridPosition.x + 1 == adjCellGridPosition.x && cellGridPosition.y     == adjCellGridPosition.y)
          spriteName += "-e";
        if (cellGridPosition.x     == adjCellGridPosition.x && cellGridPosition.y - 1 == adjCellGridPosition.y)
          spriteName += "-s";
        if (cellGridPosition.x - 1 == adjCellGridPosition.x && cellGridPosition.y     == adjCellGridPosition.y)
          spriteName += "-w";
      }
    }

    return spriteName;
  }

  private void updateAdjacentCells(Cell cell) {
    TextureAtlas roadAtlas = assetManager.get("roads.atlas", TextureAtlas.class);

    GridPoint2 cellGridPosition = cell.getGridPosition();

    adjacentCells = map.getAdjacentCells(cellGridPosition.x, cellGridPosition.y);

    adjacentCells.forEach((adjacentCell) -> {
      Structure structure = adjacentCell.getStructure();

      if (structure instanceof Road) {
        String spriteName = this.getSpriteName(adjacentCell);
        AtlasRegion roadRegion = roadAtlas.findRegion(spriteName);

        adjacentCell.getStructure().getSprite().setRegion(roadRegion);
      }
    });
  }
}
