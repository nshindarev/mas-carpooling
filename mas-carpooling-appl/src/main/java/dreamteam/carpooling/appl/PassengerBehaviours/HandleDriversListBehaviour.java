package dreamteam.carpooling.appl.PassengerBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import dreamteam.carpooling.appl.Util.Conversation;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.*;

/**
 * Общение с водителями.
 */
public class HandleDriversListBehaviour extends TickerBehaviour {

    private List<DriverState> driversStates = new ArrayList<>();
    private int iter = 0;

    private CitizenAgent myCitizenAgent = (CitizenAgent) myAgent;

    public HandleDriversListBehaviour(Agent a, long period) {
        super(a, period);
    }

    @Override
    protected void onTick() {

        /* Общий алгоритм:
        // Вкидываем всем предложения

        // Когда все ответили
            // Если какой-то водитель согласился на (минимальное) предложение
                // Соглашаемся тоже, посылаем отмену всем остальным
            // Иначе, для всех водителей:
                // Если можем поднять цену, поднимаем
                // Иначе отказываем

        // Если кто-то не отвечает слишком долго
            // Забиваем на него
        */


        boolean allAnswered = true;
        for (DriverState driverState : driversStates) {
            if (!driverState.answered) {
                allAnswered = false;
            }
        }

        // Когда все ответили
        if (allAnswered) {

            // Сортируем по возрастанию цен
            driversStates.sort((ds1, ds2) -> {
                if (ds1.curPrice > ds2.curPrice) {
                    return 1;
                } else if (ds2.curPrice > ds1.curPrice) {
                    return -1;
                } else {
                    return 0;
                }
            });

            // Смотрим, ответил ли кто-нибудь
            AID acceptedDriver = null;
            for (DriverState driversState : driversStates) {
                if (driversState.accepted) {
                    acceptedDriver = driversState.driver;
                    break;
                }
            }

            // Если какой-то водитель согласился на (минимальное) предложение
            if (acceptedDriver != null) {

                CitizenAgent.logger.info("{} goes with driver {}",
                        myAgent.getLocalName(), acceptedDriver.getLocalName());

                // Соглашаемся тоже, посылаем отмену всем остальным
                for (DriverState driverState : driversStates) {
                    ACLMessage endMessage = new ACLMessage(
                            driverState.driver == acceptedDriver ?
                                    ACLMessage.AGREE :
                                    ACLMessage.CANCEL);
                    endMessage.addReceiver(driverState.driver);
                    endMessage.setConversationId(driverState.conversationID);         // ... Устанавливаем правильный ID беседы, ...

                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MILLISECOND, Conversation.REPLY_TIME);
                    endMessage.setReplyByDate(calendar.getTime());                    // ... устанавливаем срок ответа.

                    endMessage.setOntology(Conversation.CARPOOLING_ONTOLOGY);
                    endMessage.setContent(Conversation.CONTENT_STUB);                                                                 // - начальную цену // TODO: цена

                    myCitizenAgent.send(endMessage);                                  // Отправляем сообщение

                    // TODO: что надо получить на AGREE?
                    // TODO: дождаться финального подтверждения и только тогда всех отменять и удалять
                    myCitizenAgent.removeBehaviour(this);

                }

            } else {

                for (DriverState driverState : driversStates) {
                    double newPrice = driverState.curPrice + Conversation.PRICE_STEP;
                    ACLMessage endMessage = new ACLMessage(
                            newPrice > 9999 ? // TODO: исп. getMaxPrice()
                                    ACLMessage.CANCEL :
                                    ACLMessage.PROPOSE);
                    endMessage.addReceiver(driverState.driver);
                    endMessage.setConversationId(driverState.conversationID);         // ... Устанавливаем правильный ID беседы, ...

                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MILLISECOND, Conversation.REPLY_TIME);
                    endMessage.setReplyByDate(calendar.getTime());                    // ... устанавливаем срок ответа.

                    endMessage.setOntology(Conversation.CARPOOLING_ONTOLOGY);
                    endMessage.setContent(newPrice > 9999 ? // TODO: исп. getMaxPrice()
                            Conversation.CONTENT_STUB :
                            Conversation.convertProposalDataToContent(
                                    myCitizenAgent.getStart(),
                                    myCitizenAgent.getFinish(),
                                    newPrice                                          // Записываем в контент новую цену
                            ));

                    myCitizenAgent.send(endMessage);                                  // Отправляем сообщение

                    if (newPrice > 9999) { // TODO: исп. getMaxPrice()
                        driverState.toBeDeleted = true;
                        driverState.answered = false;
                    } else {
                        driverState.curPrice = newPrice;
                        driverState.answered = false;
                        driverState.accepted = false;
                    }
                }
            }
        }

        // Начинаем беседу с новыми (ещё неопрошенными) водителями
        Iterator it = myCitizenAgent.suitableDrivers.iterator();
        while (it.hasNext()) {
            AID aid = (AID) it.next();
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
                    10.1));                                                                 // - начальную цену // TODO: цена

            myCitizenAgent.send(startConversationMessage);                                  // Отправляем сообщение

            driversStates.add(new DriverState(aid, 10.1, startConversationMessage.getConversationId())); // TODO: цена

            // Запускаем поведение, ждущее ответов данного водителя
            myCitizenAgent.addBehaviour(new CyclicBehaviour() {
                // Шаблон для фильтрации сообщений
                MessageTemplate.MatchExpression e = (MessageTemplate.MatchExpression) aclMessage ->
                       (aclMessage.getPerformative() == (ACLMessage.ACCEPT_PROPOSAL) ||
                        aclMessage.getPerformative() == (ACLMessage.REJECT_PROPOSAL)) &&
                        // TODO: добавить CANCEL?
                        // TODO: что после AGREE?
                        Objects.equals(aclMessage.getSender().getLocalName(), aid.getLocalName()) &&
                        aclMessage.getConversationId().equals(startConversationMessage.getConversationId());

                MessageTemplate template = new MessageTemplate(e);

                Date deadline = startConversationMessage.getReplyByDate();

                @Override
                public void action() {

                    ACLMessage msg = myCitizenAgent.receive(template);
                    if (msg == null) {
                        // Если ответ не пришёл вовремя, забываем про этого водителя
                        if (new Date().after(deadline)) {
                            driversStates.forEach(driverState -> {
                                if (Objects.equals(driverState.driver.getLocalName(), aid.getLocalName())) {
                                    driverState.toBeDeleted = true;
                                }
                            });
                            myCitizenAgent.removeBehaviour(this);
                        } else {
                            block(1000);
                        }
                    } else {
                        driversStates.forEach(driverState -> {
                            if (driverState.driver == aid) {
                                driverState.answered = true;
                                driverState.accepted = msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL;

                                Calendar calendar = Calendar.getInstance();
                                calendar.add(Calendar.MILLISECOND, Conversation.REPLY_TIME);
                                deadline = calendar.getTime(); // Новый срок ответа
                            }
                        });
                    }
                }
            });

            it.remove(); // TODO: проблема с поиском в YP из-за этой операции
        }

        driversStates.removeIf(driverState -> driverState.toBeDeleted);

        // Если долго нет ни одного предложения, агент идёт своим ходом
        if (driversStates.isEmpty()) {
            iter++;
        }

        if (iter > Conversation.MAX_ITER_WITH_NO_ANSWERS) {
            // TODO: вывод нужной информации

            CitizenAgent.logger.info("{} goes on his/her car or on foot", myAgent.getLocalName());
            myCitizenAgent.removeBehaviour(this);
        }

    }

    public class DriverState {
        public AID driver;
        public double curPrice;
        public boolean answered;
        public boolean accepted;
        public boolean toBeDeleted;
        public String conversationID;

        public DriverState(AID driver, double curPrice, String conversationID) {
            this.driver = driver;
            this.curPrice = curPrice;
            this.answered = false;
            this.accepted = false;
            this.conversationID = conversationID;
            this.toBeDeleted = false;
        }
    }

}

