package dreamteam.carpooling.appl.DriverBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import dreamteam.carpooling.appl.Util.Offer;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.LinkedList;
import java.util.List;

/**
 * Получение предложений от пассажиров
 */
public class WaitForAgreeBehaviour extends SimpleBehaviour {

    private List<String> ids_to_wait = new LinkedList<>();
    private long timeOut, wakeupTime;
    private boolean finished;

    private DriverFSMBehaviour myParentFSM; // Экземпляр конечного автомата водителя

    private ACLMessage msg;
    private int messagesReceived;

    MessageTemplate template;
    private int returnCode;

    public ACLMessage getMessage() { return msg; }

    public WaitForAgreeBehaviour(Agent a, int millis) {
        super(a);
        timeOut = millis;
    }

    public void onStart() {

        myParentFSM = (DriverFSMBehaviour) getParent();

        for(Offer best_offer:
                myParentFSM.myCitizenAgent.best_offer) {
            ids_to_wait.add(best_offer.id.toString());
        }

        messagesReceived = 0;
        myParentFSM = (DriverFSMBehaviour) getParent();
        template = new MessageTemplate(aclMessage ->
                        aclMessage.getPerformative() == (ACLMessage.AGREE)||
                        aclMessage.getPerformative() == (ACLMessage.CANCEL)&&
                        ids_to_wait.contains(aclMessage.getSender())); // Шаблон для предложений

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
        returnCode = DriverFSMBehaviour.NEGATIVE_CONDITION;

        msg = myAgent.receive(template);

        if( msg != null) {
            messagesReceived++;

            for (String id:                                                                 //... помечаем, что от этого агента сообщение уже получено
                   ids_to_wait)
                if (msg.getSender().toString().equals(id)){
                    ids_to_wait.remove(id);
                }

            CitizenAgent.logger.info("{} has {} received messages of {}",
                    myAgent.getLocalName(),
                    messagesReceived,
                    myParentFSM.myCitizenAgent.best_offer.size());

            // Смотрим, все ли ответили
            finished = messagesReceived == myParentFSM.myCitizenAgent.best_offer.size();
            handle( msg );
            return;

        }
        long dt = wakeupTime - System.currentTimeMillis();
        if ( dt > 0 )
            block(dt);

        else if (!finished){
            finished = true;
            handle( msg );
        }
    }

    public void handle(ACLMessage m) {

        if (m == null) {

            // Время вышло, не все ответы пришли
            returnCode = DriverFSMBehaviour.NEGATIVE_CONDITION;
            CitizenAgent.logger.info("... Time is up, received {} of {} AGREES", messagesReceived, myParentFSM.myCitizenAgent.best_offer.size() );

            /**
             *  время вышло, запоминаем молчунов и ливаем
             */

            myParentFSM.agents_didnt_answer = this.ids_to_wait;
            return;

        } else {
           if (m.getPerformative() == ACLMessage.CANCEL){

               //кто-то из агентов отказался, запоминаем его и ливаем

               myParentFSM.agent_sent_Cancel = m.getSender();
               returnCode = DriverFSMBehaviour.NEGATIVE_CONDITION;
               CitizenAgent.logger.info("Agent {} CANCELED his query", m.getSender());
               return;
           }
           else { // Performative = AGREE

               if (finished) {
                   returnCode = DriverFSMBehaviour.POSITIVE_CONDITION;                      //... все ответы пришли и последний тоже AGREE
                   return;
               }
           }
        }
    }

    public void reset() {

        messagesReceived = 0;
        ids_to_wait = new LinkedList<>();
        msg = null;
        finished = false;

        //TODO: здесь может быть косяк
        myParentFSM.agent_sent_Cancel = null;
        myParentFSM.agents_didnt_answer = null;

        super.reset();
    }

    public void reset(int dt) {
        timeOut= dt;
        reset();
    }

    @Override
    public int onEnd() {
        // TODO: POSITIVE_CONDITION - все прислали, NEGATIVE_CONDITION - кто-то не прислал
        returnCode = DriverFSMBehaviour.POSITIVE_CONDITION;
        returnCode = DriverFSMBehaviour.NEGATIVE_CONDITION;
        return returnCode;
    }
}

