package agh.darwin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SimulationEngine {
    private final List<Simulation>  simulations;
    private final List<Thread> threads = new ArrayList<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public SimulationEngine(List<Simulation> simulations) {
        this.simulations = simulations;
    }

    public void runSync(){
        for(Simulation simulation : simulations){
            simulation.run();
        }
    }

    public void runAsyncInThreadPool(){
        for(Simulation simulation : simulations){
            executor.submit(simulation);
        }
    }

    public void terminate() {
        for (Simulation simulation : simulations) {
            simulation.stop();
        }
        executor.shutdownNow();
    }
}
