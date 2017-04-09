package dreamteam.carpooling.appl;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by nbirillo on 08.04.17.
 */
public class MainParameters {

    //Параметры запуска приложения - количество агентов, автоматическая генерация или нет
    // количество агентов с машинами
    private String countAgents;
    private String autoGenerateAgents;
    private String countDriver;

    public MainParameters() {
    }

    public MainParameters(String countAgents, String autoGenerateAgents, String countDriver) {
        this.countAgents = countAgents;
        this.autoGenerateAgents = autoGenerateAgents;
        this.countDriver = countDriver;
    }


    public boolean isComplete() {
        return countAgents != null
                & autoGenerateAgents != null
                & countDriver != null;
    }


    public final String getCountAgents() {
        return countAgents;
    }

    public final void setCountAgents(String countAgents) {
        this.countAgents = countAgents;
    }

    public final String getAutoGenerateAgents() {
        return autoGenerateAgents;
    }

    public final void setAutoGenerateAgents(String autoGenerateAgents) {
        this.autoGenerateAgents = autoGenerateAgents;
    }

    public final String getСountDriver() {
        return countDriver;
    }

    public final void setСountDrivers(String countDriver) {
        this.countDriver = countDriver;
    }

}