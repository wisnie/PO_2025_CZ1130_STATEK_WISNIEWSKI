package agh.darwin;

public class Main {
    private static final Configuration DEFAULT_CONFIGURATION = new Configuration(
            new Configuration.MapDimensions(30, 20),
            new Configuration.MapVariant(null),
            new Configuration.PlantParameters(20,20, 40),
            new Configuration.AnimalParameters(50, 50, 5, 10, 8),
            new Configuration.GenotypeParameters(0, 2, 10),
            null
    );

    public static void main() {
        System.out.println("Hello, world!");
        Simulation simulation = new Simulation(DEFAULT_CONFIGURATION);

        simulation.run();
    }
}
