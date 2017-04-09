package dreamteam.carpooling.appl.Util;

import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import  java.util.Random;

import dreamteam.carpooling.appl.Util.Parameters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreatorAgent extends Agent {

    private final Integer countAgentsDefault = 5;
    private final Integer maxCapacityCarConst = 5;
    private final Integer maxCoefRandCost = 9;
    private final Integer maxVertexCity = 11; //TODO связать с городом

    @Override
    protected void setup() {

        Integer countAgents = null;

        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            countAgents = Integer.parseInt(args[0].toString());
        }

        if (countAgents == null)
            countAgents = countAgentsDefault;

        Parameters agentParameters;

        ContainerController cc = getContainerController();
        try {
            for (int i = 0; i < countAgents; i++){
                String nameAgent = "agent_" + i;
                final Random random = new Random();

                Integer capacityCar = random.nextInt(maxCapacityCarConst) + 1; // вместимость машины от 1 до maxCapacityCarConst
                Integer costPerKm = capacityCar * (random.nextInt(maxCoefRandCost) + 1);

                Integer startVertex = random.nextInt(maxVertexCity - 1) + 1;
                Integer endVertex = random.nextInt(maxVertexCity - startVertex) + startVertex + 1;

                agentParameters = new Parameters(String.valueOf(startVertex), String.valueOf(endVertex),
                        String.valueOf(capacityCar), String.valueOf(costPerKm));

                AgentController agent;

                if (agentParameters.havCar()){
                    agent  = cc.createNewAgent(nameAgent,  "dreamteam.carpooling.appl.CitizenAgent",
                            new Object[] { agentParameters.getStartPath(), agentParameters.getFinishPath(), agentParameters.getCapacityPath(), agentParameters.getCostPerKmPathh() });
                } else{
                    agent  = cc.createNewAgent(nameAgent,  "dreamteam.carpooling.appl.CitizenAgent",
                            new Object[] { agentParameters.getStartPath(), agentParameters.getFinishPath()});
                }

                agent.start();
            }

         /*   AgentController gosha  = cc.createNewAgent("gosha",  "dreamteam.carpooling.appl.CitizenAgent", new Object[] { 1, 2 });
            AgentController nastya = cc.createNewAgent("nastya", "dreamteam.carpooling.appl.CitizenAgent", new Object[] { 3, 4 });
            AgentController nick   = cc.createNewAgent("nick",   "dreamteam.carpooling.appl.CitizenAgent", new Object[] { 1, 11, 3, 10 });
            gosha.start();
            nastya.start();
            nick.start();*/

        } catch (StaleProxyException spe) {
            spe.printStackTrace();
        }
    }
}
