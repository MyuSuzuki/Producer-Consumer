package main.java.prodcons.v1;

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
                Message m = buffer.get();
                if (m == null) {
                System.out.println("Consumer " + Thread.currentThread().threadId() + " termine");
                break; // plus de messages possibles
            }
                consume(m);
                Thread.sleep(consTime);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void consume(Message m) { 
        System.out.println("Message " + m + " consommé par Consumer " + Thread.currentThread().threadId());
    }
}
