package dreamteam.carpooling.appl.PassengerBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import dreamteam.carpooling.appl.Util.Conversation;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Получение ответов на предложения
 */
public class ProposalsAnswersReceiverBehaviour extends SimpleBehaviour {
    private long timeOut, wakeupTime;
    private boolean finished;

    private PassengerFSMBehaviour myParentFSM = (PassengerFSMBehaviour) getParent();
    private ACLMessage msg;
    private int returnCode;
    private int messagesReceived = 0;

    MessageTemplate template = new MessageTemplate((MessageTemplate.MatchExpression) aclMessage ->
            (aclMessage.getPerformative() == (ACLMessage.ACCEPT_PROPOSAL) ||
                    aclMessage.getPerformative() == (ACLMessage.REJECT_PROPOSAL)));

    public ACLMessage getMessage() { return msg; }

    public ProposalsAnswersReceiverBehaviour(Agent a, int millis) {
        super(a);
        timeOut = millis;
    }

    public void onStart() {
        wakeupTime = (timeOut<0 ? Long.MAX_VALUE
                :System.currentTimeMillis() + timeOut);
    }

    @Override
    public boolean done () {
        return finished;
    }

    @Override
    public void action()
    {
        msg = myAgent.receive(template);

        if( msg != null &&
                // Проверяем по ID
                msg.getConversationId().equals(myParentFSM.currentIterationID)) {
            messagesReceived++;
            // Смотрим, все ли ответили
            finished = messagesReceived == myParentFSM.suitableDrivers.size();
            handle( msg );
            return;
        }
        long dt = wakeupTime - System.currentTimeMillis();
        if ( dt > 0 )
            block(dt);
        else {
            finished = true;
            handle( msg );
        }
    }

    public void handle(ACLMessage m) {

        if (m == null) {
            // Время вышло, не все ответы пришли
            returnCode = PassengerFSMBehaviour.NEGATIVE_CONDITION;
            return;
        }

        if (m.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
            returnCode = PassengerFSMBehaviour.POSITIVE_CONDITION;
            myParentFSM.acceptedProposal = m;
        } else { // performative == REJECT_PROPOSAL
            if (msg.getContent().equals(Conversation.NO_SEATS)) {
                // TODO: проблема - при возвращении в это поведение сбросится счётчик ответов (но это неточно)
                messagesReceived--;
                returnCode = PassengerFSMBehaviour.FORCE_REJECT;
                myParentFSM.driverToRemove = msg.getSender().getLocalName();
            }
        }

    }

    @Override
    public int onEnd() {
        return returnCode;
    }

    public void reset() {
        msg = null;
        finished = false;
        super.reset();
    }

    public void reset(int dt) {
        timeOut= dt;
        reset();
    }
}
