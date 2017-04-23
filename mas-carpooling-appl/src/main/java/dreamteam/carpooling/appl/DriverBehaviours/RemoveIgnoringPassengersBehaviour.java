package dreamteam.carpooling.appl.DriverBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import dreamteam.carpooling.appl.Util.Offer;
import jade.core.behaviours.OneShotBehaviour;

/**
 * Удаляет игнорирующих пассажиров из пула
 */
public class RemoveIgnoringPassengersBehaviour extends OneShotBehaviour {

    private DriverFSMBehaviour myParentFSM;

    @Override
    public void action() {
        myParentFSM = (DriverFSMBehaviour) getParent();

        // если кто-то нам отказал
        if (myParentFSM.agent_sent_Cancel!=null){

            for (Offer offer:
                 myParentFSM.myCitizenAgent.offersPool) {
                if (offer.id.toString().equals(myParentFSM.agent_sent_Cancel.toString())){
                    myParentFSM.myCitizenAgent.offersPool.remove(offer);
                    myParentFSM.myCitizenAgent.best_offer.remove(offer);
                 }

            }

        }

        // если кто-то нас проигнорил
        else if (myParentFSM.agents_didnt_answer != null){

            for (String didnt_answer_id:
                    myParentFSM.agents_didnt_answer) {
                for (Offer offer:
                        myParentFSM.myCitizenAgent.offersPool) {

                    if (offer.id.toString().equals(didnt_answer_id)){
                        myParentFSM.myCitizenAgent.offersPool.remove(offer);
                        myParentFSM.myCitizenAgent.best_offer.remove(offer);
                    }
                }
            }
        }
        else {
            CitizenAgent.logger.error("отказали/не дождались, но пул отказов пустой");
        }
    }
}
