package dreamteam.carpooling.appl.PassengerBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

/**
 * Обработка предложений от водителей
 */
public class HandleDriversOffersBehaviour extends TickerBehaviour {

    public HandleDriversOffersBehaviour(Agent a, long period) {
        super(a, period);
    }

    @Override
    protected void onTick() {
        CitizenAgent.logger.info("{} is handling a proposal from driver", myAgent.getAID().getName());
    }
}
