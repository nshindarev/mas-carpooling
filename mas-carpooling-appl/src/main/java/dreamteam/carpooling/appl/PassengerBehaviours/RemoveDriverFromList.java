package dreamteam.carpooling.appl.PassengerBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import jade.core.behaviours.OneShotBehaviour;

/**
 * Убрать водителя из списка в случае жёсткого отказа
 */
public class RemoveDriverFromList extends OneShotBehaviour {

    private PassengerFSMBehaviour myParentFSM;

    @Override
    public void action() {
        myParentFSM = (PassengerFSMBehaviour) getParent();
        CitizenAgent.logger.info("{} pwned by {}", myAgent.getLocalName(), myParentFSM.driverToRemove);
        myParentFSM.suitableDrivers.removeIf(
                aid -> aid.getLocalName().equals(myParentFSM.driverToRemove)
        );
    }
}
