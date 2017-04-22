package dreamteam.carpooling.appl.DriverBehaviours;

import com.sun.org.apache.xpath.internal.operations.Bool;
import dreamteam.carpooling.appl.CitizenAgent;
import dreamteam.carpooling.appl.Util.MyWeightedEdge;
import dreamteam.carpooling.appl.Util.Offer;
import jade.content.onto.basic.Equals;
import jade.core.behaviours.CyclicBehaviour;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.GraphWalk;

import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

/**
 *   Поведение проверяет, нужно ли агенту выбрать роль водителя
 *   или продолжать вбрасывать предложения другим водителям
 */
public class CheckPassengerPoolBehaviour extends CyclicBehaviour {

    private CitizenAgent myCitizenAgent = (CitizenAgent) myAgent;

    public CheckPassengerPoolBehaviour(){

    }

    @Override
    public void action() {

        /**
         * ГОШАН, ЕСЛИ ТЫ ДОШЕЛ ДО ЭТОГО МОМЕНТА, МЕНЯ СКОРЕЕ ВСЕГО УЖЕ НЕТ В ЖИВЫХ
         * НИЖЕ ТЫ НАЙДЕШЬ ВСЕ НУЖНЫЕ ПОДСКАЗКИ ДЛЯ ТОГО ЧТОБЫ ЗАКОНЧИТЬ МОЕ ДЕЛО
         *
         * Примерный алгоритм:
         * --- я смотрю наилучшую комбинацию предложений от попутчиков
         * --- эта комбинация хранится в поле агента и заполняется в HandlePassengersOffers
         * --- дальше ТУТ производится проверка того самого неравенства pp < (pd - cd) < p0  ---> (cм. issue #10)
         * --- if (неравенство == False)
         *         ->  присваиваем полю decided_to_drive у водителя = true
         *         ->  больше не вызываем это поведение
         *         ->  рассылаем в другом поведении всем заявкам ответы мол тебя беру/ тебя не беру
         *             (тем, кому отказали, нужно сказать что просто мест больше нет)
         *
         *         ?:   --- убираем себя из YP сервиса ???
         *              --- отключаем поведения и СОБИРАЕМ СТАТИСТИКУ  ------> НАСТЯ ТЕБЕ СЮДА <------
         *
         *     else
         *         -> отправляем всем-всем сообщение об отказе мол денег мало
         */

        if (myCitizenAgent == null) {
            myCitizenAgent = (CitizenAgent) myAgent;
        }

        List<Offer> best_offer = myCitizenAgent.getBestOffer();

        boolean inequality = checkInequality();

        if (!inequality){
            myCitizenAgent.decided_to_drive = true;                    // ... решили ехать на своей машине
            // просчитываем новый маршрут
        }
        else{

        }

    }


    //TODO: неправильно высчитывается cd !!!
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


        pp = myCitizenAgent.getPrice();

        pd = 0;
        for (Offer offer:
             myCitizenAgent.best_offer) {
            pd += offer.price;
        }

        p0 = myCitizenAgent.getCostByMyCar();



        // VER 1.0
        // GraphPath<String, MyWeightedEdge> cur_way = myCitizenAgent.getCurrentRoute();
        // cd = cur_way.getWeight() * myCitizenAgent.car.getCostPerKilometer();

        // VER 1.2

        /**
         * distance_till_passenger
         *
         * Pair<True, ....(path)... >  => забираем пассажира в конечной точке этого пути
         * Pair<False, ...(path)... >  => привезли пассажира в конечной точке этого пути
         *
         */
         // Map<String, List<Pair<Boolean,GraphPath<String,MyWeightedEdge>>>> distance_till_passenger = new HashMap<>();     //... внешняя хеш-таблица


        /**
         * добавим все стартовые точки из пула лучших предложений
         * True ---> за ним надо заехать
         * False --> его надо отвезти
         */
       /* List<Pair<Offer,String>> to_visit = new LinkedList<>();                                       //... для проверки того, что не попадаем в посещенные
        for (Offer offer:
                myCitizenAgent.best_offer){
            to_visit.add(new Pair<>(offer, offer.start));
        }


        for (String my_point:
                myCitizenAgent.getWayByMyCar().getVertexList()) {                                                 //... заполним для каждой точки пути водителя
                                                                                                                  //... ее shortest paths до попутчиков
            List<Pair<Boolean, GraphPath<String, MyWeightedEdge>>> cur_distances = new LinkedList<>();
            for (Offer passenger:
                 myCitizenAgent.best_offer) {

                cur_distances.add(
                        new Pair<Boolean, GraphPath<String,MyWeightedEdge>>(Boolean.TRUE, myCitizenAgent.getShortestPaths().getShortestPath(my_point, passenger.start)));
            }

            distance_till_passenger.put(my_point, cur_distances);
        }

          */                                                                                                         //... теперь нужно найти ближайшего попутчика в маршруте
        /**
         * забрали пассажира ---> добавляем во все списки new Pair<false, ...(path to fin)...>
         * привезли пассажира ---> удаляем из всех списков эту точку с финишем
         */

