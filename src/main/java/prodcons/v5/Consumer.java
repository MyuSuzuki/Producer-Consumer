package main.java.prodcons.v5;

public class Consumer implements Runnable {

    private final IProdConsBuffer buffer;
    private final int consTime;

    public Consumer(IProdConsBuffer buffer, int consTime, int totalMessages) {
        this.buffer = buffer;
        this.consTime = consTime;
    }

    @Override
    public void run() {
        try {
            while (true) { 
                System.out.println("Consumer " + Thread.currentThread().threadId() + " veut consommer");
                int k = (int)(Math.random()*5) + 1 ; // nombre aléatoire entre 1 et 5
                Message[] messages = buffer.get(k);
                if (messages == null) {
                System.out.println("Consumer " + Thread.currentThread().threadId() + " termine");
                break; // plus de messages possibles
                }
            for (Message message : messages) {              
                consume(message,k);   
                Thread.sleep(consTime);
                }
         } 
        } catch (InterruptedException e) {
            e.printStackTrace();
            }
}
    private void consume(Message m, int k) { 
        System.out.println(k  + " messages ont été consommés par Consumer " + Thread.currentThread().threadId());
    }
}
