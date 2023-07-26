package com.mygdx.game.zones;

import com.badlogic.gdx.assets.AssetManager;
import com.mygdx.game.Cell;
import com.mygdx.game.ZoneColors;

public class ResidentialZone extends Zone {
  ZoneDensity zoneDensity;

  public ResidentialZone(AssetManager assetManager, Cell cell, ZoneDensity zoneDensity) {
    super(assetManager, cell);

    this.zoneDensity = zoneDensity;

    switch (zoneDensity) {
      case LOW:
        this.sprite.setColor(ZoneColors.RESIDENTIAL_LOW);
        break;

      case MEDIUM:
        this.sprite.setColor(ZoneColors.RESIDENTIAL_MEDIUM);
        break;

      case HIGH:
        this.sprite.setColor(ZoneColors.RESIDENTIAL_HIGH);
        break;
    }
  }
}
