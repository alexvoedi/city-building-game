package com.mygdx.game.systems;

import com.artemis.BaseSystem;
import com.mygdx.game.City;
import com.mygdx.game.CityDate;

public class WeatherSystem extends BaseSystem {
  City city;
  int lastProcessedDay;

  public WeatherSystem(City city) {
    this.city = city;
    this.lastProcessedDay = -1;
  }

  @Override
  protected void processSystem() {
    CityDate date = city.getCityDate();
    int currentDayKey = date.getYear() * 1000 + date.getMonth() * 50 + date.getDay();

    if (currentDayKey != lastProcessedDay) {
      lastProcessedDay = currentDayKey;
      updateWeather(date);
    }
  }

  private void updateWeather(CityDate date) {
    int month = date.getMonth();

    String season;
    if (month == 12 || month == 1 || month == 2) {
      season = "Winter";
    } else if (month >= 3 && month <= 5) {
      season = "Spring";
    } else if (month >= 6 && month <= 8) {
      season = "Summer";
    } else {
      season = "Autumn";
    }

    city.setSeason(season);

    double roll = Math.random();
    String weather;
    float growthModifier;
    float incomeModifier;

    switch (season) {
      case "Winter":
        if (roll < 0.35) {
          weather = "Snow";
          growthModifier = 0.72f;
          incomeModifier = 0.90f;
        } else if (roll < 0.75) {
          weather = "Cloudy";
          growthModifier = 0.90f;
          incomeModifier = 0.97f;
        } else {
          weather = "Clear";
          growthModifier = 1.00f;
          incomeModifier = 1.00f;
        }
        break;

      case "Spring":
        if (roll < 0.35) {
          weather = "Rain";
          growthModifier = 0.96f;
          incomeModifier = 0.98f;
        } else if (roll < 0.80) {
          weather = "Clear";
          growthModifier = 1.10f;
          incomeModifier = 1.02f;
        } else {
          weather = "Cloudy";
          growthModifier = 1.00f;
          incomeModifier = 1.00f;
        }
        break;

      case "Summer":
        if (roll < 0.20) {
          weather = "Heatwave";
          growthModifier = 0.88f;
          incomeModifier = 0.95f;
        } else if (roll < 0.70) {
          weather = "Clear";
          growthModifier = 1.08f;
          incomeModifier = 1.03f;
        } else {
          weather = "Cloudy";
          growthModifier = 1.00f;
          incomeModifier = 1.00f;
        }
        break;

      default:
        if (roll < 0.30) {
          weather = "Rain";
          growthModifier = 0.95f;
          incomeModifier = 0.98f;
        } else if (roll < 0.75) {
          weather = "Clear";
          growthModifier = 1.02f;
          incomeModifier = 1.01f;
        } else {
          weather = "Cloudy";
          growthModifier = 0.98f;
          incomeModifier = 0.99f;
        }
        break;
    }

    city.setWeather(weather);
    city.setGrowthModifier(growthModifier);
    city.setIncomeModifier(incomeModifier);
  }
}
