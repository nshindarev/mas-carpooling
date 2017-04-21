package dreamteam.carpooling.appl.PassengerBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import dreamteam.carpooling.appl.Util.Conversation;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Calendar;
import java.util.List;

/**
 * Рассылка предложений водителям
 */
public class SendProposalsBehaviour extends OneShotBehaviour {

    private CitizenAgent myCitizenAgent = (CitizenAgent) myAgent;

    @Override
    public void action() {
        // TODO: получаем текущую цену
        double price = 10.1;

        List<AID> suitableDrivers = myCitizenAgent.suitableDrivers;

        suitableDrivers.forEach(aid -> {
            ACLMessage startConversationMessage = new ACLMessage(ACLMessage.PROPOSE);       // Создаём предложение ...
            startConversationMessage.addReceiver(aid);                                      // ... для текущего водителя. ...
            startConversationMessage.setConversationId(Conversation.getNextID());           // ... Присваиваем ID беседе, ...

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MILLISECOND, Conversation.REPLY_TIME);
            startConversationMessage.setReplyByDate(calendar.getTime());                    // ... устанавливаем срок ответа, ...

            startConversationMessage.setOntology(Conversation.CARPOOLING_ONTOLOGY);
            startConversationMessage.setContent(Conversation.convertProposalDataToContent(  // ... устанавливаем содержимое:
                    myCitizenAgent.getStart(),                                              // - старт,
                    myCitizenAgent.getFinish(),                                             // - финиш,
                    price));                                                                // - начальную цену

            myCitizenAgent.send(startConversationMessage);                                  // Отправляем сообщение

        });
    }
}
