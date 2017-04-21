package dreamteam.carpooling.appl.PassengerBehaviours;

import jade.core.behaviours.OneShotBehaviour;

/**
 * Убрать водителя из списка в случае жёсткого отказа
 */
public class RemoveDriverFromList extends OneShotBehaviour {

    private PassengerFSMBehaviour myParentFSM = (PassengerFSMBehaviour) getParent();

    @Override
    public void action() {
        myParentFSM.suitableDrivers.removeIf(
                aid -> aid.getLocalName().equals(myParentFSM.driverToRemove)
        );
    }
}
