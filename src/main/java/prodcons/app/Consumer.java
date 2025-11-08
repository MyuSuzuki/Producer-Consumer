package main.java.prodcons.app;
import main.java.prodcons.core.IProdConsBuffer;
import main.java.prodcons.core.Message;

public class Consumer implements Runnable {

    private final IProdConsBuffer buffer;
    private final int consTime;
    private static int nbMessagesConsommés;
    private static int totalMessages;

    public Consumer(IProdConsBuffer buffer, int consTime, int totalMessages) {
        this.buffer = buffer;
        this.consTime = consTime;
        Consumer.totalMessages = totalMessages;
    }

    @Override
    public void run() {
        try {
            while (true && nbMessagesConsommés < totalMessages) { //Pour faire terminer les thread j'ajoute une condition
                System.out.println("Consumer " + Thread.currentThread().threadId() + " veut consommer");
                Message m = buffer.get();
                consume(m);
                Thread.sleep(consTime);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void consume(Message m) { 
        //méthode rendu synchronized pour que l'incrémentation du nbMessagesConsommées soit cohérente
        //Pour éviter ça on aurait pu prendre un int atomic pour représenter la variable
        nbMessagesConsommés++;
        int res= totalMessages-nbMessagesConsommés ;
        System.out.println("Il reste "+res+" à lire");
        System.out.println("Message " + m + " consommé par Consumer " + Thread.currentThread().threadId());
    }
}
