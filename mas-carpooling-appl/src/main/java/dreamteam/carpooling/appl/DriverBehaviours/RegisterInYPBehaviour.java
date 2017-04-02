package dreamteam.carpooling.appl.DriverBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

/**
 * Регистрация в сервисе Yellow Pages в качестве водителя
 */
public class RegisterInYPBehaviour extends OneShotBehaviour {

    @Override
    public void action() {
        CitizenAgent.logger.info("{} is registered as a driver", myAgent.getAID().getName());

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(myAgent.getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("carpooling");
        sd.setName("JADE-carpooling");

        // TODO: добавить маршрут

        dfd.addServices(sd);

        try {
            DFService.register(myAgent, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
}