package agh.darwin;

import agh.darwin.supplementary.FinitePositionGenerator;
import agh.darwin.supplementary.Vector2i;

public class Main {
    public static void main() {
        System.out.println("Hello, world!");
        FinitePositionGenerator positionGenerator = new FinitePositionGenerator(20, 20, 20);

        for (Vector2i position : positionGenerator) {
            System.out.println(position);
        }
    }
}
