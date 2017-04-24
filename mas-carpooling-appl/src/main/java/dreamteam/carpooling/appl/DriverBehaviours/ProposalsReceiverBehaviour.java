package dreamteam.carpooling.appl.DriverBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import dreamteam.carpooling.appl.Util.Offer;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.LinkedList;

/**
 * Получение предложений от пассажиров
 */
public class ProposalsReceiverBehaviour extends SimpleBehaviour {

    private long timeOut, wakeupTime;
    private boolean finished;

    private DriverFSMBehaviour myParentFSM; // Экземпляр конечного автомата водителя

    private ACLMessage msg;
    private int messagesReceived;

    MessageTemplate template;

    public ACLMessage getMessage() { return msg; }

    public ProposalsReceiverBehaviour(Agent a, int millis) {
        super(a);
        timeOut = millis;
    }

    public void onStart() {

        messagesReceived = 0;
        myParentFSM = (DriverFSMBehaviour) getParent();
        myParentFSM.offerToAdd = new LinkedList<>();
        template = new MessageTemplate((MessageTemplate.MatchExpression) aclMessage ->
                aclMessage.getPerformative() == (ACLMessage.PROPOSE)); // Шаблон для предложений
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

        if( msg != null) {
            messagesReceived++;
            if(messagesReceived>1){
                myParentFSM.myCitizenAgent.logger.trace("{} received >1 messages", myParentFSM.myCitizenAgent.getLocalName());
            }
            finished = true;
            handle( msg );


            CitizenAgent.logger.error("{} has {} received messages",
                    myAgent.getLocalName(),
                    messagesReceived);


            timeOut = 10000;
            wakeupTime = System.currentTimeMillis() + timeOut;


            if(myParentFSM.offerToAdd == null && finished){
                CitizenAgent.logger.warn("didn't add any offer to {}", myParentFSM.myCitizenAgent.getLocalName());
            }
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
            CitizenAgent.logger.error("{} received null Message", myParentFSM.myCitizenAgent.getLocalName());
        } else {
            myParentFSM.offerToAdd.add(new Offer(m));
        }

    }

    public void reset() {
        msg = null;
        finished = false;
        timeOut = -1;
        super.reset();
    }

    public void reset(int dt) {
        timeOut= dt;
        reset();
    }
}

