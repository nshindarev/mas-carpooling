package dreamteam.carpooling.appl.DriverBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import dreamteam.carpooling.appl.Util.MyWeightedEdge;
import dreamteam.carpooling.appl.Util.Offer;
import jade.core.behaviours.OneShotBehaviour;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.GraphWalk;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Поиск лучшей комбинации заявок
 */
public class FindBestOfferBehaviour extends OneShotBehaviour {

    private int returnCode;
    private DriverFSMBehaviour myParentFSM;

    @Override
    public int onEnd() {

        /**
         *   POSITIVE_CONDITION ---> неравенство выполняется
         *   NEGATIVE_CONDITION ---> неравенство не выполняется
         */

        if (checkInequality()) {
            returnCode = DriverFSMBehaviour.POSITIVE_CONDITION;
        }
        else returnCode = DriverFSMBehaviour.NEGATIVE_CONDITION;
        return returnCode;
    }

    @Override
    public void action() {
        myParentFSM = (DriverFSMBehaviour) getParent();
        if(myParentFSM.myCitizenAgent.offersPool.size() == 0){
            CitizenAgent.logger.error("Пустой пул для {}", myParentFSM.myCitizenAgent.getLocalName());
        }
        myParentFSM.myCitizenAgent.best_offer = analyzeOffersPool();
        if (myParentFSM.myCitizenAgent.best_offer.size()>0)
            this.myParentFSM.myCitizenAgent.logger.info("!!! Best Offer Pool > 0 {}", myParentFSM.myCitizenAgent.getLocalName());
    }

    /**
     * метод перебирает все возможные комбинации на основе предложений от пассажиров
     *
     * @return список из лучших предложений
     */
    public List<Offer> analyzeOffersPool() {
        List<Offer> offers_pool = this.myParentFSM.myCitizenAgent.offersPool;
        List<Offer> best_offer_combo = new LinkedList<>();

        int n = offers_pool.size();
        double offers_profit = 0;
        double best_profit = Double.MIN_VALUE;

        // мы можем взять не больше человек, чем вместит в себя машина
        int allMasks = ((1 << n) < (this.myParentFSM.myCitizenAgent.getCarCapacity()+1) ? (1 << n) : (this.myParentFSM.myCitizenAgent.getCarCapacity()+1));
        //  int allMasks = 1 << n;
        List<Offer> curOfferList = new LinkedList<>();
        for (int i = 1; i < allMasks; i++)             // тут рассматривается одно подмножество
        {
            offers_profit = 0;

            for (int j = 0; j < n; j++) {
                if ((i & (1 << j)) > 0) {                // j-тый элемент подмножества используется, суммируем его вклад

                    curOfferList.add(offers_pool.get(j));
                }
            }

            /**
             *  оцениваем профит текущего предложения
             */

            offers_profit += FindBestOfferBehaviour.profitWithPassengers(curOfferList);
            offers_profit -= FindBestOfferBehaviour.priceWithPassengers(curOfferList, this.myParentFSM.myCitizenAgent);



            if (offers_profit > best_profit) {
                this.myParentFSM.myCitizenAgent.logger.debug("AGENT {} FOUND BEST OFFER = {}", myParentFSM.myCitizenAgent.getLocalName(), offers_profit);
                best_offer_combo = new LinkedList<>();
                best_profit = offers_profit;

                for (int k = 0; k < n; k++) {
                    if ((i & (1 << k)) > 0) {
                        best_offer_combo.add(offers_pool.get(k));
                    }
                }

                //break;

                }
            }
        if (best_offer_combo.size() == 0)
            CitizenAgent.logger.warn("Не нашли хороших предложений для {}", myParentFSM.myCitizenAgent.getLocalName());
        return best_offer_combo;
    }

