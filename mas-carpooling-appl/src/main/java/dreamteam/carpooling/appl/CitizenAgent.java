package dreamteam.carpooling.appl;

import dreamteam.carpooling.appl.DriverBehaviours.HandlePassengersOffersBehaviour;
import dreamteam.carpooling.appl.DriverBehaviours.RegisterInYPBehaviour;
import dreamteam.carpooling.appl.PassengerBehaviours.HandleDriversOffersBehaviour;

import dreamteam.carpooling.appl.Util.Parser;
import jade.core.Agent;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Агент - житель города. Каждый агент может играть две роли: пассажира и водителя.
 * В какой-то момент он должен определиться, кем он будет в итоге.
 */
public class CitizenAgent extends Agent {

    private Car car = null;
    private Graph<String, DefaultWeightedEdge> city = new Parser().getCity();

    private String start, finish;

    public static final Logger logger = LoggerFactory.getLogger(CitizenAgent.class);

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
        }

        // Поведения для роли пассажира
        addBehaviour(new HandleDriversOffersBehaviour(this, 3000));
    }

    public String getStart() {
        return start;
    }

    public String getFinish() {
        return finish;
    }
}
