package dreamteam.carpooling.appl.DriverBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import dreamteam.carpooling.appl.Util.MyWeightedEdge;
import dreamteam.carpooling.appl.Util.*;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import org.jgrapht.alg.DijkstraShortestPath;

import java.util.LinkedList;
import java.util.List;
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

    public HandlePassengersOffersBehaviour() {
    }

    @Override
    public void action() {

        /**
         * проверяем поле decided_to_drive у водителя:
         * --- if (true) -> отправляем сообщения :
         *                  если пассажир есть в best_offers списке ---> approve
         *                  если пассажира нет в best_offers        ---> no free place
         *     if (false) -> отправляем всем в списке               ---> more cash
         */
        //CitizenAgent.logger.info("{} is handling a proposal from passenger", myAgent.getAID().getLocalName());

        // TODO: dummy stub, реализовать логику расчёта и принятия/отказа

        // Шаблон для фильтрации сообщений
        MessageTemplate template = new MessageTemplate(aclMessage ->
                aclMessage.getPerformative() == (ACLMessage.PROPOSE) ||
                aclMessage.getPerformative() == (ACLMessage.AGREE)
                // TODO: обработка CANCEL?
        );

        if (myCitizenAgent == null) {
            myCitizenAgent = (CitizenAgent) myAgent;
        }
        ACLMessage msg = myCitizenAgent.receive(template);

        if (msg == null) {
            block();
        } else if (msg.getPerformative() == ACLMessage.AGREE) {
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.AGREE);
            reply.setContent(Conversation.CONTENT_STUB);
            if (new Random().nextDouble() < 0.3) myCitizenAgent.send(reply);
        } else {
            // TODO: смотрим контент, рассчитываем, подходит ли нам такая сделка
            ACLMessage reply = msg.createReply();

            double random = new Random().nextDouble();
            double divide = 0.3;
            reply.setPerformative(random < divide ?
                    ACLMessage.ACCEPT_PROPOSAL :
                    ACLMessage.REJECT_PROPOSAL);
            reply.setContent(new Random().nextDouble() > 0.7 && reply.getPerformative() == ACLMessage.REJECT_PROPOSAL ?
                    Conversation.NO_SEATS :
                    Conversation.CONTENT_STUB);
            myAgent.send(reply);
            /*CitizenAgent.logger.info("{} sent {} to passenger {} with content {}",
                    myAgent.getLocalName(),
                    random < divide ? "agreement" : "reject",
                    msg.getSender().getLocalName(), msg.getContent());*/
        }
    }


    /**
     * метод перебирает все возможные комбинации на основе предложений от пассажиров
     * @return список из лучших предложений
     */
    public List<Offer> analyzeOffersPool (){
        List<Offer> offers_pool = this.myCitizenAgent.offersPool;
        List<Offer> best_offer_combo = new LinkedList<>();

        int n = offers_pool.size();
        double offers_price = 0;
        double max_price = 0;

        // мы можем взять не больше человек, чем вместит в себя машина
        int allMasks =  ((1 << n) < this.myCitizenAgent.getCarCapacity() ? (1 << n) : this.myCitizenAgent.getCarCapacity());

        for (int i = 1; i < allMasks; i++)             // тут рассматривается одно подмножество
        {
            offers_price = 0;

            for (int j = 0; j < n; j++){
                if ((i & (1 << j)) > 0) {                // j-тый элемент подмножества используется, суммируем его вклад

                    offers_price += offers_pool.get(j).price;

                    if (offers_price > max_price){
                        best_offer_combo = new LinkedList<>();
                        max_price = offers_price;

                        for(int k = 0; k < n; k++){
                            if ((i & (1 << k)) > 0){
                                best_offer_combo.add(offers_pool.get(k));
                            }
                        }

                        break;
                    }
                }
            }
        }

        return best_offer_combo;
    }


    public class PassengerState {
        public AID passenger;
        public double proposedPrice;
        public boolean answered;
        public boolean accepted;
        public boolean toBeDeleted;
        public String conversationID;

        public PassengerState(AID driver, double curPrice, String conversationID) {
            this.passenger = driver;
            this.proposedPrice = curPrice;
            this.answered = false;
            this.accepted = false;
            this.conversationID = conversationID;
            this.toBeDeleted = false;
        }
    }
}
