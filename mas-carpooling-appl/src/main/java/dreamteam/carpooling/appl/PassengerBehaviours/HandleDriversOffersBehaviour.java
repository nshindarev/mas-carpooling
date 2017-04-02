package dreamteam.carpooling.appl.PassengerBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import jade.core.behaviours.CyclicBehaviour;

/**
 * Обработка предложений от водителей
 */
public class HandleDriversOffersBehaviour extends CyclicBehaviour {
    @Override
    public void action() {
        CitizenAgent.logger.info("{} is handling a proposal from passenger", myAgent.getAID().getName());
    }
}
