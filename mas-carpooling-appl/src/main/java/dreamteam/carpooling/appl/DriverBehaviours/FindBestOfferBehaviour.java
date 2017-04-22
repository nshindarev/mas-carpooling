package dreamteam.carpooling.appl.DriverBehaviours;

import jade.core.behaviours.OneShotBehaviour;

/**
 * Поиск лучшей комбинации заявок
 */
public class FindBestOfferBehaviour extends OneShotBehaviour {

    private int returnCode; // Условие перехода

    @Override
    public int onEnd() {
        // TODO: POSITIVE_CONDITION - неравенство выполняется, NEGATIVE_CONDITION - не выполняется
        returnCode = DriverFSMBehaviour.POSITIVE_CONDITION;
        returnCode = DriverFSMBehaviour.NEGATIVE_CONDITION;
        return returnCode;
    }

    @Override
    public void action() {
        // Можно получить здесь экземпляр автомата водителя, см. пример в ProposalsReceiver
    }
}
