package dreamteam.carpooling.appl.Util;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by nshindarev on 08.04.17.
 */
public class MyCityGraph<V,E> extends SimpleWeightedGraph<V,E> {

    public static final Logger logger = LoggerFactory.getLogger(MyCityGraph.class);
    private List<District> city_districts;
    private List<String> vertexes_used_in_districts;

    public MyCityGraph(Class<? extends E> edgeClass){

        super(edgeClass);

        vertexes_used_in_districts = new LinkedList<>();
        city_districts = new LinkedList<>();
    }

    public  List<District> getCity_districts(){
        return this.city_districts;
    }

    public District getDistrictByName(String districtName) {
        for (District district : this.city_districts) {
            if (district.getDistrictName().equals(districtName)) {
                return district;
            }
        }
        return null;
    }

    //TODO: обработать случай, где передаются вершины из другого района
    public void addCity_district(String district_name, List<String> vertexes){
       boolean b = true;
       try{
           for (String s:
                   vertexes) {
               //if(vertexes_used_in_districts.contains(s)) b = false;
           }
       }
       catch (NullPointerException ex){
           logger.error("Nullpointer!!!!!!!!");
       }
        if (b){
            this.city_districts.add(new District(district_name, vertexes));
            for (String s:
                 vertexes) {
                this.vertexes_used_in_districts.add(s);
            }
        }
    }
}
