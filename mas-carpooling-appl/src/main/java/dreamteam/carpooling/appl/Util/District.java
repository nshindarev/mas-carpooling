package dreamteam.carpooling.appl.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nshindarev on 08.04.17.
 */
public class District {

    /**
     * перечень вершин в районе
     */
    private ArrayList<String> vertexes = new ArrayList<>();

    public ArrayList<String> getVertexes(){
        return this.vertexes;
    }

    public District (List<String> v_in_district){
        for (String s:
             v_in_district) {
            vertexes.add(s);
        }
    }

    public boolean isInDistrict (String vertex){
        if (vertexes.contains(vertex)) return  true;
        else  return false;
    }

    public static boolean isInDistrict (District d, String v){
       return d.isInDistrict(v);
    }
}

