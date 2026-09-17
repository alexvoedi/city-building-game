package com.mygdx.game;

public class City {
  Map map;

  CityDate cityDate;
  int money;
  int population;
  int lastDailyBalance;
  String season;
  String weather;
  float growthModifier;
  float incomeModifier;

  public City(Map map) {
    this.map = map;

    this.cityDate = new CityDate();
    this.money = 50_000;
    this.population = 0;
    this.lastDailyBalance = 0;
    this.season = "Spring";
    this.weather = "Clear";
    this.growthModifier = 1f;
    this.incomeModifier = 1f;
  }

  public void update() {
    this.cityDate.advanceTime();
  }

  public CityDate getCityDate() {
    return cityDate;
  }

  public int getMoney() {
    return money;
  }

  public void setMoney(int money) {
    this.money = money;
  }

  public void addMoney(int amount) {
    this.money += amount;
  }

  public int getPopulation() {
    return population;
  }

  public void setPopulation(int population) {
    this.population = population;
  }

  public int getLastDailyBalance() {
    return lastDailyBalance;
  }

  public void setLastDailyBalance(int lastDailyBalance) {
    this.lastDailyBalance = lastDailyBalance;
  }

  public String getSeason() {
    return season;
  }

  public void setSeason(String season) {
    this.season = season;
  }

  public String getWeather() {
    return weather;
  }

  public void setWeather(String weather) {
    this.weather = weather;
  }

  public float getGrowthModifier() {
    return growthModifier;
  }

  public void setGrowthModifier(float growthModifier) {
    this.growthModifier = growthModifier;
  }

  public float getIncomeModifier() {
    return incomeModifier;
  }

  public void setIncomeModifier(float incomeModifier) {
    this.incomeModifier = incomeModifier;
  }
}
