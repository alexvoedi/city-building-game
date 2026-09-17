package com.mygdx.game.systems;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

import com.artemis.BaseSystem;
import com.mygdx.game.Cell;
import com.mygdx.game.Map;
import com.mygdx.game.structures.Structure;
import com.mygdx.game.structures.building.WaterSupplyBuilding;

public class WaterSupplySystem extends BaseSystem {
  private static final int CONNECTION_RADIUS = 3;
  private static final float WATER_UPDATE_SECONDS = 0.2f;

  Map map;
  float acc;

  public WaterSupplySystem(Map map) {
    this.map = map;
  }

  @Override
  protected void processSystem() {
    acc += world.getDelta();
    if (acc < WATER_UPDATE_SECONDS) {
      return;
    }
    acc = 0f;

    ArrayDeque<Cell> queue = new ArrayDeque<>();
    Set<Cell> wateredConnectionCells = new HashSet<>();
    Set<Structure> wateredStructures = new HashSet<>();
    Set<Cell> seededZoneCells = new HashSet<>();
    Set<Cell> floodedZoneCells = new HashSet<>();

    // Reset water on all cells
    for (Cell cell : map.getCells()) {
      cell.setHasWater(false);

      if (cell.getStructure() instanceof WaterSupplyBuilding) {
        cell.setHasWater(true);

        if (wateredConnectionCells.add(cell)) {
          queue.add(cell);
        }
      }
    }

    // BFS to propagate water through connections
    while (!queue.isEmpty()) {
      Cell current = queue.poll();

      for (Cell candidateConnection : map.getCellsInCircle(current.getGridPosition(), CONNECTION_RADIUS)) {
        if (candidateConnection == current || !isConnectionCell(candidateConnection)) {
          continue;
        }

        if (wateredConnectionCells.add(candidateConnection)) {
          queue.add(candidateConnection);
        }
      }
    }

    // Mark all structures and zones in range of watered connections
    for (Cell connectionCell : wateredConnectionCells) {
      connectionCell.setHasWater(true);

      for (Cell cellInRange : map.getCellsInCircle(connectionCell.getGridPosition(), CONNECTION_RADIUS)) {
        Structure structure = cellInRange.getStructure();
        if (structure != null && wateredStructures.add(structure)) {
          for (Cell structureCell : structure.getCells()) {
            structureCell.setHasWater(true);
          }
        }

        if (cellInRange.getZone() != null) {
          cellInRange.setHasWater(true);
          seededZoneCells.add(cellInRange);
        }
      }
    }

    // Flood fill zones that are seeded with water
    for (Cell seededZoneCell : seededZoneCells) {
      if (!floodedZoneCells.contains(seededZoneCell)) {
        floodZoneWater(seededZoneCell, floodedZoneCells);
      }
    }
  }

  private boolean isConnectionCell(Cell cell) {
    Structure structure = cell.getStructure();
    return structure != null;
  }

  private void floodZoneWater(Cell startCell, Set<Cell> visitedZoneCells) {
    ArrayDeque<Cell> queue = new ArrayDeque<>();

    queue.add(startCell);
    visitedZoneCells.add(startCell);

    while (!queue.isEmpty()) {
      Cell current = queue.poll();
      current.setHasWater(true);

      int x = current.getGridPosition().x;
      int y = current.getGridPosition().y;
      for (Cell adjacentCell : map.getAdjacentCells(x, y)) {
        if (adjacentCell.getZone() == null) {
          continue;
        }

        if (!visitedZoneCells.add(adjacentCell)) {
          continue;
        }

        queue.add(adjacentCell);
      }
    }
  }

}
