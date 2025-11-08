package main.java.prodcons.app;
import main.java.prodcons.core.IProdConsBuffer;
import main.java.prodcons.core.Message;

public class Producer implements Runnable {

    IProdConsBuffer buffer;
    int prodTime;
    int minProd;
    int maxProd;
    int nMessages;

    public Producer (IProdConsBuffer buffer, int prodTime, int nMessages){
         this.buffer=buffer;
         this.prodTime=prodTime;
         this.nMessages=nMessages;
    }

    @Override
    public void run() {
        try {
            for (int i=0; i< nMessages; i++){ 
                System.out.println("Producer " + Thread.currentThread().threadId() + " veut produire "); 
                produce();
                Thread.sleep(prodTime);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void produce() throws InterruptedException{
        buffer.put(new Message("Message du producteur " + Thread.currentThread().threadId() , this)); // produce a message with a random value between 0 and 99
    }
}