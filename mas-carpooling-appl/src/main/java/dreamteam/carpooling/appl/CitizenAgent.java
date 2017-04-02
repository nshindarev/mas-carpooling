package dreamteam.carpooling.appl;

import dreamteam.carpooling.appl.DriverBehaviours.HandlePassengersOffersBehaviour;
import dreamteam.carpooling.appl.DriverBehaviours.RegisterInYPBehaviour;
import dreamteam.carpooling.appl.PassengerBehaviours.HandleDriversOffersBehaviour;

import jade.core.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Агент - житель города. Каждый агент может играть две роли: пассажира и водителя.
 * В какой-то момент он должен определиться, кем он будет в итоге.
 */
public class CitizenAgent extends Agent {

    private Car car = null;

    public static final Logger logger = LoggerFactory.getLogger(CitizenAgent.class);

    @Override
    protected void setup() {
        super.setup();
        logger.info("Hello! Agent {} is ready.", getAID().getName());

        // Смотрим, есть ли машина
        Object[] args = getArguments();
        if (args != null && args.length == 2) {
            this.car = new Car(Byte.parseByte(args[0].toString()), Float.parseFloat(args[1].toString()));
            logger.info("Agent {} has car with capacity {} and cost per km {}",
                    getAID().getName(), car.getCapacity(), car.getCostPerKilometer());
        }

        // Поведения для роли водителя
        if (car != null) {
            addBehaviour(new RegisterInYPBehaviour());
            addBehaviour(new HandlePassengersOffersBehaviour(this, 3000));
        }

        // Поведения для роли пассажира
        addBehaviour(new HandleDriversOffersBehaviour(this, 3000));
    }
}
