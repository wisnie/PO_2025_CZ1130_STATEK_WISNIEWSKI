package agh.darwin.presenter;

import agh.darwin.Configuration;
import agh.darwin.Simulation;
import agh.darwin.SimulationEngine;
import agh.darwin.model.BasicMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ConfigurationPresenter {
    @FXML private TextField widthField;
    @FXML private TextField heightField;
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

    @FXML
    private void onSimulationStartClicked() throws IOException {
        int width = Integer.parseInt(widthField.getText());
        int height = Integer.parseInt(heightField.getText());

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

        Configuration configuration = new Configuration(
                new Configuration.MapDimensions(width, height),
                null,
                new Configuration.PlantParameters(startingPlants, plantsPerDay, plantEnergy),
                new Configuration.AnimalParameters(
                        startingAnimals,
                        startingEnergy,
                        dailyEnergyConsumption,
                        reproductionThreshold,
                        reproductionCost
                ),
                new Configuration.GenotypeParameters(minimumMutations, maximumMutations, genomeLength)
        );

        Simulation simulation = new Simulation(configuration);
        startSimulation(simulation, configuration);
    }

    private void startSimulation(Simulation simulation, Configuration configuration) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("simulation.fxml"));
        BorderPane viewRoot = loader.load();

        SimulationPresenter presenter = loader.getController();
        presenter.setWorldMap((BasicMap) simulation.getMap(), configuration);

        Stage stage = new Stage();
        stage.setTitle("Darwin Simulation");
        stage.setScene(new Scene(viewRoot));
        stage.show();

        SimulationEngine engine = new SimulationEngine(List.of(simulation));
        engine.runAsyncInThreadPool();
    }
}

