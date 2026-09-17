package com.mygdx.game.systems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import com.artemis.BaseSystem;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Cell;
import com.mygdx.game.TerrainType;
import com.mygdx.game.structures.network.Road;

public class TrafficSystem extends BaseSystem {
  private static final boolean RIGHT_HAND_TRAFFIC = true;
  private static final float CELL_CENTER_X = 16f;
  private static final float CELL_CENTER_Y = 8f;
  private static final float CAR_LANE_OFFSET = 1.75f;
  private static final int PATHFINDING_MAX_EXPANSIONS = 640;
  private static final int DESTINATION_SAMPLE_COUNT = 96;
  private static final int MAX_CARS = 24;
  private static final int MAX_PEDESTRIANS = 48;
  private static final float CAR_SPAWN_SECONDS = 2.2f;
  private static final float PEDESTRIAN_SPAWN_SECONDS = 1.1f;
  private static final float CANDIDATE_REFRESH_SECONDS = 1.5f;

  AssetManager assetManager;
  SpriteBatch spriteBatch;
  com.mygdx.game.Map map;

  ArrayList<MovingAgent> cars;
  ArrayList<MovingAgent> pedestrians;
  ArrayList<Cell> cachedRoadCells;
  ArrayList<Cell> cachedPedestrianCells;

  float carSpawnAcc;
  float pedestrianSpawnAcc;
  float candidateRefreshAcc;

  public TrafficSystem(AssetManager assetManager, SpriteBatch spriteBatch, com.mygdx.game.Map map) {
    this.assetManager = assetManager;
    this.spriteBatch = spriteBatch;
    this.map = map;

    this.cars = new ArrayList<>();
    this.pedestrians = new ArrayList<>();
    this.cachedRoadCells = new ArrayList<>();
    this.cachedPedestrianCells = new ArrayList<>();

    refreshCandidateCaches();
  }

  @Override
  protected void processSystem() {
    float delta = world.getDelta();

    spawnAgents(delta);

    updateCarsWithTrafficRules(delta);
    updateAgents(pedestrians, delta, this::isWalkablePedestrianCell);

    renderCars(cars, 8f, 6f);
    renderAgents(pedestrians, 12f, 10f);
  }

  private void spawnAgents(float delta) {
    carSpawnAcc += delta;
    pedestrianSpawnAcc += delta;
    candidateRefreshAcc += delta;

    if (candidateRefreshAcc >= CANDIDATE_REFRESH_SECONDS) {
      candidateRefreshAcc = 0f;
      refreshCandidateCaches();
    }

    if (carSpawnAcc >= CAR_SPAWN_SECONDS) {
      carSpawnAcc -= CAR_SPAWN_SECONDS;
      if (cars.size() < MAX_CARS) {
        spawnCar();
      }
    }

    if (pedestrianSpawnAcc >= PEDESTRIAN_SPAWN_SECONDS) {
      pedestrianSpawnAcc -= PEDESTRIAN_SPAWN_SECONDS;
      if (pedestrians.size() < MAX_PEDESTRIANS) {
        spawnPedestrian();
      }
    }
  }

  private void spawnCar() {
    Set<LaneSlot> occupiedLaneSlots = getOccupiedCarLaneSlots();
    Cell spawnCell = getRandomCachedCell(cachedRoadCells);
    if (spawnCell == null || !hasFreeLane(spawnCell, occupiedLaneSlots)) {
      return;
    }

    TextureAtlas commonAtlas = assetManager.get("common.atlas", TextureAtlas.class);
    Sprite sprite = commonAtlas.createSprite("cursor");
    sprite.setColor(new Color(1f, 0.35f, 0.1f, 1f));
    sprite.setScale(0.24f, 0.24f);

    MovingAgent car = new MovingAgent(spawnCell, sprite, 1.25f);
    cars.add(car);
  }

