package dreamteam.carpooling.appl.DriverBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Date;
import java.util.Random;

/**
 * Обработка предложений от пассажиров
 */
public class HandlePassengersOffersBehaviour extends CyclicBehaviour {

    private CitizenAgent myCitizenAgent = (CitizenAgent) myAgent;

    @Override
    public void action() {
        //CitizenAgent.logger.info("{} is handling a proposal from passenger", myAgent.getAID().getLocalName());

        // TODO: dummy stub, реализовать логику расчёта и принятия/отказа

        // Шаблон для фильтрации сообщений
        MessageTemplate template = new MessageTemplate(aclMessage ->
                aclMessage.getPerformative() == (ACLMessage.PROPOSE) // TODO: обработка AGREE, CANCEL
        );

        if (myCitizenAgent == null) {
            myCitizenAgent = (CitizenAgent) myAgent;
        }
        ACLMessage msg = myCitizenAgent.receive(template);

        if (msg == null) {
            block();
        } else {
            // TODO: смотрим контент, рассчитываем, подходит ли нам такая сделка
            ACLMessage reply = msg.createReply();
            double random = new Random().nextDouble();
            double divide = 0.5;
            reply.setPerformative(random < divide ?
                    ACLMessage.ACCEPT_PROPOSAL :
                    ACLMessage.REJECT_PROPOSAL);
            myAgent.send(reply);
            CitizenAgent.logger.info("{} sent {} to passenger {} with content {}",
                    myAgent.getLocalName(),
                    random < divide ? "agreement" : "reject",
                    msg.getSender().getLocalName(), msg.getContent());
        }
    }

}
