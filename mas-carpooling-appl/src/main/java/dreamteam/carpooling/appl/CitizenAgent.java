package dreamteam.carpooling.appl;

import dreamteam.carpooling.appl.DriverBehaviours.HandlePassengersOffersBehaviour;
import dreamteam.carpooling.appl.DriverBehaviours.RegisterInYPBehaviour;
import dreamteam.carpooling.appl.PassengerBehaviours.HandleDriversOffersBehaviour;

import dreamteam.carpooling.appl.Util.MyWeightedEdge;
import dreamteam.carpooling.appl.Util.Parser;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import org.jgrapht.Graph;
import org.jgrapht.alg.BidirectionalDijkstraShortestPath;
import org.jgrapht.alg.DijkstraShortestPath;
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
    private Graph<String, MyWeightedEdge> city = new Parser().getCity();

    private String start, finish;
    private List<MyWeightedEdge> wayWithMyCar = null;
    private double price = Double.MAX_VALUE;
    /**
     * коэфициент жадности для водителя
     */
    private double greed;

    public List<MyWeightedEdge> getCurrentRoute(){
        return  this.wayWithMyCar;
    }
    public double getCurrentPrice(){ return  this.price;}


    @Override
    protected void setup() {
        super.setup();
        logger.info("Hello! Agent {} is ready.", getAID().getName());

        Object[] args = getArguments();

        // Запоминаем старт и финиш, если они правильно заданы
        if (args != null && args.length >= 2 &&
                this.city.containsVertex(args[0].toString()) && this.city.containsVertex(args[1].toString()) &&
                !args[0].toString().equals(args[1].toString())) {
            this.start  = args[0].toString();
            this.finish = args[1].toString();
            logger.info("Agent {}: start - node {}, destination - node {}",
                    getAID().getName(), this.getStart(), this.getFinish());
        } else { // Если заданы неправильно, убиваем агента
            logger.info("Agent {} has invalid parameters", getAID().getName());
            this.doDelete();
            return;
        }

        // Смотрим, есть ли машина
        if (args.length == 4) {
            this.car = new Car(Byte.parseByte(args[2].toString()), Float.parseFloat(args[3].toString()));
            logger.info("Agent {} has car with capacity {} and cost per km {}",
                    getAID().getName(), car.getCapacity(), car.getCostPerKilometer());
        }

        // Поведения для роли водителя
        if (car != null) {
            addBehaviour(new RegisterInYPBehaviour());
            addBehaviour(new HandlePassengersOffersBehaviour(this, 3000));

            this.wayWithMyCar = null;
            getWayByMyCar();
            getCostByMyCar();
        }

        // Поведения для роли пассажира
        addBehaviour(new HandleDriversOffersBehaviour(this, 3000));

        this.greed = Math.random() * 0.15;
    }

    public String getStart() {
        return start;
    }

    public String getFinish() {
        return finish;
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
            return this.wayWithMyCar;
        }
        else{
            return this.wayWithMyCar;
        }
    }
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
