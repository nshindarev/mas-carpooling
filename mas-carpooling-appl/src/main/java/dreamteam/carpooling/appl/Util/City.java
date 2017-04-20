package dreamteam.carpooling.appl.Util;

import org.jgrapht.alg.FloydWarshallShortestPaths;

import java.io.File;

/**
 * Created by nshindarev on 20.04.17.
 */
public final class City{

    private static MyCityGraph<String, MyWeightedEdge> city;
    private static FloydWarshallShortestPaths<String, MyWeightedEdge> shortestPaths;


    private City(){

    }
    public static void createCity(){
        City.city = new Parser().getCity();
        City.shortestPaths = new FloydWarshallShortestPaths<>(city);
    }

    public static MyCityGraph<String, MyWeightedEdge> getCity (){
        return city;
    }
    public static FloydWarshallShortestPaths<String, MyWeightedEdge> getShortestPaths (){
        return shortestPaths;
    }
}
