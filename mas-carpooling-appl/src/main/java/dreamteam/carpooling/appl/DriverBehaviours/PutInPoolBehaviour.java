package dreamteam.carpooling.appl.DriverBehaviours;

import jade.core.behaviours.OneShotBehaviour;

/**
 * Помещает предложение в пул заявок
 */
public class PutInPoolBehaviour extends OneShotBehaviour {

    private DriverFSMBehaviour myParentFSM;


    @Override
    public void action() {
        myParentFSM = (DriverFSMBehaviour) getParent();

        if(myParentFSM.offerToAdd != null){
            // обновляем полученное предложение от пассажира в пуле предложений
            myParentFSM.myCitizenAgent.updateOfferInPool(myParentFSM.offerToAdd);
        }
        else{

        }
    }
}
