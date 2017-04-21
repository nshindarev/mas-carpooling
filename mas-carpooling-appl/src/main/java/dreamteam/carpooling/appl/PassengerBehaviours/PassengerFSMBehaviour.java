package dreamteam.carpooling.appl.PassengerBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import dreamteam.carpooling.appl.Util.Conversation;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.ReceiverBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.LinkedList;
import java.util.List;


/**
 * Сценарий для роли пассажира
 */
public class PassengerFSMBehaviour extends FSMBehaviour {

    public String currentIterationID;
    public List<AID> suitableDrivers = new LinkedList<>();
    public String driverToRemove;
    public ACLMessage acceptedProposal;

    private CitizenAgent myCitizenAgent = (CitizenAgent) getAgent();

    private final String SEARCH_DRIVERS_STATE                  = "Search drivers";
    private final String SEND_PROPOSALS_STATE                  = "Send proposals";
    private final String WAIT_FOR_ANSWERS_TO_PROPOSE_STATE     = "Wait for answers to PROPOSE";

    private final String RAISE_OFFER_PRICE_STATE               = "Raise offer price";
    private final String REMOVE_DRIVER_STATE                   = "Remove driver from list";

    private final String SEND_AGREE_TO_ACCEPTED_PROPOSAL_STATE = "Send agree to accepted proposal";
    private final String TRANSACTION_CONFIRMATION_STATE        = "Wait for transaction confirmation";
    private final String SEND_CANCEL_STATE                     = "Send CANCEL to other drivers";

    public static final int POSITIVE_CONDITION = 1;
    public static final int NEGATIVE_CONDITION = 0;
    public static final int FORCE_REJECT = 0;

    public PassengerFSMBehaviour(Agent a) {
        super(a);

        myCitizenAgent.setPrice(Conversation.START_PRICE);

        // Регистрируем состояния
        registerFirstState(new SearchDriversOffersInYPBehaviour(), SEARCH_DRIVERS_STATE);
        registerState(new SendProposalsBehaviour(), SEND_PROPOSALS_STATE);
        registerState(new ProposalsAnswersReceiverBehaviour(a, Conversation.REPLY_TIME), WAIT_FOR_ANSWERS_TO_PROPOSE_STATE);
        registerState(new RaiseOfferPriceBehaviour(), RAISE_OFFER_PRICE_STATE);
        registerState(new RemoveDriverFromList(), REMOVE_DRIVER_STATE);
        registerState(new SendAgreeToAcceptedProposalBehaviour(), SEND_AGREE_TO_ACCEPTED_PROPOSAL_STATE);
        registerState(new TransactionConfirmationReceiverBehaviour(a, Conversation.REPLY_TIME), TRANSACTION_CONFIRMATION_STATE);
        registerLastState(new SendCancelBehaviour(), SEND_CANCEL_STATE);

        // Регистрируем переходы
        registerDefaultTransition(SEARCH_DRIVERS_STATE, SEND_PROPOSALS_STATE);
        registerDefaultTransition(SEND_PROPOSALS_STATE, WAIT_FOR_ANSWERS_TO_PROPOSE_STATE);

        registerTransition(WAIT_FOR_ANSWERS_TO_PROPOSE_STATE, RAISE_OFFER_PRICE_STATE, NEGATIVE_CONDITION);
        registerDefaultTransition(RAISE_OFFER_PRICE_STATE, SEARCH_DRIVERS_STATE);

        registerTransition(WAIT_FOR_ANSWERS_TO_PROPOSE_STATE, REMOVE_DRIVER_STATE, FORCE_REJECT);
        registerDefaultTransition(REMOVE_DRIVER_STATE, WAIT_FOR_ANSWERS_TO_PROPOSE_STATE);

        registerTransition(WAIT_FOR_ANSWERS_TO_PROPOSE_STATE, SEND_AGREE_TO_ACCEPTED_PROPOSAL_STATE, POSITIVE_CONDITION);
        registerDefaultTransition(SEND_AGREE_TO_ACCEPTED_PROPOSAL_STATE, TRANSACTION_CONFIRMATION_STATE);

        registerTransition(TRANSACTION_CONFIRMATION_STATE, WAIT_FOR_ANSWERS_TO_PROPOSE_STATE, NEGATIVE_CONDITION);
        registerTransition(TRANSACTION_CONFIRMATION_STATE, SEND_CANCEL_STATE, POSITIVE_CONDITION);

    }

}
