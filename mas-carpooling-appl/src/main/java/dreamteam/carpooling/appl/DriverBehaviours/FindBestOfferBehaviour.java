package dreamteam.carpooling.appl.DriverBehaviours;

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

        if (checkInequality()){
            returnCode = DriverFSMBehaviour.POSITIVE_CONDITION;
        }
        else returnCode = DriverFSMBehaviour.NEGATIVE_CONDITION;
        return returnCode;
    }

    @Override
    public void action() {
        myParentFSM = (DriverFSMBehaviour) getParent();
        myParentFSM.myCitizenAgent.best_offer = analyzeOffersPool();
    }

    /**
     * метод перебирает все возможные комбинации на основе предложений от пассажиров
     * @return список из лучших предложений
     */
    public List<Offer> analyzeOffersPool (){
        List<Offer> offers_pool = this.myParentFSM.myCitizenAgent.offersPool;
        List<Offer> best_offer_combo = new LinkedList<>();

        int n = offers_pool.size();
        double offers_price = 0;
        double max_price = 0;

        // мы можем взять не больше человек, чем вместит в себя машина
        int allMasks =  ((1 << n) < this.myParentFSM.myCitizenAgent.getCarCapacity() ? (1 << n) : this.myParentFSM.myCitizenAgent.getCarCapacity());

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

    /**
     *   pp < (pd - cd) < p0,

     pp - цена, которую агент заплатит в качестве пассажира (постоянно повышается),
     pd - суммарный профит для водителя, который он получит, если возьмёт множество пассажиров,
     cd - суммарные траты водителя, которые он понесёт, если возьмёт множество пассажиров,

     (pd, cd вычисляются для каждого возможного подмножества пассажиров)
     p0 - изначальная стоимость поездки водителя (если он сам поедет; эта никода не меняется).

     * @return true, если выполняется
     */
    public boolean checkInequality (){

        double pp, pd, cd, p0;


        pp = myParentFSM.myCitizenAgent.getPrice();

        pd = 0;
        for (Offer offer:
                myParentFSM.myCitizenAgent.best_offer) {
            pd += offer.price;
        }

        p0 = myParentFSM.myCitizenAgent.getCostByMyCar();


        /**
         *    val == True ---> нужно забрать из этой точки
         *    val == False --> нужно доставить в эту точку
         */

        //GraphPath<String, MyWeightedEdge> rezult_path = new GraphWalk<String, MyWeightedEdge>();

        List<String> rezult_vertices = new LinkedList<>();
        Map<Offer, Boolean> listToVisit = new HashMap<>();

        /**
         *  составляем список вершин для посещения водителем
         */
        for (Offer offer:
                myParentFSM.myCitizenAgent.best_offer){
            listToVisit.put(offer, Boolean.TRUE);
        }

        String cur_vertex = myParentFSM.myCitizenAgent.getWayByMyCar().getStartVertex();                                            //... init start vertex
        rezult_vertices.add(cur_vertex);


        /**
         *   на случай если мы стартуем сразу с пассажирами
         */
        for (Offer offer:                                                                                               //... обновляем список необходимых посещений
                listToVisit.keySet()) {

            String vertex_to_visit = (listToVisit.get(offer)) ? offer.start : offer.finish;


            if((vertex_to_visit.equals(cur_vertex))){
                if(listToVisit.get(offer)){
                    listToVisit.put(offer, Boolean.FALSE);
                }

                listToVisit.remove(offer);
            }

        }


        while (listToVisit.size() != 0){
            Double distance_to_nearest = Double.MAX_VALUE;
            String nearest_passenger = "";

            /**
             *   выбираем ближайшую к текущему положению.
             */
            for (Offer in_list:
                    listToVisit.keySet()) {


                GraphPath<String,MyWeightedEdge> way_to_next =
                        (listToVisit.get(in_list)) ? myParentFSM.myCitizenAgent.getShortestPaths().getShortestPath(cur_vertex, in_list.start):
                                myParentFSM.myCitizenAgent.getShortestPaths().getShortestPath(cur_vertex, in_list.finish);

                if (way_to_next.getWeight() < distance_to_nearest){
                    nearest_passenger = way_to_next.getEndVertex();
                    distance_to_nearest = way_to_next.getWeight();
                }
            }
            cur_vertex = nearest_passenger;
            rezult_vertices.add(cur_vertex);

            for (Offer offer:                                                                                               //... обновляем список необходимых посещений
                    listToVisit.keySet()) {

                String vertex_to_visit = (listToVisit.get(offer)) ? offer.start : offer.finish;


                if((vertex_to_visit.equals(cur_vertex))){
                    if(listToVisit.get(offer)){
                        listToVisit.put(offer, Boolean.FALSE);
                    }

                    listToVisit.remove(offer);
                }

            }
        }

        cd = 0;
        for(int i = 1; i< rezult_vertices.size(); i++){
            cd += myParentFSM.myCitizenAgent.getCity().getEdge(rezult_vertices.get(i-1), rezult_vertices.get(i)).get_weight();
        }

        GraphPath<String, MyWeightedEdge> new_way = new GraphWalk<String, MyWeightedEdge>(myParentFSM.myCitizenAgent.getCity(), rezult_vertices, cd);
        this.myParentFSM.myCitizenAgent.setNewRoad(new_way);

        if ((pp < (pd - cd)) && ((pd - cd) < p0)) return true;
        else return false;
    }
}
