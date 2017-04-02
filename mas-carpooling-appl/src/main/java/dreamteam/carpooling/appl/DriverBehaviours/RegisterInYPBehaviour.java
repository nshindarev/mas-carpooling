package dreamteam.carpooling.appl.DriverBehaviours;

import dreamteam.carpooling.appl.CitizenAgent;
import jade.core.behaviours.OneShotBehaviour;

/**
 * Регистрация в сервисе Yellow Pages в качестве водителя
 */
public class RegisterInYPBehaviour extends OneShotBehaviour {

    @Override
    public void action() {
        CitizenAgent.logger.info("{} is registered as a driver", myAgent.getAID().getName());
        // TODO: регистрация в YP в роли водителя
    }
}