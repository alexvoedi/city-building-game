package com.mygdx.game.systems;

import com.artemis.BaseSystem;
import com.mygdx.game.City;

public class CitySystem extends BaseSystem {
  City city;

  float acc;

  public CitySystem(City city) {
    this.city = city;
  }

  @Override
  protected void processSystem() {
    this.acc += this.world.getDelta();

    if (acc > 1) {
      this.acc -= 1;

      this.city.update();
    }
  }
}
