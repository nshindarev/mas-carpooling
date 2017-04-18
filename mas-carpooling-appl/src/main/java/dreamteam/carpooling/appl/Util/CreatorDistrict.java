package dreamteam.carpooling.appl.Util;

        import java.util.ArrayList;
        import java.util.LinkedList;
        import java.util.List;
        import java.util.Queue;
        import java.lang.*;

/**
 * Created by User on 18.04.2017.
 */
public class CreatorDistrict {

    private ArrayList<District> districts = new ArrayList<>();

    public ArrayList<District> getDistricts(){
        return this.districts;
    }

    public CreatorDistrict (List<District> d_in_districts){
        for (District s:
                d_in_districts) {
            districts.add(s);
        }
    }

    public CreatorDistrict(){
        Parser parser = new Parser();
        parser.parseCityFromFile();
        parser.getCity();

        Integer countVertex = parser.getCity().vertexSet().size();
        Integer countDistrict = (int) (1.17 + Math.sqrt(1.36 + 0.13*countVertex));
        Integer countVertexDistrict = countVertex / countDistrict;
        Integer currentNumberDistrict = 1;

        List<String> v_in_district = new ArrayList<String>();

        //идем по вершинам
        for (String v:
                parser.getCity().vertexSet()){
            //добавили в район
            v_in_district.add(v);
            //помечаем прочитанной ДОБАВИТЬ

            //добавляем смежные в очередь ДОБАВИТЬ
            Queue<String> neighboringVertQueue = new LinkedList<String>();
            String elementInQueue = neighboringVertQueue.poll();

            Integer curCountVertexDistrict = v_in_district.size();
            while (elementInQueue != null){
                v_in_district.add(elementInQueue);
                //ОБРАБОТКА СЛЕД ВЕРШИН
                if (curCountVertexDistrict < countVertexDistrict){
                    //обрабатываем дальше
                    //помечаем прочитанной
                } else{
                    //помечаем ребро добавленным
                }
                elementInQueue = neighboringVertQueue.poll();
            }

            District curDistrict = new District("D" + currentNumberDistrict, v_in_district);
            this.districts.add(curDistrict);
            v_in_district = new ArrayList<String>();
            currentNumberDistrict++;
        }
    }
}
