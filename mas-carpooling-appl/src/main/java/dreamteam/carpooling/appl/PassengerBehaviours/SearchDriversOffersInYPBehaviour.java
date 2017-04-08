package dreamteam.carpooling.appl.PassengerBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

/**
 * Обработка предложений от водителей
 */
public class SearchDriversOffersInYPBehaviour extends TickerBehaviour {

    public SearchDriversOffersInYPBehaviour(Agent a, long period) {
        super(a, period);
    }

    @Override
    protected void onTick() {
        CitizenAgent.logger.info("{} is searching offers from drivers", myAgent.getAID().getName());
    }
}
