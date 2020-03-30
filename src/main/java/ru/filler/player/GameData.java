package ru.filler.player;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GameData implements Serializable {
    private int[][] data;
    private List<Integer> colors;
}
