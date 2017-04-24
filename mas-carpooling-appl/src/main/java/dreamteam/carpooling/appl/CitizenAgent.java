package dreamteam.carpooling.appl;

import dreamteam.carpooling.appl.DriverBehaviours.DriverFSMBehaviour;

import dreamteam.carpooling.appl.PassengerBehaviours.PassengerFSMBehaviour;
import dreamteam.carpooling.appl.Util.*;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import org.jgrapht.GraphPath;

import org.jgrapht.alg.FloydWarshallShortestPaths;
import org.jgrapht.graph.GraphWalk;
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

    public Car car = null;
    private String start, finish;
    private MyCityGraph<String, MyWeightedEdge> city = City.getCity();


    /**
     *   Поведения-автоматы
     */
    public PassengerFSMBehaviour myPassengerBehaviour;
    public DriverFSMBehaviour    myDriverBehaviour;

    /**
     *   wayWithMyCar  --- кратчайший путь на собственной машине
     *   myCurrentWay  --- путь, который проделаем, заезжая за попутчиками
     *   shortestPaths --- перечень всех кратчайших путей между всеми точками города
     */
    private GraphPath<String, MyWeightedEdge> wayWithMyCar = null;
    private GraphPath<String, MyWeightedEdge> myCurrentWay = null;
    private FloydWarshallShortestPaths<String, MyWeightedEdge> shortestPaths = City.getShortestPaths();

    private double price;
    private double price_by_my_car = Double.MAX_VALUE;


    /**
     *   greed              --- коэфициент жадности для водителя
     *   decided_to_drive   --- водитель принял решение ехать на своей машине
     */
    private double  greed;
    public boolean decided_to_drive = false;


    /**
     *  companions      --- ID попутчиков
     *  offersPool      --- список заявок на поездку от других пассажиров
     *  suitableDrivers --- ID водителей, которые нам подходят
     */
    public List<AID> companions = new LinkedList<>();
    public List<Offer> offersPool = new LinkedList<>();
    public List<Offer> best_offer = new LinkedList<>();

    /**
     *   get/set
     */
    public double countPrice (Iterable<MyWeightedEdge> way){

        double rez = 0;
        for (MyWeightedEdge edge:
             way) {
            rez += edge.get_weight();
        }
        return rez;
    }
    public int getCarCapacity (){ return this.car.getCapacity(); }
    public GraphPath<String, MyWeightedEdge> getCurrentRoute(){
        return myCurrentWay;
    }

    /**
     * метод, обновляющий путь для водителя
     * @param input список ребер, через которые проедет водитель
     */
    public void setNewRoad(List<MyWeightedEdge> input){
        this.myCurrentWay = new GraphWalk<String, MyWeightedEdge>(city, start, finish, input, this.countPrice(input));
    }
    public void setNewRoad (GraphPath<String, MyWeightedEdge> input){
        this.myCurrentWay = input;
    }
    public void setNewRoad (List<String> input, double weight){
        this.myCurrentWay = new GraphWalk<String, MyWeightedEdge>(city, input, weight);
    }


    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getStart()  {
        return start;
    }
    public String getFinish() {
        return finish;
    }

    /**
     *   для статистики
     */
    public List<String> getFinalRoadVertexes(){
        return this.getCurrentRoute().getVertexList();
    }


    public double getGreed()  { return greed; }

    /**
     * get/update методы для работы с пулом предложений
     */
    public List<Offer> getOffersPool(){ return offersPool; }
    public List<Offer> getBestOffer() { return best_offer; }
    public void updateOfferInPool(Offer offer){
        for (Offer in_pool
                : this.offersPool) {

            if (in_pool.id.getName().equals(offer.id.getName())) {
                this.offersPool.remove(in_pool);
            }

        }
        this.offersPool.add(offer);

        if(offersPool.size() == 0){
            this.logger.warn("empty offer pool in agent {}", this.getLocalName());
        }
    }
    public void deleteOfferFromPool(Offer offer){
        if (this.offersPool.contains(offer)) {
            this.offersPool.remove(offer);
            if(this.best_offer.contains(offer))
                this.best_offer.remove(offer);
        }

    }

    public FloydWarshallShortestPaths<String, MyWeightedEdge> getShortestPaths() { return this.shortestPaths; }

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
            this.wayWithMyCar = null;
            try {
                getWayByMyCar();
                getCostByMyCar();
                ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                message.setContent("sc" + getWayByMyCar().getWeight());
                message.addReceiver(new AID(Conversation.SECRETARY_NAME, AID.ISLOCALNAME));
                this.send(message);
            }
            catch (NullPointerException ex){
                this.logger.error(ex.getMessage());
            }


            this.myDriverBehaviour = new DriverFSMBehaviour(this);
            addBehaviour(myDriverBehaviour);

        }

        this.greed = Math.random() * 0.15;

        // Поведения для роли пассажира
//        if (car == null) {
            myPassengerBehaviour = new PassengerFSMBehaviour(this);
            addBehaviour(myPassengerBehaviour);
//        }


    }


    /**
     * Расчет оптимального алгоритма следования от старта к финишу
     * на собственном автомобиле
     * @return смотрим в таблицу результатов Флойда-Уоршелла
     */
     public GraphPath<String, MyWeightedEdge> getWayByMyCar(){
        if (this.wayWithMyCar == null) {
            this.wayWithMyCar = shortestPaths.getShortestPath(start, finish);

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
     * получить стоимость маршрута на собственном авто по кратчайшему пути
     * @return
     */
    public double getCostByMyCar(){
        double sum = 0;
        if (this.price_by_my_car == Double.MAX_VALUE){
            for (MyWeightedEdge e:
                 getWayByMyCar().getEdgeList()) {
                sum += e.get_weight();
            }
            sum *= car.getCostPerKilometer();
            this.price_by_my_car = sum;
        }
        else sum = this.price_by_my_car;
        return sum;
    }
    public MyCityGraph<String, MyWeightedEdge> getCity() {
        return city;
    }



    public void deregister(){
        this.takeDown();
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


