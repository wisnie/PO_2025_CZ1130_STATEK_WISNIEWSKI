package agh.darwin.model;

import agh.darwin.Configuration;
import agh.darwin.supplementary.Vector2i;

import java.util.*;
import java.util.function.Function;

public class ScentMap extends BasicMap {
    private final HashMap<Vector2i, List<Scent>> scents = new HashMap<>();
    private final LinkedList<Scent> activeScents = new LinkedList<>();

    private final SplittableRandom random = new SplittableRandom();

    public ScentMap(int width, int height, Function<Animal, AnimalAccessStatistics> accessStats, Configuration configuration) {
        super(width, height, accessStats, configuration);
    }

    // Wybrano opcję, w której dostęp do pola, na którym jest zapach jest
    // zawsze w O(1). Kosztem tego podejścia są dwa źródła prawdy o zapachach,
    // natomiast wydaje się, że w praktyce powinno osiągać to lepsze wyniki,
    // gdy jest zdecydowanie więcej zwierząt od zapachów.

    // Przechowywanie referencji w promieniu R zajmuje O(R^2) i pozwala na sprawdzenie
    // obecności zapachu w O(1), gdy zapach się wywietrza, zostaje usuwany od razu.
    // Można do tego podejść także leniwie, więc usuwać zapach, gdy zwierze jest w zasięgu
    // zapachu, który już jest nieaktywny, od razu go usuwamy. Jeszcze innym alternatywnym
    // podejściem byłoby przechowywanie w jednym miejscu zapachów i sprawdzanie w każdej
    // iteracji, czy zwierze jest w zasięgu, złożoności operacyjnej rzędu O(A * S), gdzie
    // A - zwierzęta, S - zapachy.

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
