package agh.darwin.presenter;

import agh.darwin.Configuration;
import agh.darwin.Simulation;
import agh.darwin.SimulationEngine;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ConfigurationPresenter {
    @FXML private TextField widthField;
    @FXML private TextField heightField;
    @FXML private ComboBox<String> mapVariantField;
    @FXML private TextField durationOfOneDay;
    @FXML private TextField startingPlantsField;
    @FXML private TextField plantsPerDayField;
    @FXML private TextField plantEnergyField;
    @FXML private TextField startingAnimalsField;
    @FXML private TextField startingEnergyField;
    @FXML private TextField dailyEnergyConsumptionField;
    @FXML private TextField reproductionThresholdField;
    @FXML private TextField reproductionCostField;
    @FXML private TextField minimumMutationsField;
    @FXML private TextField maximumMutationsField;
    @FXML private TextField genomeLengthField;
    @FXML private TextField scentDaysField;
    @FXML private TextField probabilityOfSmellingField;
    @FXML private TextField scentRadiusField;

    @FXML
    private void onSimulationStartClicked() throws IOException {
        int width = Integer.parseInt(widthField.getText());
        int height = Integer.parseInt(heightField.getText());
        String mapVariant = mapVariantField.getValue();
        int dayLength = Integer.parseInt(durationOfOneDay.getText());
        int startingPlants = Integer.parseInt(startingPlantsField.getText());
        int plantsPerDay = Integer.parseInt(plantsPerDayField.getText());
        int plantEnergy = Integer.parseInt(plantEnergyField.getText());
        int startingAnimals = Integer.parseInt(startingAnimalsField.getText());
        int startingEnergy = Integer.parseInt(startingEnergyField.getText());
        int dailyEnergyConsumption = Integer.parseInt(dailyEnergyConsumptionField.getText());

        int reproductionThreshold = Integer.parseInt(reproductionThresholdField.getText());
        int reproductionCost = Integer.parseInt(reproductionCostField.getText());
        int minimumMutations = Integer.parseInt(minimumMutationsField.getText());
        int maximumMutations = Integer.parseInt(maximumMutationsField.getText());
        int genomeLength = Integer.parseInt(genomeLengthField.getText());
        int scentDays = Integer.parseInt(scentDaysField.getText());

        double probabilityOfSmelling = Double.parseDouble(probabilityOfSmellingField.getText());
        int scentRadius = Integer.parseInt(scentRadiusField.getText());

        if (width < 0 || height < 0 || width > 80 || height > 80) {
            throw new IllegalArgumentException("Invalid Boundaries Parameters");
        }

        if (dayLength < 50 || dayLength > 3000) {
            throw new IllegalArgumentException("Invalid Day Length");
        }

        if (startingPlants < 0 || startingPlants > height*width) {
            throw new IllegalArgumentException("Invalid Starting Plants Number");
        }

        if (plantsPerDay < 0 || plantsPerDay > height*width) {
            throw new IllegalArgumentException("Invalid Plants per Day Number");
        }

        if (plantEnergy < 0) {
            throw new IllegalArgumentException("Invalid Plants Energy Value");
        }

        if (startingAnimals < 0 || startingAnimals > height*width) {
            throw new IllegalArgumentException("Invalid Starting Animals Number");
        }

        if (startingEnergy < 0) {
            throw new IllegalArgumentException("Invalid Starting Energy Value");
        }

        if (dailyEnergyConsumption < 0) {
            throw new IllegalArgumentException("Invalid Daily Energy Consumption Value");
        }

        if (reproductionThreshold < 0) {
            throw new IllegalArgumentException("Invalid Reproduction Threshold Value");
        }

        if (reproductionCost < 0) {
            throw new IllegalArgumentException("Invalid Reproduction Cost Value");
        }

        if (genomeLength < 0) {
            throw new IllegalArgumentException("Invalid Genome Length");
        }

        if (minimumMutations < 0 || maximumMutations > genomeLength) {
            throw new IllegalArgumentException("Invalid Mutations Number");
        }

        if (scentDays < 0) {
            throw new IllegalArgumentException("Invalid Scent Days Number");
        }

        if (probabilityOfSmelling < 0 || probabilityOfSmelling > 1) {
            throw new IllegalArgumentException("Invalid Probability Of Smelling Number");
        }

        if (scentRadius < 0) {
            throw new IllegalArgumentException("Invalid Scent Radius Number");
        }

        Configuration configuration = new Configuration(
                new Configuration.MapDimensions(width, height),
                new Configuration.MapVariant(mapVariant),
                new Configuration.PlantParameters(startingPlants, plantsPerDay, plantEnergy),
                new Configuration.AnimalParameters(
                        startingAnimals,
                        startingEnergy,
                        dailyEnergyConsumption,
                        reproductionThreshold,
                        reproductionCost
                ),
                new Configuration.GenotypeParameters(minimumMutations, maximumMutations, genomeLength),
                new Configuration.ScentParameters(scentDays, probabilityOfSmelling, scentRadius),
                new Configuration.DayLength(dayLength)
        );

        Simulation simulation = new Simulation(configuration);
        startSimulation(simulation, configuration);
    }

    private void startSimulation(Simulation simulation, Configuration configuration) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("simulation.fxml"));
        BorderPane viewRoot = loader.load();

        SimulationPresenter presenter = loader.getController();
        presenter.setSimulation(simulation, configuration);

        Stage stage = new Stage();
        stage.setTitle("Darwin Simulation");
        stage.setScene(new Scene(viewRoot));
        stage.show();

        SimulationEngine engine = new SimulationEngine(List.of(simulation));
        engine.runAsyncInThreadPool();
    }
}