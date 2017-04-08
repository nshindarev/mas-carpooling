package dreamteam.carpooling.appl;

import dreamteam.carpooling.appl.Util.Parser;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLineParser;

import dreamteam.carpooling.appl.MainParameters;

/**
 * Created by nshindarev on 17.03.17.
 */
public class Main {

    public static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Application started by {} class. ", Main.class.getName());
        MainParameters countAgents = parseParameters(args);

        jade.Boot.main(new String[] {
                "-gui",
                "god:dreamteam.carpooling.appl.Util.CreatorAgent(" + countAgents + ")"
        });

        // в качестве параметра -> путь к файлу .gml от mas-carpooling
        Parser parser = new Parser();
        Graph<String, DefaultWeightedEdge> city = parser.getCity();

    }

    private static final String countAgents = "count_agents";

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

                if(countPar == null) {
                    logger.error("Не задан параметр {}", par);
                    params = false;
                }
            }
        }
        catch (ParseException pe){
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
