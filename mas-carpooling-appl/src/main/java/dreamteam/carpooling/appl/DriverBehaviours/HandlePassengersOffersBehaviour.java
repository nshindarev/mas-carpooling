package dreamteam.carpooling.appl.DriverBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import dreamteam.carpooling.appl.Util.MyWeightedEdge;
import dreamteam.carpooling.appl.Util.*;
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


    //TODO: переделать
    /**
     * анализ предложения от пассажира
     * @param cashValue
     * @return
     */
    public boolean canTakePassenger (double cashValue, String start, String finish){

        if (this.myCitizenAgent.getCarCapacity() > this.myCitizenAgent.companions.size()){
            if (countAcceptablePrice(start, finish) <= cashValue){
                /* TODO: убрать отсюда переопределение маршрута.
                *  TODO: переопределять его там где пассажир ответил согласием
                */
                // переопределим новый маршрут
                List<MyWeightedEdge> newRoadFirstPart = DijkstraShortestPath.findPathBetween(this.myCitizenAgent.getCity(), this.myCitizenAgent.getStart(), start);
                List<MyWeightedEdge> newRoadMiddlPart = DijkstraShortestPath.findPathBetween(this.myCitizenAgent.getCity(), this.myCitizenAgent.getStart(), this.myCitizenAgent.getFinish());
                List<MyWeightedEdge> newRoadFinalPart = DijkstraShortestPath.findPathBetween(this.myCitizenAgent.getCity(), finish, this.myCitizenAgent.getFinish());

                newRoadFirstPart.addAll(newRoadMiddlPart);
                newRoadFirstPart.addAll(newRoadFinalPart);

                this.myCitizenAgent.setNewRoad(newRoadFirstPart);

                return true;
            }
            else return false;

        }
        else return false;

    }

    /**
     * считаем цену для пассажира с учётом изменения маршрута за его счет
     * @param start
     * @param finish
     */
    public double countAcceptablePrice (String start, String finish){

        boolean found_start_in_route = false;
        boolean found_finish_in_route = false;
        double price = 0;
        double additionalPrice = 0;
        double greed_value = 0;
        LinkedList<MyWeightedEdge> newRoute = new LinkedList<>();

       for (MyWeightedEdge road_in_route:
             myCitizenAgent.getCurrentRoute().getEdgeList()) {

            if(road_in_route.getSource().equals(start) ||
                    road_in_route.getTarget().equals(start)){
                found_start_in_route = true;
            }

            if(road_in_route.getSource().equals(finish) ||
                    road_in_route.getTarget().equals(finish)){
                found_finish_in_route = true;
            }

        }
        if (found_start_in_route && found_finish_in_route){

            for (MyWeightedEdge edge_in_route:
                 myCitizenAgent.getCurrentRoute().getEdgeList()) {
              price += edge_in_route.get_weight();
            }
            greed_value = price * myCitizenAgent.getGreed();
            price /= myCitizenAgent.companions.size();
            price += greed_value;
        }
        // TODO: переопределить currentRoute
        else if (!found_finish_in_route && !found_start_in_route){

            for (MyWeightedEdge till_start_point:
                    DijkstraShortestPath.findPathBetween(this.myCitizenAgent.getCity(), this.myCitizenAgent.getStart(), start)) {
                additionalPrice += till_start_point.get_weight();

            }

            for (MyWeightedEdge edge_in_route:
                    myCitizenAgent.getCurrentRoute().getEdgeList()) {
                price += edge_in_route.get_weight();
            }

            for (MyWeightedEdge till_finish_point:
                    DijkstraShortestPath.findPathBetween(this.myCitizenAgent.getCity(), this.myCitizenAgent.getStart(), start)) {
                additionalPrice += till_finish_point.get_weight();
            }

            greed_value = price * myCitizenAgent.getGreed();
            price /= myCitizenAgent.companions.size();
            price += additionalPrice;
            price += greed_value;


        }
        else if (!found_start_in_route){


            for (MyWeightedEdge till_start_point:
                    DijkstraShortestPath.findPathBetween(this.myCitizenAgent.getCity(), this.myCitizenAgent.getStart(), start)) {
                additionalPrice += till_start_point.get_weight();

            }

            for (MyWeightedEdge edge_in_route:
                    myCitizenAgent.getCurrentRoute().getEdgeList()) {
                price += edge_in_route.get_weight();
            }


            greed_value = price * myCitizenAgent.getGreed();
            price /= myCitizenAgent.companions.size();
            price += additionalPrice;
            price += greed_value;
        }
        else if (!found_finish_in_route){

           for (MyWeightedEdge edge_in_route:
                    myCitizenAgent.getCurrentRoute().getEdgeList()) {
                price += edge_in_route.get_weight();
            }
            for (MyWeightedEdge till_finish_point:
                    DijkstraShortestPath.findPathBetween(this.myCitizenAgent.getCity(), this.myCitizenAgent.getStart(), start)) {
                additionalPrice += till_finish_point.get_weight();

            }


            greed_value = price * myCitizenAgent.getGreed();
            price /= myCitizenAgent.companions.size();
            price += additionalPrice;
            price += greed_value;
        }



        if (price != 0) return price;
        else return Double.MAX_VALUE;



    }


    public void countNewPrices(){

    }
}
