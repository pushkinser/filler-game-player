package ru.filler.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingInt;

@Service
@Slf4j
public class PlayerService {

    public PlayerResponse process(GameData data) {
        int[][] gameField = data.getData();
        int myColor = gameField[0][0];
        int enemyColor = gameField[gameField.length - 1][gameField[0].length - 1];

        List<ColorCount> neighbourColorCounts = searchNeighbour(gameField, myColor);

        int color = getColor(data, neighbourColorCounts);

        log.info((color == myColor || color == enemyColor)
                ? "Error try pick busy color"
                : "Return color: {}, my color: {}, enemy color: {}", color, myColor, enemyColor);
        return new PlayerResponse(color);
    }

    private int getColor(GameData data, List<ColorCount> neighbourColorCounts) {
        int[][] gameField = data.getData();
        int myColor = gameField[0][0];
        int enemyColor = gameField[gameField.length - 1][gameField[0].length - 1];

        List<Integer> allowed = data.getColors().stream()
                .filter(c -> c != myColor && c != enemyColor)
                .collect(Collectors.toList());

        return neighbourColorCounts.stream()
                .filter(colorCount -> allowed.contains(colorCount.getColor()))
                .max(comparingInt(ColorCount::getCount))
                .map(ColorCount::getColor)
                .orElse(new Random().nextInt(allowed.size()));
    }

    private List<ColorCount> searchNeighbour(int[][] gameField, int myColor) {
        int length = gameField.length;
        boolean[][] myField = initVisitedMatrix(length);

        dfs(gameField, myField, 0, 0, myColor);

        boolean[][] isNeighbour = initVisitedMatrix(length);
        boolean[][] visited = initVisitedMatrix(length);

        dfsNeighbours(gameField, isNeighbour, myField, visited, 0, 0, myColor);

        List<Integer> colors = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
//                if (myField[i][j]) {
////                    System.out.print(" " + gameField[i][j] + " ");
//                }
                if (isNeighbour[i][j]) {
//                    System.out.print("*" + gameField[i][j] + " ");
                    colors.add(gameField[i][j]);
                }
            }
//            System.out.println();
        }

        Map<Integer, Integer> countMap = new HashMap<>();
        for (Integer color : colors) {
            if (countMap.get(color) != null) {
                Integer currentCount = countMap.get(color);
                currentCount = currentCount + 1;
                countMap.put(color, currentCount);
            } else {
                countMap.put(color, 1);
            }
        }

        return countMap.keySet().stream()
                .map(color -> new ColorCount(color, countMap.get(color)))
                .collect(Collectors.toList());
    }

    private boolean[][] initVisitedMatrix(int length) {
        boolean[][] visited = new boolean[length][length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                visited[i][j] = false;
            }
        }
        return visited;
    }

    public void dfsNeighbours(int[][] gameField, boolean[][] isNeighbour, boolean[][] myField, boolean[][] visited, int x, int y, int color) {
        if (myField[x][y] && !visited[x][y]) {
            visited[x][y] = true;
            if (x < (gameField.length - 1)) {
                dfsNeighbours(gameField, isNeighbour, myField, visited, x + 1, y, color);
            }
            if (x > 0) {
                dfsNeighbours(gameField, isNeighbour, myField, visited, x - 1, y, color);
            }
            if (y < (gameField.length - 1)) {
                dfsNeighbours(gameField, isNeighbour, myField, visited, x, y + 1, color);
            }
            if (y > 0) {
                dfsNeighbours(gameField, isNeighbour, myField, visited, x, y - 1, color);
            }
        }
        if (!myField[x][y] && !visited[x][y]) {
//            isNeighbour[x][y] = true;
            dfs(gameField, isNeighbour, x, y, gameField[x][y]);
        }
    }

    public void dfs(int[][] gameField, boolean[][] visited, int x, int y, int color) {
        if (!visited[x][y]) {
            visited[x][y] = true;
            if (x < (gameField.length - 1) && gameField[x + 1][y] == color) {
                dfs(gameField, visited, x + 1, y, color);
            }
            if (x > 0 && gameField[x - 1][y] == color) {
                dfs(gameField, visited, x - 1, y, color);
            }
            if (y < (gameField.length - 1) && gameField[x][y + 1] == color) {
                dfs(gameField, visited, x, y + 1, color);
            }
            if (y > 0 && gameField[x][y - 1] == color) {
                dfs(gameField, visited, x, y - 1, color);
            }
        }
    }

    @Data
    @AllArgsConstructor
    private static class ColorCount {
        Integer color;
        Integer count;
    }
}
