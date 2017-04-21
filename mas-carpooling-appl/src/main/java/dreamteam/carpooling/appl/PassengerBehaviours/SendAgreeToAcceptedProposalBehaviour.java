package dreamteam.carpooling.appl.PassengerBehaviours;

import dreamteam.carpooling.appl.Util.Conversation;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Calendar;

/**
 * Отправка согласия на принятый PROPOSAL
 */
public class SendAgreeToAcceptedProposalBehaviour extends OneShotBehaviour {

    private PassengerFSMBehaviour myParentFSM = (PassengerFSMBehaviour) getParent();

    @Override
    public void action() {

        ACLMessage reply = myParentFSM.acceptedProposal.createReply();
        reply.setPerformative(ACLMessage.AGREE);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, Conversation.REPLY_TIME);
        reply.setReplyByDate(calendar.getTime());

        reply.setContent(Conversation.CONTENT_STUB);

        myParentFSM.getAgent().send(reply);
    }
}
