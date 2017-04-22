package dreamteam.carpooling.appl.DriverBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import dreamteam.carpooling.appl.Util.Conversation;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;

/**
 * Сценарий для роли водителя
 */
public class DriverFSMBehaviour extends FSMBehaviour {

    private CitizenAgent myCitizenAgent; // Наш агент

    private final String REGISTER_IN_YP_STATE             = "Register in YP";
    private final String WAIT_FOR_PROPOSALS_STATE         = "Wait for proposals";
    private final String PUT_IN_POOL_STATE                = "Put proposals to pool";
    private final String FIND_BEST_OFFER_STATE            = "Find best offer";

    private final String REJECT_ALL_PROPOSALS_STATE       = "REJECT all proposals";
    private final String ACCEPT_BEST_PROPOSALS_STATE      = "ACCEPT best proposals";
    private final String WAIT_FOR_AGREE_STATE             = "Wait for AGREE of best proposals";

    private final String REMOVE_IGNORING_PASSENGERS_STATE = "Remove ignoring passengers from pool";

    private final String CONFIRM_TRANSACTION_STATE        = "Confirm transaction";

    public static final int POSITIVE_CONDITION = 1;
    public static final int NEGATIVE_CONDITION = 0;

    // TODO: перенести по максимуму логику водителя из класса CitizenAgent

    public DriverFSMBehaviour(Agent a) {
        super(a);

        // Регистрируем состояния
        registerFirstState(new RegisterInYPBehaviour(), REGISTER_IN_YP_STATE); // TODO: не забудь убрать это из класса Citizen! А то он зарегается дважды
        registerState(new ProposalsReceiverBehaviour(a, -1), WAIT_FOR_PROPOSALS_STATE); // TODO: второй параметр конструктора = тайм-аут на получение сообщений
        registerState(new PutInPoolBehaviour(), PUT_IN_POOL_STATE);
        registerState(new FindBestOfferBehaviour(), FIND_BEST_OFFER_STATE);
        registerState(new RejectAllProposalsBehaviour(), REJECT_ALL_PROPOSALS_STATE);
        registerState(new AcceptBestProposalsBehaviour(), ACCEPT_BEST_PROPOSALS_STATE);
        registerState(new WaitForAgreeBehaviour(a, Conversation.REPLY_TIME), WAIT_FOR_AGREE_STATE);
        registerState(new RemoveIgnoringPassengersBehaviour(), REMOVE_IGNORING_PASSENGERS_STATE);
        registerLastState(new ConfirmTransactionState(), CONFIRM_TRANSACTION_STATE);

        // Регистреруем переходы
        registerDefaultTransition(
                REGISTER_IN_YP_STATE,
                WAIT_FOR_PROPOSALS_STATE
        );
        registerDefaultTransition(
                WAIT_FOR_PROPOSALS_STATE,
                PUT_IN_POOL_STATE
        );
        registerDefaultTransition(
                PUT_IN_POOL_STATE,
                FIND_BEST_OFFER_STATE
        );

        registerTransition(
                FIND_BEST_OFFER_STATE,
                REJECT_ALL_PROPOSALS_STATE,
                POSITIVE_CONDITION
        );
        registerDefaultTransition(
                REJECT_ALL_PROPOSALS_STATE,
                WAIT_FOR_PROPOSALS_STATE // TODO: тут ещё возможно надо будет делать reset() ресивера, хз
        );

        registerTransition(
                FIND_BEST_OFFER_STATE,
                ACCEPT_BEST_PROPOSALS_STATE,
                NEGATIVE_CONDITION
        );

        registerDefaultTransition(
                ACCEPT_BEST_PROPOSALS_STATE,
                WAIT_FOR_AGREE_STATE
        );

        registerTransition(
                WAIT_FOR_AGREE_STATE,
                CONFIRM_TRANSACTION_STATE,
                POSITIVE_CONDITION
        );
        registerTransition(
                WAIT_FOR_AGREE_STATE,
                REMOVE_IGNORING_PASSENGERS_STATE,
                NEGATIVE_CONDITION
        );

        registerDefaultTransition(
                REMOVE_IGNORING_PASSENGERS_STATE,
                FIND_BEST_OFFER_STATE,
                new String[]{ WAIT_FOR_AGREE_STATE }
        );

    }

}
