package dreamteam.carpooling.appl.DriverBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import jade.core.behaviours.CyclicBehaviour;

/**
 * Обработка предложений от пассажиров
 */
public class HandlePassengersOffersBehaviour extends CyclicBehaviour {
    @Override
    public void action() {
        CitizenAgent.logger.info("{} is handling a proposal from passenger", myAgent.getAID().getName());
    }
}
