package main.java.prodcons.app;
import main.java.prodcons.core.IProdConsBuffer;
import main.java.prodcons.core.Message;

public class Consumer extends Thread {

    private final IProdConsBuffer buffer;
    private final int consTime;

    public Consumer(IProdConsBuffer buffer, int consTime) {
        this.buffer = buffer;
        this.consTime = consTime;
        this.start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                Message m = buffer.get();
                consume(m);
                sleep(consTime);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void consume(Message m) {
        System.out.println("Consumer " + Thread.currentThread().threadId() + " consomme " + m);
    }
}