      /*  Pair<Boolean, GraphPath<String, MyWeightedEdge>> way_to_closest_passenger = null;
        Double min_route = Double.MAX_VALUE;
        Boolean st_fin;

        for (String start_in_new_hook:                                                                             //... выбираем в цикле вершину с минимальным расстоянием
                distance_till_passenger.keySet()){                                                                 //... дополним информацией: забрали/ привезли
            for (Pair<Boolean, GraphPath<String, MyWeightedEdge>> shortest_path:
                  distance_till_passenger.get(start_in_new_hook)) {

                // поиск минимального отклонения от текущего маршрута
                if (shortest_path.second.getWeight() <= min_route){

                    min_route = shortest_path.second.getWeight();
                    way_to_closest_passenger =  shortest_path ;

                }
            }


            if (way_to_closest_passenger != null){

                // помечаем, что посетили вершину в планах посещений:
                for (Pair<Offer, String> to_vis:
                        to_visit){
                    if (to_vis.second.equals(way_to_closest_passenger.second.getEndVertex()))
                    {
                        if(to_vis.first.start.equals(to_vis.second)){                                          // в планах посещений хранится как старт
                            to_visit.remove(to_vis);
                            to_visit.add(new Pair<>(to_vis.first, to_vis.first.finish));
                        }

                        else if (to_vis.first.finish.equals(to_vis.second)){                                   // в планах посещений хранится как финиш

                            to_visit.remove(to_vis);
                        }
                    }
                }


                List<Pair<Boolean,GraphPath<String,MyWeightedEdge>>> from_newpoint_to_another = new LinkedList<>();        // добавляем новую вершину в пути в хеш-таблицу


                for (Pair<Offer, String> to_vis:
                        to_visit) {
                    from_newpoint_to_another.add(new Pair<>(to_vis.first.start.equals(to_vis.second) ? Boolean.TRUE:Boolean.FALSE
                            , myCitizenAgent.getShortestPaths().getShortestPath(way_to_closest_passenger.second.getEndVertex(), to_vis.second)));
                }

                distance_till_passenger.put(way_to_closest_passenger.second.getEndVertex(), from_newpoint_to_another);

*/
                /**
                 * Update Hash Table
                 */

                // если нам нужно заехать за пассажиром
                // обновляем каждый список в хеш-таблице start -> finish
              /*  if (way_to_closest_passenger.first) {

                    for (List<Pair<Boolean, GraphPath<String,MyWeightedEdge>>> cur_sh_paths:
                         distance_till_passenger.values()) {

                        for (Pair<Boolean, GraphPath<String, MyWeightedEdge>> cur_sh_path:
                             cur_sh_paths) {
                            if
                        }
                    }
                }


                //если нужно отвезти пассажира
                //удаляем
                else {



                }

            }


        }*/


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
                myCitizenAgent.best_offer){
            listToVisit.put(offer, Boolean.TRUE);
        }

        String cur_vertex = myCitizenAgent.getWayByMyCar().getStartVertex();                                            //... init start vertex
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
                        (listToVisit.get(in_list)) ? myCitizenAgent.getShortestPaths().getShortestPath(cur_vertex, in_list.start):
                                                     myCitizenAgent.getShortestPaths().getShortestPath(cur_vertex, in_list.finish);

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
           cd += myCitizenAgent.getCity().getEdge(rezult_vertices.get(i-1), rezult_vertices.get(i)).get_weight();
        }

        GraphPath<String, MyWeightedEdge> new_way = new GraphWalk<String, MyWeightedEdge>(myCitizenAgent.getCity(), rezult_vertices, cd);
        this.myCitizenAgent.setNewRoad(new_way);

        if ((pp < (pd - cd)) && ((pd - cd) < p0)) return true;
        else return false;
    }



    public void setNewRoad(){

    }
}

