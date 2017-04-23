package dreamteam.carpooling.appl.DriverBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import dreamteam.carpooling.appl.Util.Conversation;
import dreamteam.carpooling.appl.Util.Offer;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Calendar;

/**
 * Подтверждение транзакции
 */
public class ConfirmTransactionState extends OneShotBehaviour {

    private DriverFSMBehaviour myParentFSM;

    @Override
    public void action() {

        myParentFSM = (DriverFSMBehaviour) getParent();

        // для каждого предложения из best_offers генерим Agree

        for (Offer offer:
                myParentFSM.myCitizenAgent.best_offer) {

            ACLMessage reply = offer.message.createReply();
            reply.setPerformative(ACLMessage.AGREE);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MILLISECOND, Conversation.REPLY_TIME);
            reply.setReplyByDate(calendar.getTime());

            reply.setContent(Conversation.CONTENT_STUB);

            myParentFSM.getAgent().send(reply);

            CitizenAgent.logger.info("{} driver AGREED driving with proposal from {}",
                    myAgent.getLocalName(),
                    offer.message.getSender().getLocalName());

        }

        //TODO: удалиться из YP и отменить сценарий пассажира
        myParentFSM.myCitizenAgent.offersPool.clear();
        myParentFSM.myCitizenAgent.best_offer.clear();
    }
}
