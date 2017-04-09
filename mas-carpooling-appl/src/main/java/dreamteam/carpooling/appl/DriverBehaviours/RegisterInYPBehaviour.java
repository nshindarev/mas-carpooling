package dreamteam.carpooling.appl.DriverBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import dreamteam.carpooling.appl.Util.District;
import dreamteam.carpooling.appl.Util.MyWeightedEdge;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.List;

/**
 * Регистрация в сервисе Yellow Pages в качестве водителя
 */
public class RegisterInYPBehaviour extends OneShotBehaviour {

    @Override
    public void action() {

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(myAgent.getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("carpooling");
        sd.setName("JADE-carpooling");

        List<MyWeightedEdge> route = ((CitizenAgent) myAgent).getCurrentRoute();
        District[] districts = getDistrictsByRoute(route);

        StringBuilder districtsParam = new StringBuilder();
        for (District district : districts) {
            districtsParam.append(district.getDistrictName()).append(",");
        }
        districtsParam.deleteCharAt(districtsParam.length() - 1);

        Property p = new Property();
        p.setName("districts");
        p.setValue(districts);
        sd.addProperties(p);

        dfd.addServices(sd);

        try {
            DFService.register(myAgent, dfd);
            CitizenAgent.logger.info("{} is registered as a driver", myAgent.getAID().getName());
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private District[] getDistrictsByRoute(List<MyWeightedEdge> route) {
        // TODO: определять районы по маршруту
        return new District[0];
    }
}
