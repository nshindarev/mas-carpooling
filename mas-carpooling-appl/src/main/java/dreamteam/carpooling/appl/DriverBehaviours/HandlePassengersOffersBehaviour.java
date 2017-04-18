package dreamteam.carpooling.appl.DriverBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import dreamteam.carpooling.appl.Util.MyWeightedEdge;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import org.jgrapht.alg.DijkstraShortestPath;

import java.util.LinkedList;
import java.util.List;

/**
 * Обработка предложений от пассажиров
 */
public class HandlePassengersOffersBehaviour extends TickerBehaviour {


    private CitizenAgent myCitizenAgent = (CitizenAgent) myAgent;

    public HandlePassengersOffersBehaviour(Agent a, long period) {
        super(a, period);
    }

    @Override
    protected void onTick() {
        //CitizenAgent.logger.info("{} is handling a proposal from passenger", myAgent.getAID().getLocalName());
    }

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
             myCitizenAgent.getCurrentRoute()) {

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
                 myCitizenAgent.getCurrentRoute()) {
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
                    myCitizenAgent.getCurrentRoute()) {
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
                    myCitizenAgent.getCurrentRoute()) {
                price += edge_in_route.get_weight();
            }


            greed_value = price * myCitizenAgent.getGreed();
            price /= myCitizenAgent.companions.size();
            price += additionalPrice;
            price += greed_value;
        }
        else if (!found_finish_in_route){

           for (MyWeightedEdge edge_in_route:
                    myCitizenAgent.getCurrentRoute()) {
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
