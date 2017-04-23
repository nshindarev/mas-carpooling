package dreamteam.carpooling.appl.Util;

import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import  java.util.Random;

public class CreatorAgent extends Agent {

    private final Integer countAgentsDefault = 5;
    private final Integer maxCapacityCarConst = 5;
    private final Integer maxCoefRandCost = 9;
    private Integer maxVertexCity = 11;

    @Override
    protected void setup() {

        maxVertexCity = new Parser().getCity().vertexSet().size();

        /**
         *   для доступа агентов к карте через статическое поле
         */

        City.createCity();

        Integer countAgents = null;
        Integer countDrivers = null;
        Boolean isAutoGenerateAgents = true;
        Integer delayAgents = null;

        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            if (args[0].toString().equals("yes")){
                isAutoGenerateAgents = true;
            } else{
                isAutoGenerateAgents = false;
            }
            countAgents = Integer.parseInt(args[1].toString());
            countDrivers = Integer.parseInt(args[2].toString());
            delayAgents = Integer.parseInt(args[3].toString());
        }

        if (countDrivers > countAgents){
            countDrivers = countAgents;
        }

        ContainerController cc = getContainerController();
        AgentController agent;

        try {
            if (!isAutoGenerateAgents){
                // Ручное создание агентов

                AgentController secretary = cc.createNewAgent("secretary", "dreamteam.carpooling.appl.Util.StatsAgent",
                        new Object[] { 4 });
                secretary.start();

                AgentController gosha   = cc.createNewAgent("gosha",   "dreamteam.carpooling.appl.CitizenAgent", new Object[] { 1, 2 });
                AgentController nastya  = cc.createNewAgent("nastya",  "dreamteam.carpooling.appl.CitizenAgent", new Object[] { 3, 4 });
                AgentController nick    = cc.createNewAgent("nick",    "dreamteam.carpooling.appl.CitizenAgent", new Object[] { 1, 11, 3, 10 });
                AgentController iskrich = cc.createNewAgent("iskrich", "dreamteam.carpooling.appl.CitizenAgent", new Object[] { 1, 7, 3, 10 });

                gosha.start();
                nastya.start();
                nick.start();
                iskrich.start();

                return;
            }
        } catch (StaleProxyException spe) {
            spe.printStackTrace();
        }

        if (countAgents == null)
            countAgents = countAgentsDefault;

        AgentController secretary;
        try {
            secretary = cc.createNewAgent("secretary",  "dreamteam.carpooling.appl.Util.StatsAgent",
                    new Object[] { countAgents.toString() });
            secretary.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

        Parameters agentParameters;

        Integer curCountDriver = 0;

        try {

            // Автоматическая генерация
            for (int i = 0; i < countAgents; i++){
                String nameAgent = "agent_" + i;
                final Random random = new Random();

                Integer capacityCar = 0;
                if (curCountDriver < countDrivers){
                    capacityCar = random.nextInt(maxCapacityCarConst) + 1; // вместимость машины от 1 до maxCapacityCarConst
                    curCountDriver++;
                }
                Integer costPerKm = capacityCar * (random.nextInt(maxCoefRandCost) + 1);

                Integer startVertex = random.nextInt(maxVertexCity - 1) + 1;
                Integer endVertex = random.nextInt(maxVertexCity - startVertex) + startVertex + 1;

                agentParameters = new Parameters(String.valueOf(startVertex), String.valueOf(endVertex),
                        String.valueOf(capacityCar), String.valueOf(costPerKm));


                if (agentParameters.havCar()){
                    agent  = cc.createNewAgent(nameAgent,  "dreamteam.carpooling.appl.CitizenAgent",
                            new Object[] { agentParameters.getStartVertex(), agentParameters.getFinishVertex(), agentParameters.getCapacity(), agentParameters.getCostPerKmPathh() });
                } else{
                    agent  = cc.createNewAgent(nameAgent,  "dreamteam.carpooling.appl.CitizenAgent",
                            new Object[] { agentParameters.getStartVertex(), agentParameters.getFinishVertex()});
                }

                agent.start();
                Thread.sleep(delayAgents.intValue());
            }

        } catch (StaleProxyException spe) {
            spe.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
