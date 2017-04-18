package dreamteam.carpooling.appl.Util;

        import org.jgrapht.Graphs;

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
    private List<String> vertexVisited = new ArrayList<String>();

    public ArrayList<District> getDistricts(){
        return this.districts;
    }

    public CreatorDistrict (List<District> d_in_districts){
        for (District s:
                d_in_districts) {
            districts.add(s);
        }
    }

    public CreatorDistrict(Parser parser){

        MyCityGraph<String, MyWeightedEdge> city = parser.getCity();

        //всего вершин, всего районов, вершин в районе (примерно), номер района
        Integer countVertex = city.vertexSet().size();
        Integer countDistrict = (int) (1.17 + Math.sqrt(1.36 + 0.13*countVertex) + 1);
        Integer countVertexDistrict = countVertex / countDistrict;
        Integer currentNumberDistrict = 1;

        //вершины в текущем районе
        List<String> v_in_district = new ArrayList<String>();

        Queue<String> neighboringVertQueue = new LinkedList<String>();

        //идем по вершинам
        for (String v:
                city.vertexSet()){
            Boolean isAllVisited = true;

                HandleVertex(isAllVisited, city, countVertexDistrict, v_in_district, v, neighboringVertQueue);

                if (v_in_district.size() != 0) {
                    //помечаем использованные дуги
                    for (String vertex:
                            v_in_district){
                        for (MyWeightedEdge edge :
                                city.edgesOf(vertex)){
                            edge.setUsed(true);
                        }
                    }

                    //создаем район
                    District curDistrict = new District("D" + currentNumberDistrict, v_in_district);
                    this.districts.add(curDistrict);
                    v_in_district = new ArrayList<String>();
                    currentNumberDistrict++;
                }
            }
    }

    private void HandleVertex(Boolean isAllVisited, MyCityGraph<String, MyWeightedEdge> sity, Integer countVertexDistrict, List<String> v_in_district, String v, Queue<String> neighboringVertQueue) {
        //Если все дуги из вершины посещены, то пропускаем ее
        isAllVisited = true;
        for (MyWeightedEdge edge :
                sity.edgesOf(v)) {

            if (!edge.getUsed()) {
                isAllVisited = false;
                break;
            }
        }

        if (!isAllVisited) {
            //добавили в район, если по ней еще не ходили и ее там уже нет
            if (!vertexVisited.contains(v)) {
                if (!v_in_district.contains(v)) {
                    v_in_district.add(v);
                }
            }
            //помечаем прочитанной
            vertexVisited.add(v);

            //добавляем смежные в очередь
            List<String> listNeighboringVer = Graphs.neighborListOf(sity, v);
            for (String nv :
                    listNeighboringVer) {
                 if(!v_in_district.contains(nv)) {
                     if (!vertexVisited.contains(nv)) {
                         v_in_district.add(nv);
                     }
                         //если добавили, то добавляем в очередь
                         neighboringVertQueue.add(nv);
                     }
            }

            String elementInQueue = neighboringVertQueue.poll();

            Integer curCountVertexDistrict = v_in_district.size();
            while (elementInQueue != null) {
                //помечаем ребро добавленным
                if (sity.getEdge(v, elementInQueue) != null)
                    sity.getEdge(v, elementInQueue).setUsed(true);
                if (sity.getEdge(elementInQueue, v) != null)
                    sity.getEdge(elementInQueue, v).setUsed(true);
                //ОБРАБОТКА СЛЕД ВЕРШИН
                if (curCountVertexDistrict < countVertexDistrict) {
                    //обрабатываем дальше
                    HandleVertex(isAllVisited, sity, countVertexDistrict, v_in_district, elementInQueue, neighboringVertQueue);
                } else {
                    //добавляем вершину в район, если ее там нет
                    if(!v_in_district.contains(elementInQueue)) {
                        v_in_district.add(elementInQueue);
                    }

                }
                elementInQueue = neighboringVertQueue.poll();
            }
        }
    }
}
