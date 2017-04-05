package dreamteam.carpooling.appl.Util;

import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class CreatorAgent extends Agent {

    @Override
    protected void setup() {
        ContainerController cc = getContainerController();
        try {
            AgentController gosha  = cc.createNewAgent("gosha",  "dreamteam.carpooling.appl.CitizenAgent", new Object[] { 1, 2 });
            AgentController nastya = cc.createNewAgent("nastya", "dreamteam.carpooling.appl.CitizenAgent", new Object[] { 3, 4 });
            AgentController nick   = cc.createNewAgent("nick",   "dreamteam.carpooling.appl.CitizenAgent", new Object[] { 5, 6, 3, 10 });
            gosha.start();
            nastya.start();
            nick.start();
        } catch (StaleProxyException spe) {
            spe.printStackTrace();
        }
    }
}
