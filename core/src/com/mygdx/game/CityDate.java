package com.mygdx.game;

import java.util.Arrays;

public class CityDate {
  int year;
  int month;
  int day;

  public CityDate() {
    this.year = 2000;
    this.month = 1;
    this.day = 1;
  }

  public void advanceTime() {
    nextDay();
  }

  private void nextDay() {
    if (day == 28 && month == 2 && !isLeapYear()) {
      day = 1;
      nextMonth();
    } else if (day == 29 && month == 2 && isLeapYear()) {
      day = 1;
      nextMonth();
    } else if (day == 30 && Arrays.stream(new int[]{4,6,9,11}).anyMatch((n) -> n == month)) {
      day = 1;
      nextMonth();
    } else if (day == 31 && Arrays.stream(new int[]{1,3,5,7,8,10,12}).anyMatch((n) -> n == month)) {
      day = 1;
      nextMonth();
    } else {
      day++;
    }
  }

  private void nextMonth() {
    if (month == 12) {
      month = 1;
      nextYear();
    } else {
      month++;
    }
  }

  private void nextYear() {
    year++;
  }

  private boolean isLeapYear() {
    return year % 4 == 0 && year % 100 != 0;
  }

  public String toString() {
    return day + "." + month + "." + year;
  }
}
