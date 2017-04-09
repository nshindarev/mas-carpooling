package dreamteam.carpooling.appl;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by nbirillo on 08.04.17.
 */
public class MainParameters {

    //Параметры запуска приложения - количество агентов, автоматическая генерация или нет
    // количество агентов с машинами, время задержки при создании агента
    private String countAgents;
    private String autoGenerateAgents;
    private String countDriver;
    private String delayAgents;

    public MainParameters() {
    }

    public MainParameters(String countAgents, String autoGenerateAgents, String countDriver, String delayAgents) {
        this.countAgents = countAgents;
        this.autoGenerateAgents = autoGenerateAgents;
        this.countDriver = countDriver;
        this.delayAgents = delayAgents;
    }


    public boolean isComplete() {
        return countAgents != null
                & autoGenerateAgents != null
                & countDriver != null
                & delayAgents != null;
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

    public final String getDelayAgents() {
        return delayAgents;
    }

    public final void setDelayAgents(String delayAgents) {
        this.delayAgents = delayAgents;
    }

}