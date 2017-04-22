package dreamteam.carpooling.appl.DriverBehaviours;

import jade.core.behaviours.OneShotBehaviour;

/**
 * Подтверждение транзакции
 */
public class ConfirmTransactionState extends OneShotBehaviour {
    @Override
    public void action() {
        // Можно получить здесь экземпляр автомата водителя, см. пример в ProposalsReceiver
        // TODO: здесь надо послать всем выбранным пассажирам AGREE, а также не забыть удалиться из YP и отменить сценарий пассажира
    }
}
