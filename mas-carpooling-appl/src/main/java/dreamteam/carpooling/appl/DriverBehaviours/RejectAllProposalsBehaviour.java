package dreamteam.carpooling.appl.DriverBehaviours;

import jade.core.behaviours.OneShotBehaviour;

/**
 * Посылает отказ на все заявки в пуле
 */
public class RejectAllProposalsBehaviour extends OneShotBehaviour {
    @Override
    public void action() {
        // Можно получить здесь экземпляр автомата водителя, см. пример в ProposalsReceiver
        // Также надо видимо ещё очистить пул заявок, а не только отправить REJECT, но это тебе виднее
    }
}
