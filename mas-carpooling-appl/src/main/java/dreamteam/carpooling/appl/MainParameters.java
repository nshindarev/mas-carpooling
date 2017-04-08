package dreamteam.carpooling.appl;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by nbirillo on 08.04.17.
 */
public class MainParameters {

    //Параметры запуска приложения - количество агентов
    private String countAgents;

    public MainParameters() {
    }

    public MainParameters(String countAgents) {
        this.countAgents = countAgents;
    }


    public boolean isComplete() {
        return countAgents != null;
    }


    public final String getCountAgents() {
        return countAgents;
    }

    public final void setCountAgents(String countAgents) {
        this.countAgents = countAgents;
    }

}