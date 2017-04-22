package dreamteam.carpooling.appl.PassengerBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import dreamteam.carpooling.appl.Util.Conversation;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Получение подтверждения транзакции (финальное согласие водителя)
 */
public class TransactionConfirmationReceiverBehaviour extends SimpleBehaviour {
    private long timeOut, wakeupTime;
    private boolean finished;

    private PassengerFSMBehaviour myParentFSM;

    private ACLMessage msg;
    private int returnCode;

    MessageTemplate template;

    public ACLMessage getMessage() { return msg; }

    public TransactionConfirmationReceiverBehaviour(Agent a, int millis) {
        super(a);
        timeOut = millis;
    }

    public void onStart() {
        myParentFSM = (PassengerFSMBehaviour) getParent();
        wakeupTime = (timeOut<0 ? Long.MAX_VALUE
                :System.currentTimeMillis() + timeOut);

        // Ждём подтверждения от конкретного водителя
        template = new MessageTemplate((MessageTemplate.MatchExpression) aclMessage ->
                (aclMessage.getPerformative() == (ACLMessage.AGREE) &&
                 aclMessage.getSender().getLocalName().equals(myParentFSM.acceptedProposal.getSender().getLocalName())));
    }

    @Override
    public boolean done () {
        return finished;
    }

    @Override
    public void action()
    {
        msg = myAgent.receive(template);

        if( msg != null) {
            finished = true;
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
            /*CitizenAgent.logger.info("{} did not receive AGREE from {}",
                    myAgent.getLocalName(),
                    myParentFSM.acceptedProposal.getSender().getLocalName());*/
        }
        returnCode = (m == null) ?
                PassengerFSMBehaviour.NEGATIVE_CONDITION : // Время вышло, подтверждение не пришло
                PassengerFSMBehaviour.POSITIVE_CONDITION;  // Подтверждение пришло
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
