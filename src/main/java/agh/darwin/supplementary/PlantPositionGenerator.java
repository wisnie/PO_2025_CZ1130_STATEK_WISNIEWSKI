package agh.darwin.supplementary;



import agh.darwin.model.Plant;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;


public class PlantPositionGenerator implements Iterable<Vector2i> {
    private static final double JUNGLE_MAP_COVERAGE = 0.2;
    private static final double PLANT_IN_JUNGLE_POSSIBILITY = 0.8;
    private final int mapWidth;
    private final int mapHeight;
    private final int totalPositions;
    private final int jungleHeight;
    private final int jungleMinY;
    private final int junglePositionsNumber;
    private final int dailyPlantNumber;
    private final HashMap<Vector2i, Plant> plants;


    private final Random random = new Random();

    public PlantPositionGenerator(int mapWidth, int mapHeight, int dailyPlantNumber, HashMap<Vector2i, Plant> plants) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.totalPositions = mapWidth * mapHeight;
        this.dailyPlantNumber = dailyPlantNumber;
        this.jungleHeight = (int) (mapHeight * JUNGLE_MAP_COVERAGE);
        this.jungleMinY = (mapHeight - jungleHeight) / 2;
        this.junglePositionsNumber = mapWidth * jungleHeight;
        this.plants = plants;

    }

    @Override
    public Iterator<Vector2i> iterator() {
        return new Iterator<>() {
            private int plantCount = 0;
            private int jungleIndex = 0;
            private int steppeIndex = 0;

            private final int[] junglePositions = new int[junglePositionsNumber];

            {
                for (int i = 0; i < junglePositionsNumber; i++) {
                    junglePositions[i] = i;
                }
            }

            private final int[] steppePositions = new int[totalPositions-junglePositionsNumber];

            {
                for (int i = 0; i < totalPositions-junglePositionsNumber; i++) {
                    steppePositions[i] = i;
                }
            }
            @Override
            public boolean hasNext() {
                return plantCount < dailyPlantNumber;
            }
            @Override
            public Vector2i next() {
                plantCount++;
                double jungleOrSteppe= random.nextDouble();
                if (jungleOrSteppe <= PLANT_IN_JUNGLE_POSSIBILITY) {
                    while (jungleIndex < junglePositionsNumber) {
                        int randomIndex = jungleIndex + random.nextInt(junglePositionsNumber - jungleIndex);
                        int temporary = junglePositions[jungleIndex];
                        int drawn = junglePositions[randomIndex];
                        junglePositions[randomIndex] = temporary;
                        junglePositions[jungleIndex] = drawn;
                        final int X = (drawn % mapWidth);
                        final int Y = jungleMinY + (drawn / mapWidth);
                        jungleIndex++;
                        Vector2i position = new Vector2i(X, Y);
                        if (!plants.containsKey(position)) {
                            return position;
                        }
                    }
                    return null;
                }
                else {
                    while (steppeIndex < totalPositions-junglePositionsNumber) {
                        int randomIndex = steppeIndex + random.nextInt(totalPositions - junglePositionsNumber - steppeIndex);
                        int temporary = steppePositions[steppeIndex];
                        int drawn = steppePositions[randomIndex];
                        steppePositions[randomIndex] = temporary;
                        steppePositions[steppeIndex] = drawn;

                        final int X = (drawn % mapWidth);
                        int Y = (drawn / mapWidth);
                        if (Y >= jungleMinY) {
                            Y += jungleHeight;
                        }
                        steppeIndex++;
                        Vector2i position = new Vector2i(X, Y);
                        if (!plants.containsKey(position)) {
                            return position;
                            }
                    }
                    return null;

                }

            }
        };
    }
}