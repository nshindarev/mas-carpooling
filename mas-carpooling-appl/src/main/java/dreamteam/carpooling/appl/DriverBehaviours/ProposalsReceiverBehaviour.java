package dreamteam.carpooling.appl.DriverBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import dreamteam.carpooling.appl.Util.Conversation;
import dreamteam.carpooling.appl.Util.Offer;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.List;

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
        myParentFSM = (DriverFSMBehaviour) getParent();
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
            finished = true;
            handle( msg );
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
            CitizenAgent.logger.warn("{} received null Message", myParentFSM.myCitizenAgent.getLocalName());

            ACLMessage stats = new ACLMessage(ACLMessage.INFORM);
            stats.addReceiver(new AID(Conversation.SECRETARY_NAME, AID.ISLOCALNAME));
            stats.setOntology(Conversation.CARPOOLING_ONTOLOGY);

            List<String> route = myParentFSM.myCitizenAgent.getWayByMyCar().getVertexList();
            String routeMsg = "";
            for (String r : route) {
                routeMsg = routeMsg.concat(r).concat(",");
            }
            routeMsg = routeMsg.substring(0, routeMsg.length() - 1);

            stats.setContent(routeMsg);
            myAgent.send(stats);

            myParentFSM.myCitizenAgent.removeBehaviour(myParentFSM.myCitizenAgent.myDriverBehaviour);
        } else {
            myParentFSM.offerToAdd = new Offer(m);
        }

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

