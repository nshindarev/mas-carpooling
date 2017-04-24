package dreamteam.carpooling.appl.PassengerBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import dreamteam.carpooling.appl.Util.Conversation;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Повышение цены предложения
 */
public class RaiseOfferPriceBehaviour extends OneShotBehaviour {

    @Override
    public void action() {
        CitizenAgent myCitizenAgent = (CitizenAgent) myAgent;
        myCitizenAgent.setPrice(myCitizenAgent.getPrice() + Conversation.PRICE_STEP);
        if (myCitizenAgent.getPrice() > 3000) {
            /*ACLMessage stats = new ACLMessage(ACLMessage.INFORM);
            stats.addReceiver(new AID(Conversation.SECRETARY_NAME, AID.ISLOCALNAME));
            stats.setOntology(Conversation.CARPOOLING_ONTOLOGY);
            stats.setContent(Conversation.NOT_FOUND_DRIVER);
            myCitizenAgent.send(stats);*/
            myCitizenAgent.removeBehaviour(myCitizenAgent.myPassengerBehaviour);
        } else
            CitizenAgent.logger.info("{} raises price to {}",
                    myAgent.getLocalName(),
                    myCitizenAgent.getPrice());
    }
}
