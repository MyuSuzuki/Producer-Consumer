package main.java.prodcons.core;
import main.java.prodcons.app.Producer;

public class Message {
    private final String content;
    Producer prod;

    public Message(String content, Producer prod) {
        this.prod = prod;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return content;
    }

    public Producer getProducer() {
        return prod;
    }
}