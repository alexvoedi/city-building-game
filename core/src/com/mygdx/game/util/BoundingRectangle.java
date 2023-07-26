package com.mygdx.game.util;

import com.badlogic.gdx.math.Vector2;

public class BoundingRectangle {
  Vector2 start;
  Vector2 end;

  public BoundingRectangle(Vector2 start, Vector2 end) {
    this.start = start;
    this.end = end;
  }

  public Vector2 getStart() {
    return this.start;
  }

  public Vector2 getEnd() {
    return this.end;
  }

  public float getLeft() {
    return this.start.x;
  }

  public float getTop() {
    return this.start.y;
  }

  public float getRight() {
    return this.end.x;
  }

  public float getBottom() {
    return this.end.x;
  }
}
