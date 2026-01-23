package agh.darwin.model;

import agh.darwin.supplementary.OrdinalDirection;
import agh.darwin.supplementary.Setting;
import agh.darwin.supplementary.Vector2i;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SphereTopologyTest {
    // A 10x10 map dimensions.
    final Boundary BOUNDARY = new Boundary(new Vector2i(0, 0), new Vector2i(9, 9));

    @Test
    void movesToValidField() {
        SphereTopology topology = new SphereTopology(BOUNDARY);

        Vector2i position = new Vector2i(5, 5);
        assertEquals(new Vector2i(5, 6) ,
                topology.moveForward(position, OrdinalDirection.NORTH)
                        .position());

        assertEquals(new Vector2i(6, 6) ,
                topology.moveForward(position, OrdinalDirection.NORTHEAST)
                        .position());

        assertEquals(new Vector2i(6, 5) ,
                topology.moveForward(position, OrdinalDirection.EAST)
                        .position());

        assertEquals(new Vector2i(6, 4) ,
                topology.moveForward(position, OrdinalDirection.SOUTHEAST)
                        .position());

        assertEquals(new Vector2i(5, 4) ,
                topology.moveForward(position, OrdinalDirection.SOUTH)
                        .position());

        assertEquals(new Vector2i(4, 4) ,
                topology.moveForward(position, OrdinalDirection.SOUTHWEST)
                        .position());

        assertEquals(new Vector2i(4, 5) ,
                topology.moveForward(position, OrdinalDirection.WEST)
                        .position());

        assertEquals(new Vector2i(4, 6) ,
                topology.moveForward(position, OrdinalDirection.NORTHWEST)
                        .position());
    }

    @Test
    void teleportsUponHittingVerticalBoundary() {
        SphereTopology topology = new SphereTopology(BOUNDARY);
        Vector2i position = new Vector2i(5, 9);

        Setting setting = topology.moveForward(position, OrdinalDirection.NORTH);
        assertEquals(OrdinalDirection.SOUTH, setting.direction());
        assertEquals(new Vector2i(5, 9), setting.position());

        position = new Vector2i(5, 0);
        setting = topology.moveForward(position, OrdinalDirection.SOUTH);
        assertEquals(OrdinalDirection.NORTH, setting.direction());
        assertEquals(new Vector2i(5, 0), setting.position());
    }

    @Test
    void bouncesBackUponHittingPole() {
        SphereTopology topology = new SphereTopology(BOUNDARY);
        Vector2i position = new Vector2i(0, 5);

        Setting setting = topology.moveForward(position, OrdinalDirection.WEST);
        assertEquals(OrdinalDirection.WEST, setting.direction());
        assertEquals(new Vector2i(9, 5), setting.position());

        position = new Vector2i(9, 5);
        setting = topology.moveForward(position, OrdinalDirection.EAST);
        assertEquals(OrdinalDirection.EAST, setting.direction());
        assertEquals(new Vector2i(0, 5), setting.position());
    }

    @Test
    void keepsValidOrientation() {
        SphereTopology topology = new SphereTopology(BOUNDARY);

        Vector2i position = new Vector2i(5, 5);

        assertEquals(OrdinalDirection.NORTH,
                topology.moveForward(position, OrdinalDirection.NORTH)
                        .direction());

        assertEquals(OrdinalDirection.NORTHEAST,
                topology.moveForward(position, OrdinalDirection.NORTHEAST)
                        .direction());

        assertEquals(OrdinalDirection.EAST,
                topology.moveForward(position, OrdinalDirection.EAST)
                        .direction());

        assertEquals(OrdinalDirection.SOUTHEAST,
                topology.moveForward(position, OrdinalDirection.SOUTHEAST)
                        .direction());

        assertEquals(OrdinalDirection.SOUTH,
                topology.moveForward(position, OrdinalDirection.SOUTH)
                        .direction());

        assertEquals(OrdinalDirection.SOUTHWEST,
                topology.moveForward(position, OrdinalDirection.SOUTHWEST)
                        .direction());

        assertEquals(OrdinalDirection.WEST,
                topology.moveForward(position, OrdinalDirection.WEST)
                        .direction());

        assertEquals(OrdinalDirection.NORTHWEST,
                topology.moveForward(position, OrdinalDirection.NORTHWEST)
                        .direction());
    }
}