package dreamteam.carpooling.appl.Util;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by nbirillo on 08.04.17.
 */
public class Parameters {

    //Параметры агента - стартовая вершина, конечная вершина,
    // вместимость машины, стоимость проезда за км

    private String startPath;
    private String finishPath;
    private String capacityPath;
    private String costPerKmPath;

    public Parameters() {
    }

    public Parameters(String startPath, String finishPath, String capacityPath, String costPerKmPath) {
        this.startPath = startPath;
        this.finishPath = finishPath;
        this.capacityPath = capacityPath;
        this.costPerKmPath = costPerKmPath;
    }


    public boolean isComplete() {
        return startPath != null
                && finishPath != null
                && capacityPath   != null
                && costPerKmPath   != null;
    }


    public final String getStartPath() {
        return startPath;
    }

    public final void setStartPath(String startPath) {
        this.startPath = startPath;
    }

    public final String getFinishPath() {
        return finishPath;
    }

    public final void setFinishPath(String finishPath) {
        this.finishPath = finishPath;
    }

    public final String getCapacityPath() {
        return capacityPath;
    }

    public final void setCapacityPath(String destPath) {
        this.capacityPath = capacityPath;
    }

    public final String getCostPerKmPathh() {
        return costPerKmPath;
    }

    public final void setCostPerKmPath(String costPerKmPath) {
        this.costPerKmPath = costPerKmPath;
    }

}