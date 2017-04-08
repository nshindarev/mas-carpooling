package dreamteam.carpooling.appl;

import dreamteam.carpooling.appl.Util.MyCityGraph;
import dreamteam.carpooling.appl.Util.MyWeightedEdge;
import dreamteam.carpooling.appl.Util.Parser;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;

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
        MyCityGraph<String, MyWeightedEdge> city = parser.getCity();

        city.addCity_district("D1", new LinkedList<String>() {{
            add("1");
            add("2");
            add("3");
            add("4");
        }});

        city.addCity_district("D1", new LinkedList<String>() {{
            add("5");
            add("6");
            add("7");
            add("8");
        }});

        city.getCity_districts().toString();


        logger.debug(city.getCity_districts().toString());
        logger.isDebugEnabled();
    }

}
