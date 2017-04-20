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
import org.jgrapht.GraphPath;

import java.util.ArrayList;
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

        GraphPath<String, MyWeightedEdge> route = ((CitizenAgent) myAgent).getCurrentRoute();
        List<District> districts = getDistrictsByRoute(route.getEdgeList());

        StringBuilder districtsParam = new StringBuilder();
        for (District district : districts) {
            districtsParam.append(district.getDistrictName()).append(",");
        }
        districtsParam.deleteCharAt(districtsParam.length() - 1);

        Property p = new Property();
        p.setName("districts");
        p.setValue(districtsParam.toString());
        sd.addProperties(p);

        dfd.addServices(sd);

        try {
            DFService.register(myAgent, dfd);
            CitizenAgent.logger.info("{} is registered as a driver", myAgent.getAID().getLocalName());
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private List<District> getDistrictsByRoute(List<MyWeightedEdge> route) {
        List<District> result = new ArrayList<>();

        for (District district : ((CitizenAgent) myAgent).getCity().getCity_districts()) {
            for (MyWeightedEdge edge : route) {
                if (!result.contains(district) && (
                        district.getVertexes().contains(edge.getSource().toString()) ||
                        district.getVertexes().contains(edge.getTarget().toString()))) {
                    result.add(district);
                }
            }
        }

        return result;
    }
}
