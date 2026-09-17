package com.mygdx.game.tools.building;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.mygdx.game.Cell;
import com.mygdx.game.Map;
import com.mygdx.game.structures.Structure;
import com.mygdx.game.structures.network.Road;

public class BulldozerTool extends BuildingTool {
  public BulldozerTool(AssetManager assetManager, Map map) {
    super(assetManager, map);
  }

  @Override
  public void build(ArrayList<Cell> cells) {
    TextureAtlas roadAtlas = assetManager.get("roads.atlas", TextureAtlas.class);
    Set<Cell> touchedRoadNeighbors = new HashSet<>();

    for (Cell cell : cells) {
      if (cell == null) {
        continue;
      }

      Structure structure = cell.getStructure();
      if (structure != null) {
        for (Cell occupiedCell : structure.getCells()) {
          occupiedCell.setStructure(null);
          touchedRoadNeighbors.addAll(getRoadNeighbors(occupiedCell));
        }
      }

      cell.setZone(null);
      cell.setHasPower(false);
      touchedRoadNeighbors.addAll(getRoadNeighbors(cell));
    }

    for (Cell roadCell : touchedRoadNeighbors) {
      Structure structure = roadCell.getStructure();
      if (!(structure instanceof Road)) {
        continue;
      }

      String spriteName = getRoadSpriteName(roadCell);
      AtlasRegion roadRegion = roadAtlas.findRegion(spriteName);
      if (roadRegion != null) {
        structure.getSprite().setRegion(roadRegion);
      }
    }
  }

  private ArrayList<Cell> getRoadNeighbors(Cell cell) {
    GridPoint2 gridPosition = cell.getGridPosition();
    ArrayList<Cell> adjacentCells = map.getAdjacentCells(gridPosition.x, gridPosition.y);

    ArrayList<Cell> roadNeighbors = new ArrayList<>();
    for (Cell adjacentCell : adjacentCells) {
      if (adjacentCell.getStructure() instanceof Road) {
        roadNeighbors.add(adjacentCell);
      }
    }

    return roadNeighbors;
  }

  private String getRoadSpriteName(Cell cell) {
    String spriteName = "road";
    GridPoint2 cellGridPosition = cell.getGridPosition();

    ArrayList<Cell> adjacentCells = map.getAdjacentCells(cellGridPosition.x, cellGridPosition.y);

    for (Cell adjacentCell : adjacentCells) {
      GridPoint2 adjCellGridPosition = adjacentCell.getGridPosition();

      if (adjacentCell.getStructure() instanceof Road) {
        if (cellGridPosition.x == adjCellGridPosition.x && cellGridPosition.y + 1 == adjCellGridPosition.y)
          spriteName += "-n";
        if (cellGridPosition.x + 1 == adjCellGridPosition.x && cellGridPosition.y == adjCellGridPosition.y)
          spriteName += "-e";
        if (cellGridPosition.x == adjCellGridPosition.x && cellGridPosition.y - 1 == adjCellGridPosition.y)
          spriteName += "-s";
        if (cellGridPosition.x - 1 == adjCellGridPosition.x && cellGridPosition.y == adjCellGridPosition.y)
          spriteName += "-w";
      }
    }

    return spriteName;
  }
}
