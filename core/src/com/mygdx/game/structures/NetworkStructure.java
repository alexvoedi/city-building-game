package com.mygdx.game.structures;

import java.util.ArrayList;
import java.util.Arrays;

import com.mygdx.game.Cell;

public abstract class NetworkStructure extends Structure {
  public NetworkStructure(Cell cell) {
    super(new ArrayList<>(Arrays.asList(cell)));
  }
}
