package agh.darwin.presenter;

import agh.darwin.Configuration;
import agh.darwin.Simulation;
import agh.darwin.model.*;
import agh.darwin.supplementary.Vector2i;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class SimulationPresenter implements MapEventListener {
    @FXML
    private GridPane mapGrid;
    @FXML
    private Label statsLabel;
    @FXML
    private Label animalStatsLabel;
    @FXML
    private Button pauseButton;
    @FXML
    private ComboBox<Animal> animalListField;

    private GridMap map;
    private Configuration configuration;
    private Simulation simulation;
    private boolean pause = false;
    private Animal trackedAnimal = null;

    private static final double JUNGLE_MAP_COVERAGE = 0.2;


    public void setSimulation(Simulation simulation, Configuration configuration) {
        this.simulation = simulation;
        this.map = simulation.getMap();
        this.configuration = configuration;
        map.subscribe(this);
        animalListField.setOnAction(event -> {
            Animal selected = animalListField.getValue();
            if (selected != null) {
                this.trackedAnimal = selected;
                updateTrackedAnimalStats();
                drawMap();
            }
        });
        animalListField.setDisable(true);
        drawMap();
        drawMap();
    }

    @FXML
    private void onPauseClicked() {
        if (pause) {
            simulation.resume();
            pauseButton.setText("Pause");
            animalListField.setDisable(true);
            animalListField.setItems(FXCollections.observableArrayList());
        } else {
            simulation.pause();
            pauseButton.setText("Resume");
            animalListField.setDisable(false);
            animalListField.setPromptText("Click on animal");
        }
        pause = !pause;
    }

    @Override
    public GridMap mapChanged(GridMap gridMap, String message) {
        Platform.runLater(() -> {
            if (simulation == null) return;

            synchronized (simulation) {
                drawMap();
                statsLabel.setText(simulation.getStatistics());
                if (trackedAnimal != null) {
                    updateTrackedAnimalStats();
                }
            }
        });

        return gridMap;
    }

    private void updateTrackedAnimalStats() {
        if (trackedAnimal == null) {
            animalStatsLabel.setText("No animal choosen");
            return;
        }
        AnimalAccessStatistics stats = simulation.provideStatistics(trackedAnimal);
        animalStatsLabel.setText(stats.toString());
    }

    private void drawMap() {
        mapGrid.getChildren().clear();
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();

        double availableWidth = mapGrid.getWidth();
        double availableHeight = mapGrid.getHeight();

        if (availableWidth <= 0 || availableHeight <= 0) return;

        int width = configuration.mapDimensions().width();
        int height = configuration.mapDimensions().height();
        int jungleHeight = (int) (height * JUNGLE_MAP_COVERAGE);
        int jungleMinY = (height - jungleHeight) / 2;
        int jungleMaxY = jungleHeight + jungleMinY;
        double cellSize = Math.min(availableWidth / width, availableHeight / height);
        HashMap<Vector2i, Plant> plants = map.getPlants();
        HashMap<Vector2i, List<Animal>> animals = map.getAnimals();

        for (int i = 0; i < width; i++) mapGrid.getColumnConstraints().add(new ColumnConstraints(cellSize));
        for (int i = 0; i < height; i++) mapGrid.getRowConstraints().add(new RowConstraints(cellSize));

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Vector2i position = new Vector2i(x, y);
                StackPane cell = new StackPane();

                Rectangle background = new Rectangle(cellSize, cellSize);
                if (y >= jungleMinY && y < jungleMaxY) {
                    background.setFill(Color.FORESTGREEN);
                } else {
                    background.setFill(Color.SANDYBROWN);
                }
                cell.getChildren().add(background);

                if (plants.containsKey(position)) {
                    Rectangle plant = new Rectangle(cellSize, cellSize);
                    plant.setFill(Color.LIMEGREEN);
                    cell.getChildren().add(plant);
                }

                if (animals.containsKey(position)) {
                    List<Animal> animalsOnField = animals.get(position);
                    if (animalsOnField != null && !animalsOnField.isEmpty()) {
                        Comparator<Animal> strengthSorter = Comparator
                                .comparing(Animal::getEnergy)
                                .thenComparing(animal -> 0.0)
                                .reversed();

                        Rectangle animal = new Rectangle(cellSize, cellSize);
                        animalsOnField.sort(strengthSorter);
                        Animal winner = animalsOnField.getFirst();
                        if (winner.getGenotype().equals(simulation.getMostPopularGenotype())) {
                            animal =  new Rectangle(cellSize*0.8, cellSize*0.8);
                            Rectangle border = new Rectangle(cellSize, cellSize);
                            border.setStroke(Color.RED);
                            cell.getChildren().add(border);

                        }


                        double startEnergy = configuration.animalParams().startingEnergy();
                        double currentEnergy = winner.getEnergy().amount();
                        double energyRatio = Math.min(currentEnergy/startEnergy, 1.0);

                        animal.setFill(Color.LIGHTBLUE.interpolate(Color.DARKBLUE, energyRatio));

                        final List<Animal> animalsOnCLickedField = animalsOnField;

                        animal.setOnMouseClicked(event -> {
                            if (pause) {
                                animalListField.setItems(FXCollections.observableArrayList(animalsOnCLickedField));
                                if (!animalsOnCLickedField.isEmpty()) {
                                    animalListField.getSelectionModel().select(0);
                                }
                            }
                        });


                        cell.getChildren().add(animal);
                    }
                }

                mapGrid.add(cell, x, y);
                GridPane.setHalignment(cell, HPos.CENTER);
            }
        }
    }
}