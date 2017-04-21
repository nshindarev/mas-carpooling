package dreamteam.carpooling.appl.PassengerBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import dreamteam.carpooling.appl.Util.District;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.util.leap.Iterator;

import java.util.LinkedList;
import java.util.List;

/**
 * Поиск предложений от водителей. Когда пассажир находит подходящего водителя,
 * он записывает его в персональный список.
 */
public class SearchDriversOffersInYPBehaviour extends OneShotBehaviour {

    private CitizenAgent myCitizenAgent = (CitizenAgent) myAgent;

    public List<AID> addedDrivers = new LinkedList<>();

    @Override
    public void action() {
        // CitizenAgent.logger.info("{} is searching offers from drivers", myAgent.getAID().getLocalName());

        try {
            // Build the description used as template for the search
            DFAgentDescription agentDescription = new DFAgentDescription();
            ServiceDescription serviceDescription = new ServiceDescription();
            serviceDescription.setType("carpooling");
            agentDescription.addServices(serviceDescription);

            DFAgentDescription[] results = DFService.search(myAgent, agentDescription);

            if (results.length > 0) {
                for (DFAgentDescription dfd : results) {
                    AID provider = dfd.getName();
                    // The same agent may provide several services; we are only interested
                    // in the carpooling one
                    Iterator it = dfd.getAllServices();
                    while (it.hasNext()) {
                        ServiceDescription sd = (ServiceDescription) it.next();
                        if (sd.getType().equals("carpooling")) {
                            // Смотрим на маршрут водителя
                            Iterator properties = sd.getAllProperties();
                            while (properties.hasNext()) {
                                Property districts = (Property) properties.next();
                                // Если маршрут подходит и этого водителя ещё нет в нашем списке, добавляем его
                                if (!provider.equals(myAgent.getAID())
                                        && !addedDrivers.contains(provider)
                                        && districtsAreSuitable(districts.getValue().toString().split(","))) {
                                    myCitizenAgent.suitableDrivers.add(provider);
                                    addedDrivers.add(provider);
                                    CitizenAgent.logger.info("{} found new suitable driver: {}",
                                            myAgent.getAID().getLocalName(),
                                            provider.getLocalName());
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private boolean districtsAreSuitable(String[] districts) {
        boolean foundStartDistrict  = false;
        boolean foundFinishDistrict = false;
        boolean districtIsSuitable = false;

        String startVertex  = myCitizenAgent.getStart();
        String finishVertex = myCitizenAgent.getFinish();

        for (String name: districts){
            District district = myCitizenAgent.getCity().getDistrictByName(name);

            if(!foundStartDistrict){
                foundStartDistrict = district.isInDistrict(startVertex);

                /**
                 * если нашли старт в районе, финиш тоже ищем в нём
                 */
                if(foundStartDistrict){
                    foundFinishDistrict = district.isInDistrict(finishVertex);
                    districtIsSuitable = foundFinishDistrict;
                }
            }
            /**
             * если старт уже найден, ищем только финиш
             */
            else if(!foundFinishDistrict){
                foundFinishDistrict = district.isInDistrict(finishVertex);
                districtIsSuitable = foundFinishDistrict;
            }
            if (districtIsSuitable) break;
        }
        return  districtIsSuitable;
    }
}