  private boolean hasFreeLane(Cell cell, Set<LaneSlot> occupiedLaneSlots) {
    int occupiedLanes = 0;

    for (LaneSlot slot : occupiedLaneSlots) {
      if (slot.cell == cell) {
        occupiedLanes++;
      }
    }

    return occupiedLanes < 2;
  }

  private void updateCarsWithTrafficRules(float delta) {
    Set<LaneSlot> occupiedLaneSlots = getOccupiedCarLaneSlots();
    Set<LaneSlot> reservedTargetLaneSlots = new HashSet<>();

    for (MovingAgent car : cars) {
      if (car.toCell == null) {
        assignNewPath(car, this::isRoadCell, true);
      }

      if (car.toCell == null) {
        car.blockedTicks = 0;
        continue;
      }

      LaneSlot currentLaneSlot = getLaneSlotForMovement(car.fromCell, car.toCell, car);
      LaneSlot targetLaneSlot = new LaneSlot(car.toCell, currentLaneSlot.laneX, currentLaneSlot.laneY);

      boolean targetOccupiedByAnotherCar = occupiedLaneSlots.contains(targetLaneSlot) && !targetLaneSlot.equals(currentLaneSlot);
      boolean targetAlreadyReserved = !reservedTargetLaneSlots.add(targetLaneSlot);

      if (targetOccupiedByAnotherCar || targetAlreadyReserved) {
        car.blockedTicks++;
        if (car.blockedTicks > 120) {
          car.toCell = null;
          car.path = null;
          car.pathIndex = 0;
          car.progress = 0f;
          car.blockedTicks = 0;
        }
        continue;
      }

      car.blockedTicks = 0;
      car.progress += delta * car.speed;

      if (car.progress >= 1f) {
        occupiedLaneSlots.remove(currentLaneSlot);

        car.fromCell = car.toCell;
        car.progress = 0f;
        car.laneX = targetLaneSlot.laneX;
        car.laneY = targetLaneSlot.laneY;
        occupiedLaneSlots.add(targetLaneSlot);

        if (car.path != null && car.pathIndex < car.path.size() - 1) {
          car.pathIndex++;
          car.toCell = car.path.get(car.pathIndex);
        } else {
          car.toCell = null;
          car.path = null;
          car.pathIndex = 0;
        }
      }
    }
  }

  private void spawnPedestrian() {
    Cell spawnCell = getRandomCachedCell(cachedPedestrianCells);
    if (spawnCell == null) {
      return;
    }

    TextureAtlas commonAtlas = assetManager.get("common.atlas", TextureAtlas.class);
    Sprite sprite = commonAtlas.createSprite("cell");
    sprite.setColor(new Color(0.2f, 0.75f, 1f, 1f));
    sprite.setScale(0.28f, 0.28f);

    MovingAgent pedestrian = new MovingAgent(spawnCell, sprite, 0.7f);
    pedestrians.add(pedestrian);
  }

  private void updateAgents(ArrayList<MovingAgent> agents, float delta, Predicate<Cell> walkable) {
    for (MovingAgent agent : agents) {
      if (agent.toCell == null) {
        assignNewPath(agent, walkable, false);
      }

      if (agent.toCell == null) {
        continue;
      }

      agent.progress += delta * agent.speed;
      if (agent.progress >= 1f) {
        agent.fromCell = agent.toCell;
        agent.progress = 0f;

        if (agent.path != null && agent.pathIndex < agent.path.size() - 1) {
          agent.pathIndex++;
          agent.toCell = agent.path.get(agent.pathIndex);
        } else {
          agent.toCell = null;
          agent.path = null;
          agent.pathIndex = 0;
        }
      }
    }
  }

  private void renderAgents(ArrayList<MovingAgent> agents, float offsetX, float offsetY) {
    for (MovingAgent agent : agents) {
      Vector2 from = agent.fromCell.getPosition();
      Vector2 to = (agent.toCell != null) ? agent.toCell.getPosition() : from;

      float x = MathUtils.lerp(from.x, to.x, agent.progress) + offsetX;
      float y = MathUtils.lerp(from.y, to.y, agent.progress) + offsetY;

      agent.sprite.setPosition(x, y);
      agent.sprite.draw(spriteBatch);
    }
  }

