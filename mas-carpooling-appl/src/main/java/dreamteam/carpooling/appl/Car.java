package dreamteam.carpooling.appl;

/**
 * Машина.
 * Может присутствовать или отсутствовать у агента.
 */
public class Car {

    private byte capacity;
    private float costPerKilometer;

    /**
     * @param capacity вместимость (сколько человек может поместиться, кроме водителя)
     * @param fuelConsumption стоимость проезда одного километра
     */
    public Car(byte capacity, float fuelConsumption) {
        this.capacity = capacity;
        this.costPerKilometer = fuelConsumption;
    }

    /**
     * Вместимость (сколько человек может поместиться, кроме водителя)
     * @return вместимость
     */
    public byte getCapacity() {
        return capacity;
    }

    /**
     * Стоимость проезда одного километра
     * @return стоимость проезда одного километра в условных кредитах
     */
    public float getCostPerKilometer() {
        return costPerKilometer;
    }
}
