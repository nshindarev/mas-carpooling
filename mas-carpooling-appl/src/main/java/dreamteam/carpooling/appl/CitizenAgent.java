package dreamteam.carpooling.appl;

import jade.core.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Георгий on 02.04.2017.
 */
public class CitizenAgent extends Agent {

    private static final Logger logger = LoggerFactory.getLogger(CitizenAgent.class);

    @Override
    protected void setup() {
        super.setup();
        logger.info("Hello! Agent {} is ready.", getAID().getName());
    }
}
