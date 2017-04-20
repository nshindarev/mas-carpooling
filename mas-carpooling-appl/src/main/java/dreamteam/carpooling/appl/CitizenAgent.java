package dreamteam.carpooling.appl;

import dreamteam.carpooling.appl.DriverBehaviours.HandlePassengersOffersBehaviour;
import dreamteam.carpooling.appl.DriverBehaviours.RegisterInYPBehaviour;

import dreamteam.carpooling.appl.PassengerBehaviours.HandleDriversListBehaviour;
import dreamteam.carpooling.appl.Util.City;
import dreamteam.carpooling.appl.Util.MyWeightedEdge;
import dreamteam.carpooling.appl.PassengerBehaviours.SearchDriversOffersInYPBehaviour;
import dreamteam.carpooling.appl.Util.MyCityGraph;
import dreamteam.carpooling.appl.Util.Parser;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import org.jgrapht.Graph;
import org.jgrapht.alg.BidirectionalDijkstraShortestPath;
import org.jgrapht.alg.DijkstraShortestPath;

import jade.domain.DFService;
import jade.domain.FIPAException;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Агент - житель города. Каждый агент может играть две роли: пассажира и водителя.
 * В какой-то момент он должен определиться, кем он будет в итоге.
 */
public class CitizenAgent extends Agent {

    public static final Logger logger = LoggerFactory.getLogger(CitizenAgent.class);

    private Car car = null;
    private String start, finish;
    private MyCityGraph<String, MyWeightedEdge> city = City.getCity();


    /**
     *   wayWithMyCar  --- кратчайший путь на собственной машине
     *   myCurrentWay  --- путь, который проделаем, заезжая за попутчиками
     *   shortestPaths --- перечень всех кратчайших путей между всеми точками города
     */
    private List<MyWeightedEdge> wayWithMyCar = null;
    private List<MyWeightedEdge> myCurrentWay = null;


    //TODO: переделать счетчик цен
    private double price = 1;
    /**
     *   greed --- коэфициент жадности для водителя
     */
    private double greed;


    /**
     * companions      --- ID попутчиков
     * offersPool      --- список заявок на поездку от других пассажиров
     * suitableDrivers --- ID водителей, которые нам подходят
     */
    public List<AID> companions = new LinkedList<>();
    public List<Offer> offersPool = new LinkedList<>();
    public List<AID> suitableDrivers = new LinkedList<>();

    /**
     *   get/set
     */
    public int getCarCapacity (){ return this.car.getCapacity(); }
    public List<MyWeightedEdge> getCurrentRoute(){
        return myCurrentWay;
    }
    public void setNewRoad(Iterable<MyWeightedEdge> input){
        this.myCurrentWay = new LinkedList<MyWeightedEdge>();
        for (MyWeightedEdge e:
             input) {
            this.myCurrentWay.add(e);
        }
    }


    public String getStart()  {
        return start;
    }
    public String getFinish() {
        return finish;
    }
    public double getGreed()  { return greed; }


    @Override
    protected void setup() {
        super.setup();
        //logger.info("Hello! Agent {} is ready.", getAID().getLocalName());

        Object[] args = getArguments();

        // Запоминаем старт и финиш, если они правильно заданы
        if (args != null && args.length >= 2 &&
                this.city.containsVertex(args[0].toString()) && this.city.containsVertex(args[1].toString()) &&
                !args[0].toString().equals(args[1].toString())) {
            this.start  = args[0].toString();
            this.finish = args[1].toString();
            logger.info("{}: start - node {}, destination - node {}",
                    getAID().getLocalName(), this.getStart(), this.getFinish());
        } else { // Если заданы неправильно, убиваем агента
            logger.info("{} has invalid parameters", getAID().getLocalName());
            this.doDelete();
            return;
        }

        // Смотрим, есть ли машина
        if (args.length == 4) {
            this.car = new Car(Byte.parseByte(args[2].toString()), Float.parseFloat(args[3].toString()));
            logger.info("{} has car with capacity {} and cost per km {}",
                    getAID().getLocalName(), car.getCapacity(), car.getCostPerKilometer());
        }

        // Поведения для роли водителя
        if (car != null) {
            addBehaviour(new RegisterInYPBehaviour());
            addBehaviour(new HandlePassengersOffersBehaviour());

            this.wayWithMyCar = null;
            getWayByMyCar();
            getCostByMyCar();

        }

        this.greed = Math.random() * 0.15;

        // Поведения для роли пассажира
        addBehaviour(new SearchDriversOffersInYPBehaviour(this, 3000));
        addBehaviour(new HandleDriversListBehaviour(this, 1000));
    }





    /**
     * Расчет оптимального алгоритма следования от старта к финишу
     * на собственном автомобиле
     * @return результат работы алгоритма Дейкстры
     */
     public List<MyWeightedEdge> getWayByMyCar(){
        if (this.wayWithMyCar == null) {
            wayWithMyCar = new LinkedList<>();
            this.wayWithMyCar =  DijkstraShortestPath.findPathBetween(this.city, start, finish);

            if(myCurrentWay == null){
                myCurrentWay = wayWithMyCar;
            }
            return this.wayWithMyCar;
        }
        else{
            return this.wayWithMyCar;
        }

    }

    /**
     * получить стоимость маршрута на собственном авто
     * @return
     */
    public double getCostByMyCar(){
        double sum = 0;
        if (this.price == Double.MAX_VALUE){
            for (MyWeightedEdge e:
                 getWayByMyCar()) {
                sum += e.get_weight();
            }
            sum *= car.getCapacity();
            this.price = sum;
        }
        else sum = this.price;
        return sum;
    }
    public MyCityGraph<String, MyWeightedEdge> getCity() {
        return city;
    }



    @Override
    protected void takeDown() {
        // Deregister from the yellow pages
        try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

}

class Offer {

    public String start, finish;
    public double price;
    public AID id;

    public Offer(String start, String finish, double price, AID id){
        this.id = id;
        this.start = start;
        this.finish = finish;
        this.price = price;
    }
}