    /**
     * pp < (cd - pd) < p0,
     * <p>
     * pp - цена, которую агент заплатит в качестве пассажира (постоянно повышается),
     * pd - суммарный профит для водителя, который он получит, если возьмёт множество пассажиров,
     * cd - суммарные траты водителя, которые он понесёт, если возьмёт множество пассажиров,
     * <p>
     * (pd, cd вычисляются для каждого возможного подмножества пассажиров)
     * p0 - изначальная стоимость поездки водителя (если он сам поедет; эта никода не меняется).
     *
     * @return true, если выполняется
     */
    public boolean checkInequality() {

        double pp, pd, cd, p0;


        pp = myParentFSM.myCitizenAgent.getPrice();

        pd = 0;
        for (Offer offer :
                myParentFSM.myCitizenAgent.best_offer) {
            pd += offer.price;
        }

        p0 = myParentFSM.myCitizenAgent.getCostByMyCar();


        /**
         *    val == True ---> нужно забрать из этой точки
         *    val == False --> нужно доставить в эту точку
         */

        List<String> rezult_vertices = new LinkedList<>();
        Map<Offer, Boolean> listToVisit = new HashMap<>();



        /**
         *  составляем список вершин для посещения водителем
         */
        for (Offer offer :
                myParentFSM.myCitizenAgent.best_offer) {
            listToVisit.put(offer, Boolean.TRUE);
        }

        String cur_vertex = myParentFSM.myCitizenAgent.getWayByMyCar().getStartVertex();                                            //... init start vertex
        rezult_vertices.add(cur_vertex);


        /**
         *   на случай если мы стартуем сразу с пассажирами
         */
        for (Offer offer :                                                                                               //... обновляем список необходимых посещений
                listToVisit.keySet()) {

            String vertex_to_visit = (listToVisit.get(offer)) ? offer.start : offer.finish;


            if ((vertex_to_visit.equals(cur_vertex))) {
                if (listToVisit.get(offer)) {
                    listToVisit.remove(offer);
                    listToVisit.put(offer, Boolean.FALSE);
                }
            }

        }


        while (listToVisit.size() != 0) {
            Double distance_to_nearest = Double.MAX_VALUE;
            String nearest_passenger = "";

            /**
             *   выбираем ближайшую к текущему положению.
             */



            String nearest_in_trip_to_save = "";        // от этой найдем ближайший путь до новой вершины в маршруте

            for (Offer in_list :
                    listToVisit.keySet()) {

                    GraphPath<String, MyWeightedEdge> way_to_next =
                            (listToVisit.get(in_list)) ? myParentFSM.myCitizenAgent.getShortestPaths().getShortestPath(cur_vertex, in_list.start) :
                                    myParentFSM.myCitizenAgent.getShortestPaths().getShortestPath(cur_vertex, in_list.finish);

                    if (way_to_next.getWeight() < distance_to_nearest) {
                        nearest_passenger = way_to_next.getEndVertex();
                        distance_to_nearest = way_to_next.getWeight();
                        nearest_in_trip_to_save = cur_vertex;
                    }
            }

            // данная реализация смотрит ближайшие точки в нашем маршруте
            // list_to_search_nearest.add(nearest_passenger);



            /**
             *   проверяем, что мы еще не стоим на ближайшей
             */

            if(!rezult_vertices.get(rezult_vertices.size()-1).equals(nearest_in_trip_to_save))
                rezult_vertices.add(nearest_in_trip_to_save);

            rezult_vertices.add(nearest_passenger);
            cur_vertex = nearest_passenger;


            for (Offer offer :                                                                                               //... обновляем список необходимых посещений
                    listToVisit.keySet()) {

                String vertex_to_visit = (listToVisit.get(offer)) ? offer.start : offer.finish;


                if ((vertex_to_visit.equals(nearest_passenger))) {
                    if (listToVisit.get(offer)) {
                        listToVisit.put(offer, Boolean.FALSE);
                    }
                    else {
                        listToVisit.remove(offer);
                    }
                }

            }
        }
        if(!rezult_vertices.get(rezult_vertices.size()-1).equals(myParentFSM.myCitizenAgent.getWayByMyCar().getEndVertex()))
            rezult_vertices.add(myParentFSM.myCitizenAgent.getWayByMyCar().getEndVertex());

        cd = 0;
        for (int i = 1; i < rezult_vertices.size(); i++) {

            cd += myParentFSM.myCitizenAgent.getShortestPaths().shortestDistance(rezult_vertices.get(i - 1), rezult_vertices.get(i));
        }
        cd *= myParentFSM.myCitizenAgent.car.getCostPerKilometer();


        /**
         *  обновим GraphWalk с учетом нового маршрута
         */

        List<String> newVertexList = new LinkedList<>();

        for (int i = 0; i < (rezult_vertices.size()-1); i++){
            if(newVertexList.size()!=0){
                newVertexList.remove(newVertexList.size()-1);
            }

            try {
              newVertexList.addAll(myParentFSM.myCitizenAgent.getShortestPaths().getShortestPathAsVertexList(rezult_vertices.get(i), rezult_vertices.get(i+1)));
            }
            catch (NullPointerException ex){
                CitizenAgent.logger.error("пытаемся сделать новый список размера " + newVertexList.size() );
            }
        }
            this.myParentFSM.myCitizenAgent.setNewRoad(newVertexList, cd);

        /*
        GraphPath<String, MyWeightedEdge> new_way = new GraphWalk<String, MyWeightedEdge>(myParentFSM.myCitizenAgent.getCity(), rezult_vertices, cd);
        this.myParentFSM.myCitizenAgent.setNewRoad(new_way); */

        if (pp < p0) {
            CitizenAgent.logger.info("{} goes by his/her car", myAgent.getLocalName());
            myParentFSM.myCitizenAgent.removeBehaviour(myParentFSM.myCitizenAgent.myPassengerBehaviour);
        }

        if ((pp < (cd - pd)) && (pp < p0))
            return true;
        else
            return false;
    }

