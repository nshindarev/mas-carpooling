package dreamteam.carpooling.appl.PassengerBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import dreamteam.carpooling.appl.Util.Conversation;
import jade.core.behaviours.OneShotBehaviour;

/**
 * Повышение цены предложения
 */
public class RaiseOfferPriceBehaviour extends OneShotBehaviour {

    private CitizenAgent myCitizenAgent = (CitizenAgent) getAgent();

    @Override
    public void action() {
        myCitizenAgent.setPrice(myCitizenAgent.getPrice() + Conversation.PRICE_STEP);
    }
}
