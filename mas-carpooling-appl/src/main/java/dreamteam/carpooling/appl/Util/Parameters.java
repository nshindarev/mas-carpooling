package dreamteam.carpooling.appl.Util;

/**
 * Created by nbirillo on 08.04.17.
 */
public class Parameters {

    //Параметры агента - стартовая вершина, конечная вершина,
    // вместимость машины, стоимость проезда за км

    private String startVertex;
    private String finishVertex;
    private String capacity;
    private String costPerKm;

    public Parameters() {
    }

    public Parameters(String startVertex, String finishVertex, String capacity, String costPerKm) {
        this.startVertex = startVertex;
        this.finishVertex = finishVertex;
        this.capacity = capacity;
        this.costPerKm = costPerKm;
    }

    public Parameters(String startVertex, String finishVertex) {
        this.startVertex = startVertex;
        this.finishVertex = finishVertex;
        this.capacity = null;
        this.costPerKm = null;
    }


    public boolean isComplete() {
        return startVertex != null
                && finishVertex != null
                && capacity != null
                && costPerKm != null;
    }

    public boolean havCar() {
        return !capacity.equals("0");
    }


    public final String getStartVertex() {
        return startVertex;
    }

    public final void setStartVertex(String startVertex) {
        this.startVertex = startVertex;
    }

    public final String getFinishVertex() {
        return finishVertex;
    }

    public final void setFinishVertex(String finishVertex) {
        this.finishVertex = finishVertex;
    }

    public final String getCapacity() {
        return capacity;
    }

    public final void setCapacity(String destPath) {
        this.capacity = capacity;
    }

    public final String getCostPerKmPathh() {
        return costPerKm;
    }

    public final void setCostPerKm(String costPerKm) {
        this.costPerKm = costPerKm;
    }

}