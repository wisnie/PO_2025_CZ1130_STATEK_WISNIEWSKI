package agh.darwin.supplementary;

import agh.darwin.model.GridMap;

public interface MapEventListener {
    void mapChanged(GridMap gridMap, String message);
}
