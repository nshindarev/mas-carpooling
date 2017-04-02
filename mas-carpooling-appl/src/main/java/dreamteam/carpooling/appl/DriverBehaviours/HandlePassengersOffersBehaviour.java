package dreamteam.carpooling.appl.DriverBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

/**
 * Обработка предложений от пассажиров
 */
public class HandlePassengersOffersBehaviour extends TickerBehaviour {

    public HandlePassengersOffersBehaviour(Agent a, long period) {
        super(a, period);
    }

    @Override
    protected void onTick() {
        CitizenAgent.logger.info("{} is handling a proposal from passenger", myAgent.getAID().getName());
    }
}
