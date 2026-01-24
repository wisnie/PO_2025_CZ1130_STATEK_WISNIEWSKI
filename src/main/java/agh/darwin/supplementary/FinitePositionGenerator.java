package agh.darwin.supplementary;


import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;


public class FinitePositionGenerator implements Iterable<Vector2i> {
    private final int entityCount;
    private final int mapWidth;
    private final int positionsCount;


    public FinitePositionGenerator(int mapWidth, int mapHeight, int entityCount) {
        this.entityCount = entityCount;
        this.mapWidth = mapWidth;
        this.positionsCount = mapWidth * mapHeight;
    }

    @Override
    public Iterator<Vector2i> iterator(){
        return new Iterator<>(){
            private int count = 0;
            private final int[] positions = new int[positionsCount];
            private final Random random = new Random();

            {
                for (int i = 0; i < positionsCount; i++) {
                    positions[i] = i;
                }
            }

            @Override
            public boolean hasNext() {
                return count < entityCount;

            }

            @Override
            public Vector2i next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                int randomIndex = count + random.nextInt(positionsCount - count);
                int temporary = positions[count];
                int drawn = positions[randomIndex];

                positions[randomIndex] = temporary;
                positions[count] = drawn;

                Vector2i drawnPosition = new Vector2i(drawn % mapWidth, drawn / mapWidth);
                count++;

                return drawnPosition;

            }
        };
    }
}