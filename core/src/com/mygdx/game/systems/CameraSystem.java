package com.mygdx.game.systems;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

import static com.mygdx.game.Cell.CELL_SIZE;
import static com.mygdx.game.Map.GAME_WORLD_SIZE;

public class CameraSystem extends BaseSystem implements InputProcessor {

  OrthographicCamera camera;

  ArrayList<Integer> pressedKeys;
  ArrayList<Integer> pressedButtons;

  Vector2 pointerDownPosition;
  Vector2 currentPointerPosition;

  public CameraSystem(OrthographicCamera camera) {
    this.camera = camera;

    pressedKeys = new ArrayList<>();
    pressedButtons = new ArrayList<>();
  }

  @Override
  public boolean keyDown(int keycode) {
    pressedKeys.add(keycode);

    return false;
  }

  @Override
  public boolean keyUp(int keycode) {
    pressedKeys.remove(Integer.valueOf(keycode));

    return false;
  }

  @Override
  public boolean keyTyped(char character) {
    return false;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    pressedButtons.add(button);

    if (button == Input.Buttons.RIGHT) {
      pointerDownPosition = new Vector2(screenX, screenY);
      currentPointerPosition = new Vector2(screenX, screenY);

      return true;
    }

    return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    pressedButtons.remove(Integer.valueOf(button));

    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    if (pressedButtons.contains(Input.Buttons.RIGHT)) {
      currentPointerPosition = new Vector2(screenX, screenY);

      return true;
    }

    return false;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    return false;
  }

  @Override
  public boolean scrolled(float amountX, float amountY) {
    int zoomStep = 2;
    float maxZoom = 0.5f;
    float minZoom = 8;

    if (amountY > 0) {
      camera.zoom = MathUtils.clamp(camera.zoom * zoomStep, maxZoom, minZoom);
    }

    if (amountY < 0) {
      camera.zoom = MathUtils.clamp(camera.zoom / zoomStep, maxZoom, minZoom);
    }

    return false;
  }

  public void update(float delta) {
    float cameraSpeed = 300f;
    float deltaPosition = cameraSpeed * delta * camera.zoom;

    if (pressedKeys.size() > 0) {

      if (pressedKeys.contains(Input.Keys.RIGHT)) {
        camera.position.x = clampCameraX(camera.position.x + deltaPosition);
      }

      if (pressedKeys.contains(Input.Keys.LEFT)) {
        camera.position.x = clampCameraX(camera.position.x - deltaPosition);
      }

      if (pressedKeys.contains(Input.Keys.UP)) {
        camera.position.y = clampCameraY(camera.position.y + deltaPosition);
      }

      if (pressedKeys.contains(Input.Keys.DOWN)) {
        camera.position.y = clampCameraY(camera.position.y - deltaPosition);
      }
    }

    if (pressedButtons.size() > 0) {
      if (pressedButtons.contains(Input.Buttons.RIGHT)) {
        Vector2 direction = currentPointerPosition.cpy().sub(pointerDownPosition).scl(0.005f);

        camera.position.x = clampCameraX(camera.position.x + direction.x * deltaPosition);
        camera.position.y = clampCameraY(camera.position.y - direction.y * deltaPosition);
      }
    }
  }

  private float clampCameraX(float x) {
    return MathUtils.clamp(
      x,
      0,
      GAME_WORLD_SIZE * CELL_SIZE.x
    );
  }

  private float clampCameraY(float y) {
    return MathUtils.clamp(
      y,
      - 0.5f * GAME_WORLD_SIZE * CELL_SIZE.y,
      + 0.5f * GAME_WORLD_SIZE * CELL_SIZE.y
    );
  }

  @Override
  protected void processSystem() {
    update(world.delta);
  }
}
