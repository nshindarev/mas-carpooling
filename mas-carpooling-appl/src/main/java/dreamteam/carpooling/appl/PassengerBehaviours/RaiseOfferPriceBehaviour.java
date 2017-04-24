package dreamteam.carpooling.appl.PassengerBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import dreamteam.carpooling.appl.Util.Conversation;
import jade.core.behaviours.OneShotBehaviour;

/**
 * Повышение цены предложения
 */
public class RaiseOfferPriceBehaviour extends OneShotBehaviour {

    @Override
    public void action() {
        CitizenAgent myCitizenAgent = (CitizenAgent) myAgent;
        myCitizenAgent.setPrice(myCitizenAgent.getPrice() + Conversation.PRICE_STEP);
        CitizenAgent.logger.trace("{} raises price to {}",
                myAgent.getLocalName(),
                myCitizenAgent.getPrice());
    }
}
