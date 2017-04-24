package dreamteam.carpooling.appl.DriverBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import dreamteam.carpooling.appl.Util.Conversation;
import dreamteam.carpooling.appl.Util.Offer;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Послать ответ с согласием на лучшие заявки
 */
public class AcceptBestProposalsBehaviour extends OneShotBehaviour {


    private DriverFSMBehaviour myParentFSM;

    private List<Offer> not_best_offer;

    @Override
    public void action() {
        not_best_offer = new LinkedList<>();
        myParentFSM = (DriverFSMBehaviour) getParent();

        // для каждого предложения из best_offers генерим ACCEPT
        for (Offer offer:
                myParentFSM.myCitizenAgent.getBestOffer()) {

            ACLMessage reply = offer.message.createReply();
            reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MILLISECOND, Conversation.REPLY_TIME);
            reply.setReplyByDate(calendar.getTime());

            reply.setContent(Conversation.CONTENT_STUB);

            myParentFSM.getAgent().send(reply);

            CitizenAgent.logger.debug("{} driver ACCEPTED proposal from {}",
                    myAgent.getLocalName(),
                    offer.message.getSender().getLocalName());
        }



        // убираем из пула все лучшие предложения, на которые уже ответили
        for (Offer offer:
                myParentFSM.myCitizenAgent.getOffersPool()) {
            not_best_offer.add(offer);
        }
        for (Offer best_offer:
             myParentFSM.myCitizenAgent.getBestOffer()) {
            for (Offer simple_offer:
                 not_best_offer) {

                if (best_offer.id.getName().equals(simple_offer.id.getName())){
                    not_best_offer.remove(simple_offer);
                }

            }

        }

        // для каждого из оставшихся предложений из пула генерим REJECT
        for (Offer offer:
                not_best_offer) {

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
