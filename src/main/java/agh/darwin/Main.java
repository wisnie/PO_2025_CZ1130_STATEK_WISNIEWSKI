package agh.darwin;

public class Main {
    private static final Configuration DEFAULT_CONFIGURATION = new Configuration(
            new Configuration.MapDimensions(30, 20),
            new Configuration.MapVariant(null),
            new Configuration.PlantParameters(20,20, 40),
            new Configuration.AnimalParameters(50, 50, 5, 10, 8),
            new Configuration.GenotypeParameters(0, 2, 10),
            new Configuration.ScentParameters(10, 0.8, 3)
    );

    public static void main() {
        System.out.println("Hello, world!");
        Simulation simulation = new Simulation(DEFAULT_CONFIGURATION);

        try {
            simulation.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
