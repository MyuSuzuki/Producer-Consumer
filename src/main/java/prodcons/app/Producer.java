package main.java.prodcons.app;
import main.java.prodcons.core.IProdConsBuffer;
import main.java.prodcons.core.Message;

public class Producer extends Thread {

    IProdConsBuffer buffer;
    int prodTime;
    int minProd;
    int maxProd;

    public Producer (IProdConsBuffer buffer, int prodTime, int minProd, int maxProd){
         this.buffer=buffer;
         this.prodTime=prodTime;
         this.minProd=minProd;
         this.maxProd=maxProd;
         this.start();
    }

    @Override
    public void run() {
        try {
            for (int i=0; i< (int)(Math.random()*(maxProd-minProd)+minProd); i++){ // produce a random number of messages between minProd and maxProd
                produce();
                sleep(prodTime);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void produce() throws InterruptedException{
        buffer.put(new Message("New message", this)); // produce a message with a random value between 0 and 99
    }
}