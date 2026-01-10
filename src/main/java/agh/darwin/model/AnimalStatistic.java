package agh.darwin.model;

public class AnimalStatistic {
    private final int birthDay;
    private int plantsEaten;
    private int offspringCount;
    private int deathDay;

    public AnimalStatistic(int birthDay) {
        this.birthDay = birthDay;
    }

    public int getAge(int day) {
        return day - birthDay;
    }

    public int getOffspringCount() {
        return offspringCount;
    }

    public void handleEvent(AnimalEvent animalEvent, int day) {
        switch (animalEvent) {
            case PLANT_CONSUMPTION -> plantsEaten++;
            case REPRODUCTION -> offspringCount++;
            case DEATH -> deathDay = day;
        }
    }
}
