package agh.darwin.presenter;

import agh.darwin.Configuration;
import agh.darwin.model.Animal;
import agh.darwin.model.ScentMap; // ZMIANA
import agh.darwin.model.GridMap;
import agh.darwin.model.Plant;
import agh.darwin.model.MapEventListener;
import agh.darwin.supplementary.Vector2i;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
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
    @FXML private Label statsLabel;

    private ScentMap map;
    private Configuration configuration;

    private static final double JUNGLE_MAP_COVERAGE = 0.2;


    public void setWorldMap(ScentMap map, Configuration configuration) {
        this.map = map;
        this.configuration = configuration;
        map.subscribe(this);
        drawMap();
    }

    @Override
    public GridMap mapChanged(GridMap gridMap, String message) {
        Platform.runLater(() -> {
            drawMap();
            statsLabel.setText(message);
        });

        return gridMap;
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

                        animalsOnField.sort(strengthSorter);
                        Animal winner = animalsOnField.getFirst();
                        Rectangle animal = new Rectangle(cellSize, cellSize);

                        double startEnergy = configuration.animalParams().startingEnergy();
                        double currentEnergy = winner.getEnergy().amount();
                        double energyRatio = currentEnergy / startEnergy;

                        if (energyRatio <= 0.5) {
                            animal.setFill(Color.RED);
                        } else if (energyRatio <= 1) {
                            animal.setFill(Color.ORANGE);
                        } else {
                            animal.setFill(Color.BLUE);
                        }

                        cell.getChildren().add(animal);
                    }
                }

                mapGrid.add(cell, x, y);
                GridPane.setHalignment(cell, HPos.CENTER);
            }
        }
    }
}