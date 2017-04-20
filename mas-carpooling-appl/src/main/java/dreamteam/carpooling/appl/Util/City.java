package dreamteam.carpooling.appl.Util;

import java.io.File;

/**
 * Created by nshindarev on 20.04.17.
 */
public final class City{

    private static MyCityGraph<String, MyWeightedEdge> city;

    private City(){

    }
    public static void createCity(){
        City.city = new Parser().getCity();
    }
    public static void createCity(File file){
        City.city = new Parser(file).getCity();
    }

    public static MyCityGraph<String, MyWeightedEdge> getCity (){
        return city;
    }
}
