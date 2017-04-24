package dreamteam.carpooling.appl.DriverBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import dreamteam.carpooling.appl.Util.Conversation;
import dreamteam.carpooling.appl.Util.Offer;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.LinkedList;
import java.util.List;

/**
 * Сценарий для роли водителя
 */
public class DriverFSMBehaviour extends FSMBehaviour {

    public CitizenAgent myCitizenAgent; // Наш агент

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

    /**
     *   поля для различных состояний
     */
    public Offer offerToAdd;
    public AID agent_sent_Cancel;
    public List<String> agents_didnt_answer;



    public DriverFSMBehaviour(Agent a) {
        super(a);


        myCitizenAgent = (CitizenAgent) myAgent;
        myCitizenAgent.setPrice(Conversation.START_PRICE);

        // Регистрируем состояния
        registerFirstState(new RegisterInYPBehaviour(), REGISTER_IN_YP_STATE);
        registerState(new ProposalsReceiverBehaviour(a, -1), WAIT_FOR_PROPOSALS_STATE);
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
                WAIT_FOR_PROPOSALS_STATE, // TODO: проверить корректность обнуления ресивера
                new String[] {WAIT_FOR_PROPOSALS_STATE}

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
                (myCitizenAgent.offersPool.size()>0)?FIND_BEST_OFFER_STATE:WAIT_FOR_PROPOSALS_STATE,
                new String[]{ WAIT_FOR_AGREE_STATE }
        );

    }

    public List<String> getPassengersList (){
        List<String> to_return = new LinkedList<>();
        for (Offer best_offer:
                myCitizenAgent.best_offer){
            to_return.add(best_offer.message.getSender().getLocalName());
        }
        return to_return;
    }

}
