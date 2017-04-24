package dreamteam.carpooling.appl.DriverBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import dreamteam.carpooling.appl.PassengerBehaviours.PassengerFSMBehaviour;
import dreamteam.carpooling.appl.Util.Conversation;
import dreamteam.carpooling.appl.Util.Offer;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Calendar;

/**
 * Посылает отказ на все заявки в пуле
 */
public class RejectAllProposalsBehaviour extends OneShotBehaviour {

    private DriverFSMBehaviour myParentFSM;

    @Override
    public void action() {

        myParentFSM = (DriverFSMBehaviour) getParent();

        // для каждого предложения из пула генерим REJECT
        for (Offer offer:
             myParentFSM.myCitizenAgent.getOffersPool()) {

            ACLMessage reply = offer.message.createReply();
            reply.setPerformative(ACLMessage.REJECT_PROPOSAL);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MILLISECOND, Conversation.REPLY_TIME);
            reply.setReplyByDate(calendar.getTime());

            reply.setContent(Conversation.CONTENT_STUB);

            myParentFSM.getAgent().send(reply);

            CitizenAgent.logger.debug("{} driver REJECTED proposal from {}",
                    myAgent.getLocalName(),
                    offer.message.getSender().getLocalName());

        }

        myParentFSM.myCitizenAgent.offersPool.clear();
    }
}