    public static double priceWithPassengers(List<Offer> cur_best_offer, CitizenAgent myCitizenAgent){

        /**
         *    val == True ---> нужно забрать из этой точки
         *    val == False --> нужно доставить в эту точку
         */

        List<String> rezult_vertices = new LinkedList<>();
        Map<Offer, Boolean> listToVisit = new HashMap<>();



        /**
         *  составляем список вершин для посещения водителем
         */
        for (Offer offer :
                cur_best_offer) {
            listToVisit.put(offer, Boolean.TRUE);
        }

        String cur_vertex = myCitizenAgent.getWayByMyCar().getStartVertex();                                            //... init start vertex
        rezult_vertices.add(cur_vertex);


        /**
         *   на случай если мы стартуем сразу с пассажирами
         */
        for (Offer offer :                                                                                               //... обновляем список необходимых посещений
                listToVisit.keySet()) {

            String vertex_to_visit = (listToVisit.get(offer)) ? offer.start : offer.finish;


            if ((vertex_to_visit.equals(cur_vertex))) {
                if (listToVisit.get(offer)) {
                    listToVisit.remove(offer);
                    listToVisit.put(offer, Boolean.FALSE);
                }
            }

        }


        while (listToVisit.size() != 0) {
            Double distance_to_nearest = Double.MAX_VALUE;
            String nearest_passenger = "";

            /**
             *   выбираем ближайшую к текущему положению.
             */



            String nearest_in_trip_to_save = "";        // от этой найдем ближайший путь до новой вершины в маршруте

            for (Offer in_list :
                    listToVisit.keySet()) {

                GraphPath<String, MyWeightedEdge> way_to_next =
                        (listToVisit.get(in_list)) ? myCitizenAgent.getShortestPaths().getShortestPath(cur_vertex, in_list.start) :
                                myCitizenAgent.getShortestPaths().getShortestPath(cur_vertex, in_list.finish);

                if (way_to_next.getWeight() < distance_to_nearest) {
                    nearest_passenger = way_to_next.getEndVertex();
                    distance_to_nearest = way_to_next.getWeight();
                    nearest_in_trip_to_save = cur_vertex;
                }
            }

            // данная реализация смотрит ближайшие точки в нашем маршруте
            // list_to_search_nearest.add(nearest_passenger);



            /**
             *   проверяем, что мы еще не стоим на ближайшей
             */

            if(!rezult_vertices.get(rezult_vertices.size()-1).equals(nearest_in_trip_to_save))
                rezult_vertices.add(nearest_in_trip_to_save);

            rezult_vertices.add(nearest_passenger);
            cur_vertex = nearest_passenger;


            for (Offer offer :                                                                                               //... обновляем список необходимых посещений
                    listToVisit.keySet()) {

                String vertex_to_visit = (listToVisit.get(offer)) ? offer.start : offer.finish;


                if ((vertex_to_visit.equals(nearest_passenger))) {
                    if (listToVisit.get(offer)) {
                        listToVisit.put(offer, Boolean.FALSE);
                    }
                    else {
                        listToVisit.remove(offer);
                    }
                }

            }
        }
        if(!rezult_vertices.get(rezult_vertices.size()-1).equals(myCitizenAgent.getWayByMyCar().getEndVertex()))
            rezult_vertices.add(myCitizenAgent.getWayByMyCar().getEndVertex());

        double cd = 0;
        for (int i = 1; i < rezult_vertices.size(); i++) {

            cd += myCitizenAgent.getShortestPaths().shortestDistance(rezult_vertices.get(i - 1), rezult_vertices.get(i));
        }
        cd *= myCitizenAgent.car.getCostPerKilometer();


        /**
         *  обновим GraphWalk с учетом нового маршрута
         */

        List<String> newVertexList = new LinkedList<>();

        for (int i = 0; i < (rezult_vertices.size()-1); i++){
            if(newVertexList.size()!=0){
                newVertexList.remove(newVertexList.size()-1);
            }

            try {
                newVertexList.addAll(myCitizenAgent.getShortestPaths().getShortestPathAsVertexList(rezult_vertices.get(i), rezult_vertices.get(i+1)));
            }
            catch (NullPointerException ex){
                CitizenAgent.logger.error("пытаемся сделать новый список размера " + newVertexList.size() );
            }
        }
        //myCitizenAgent.setNewRoad(newVertexList, cd);
        return cd;
    }
    public static double profitWithPassengers(List<Offer> curOffer){

        double rez = 0;
        if(curOffer.size() == 0){
            return 0;
        }
        else {
            for (Offer cur_best_offer:
                 curOffer) {
                rez += cur_best_offer.price;
            }
        }
        return rez;
    }
}
