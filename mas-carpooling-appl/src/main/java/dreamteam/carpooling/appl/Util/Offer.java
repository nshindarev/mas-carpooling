package dreamteam.carpooling.appl.Util;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.List;

/**
 * Объект представляет из себя предложение с ценой и парой (start,finish) из пула предложений водителя
 */
public class Offer {
    public String start, finish;
    public double price;
    public AID id;
    public ACLMessage message;

    public Offer (ACLMessage message){

      if(message.getPerformative() == ACLMessage.PROPOSE){
          this.id = message.getSender();
          String[] message_info = message.getContent().split(",");

          this.start = message_info[0];
          this.finish = message_info[1];
          this.price  = Double.parseDouble(message_info[2]);

          this.message = message;
      }
    }

    public Offer(String start, String finish, double price, AID id){
        this.id = id;
        this.start = start;
        this.finish = finish;
        this.price = price;
    }
}
