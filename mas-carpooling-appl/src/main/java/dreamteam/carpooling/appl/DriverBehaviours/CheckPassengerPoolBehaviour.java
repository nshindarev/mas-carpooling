package dreamteam.carpooling.appl.DriverBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import dreamteam.carpooling.appl.Util.MyWeightedEdge;
import dreamteam.carpooling.appl.Util.Offer;
import jade.core.behaviours.CyclicBehaviour;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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



        GraphPath<String, MyWeightedEdge> cur_way = myCitizenAgent.getCurrentRoute();
        cd = cur_way.getWeight() * myCitizenAgent.car.getCostPerKilometer();

        /*Map<String, Map<String, Double>> distance_till_passenger = new HashMap<>();
        for (String s:
             myCitizenAgent.getWayByMyCar().getVertexList()) {

            distance_till_passenger.put(s, new HashMap<>());
            for (Offer approved:
                 myCitizenAgent.best_offer) {

                Map<String, Double> cur_ways = new HashMap<>();

                // TODO: реализовать всю ту муть с HashMap-ами, которую навыдумывал
                //    distance_till_passenger.put(s, );
                //    distance_till_passenger.values().add("Start",  new Double(myCitizenAgent.getShortestPaths().shortestDistance(s, approved.start)));

            }

        }*/

        if ((pp < (pd - cd)) && ((pd - cd) < p0)) return true;
        else return false;
    }



    public void setNewRoad(){

    }
}
