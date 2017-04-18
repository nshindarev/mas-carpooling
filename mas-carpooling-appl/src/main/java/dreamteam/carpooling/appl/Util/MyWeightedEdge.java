package dreamteam.carpooling.appl.Util;

import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Created by nshindarev on 09.04.17.
 */
public class MyWeightedEdge extends DefaultWeightedEdge {
    private  Boolean used = false;

    public MyWeightedEdge(){
        super();
    }
    public double get_weight(){
        return this.getWeight();
    }

    public Object getSource() {
        return super.getSource();
    }

    public Object getTarget() {
        return super.getTarget();
    }

    public final Boolean getUsed() {
        return used;
    }

    public final void setUsed(Boolean used) {
        this.used = used;
    }

}
