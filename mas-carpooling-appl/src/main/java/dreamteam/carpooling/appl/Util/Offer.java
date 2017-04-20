package dreamteam.carpooling.appl.Util;

import jade.core.AID;

/**
 * Объект представляет из себя предложение с ценой и парой (start,finish) из пула предложений водителя
 */
public class Offer {

    public String start, finish;
    public double price;
    public AID id;

    public Offer(String start, String finish, double price, AID id){
        this.id = id;
        this.start = start;
        this.finish = finish;
        this.price = price;
    }
}
