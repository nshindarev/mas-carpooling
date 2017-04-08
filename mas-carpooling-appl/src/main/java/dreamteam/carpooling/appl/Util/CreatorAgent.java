package dreamteam.carpooling.appl.Util;

import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import dreamteam.carpooling.appl.Util.Parameters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreatorAgent extends Agent {

    @Override
    protected void setup() {

        Integer countAgents = 5;

        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            countAgents = Integer.parseInt(args[0].toString());
        }

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
