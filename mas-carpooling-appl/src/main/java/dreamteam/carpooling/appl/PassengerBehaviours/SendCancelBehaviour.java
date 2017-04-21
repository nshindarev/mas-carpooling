package dreamteam.carpooling.appl.PassengerBehaviours;

import dreamteam.carpooling.appl.Util.Conversation;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.List;

/**
 * Когда пассажир получил подтверждение от водителя, он посылает всем остальным отмену
 */
public class SendCancelBehaviour extends OneShotBehaviour {

    private final PassengerFSMBehaviour myParentFSM = (PassengerFSMBehaviour) getParent();

    @Override
    public void action() {
        List<AID> suitableDrivers = myParentFSM.suitableDrivers;
        String id = myParentFSM.currentIterationID;

        suitableDrivers.forEach(aid -> {

            if (!aid.getLocalName().equals(myParentFSM.acceptedProposal.getSender().getLocalName())) {

                ACLMessage cancelMsg = new ACLMessage(ACLMessage.CANCEL ); // Отмена предложения
                cancelMsg.addReceiver(aid);
                cancelMsg.setConversationId(id);

                cancelMsg.setOntology(Conversation.CARPOOLING_ONTOLOGY);
                cancelMsg.setContent(Conversation.CONTENT_STUB);                                                                // - цену

                myAgent.send(cancelMsg);

            }
        });

        // TODO: выводить лог?

    }
}
