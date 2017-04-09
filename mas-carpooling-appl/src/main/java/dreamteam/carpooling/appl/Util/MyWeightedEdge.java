package dreamteam.carpooling.appl.Util;

import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Created by nshindarev on 09.04.17.
 */
public class MyWeightedEdge extends DefaultWeightedEdge {
    public MyWeightedEdge(){
        super();
    }
    public double get_weight(){
        return this.getWeight();
    }

}
