package dreamteam.carpooling.appl;

import dreamteam.carpooling.appl.Util.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nshindarev on 17.03.17.
 */
public class Main {

    public static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Application started by {} class. ", Main.class.getName());
        jade.Boot.main(new String[] {
                "-gui",
                "god:dreamteam.carpooling.appl.Util.CreatorAgent"
        });



        // в качестве параметра -> путь к файлу .gml от mas-carpooling
        Parser parser = new Parser();
        parser.parseCityFromFile();
    }

}
