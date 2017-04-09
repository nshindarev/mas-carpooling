package dreamteam.carpooling.appl;


import dreamteam.carpooling.appl.Util.MyCityGraph;
import dreamteam.carpooling.appl.Util.MyWeightedEdge;
import dreamteam.carpooling.appl.Util.Parser;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLineParser;

import dreamteam.carpooling.appl.MainParameters;

import java.util.LinkedList;

/**
 * Created by nshindarev on 17.03.17.
 */
public class Main {

    public static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final String countAgents = "count_agents";

    public static void main(String[] args) {
        logger.info("Application started by {} class. ", Main.class.getName());
        MainParameters countAgents = parseParameters(args);

        jade.Boot.main(new String[] {
                "-gui",
                "god:dreamteam.carpooling.appl.Util.CreatorAgent(" + countAgents.getCountAgents() + ")"
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


    private static MainParameters parseParameters(String... args) {
        Options options = new Options();

        options.addOption(countAgents, true, "Количество агентов в городе");

        MainParameters parameters = new MainParameters();
        boolean params = true;
        CommandLineParser parser = new DefaultParser();
        CommandLine cl = null;

        try {
            cl = parser.parse(options, args);
            for (String par : new String[]{countAgents}) {
                String countPar = cl.getOptionValue(par);

                if (countPar == null) {
                    logger.error("Не задан параметр {}", par);
                    params = false;
                }

                if (params) {
                    // count_agents
                    String ca = cl.getOptionValue(countAgents);
                    parameters.setCountAgents(ca);
                }
            }
        } catch (ParseException pe) {
            logger.error("Не удаётся разобрать строку параметров: {}", pe.getLocalizedMessage());
            params = false;
        }

        if (cl == null || cl.hasOption("h") || cl.hasOption("help") || !params) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.setWidth(132);
            helpFormatter.printHelp(" аргументы для запуска утилиты", options);
            return null;
        }
        logger.debug("Parameters: {}", parameters.toString());
        return parameters.isComplete() ? parameters : null;
    }


}
