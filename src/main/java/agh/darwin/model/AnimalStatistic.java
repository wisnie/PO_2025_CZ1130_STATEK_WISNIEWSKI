package agh.darwin.model;

public class AnimalStatistic {
    private final int birthDay;
    private int plantsEaten;
    private int offspringCount;
    private  int deathDay = -1;

    public AnimalStatistic(int birthDay) {
        this.birthDay = birthDay;
    }

    public int getAge(int day) {
        if (deathDay == -1) {
            return day - birthDay;
        } else {
            return deathDay - birthDay;
        }
    }

    public int getOffspringCount() {
        return offspringCount;
    }

    public int getBirthDay() {
        return birthDay;
    }

    public int getDeathDay() {
        return deathDay;
    }

    public int getPlantsEaten() {
        return plantsEaten;
    }

    public void handleEvent(AnimalEvent animalEvent, int day) {
        switch (animalEvent) {
            case REPLACEMENT -> {}
            case PLANT_CONSUMPTION -> plantsEaten++;
            case REPRODUCTION -> offspringCount++;
            case DEATH -> deathDay = day;
        }
    }
}