  private void renderCars(ArrayList<MovingAgent> agents, float offsetX, float offsetY) {
    for (MovingAgent agent : agents) {
      Vector2 from = agent.fromCell.getPosition();
      Vector2 to = (agent.toCell != null) ? agent.toCell.getPosition() : from;

      float x = MathUtils.lerp(from.x, to.x, agent.progress) + CELL_CENTER_X;
      float y = MathUtils.lerp(from.y, to.y, agent.progress) + CELL_CENTER_Y;

      float moveX = to.x - from.x;
      float moveY = to.y - from.y;
      float len = (float) Math.sqrt(moveX * moveX + moveY * moveY);

      if (len > 0.001f) {
        float perpX = moveY / len;
        float perpY = -moveX / len;

        float laneSign = RIGHT_HAND_TRAFFIC ? 1f : -1f;
        x += laneSign * perpX * CAR_LANE_OFFSET;
        y += laneSign * perpY * CAR_LANE_OFFSET;
      }

      agent.sprite.setCenter(x, y);
      agent.sprite.draw(spriteBatch);
    }
  }

  private void assignNewPath(MovingAgent agent, Predicate<Cell> walkable, boolean roadMode) {
    Cell destination = pickDestination(agent.fromCell, walkable, roadMode);
    if (destination == null || destination == agent.fromCell) {
      return;
    }

    List<Cell> path = findPath(agent.fromCell, destination, walkable);
    if (path == null || path.size() < 2) {
      return;
    }

    agent.path = path;
    agent.pathIndex = 1;
    agent.toCell = path.get(1);
    agent.progress = 0f;

    LaneSlot laneSlot = getLaneSlotForMovement(agent.fromCell, agent.toCell, agent);
    agent.laneX = laneSlot.laneX;
    agent.laneY = laneSlot.laneY;
  }

  private Cell pickDestination(Cell source, Predicate<Cell> walkable, boolean roadMode) {
    List<Cell> sourceList = roadMode ? cachedRoadCells : cachedPedestrianCells;
    if (sourceList.isEmpty()) {
      return null;
    }

    List<Cell> candidates = new ArrayList<>();
    int sourceX = source.getGridPosition().x;
    int sourceY = source.getGridPosition().y;

    int attempts = Math.min(DESTINATION_SAMPLE_COUNT, sourceList.size());
    for (int i = 0; i < attempts; i++) {
      Cell cell = sourceList.get(MathUtils.random(sourceList.size() - 1));
      if (!walkable.test(cell)) {
        continue;
      }

      int dx = Math.abs(cell.getGridPosition().x - sourceX);
      int dy = Math.abs(cell.getGridPosition().y - sourceY);
      int distance = dx + dy;

      if (distance >= 6 && distance <= 30) {
        candidates.add(cell);
      }
    }

    if (candidates.isEmpty()) {
      return null;
    }

    int index = MathUtils.random(candidates.size() - 1);
    return candidates.get(index);
  }

  private List<Cell> findPath(Cell source, Cell destination, Predicate<Cell> walkable) {
    LinkedList<Cell> queue = new LinkedList<>();
    Set<Cell> visited = new HashSet<>();
    Map<Cell, Cell> previous = new HashMap<>();
    int expansions = 0;

    queue.add(source);
    visited.add(source);

    while (!queue.isEmpty()) {
      if (expansions++ > PATHFINDING_MAX_EXPANSIONS) {
        return null;
      }

      Cell current = queue.poll();
      if (current == destination) {
        break;
      }

      int x = current.getGridPosition().x;
      int y = current.getGridPosition().y;
      for (Cell adjacentCell : map.getAdjacentCells(x, y)) {
        if (!walkable.test(adjacentCell) || visited.contains(adjacentCell)) {
          continue;
        }

        visited.add(adjacentCell);
        previous.put(adjacentCell, current);
        queue.add(adjacentCell);
      }
    }

    if (!visited.contains(destination)) {
      return null;
    }

    List<Cell> path = new ArrayList<>();
    Cell current = destination;
    while (current != null) {
      path.add(current);
      if (current == source) {
        break;
      }
      current = previous.get(current);
    }

    Collections.reverse(path);
    return path;
  }

