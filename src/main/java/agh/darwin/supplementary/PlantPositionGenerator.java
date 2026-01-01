package agh.darwin.supplementary;


import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;


public class PlantPositionGenerator implements Iterable<Vector2i> {
    private static final double JUNGLE_MAP_COVERAGE = 0.2;
    private final int mapWidth;
    private final int mapHeight;
    private final int totalPositions;


    public PlantPositionGenerator(int mapWidth, int mapHeight) {
        this.mapHeight = mapHeight;
        this.mapWidth = mapWidth;
        this.totalPositions = mapWidth * mapHeight;
    }

    @Override
    public Iterator<Vector2i> iterator(){
        return new Iterator<>(){
            private int jungleCount = 0;
            private int northHemisphereCount = 0;
            private int southHemisphereCount = 0;

            private final Random random = new Random();


            @Override
            public boolean hasNext() {
                return (jungleCount + northHemisphereCount + southHemisphereCount) < totalPositions;
            }

            @Override
            public Vector2i next() {
                int drawn = random.nextInt(9);


                return null;
            }
        };
    }
}