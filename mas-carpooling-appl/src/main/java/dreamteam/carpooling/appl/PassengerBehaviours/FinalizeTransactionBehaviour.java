package dreamteam.carpooling.appl.PassengerBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import dreamteam.carpooling.appl.Util.Conversation;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.List;

/**
 * Окончательные действия пассажира после подтверждения от водителя.
 * В частности, он посылает всем остальным отмену
 */
public class FinalizeTransactionBehaviour extends OneShotBehaviour {

    private PassengerFSMBehaviour myParentFSM;

    @Override
    public void action() {

        myParentFSM = (PassengerFSMBehaviour) getParent();

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
        CitizenAgent.logger.info("{} goes with driver {} for price {}",
                myAgent.getLocalName(),
                myParentFSM.acceptedProposal.getSender().getLocalName(),
                ((CitizenAgent) myAgent).getPrice());

    }
}
