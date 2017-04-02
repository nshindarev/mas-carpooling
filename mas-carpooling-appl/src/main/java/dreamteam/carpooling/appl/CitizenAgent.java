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

    public static final Logger logger = LoggerFactory.getLogger(CitizenAgent.class);

    @Override
    protected void setup() {
        super.setup();
        logger.info("Hello! Agent {} is ready.", getAID().getName());

        // Поведения для роли водителя
        addBehaviour(new RegisterInYPBehaviour());
        addBehaviour(new HandlePassengersOffersBehaviour(this, 3000));

        // Поведения для роли пассажира
        addBehaviour(new HandleDriversOffersBehaviour(this, 3000));
    }
}
