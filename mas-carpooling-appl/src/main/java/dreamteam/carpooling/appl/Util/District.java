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
    private String districtName;

    public ArrayList<String> getVertexes(){
        return this.vertexes;
    }
    public String getDistrictName(){ return this.districtName; }

    public District (String districtName, List<String> v_in_district){
        for (String s:
             v_in_district) {
            vertexes.add(s);
        }
        this.districtName = districtName;
    }

    public boolean isInDistrict (String vertex){
        if (vertexes.contains(vertex)) return  true;
        else  return false;
    }

    public static boolean isInDistrict (District d, String v){
       return d.isInDistrict(v);
    }


}

