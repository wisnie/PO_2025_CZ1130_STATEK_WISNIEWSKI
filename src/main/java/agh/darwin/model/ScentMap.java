package agh.darwin.model;

import agh.darwin.Configuration;
import agh.darwin.supplementary.Vector2i;

import java.util.*;
import java.util.function.Function;

public class ScentMap extends BasicMap {
    private final HashMap<Vector2i, List<Scent>> scents = new HashMap<>();
    private final LinkedList<Scent> activeScents = new LinkedList<>();

    private final SplittableRandom random;

    public ScentMap(int width, int height, Function<Animal, AnimalAccessStatistics> accessStats, Configuration configuration) {
        super(width, height, accessStats, configuration);
        this.random = new SplittableRandom();
    }

    @Override
    public void simulateDayProgression() {
        super.simulateDayProgression();

        Iterator<Scent> iterator = activeScents.iterator();
        while (iterator.hasNext()) {
            Scent s = iterator.next();
            s.decreaseLongevity();
            if (s.scentGone()) {
                dissipateScent(s.getOrigin(), s);
                iterator.remove();
            }
        }
    }

    @Override
    protected void moveAnimal(Animal animal) {
        Vector2i initialField = animal.getPosition();
        double drawn = random.nextDouble();
        boolean followsScents = drawn < configuration
                .scentParams()
                .probabilityOfSmelling();

        if (scents.containsKey(initialField) && followsScents) {
            List<Scent> scentsOnField = scents.get(initialField);
            final Comparator<Scent> scentComparator = Comparator
                    .comparingInt((Scent s) -> Vector2i.calcChebyshevMetric(s.getOrigin(), initialField))
                    .thenComparing(Scent::getScentLongevity);

            Scent followedScent = Collections.min(scentsOnField, scentComparator);
            animal.moveTowardsScent(followedScent);

            removeAnimal(initialField, animal);
            addAnimal(animal);
        } else {
            super.moveAnimal(animal);
        }
    }

    @Override
    protected void reproducePair(List<Animal> animalPair) {
        super.reproducePair(animalPair);
        addScent(animalPair.getFirst().getPosition());
    }

    private void addScent(Vector2i position) {
        Optional<Scent> foundScent = scents
                .getOrDefault(position, Collections.emptyList())
                .stream()
                .filter(s -> s.getOrigin().equals(position))
                .findFirst();

        if (foundScent.isPresent()) {
            foundScent.get().increaseLongevity(configuration
                    .scentParams().scentDays());
        } else {
           spreadScent(position);
        }
    }

    private void spreadScent(Vector2i scentOrigin) {
        Boundary scentBoundary = getScentBoundary(scentOrigin);

        Scent scent = new Scent(scentOrigin, configuration.scentParams().scentDays());
        for (int i = 0; i < scentBoundary.upperBound().x() - scentBoundary.lowerBound().x(); i++) {
            for (int j = 0; j < scentBoundary.upperBound().y() - scentBoundary.lowerBound().y(); j++) {
                Vector2i field = scentBoundary.lowerBound().add(new Vector2i(i, j));
                scents
                        .computeIfAbsent(field, k -> new ArrayList<>())
                        .add(scent);
            }
        }

        activeScents.add(scent);
    }

    private void dissipateScent(Vector2i scentOrigin, Scent scent) {
        Boundary scentBoundary = getScentBoundary(scentOrigin);

        for (int i = 0; i < scentBoundary.upperBound().x() - scentBoundary.lowerBound().x(); i++) {
            for (int j = 0; j < scentBoundary.upperBound().y() - scentBoundary.lowerBound().y(); j++) {
                Vector2i field = scentBoundary.lowerBound().add(new Vector2i(i, j));
                List<Scent> fieldScents =  scents.get(field);
                fieldScents.remove(scent);

                if (fieldScents.isEmpty()) scents.remove(field);
            }
        }
    }

    private Boundary getScentBoundary(Vector2i scentOrigin) {
        final int radius = configuration.scentParams().radius();
        final Vector2i scentLowerBound = scentOrigin.subtract(new Vector2i(radius, radius));
        final Vector2i scentUpperBound = scentOrigin.add(new Vector2i(radius, radius));

        return new Boundary(
                scentLowerBound.upperRight(boundary.lowerBound()),
                scentUpperBound.lowerLeft(boundary.upperBound())
        );
    }
}