  private Cell getRandomCachedCell(List<Cell> candidates) {
    if (candidates == null || candidates.isEmpty()) {
      return null;
    }

    int index = MathUtils.random(candidates.size() - 1);
    return candidates.get(index);
  }

  private boolean isRoadCell(Cell cell) {
    return cell.getStructure() instanceof Road;
  }

  private void refreshCandidateCaches() {
    cachedRoadCells.clear();
    cachedPedestrianCells.clear();

    for (Cell cell : map.getCells()) {
      if (isRoadCell(cell)) {
        cachedRoadCells.add(cell);
      }

      if (isWalkablePedestrianCell(cell)) {
        cachedPedestrianCells.add(cell);
      }
    }
  }

  private Set<LaneSlot> getOccupiedCarLaneSlots() {
    Set<LaneSlot> occupied = new HashSet<>();
    for (MovingAgent car : cars) {
      if (car.fromCell != null) {
        occupied.add(getLaneSlotForMovement(car.fromCell, car.toCell, car));
      }
    }
    return occupied;
  }

  private LaneSlot getLaneSlotForMovement(Cell fromCell, Cell toCell, MovingAgent agent) {
    if (fromCell == null) {
      return null;
    }

    if (toCell == null) {
      return new LaneSlot(fromCell, agent.laneX, agent.laneY);
    }

    Vector2 from = fromCell.getPosition();
    Vector2 to = toCell.getPosition();

    float moveX = to.x - from.x;
    float moveY = to.y - from.y;
    float len = (float) Math.sqrt(moveX * moveX + moveY * moveY);
    if (len <= 0.001f) {
      return new LaneSlot(fromCell, agent.laneX, agent.laneY);
    }

    float perpX = moveY / len;
    float perpY = -moveX / len;
    float laneSign = RIGHT_HAND_TRAFFIC ? 1f : -1f;

    int laneX = Math.round(Math.signum(laneSign * perpX));
    int laneY = Math.round(Math.signum(laneSign * perpY));

    return new LaneSlot(fromCell, laneX, laneY);
  }

  private boolean isWalkablePedestrianCell(Cell cell) {
    if (cell.getTerrain().getTerrainType() == TerrainType.WATER) {
      return false;
    }

    return cell.getZone() != null || cell.getStructure() != null;
  }

  private static class MovingAgent {
    Cell fromCell;
    Cell toCell;
    List<Cell> path;
    int pathIndex;
    Sprite sprite;
    float speed;
    float progress;
    int blockedTicks;
    int laneX;
    int laneY;

    MovingAgent(Cell fromCell, Sprite sprite, float speed) {
      this.fromCell = fromCell;
      this.sprite = sprite;
      this.speed = speed;
      this.progress = 0f;
      this.toCell = null;
      this.path = null;
      this.pathIndex = 0;
      this.blockedTicks = 0;
      this.laneX = 0;
      this.laneY = 0;
    }
  }

  private static class LaneSlot {
    Cell cell;
    int laneX;
    int laneY;

    LaneSlot(Cell cell, int laneX, int laneY) {
      this.cell = cell;
      this.laneX = laneX;
      this.laneY = laneY;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;
      LaneSlot laneSlot = (LaneSlot) o;
      return laneX == laneSlot.laneX && laneY == laneSlot.laneY && cell == laneSlot.cell;
    }

    @Override
    public int hashCode() {
      return Objects.hash(System.identityHashCode(cell), laneX, laneY);
    }
  }
}