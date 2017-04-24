package dreamteam.carpooling.appl.DriverBehaviours;

import dreamteam.carpooling.appl.Util.Offer;
import jade.core.behaviours.OneShotBehaviour;

/**
 * Помещает предложение в пул заявок
 */
public class PutInPoolBehaviour extends OneShotBehaviour {

    private DriverFSMBehaviour myParentFSM;


    @Override
    public void action() {
        myParentFSM = (DriverFSMBehaviour) getParent();

        // обновляем полученное предложение от пассажира в пуле предложений
        if(myParentFSM.offerToAdd != null){
            if(myParentFSM.offerToAdd.size()>1){
                myParentFSM.myCitizenAgent.logger.trace(">1 offers received");
            }
            for (Offer offer:
                 myParentFSM.offerToAdd) {
                myParentFSM.myCitizenAgent.updateOfferInPool(offer);
            }

        }

        if(myParentFSM.myCitizenAgent.offersPool.size()==0){
            myParentFSM.myCitizenAgent.logger.error("offersPool пустой ");
        }
    }
}
