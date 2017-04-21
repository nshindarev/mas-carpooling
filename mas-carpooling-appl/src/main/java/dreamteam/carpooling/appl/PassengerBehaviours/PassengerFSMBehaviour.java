package dreamteam.carpooling.appl.PassengerBehaviours;

import dreamteam.carpooling.appl.Util.Conversation;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.ReceiverBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


/**
 * Сценарий для роли пассажира
 */
public class PassengerFSMBehaviour extends FSMBehaviour {

    private final String SEARCH_DRIVERS_STATE                  = "Search drivers";
    private final String SEND_PROPOSALS_STATE                  = "Send proposals";
    private final String WAIT_FOR_ANSWERS_TO_PROPOSE_STATE     = "Wait for answers to PROPOSE";

    private final String RAISE_OFFER_PRICE_STATE               = "Raise offer price";
    private final String DECIDE_TO_BE_DRIVER_STATE             = "Decide to be a driver";

    private final String SEND_AGREE_TO_ACCEPTED_PROPOSAL_STATE = "Send agree to accepted proposal";
    private final String TRANSACTION_CONFIRMATION_STATE        = "Wait for transaction confirmation";
    private final String SEND_CANCEL_STATE                     = "Send CANCEL to other drivers";

    private final int POSITIVE_CONDITON = 1;
    private final int NEGATIVE_CONDITON = 0;

    MessageTemplate proposalsAnswersTemplate = new MessageTemplate((MessageTemplate.MatchExpression) aclMessage ->
           (aclMessage.getPerformative() == (ACLMessage.ACCEPT_PROPOSAL) ||
            aclMessage.getPerformative() == (ACLMessage.REJECT_PROPOSAL)));
            // TODO: добавить счётчик ответов
            // TODO: текущий ID беседы?

    // TODO: разбораться, как делать receive
    private ReceiverBehaviour.Handle proposalsAnswersHandle = new ReceiverBehaviour.Handle() {
        @Override
        public ACLMessage getMessage() throws ReceiverBehaviour.TimedOut, ReceiverBehaviour.NotYetReady {
            return null;
        }
    };

    private final Behaviour proposalsAnswersReceiverBehaviour =
            new ReceiverBehaviour(myAgent, proposalsAnswersHandle, Conversation.REPLY_TIME, proposalsAnswersTemplate);

    // TODO: шаблон и прочая муть
    private final ReceiverBehaviour transactionConfirmationReceiverBehaviour = null;

    public PassengerFSMBehaviour(Agent a) {
        super(a);

        // Регистрируем состояния
        registerFirstState(new SearchDriversOffersInYPBehaviour(), SEARCH_DRIVERS_STATE);
        registerState(new SendProposalsBehaviour(), SEND_PROPOSALS_STATE);
        registerState(proposalsAnswersReceiverBehaviour, WAIT_FOR_ANSWERS_TO_PROPOSE_STATE);
        registerState(new RaiseOfferPriceBehaviour(), RAISE_OFFER_PRICE_STATE);
        registerLastState(new DecideToBeADriverBehaviour(), DECIDE_TO_BE_DRIVER_STATE);
        registerState(new SendAgreeToAcceptedProposalBehaviour(), SEND_AGREE_TO_ACCEPTED_PROPOSAL_STATE);
        registerState(transactionConfirmationReceiverBehaviour, TRANSACTION_CONFIRMATION_STATE);
        registerLastState(new SendCancelBehaviour(), SEND_CANCEL_STATE);

        // Регистрируем переходы
        registerDefaultTransition(SEARCH_DRIVERS_STATE, SEND_PROPOSALS_STATE);
        registerDefaultTransition(SEND_PROPOSALS_STATE, WAIT_FOR_ANSWERS_TO_PROPOSE_STATE);

        registerTransition(WAIT_FOR_ANSWERS_TO_PROPOSE_STATE, RAISE_OFFER_PRICE_STATE, NEGATIVE_CONDITON);

        registerTransition(RAISE_OFFER_PRICE_STATE, DECIDE_TO_BE_DRIVER_STATE,  NEGATIVE_CONDITON);
        registerTransition(RAISE_OFFER_PRICE_STATE, SEARCH_DRIVERS_STATE, POSITIVE_CONDITON);

        registerTransition(WAIT_FOR_ANSWERS_TO_PROPOSE_STATE, SEND_AGREE_TO_ACCEPTED_PROPOSAL_STATE, POSITIVE_CONDITON);

        registerDefaultTransition(SEND_AGREE_TO_ACCEPTED_PROPOSAL_STATE, TRANSACTION_CONFIRMATION_STATE);

        registerTransition(TRANSACTION_CONFIRMATION_STATE, WAIT_FOR_ANSWERS_TO_PROPOSE_STATE, NEGATIVE_CONDITON);
        registerTransition(TRANSACTION_CONFIRMATION_STATE, SEND_CANCEL_STATE,                 POSITIVE_CONDITON);

    }
}
