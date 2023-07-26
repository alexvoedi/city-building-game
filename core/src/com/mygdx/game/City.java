package com.mygdx.game;

public class City {
  Map map;

  CityDate cityDate;
  int money;

  public City(Map map) {
    this.map = map;

    this.cityDate = new CityDate();
    this.money = 50_000;
  }

  public void update() {
    this.cityDate.advanceTime();
  }
}
