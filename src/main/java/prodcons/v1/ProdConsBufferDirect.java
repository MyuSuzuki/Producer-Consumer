package main.java.prodcons.v1;

import main.java.prodcons.core.IProdConsBuffer;
import main.java.prodcons.core.Message;

public class ProdConsBufferDirect implements IProdConsBuffer {

    private final Message[] buffer;  // tampon fixe
    private final int bufSz;         // taille max du buffer
    private int in = 0;              // indice d'insertion
    private int out = 0;             // indice de retrait
    private int n = 0;               // nombre actuel de messages dans le buffer
    private int totmsg = 0;          // total des messages produits depuis la création

    public ProdConsBufferDirect(int bufSz) {
        this.bufSz = bufSz;
        this.buffer = new Message[bufSz];
    }

    @Override
    public synchronized void put(Message m) throws InterruptedException {
        // attendre qu'il y ait une place libre
        while (n == bufSz) {
            wait();
        }

        // insérer le message à la position 'in'
        buffer[in] = m;
        in = (in + 1) % bufSz;
        n++;
        totmsg++;

        System.out.println("[BUFFER] Message produit : " + m + " | n=" + n);

        // réveiller les consommateurs éventuels
        notifyAll();
    }

    @Override
    public synchronized Message get() throws InterruptedException {
        // attendre qu'il y ait un message
        while (n == 0) {
            wait();
        }

        // retirer le message à la position 'out'
        Message m = buffer[out];
        buffer[out] = null;
        out = (out + 1) % bufSz;
        n--;

        System.out.println("[BUFFER] Message consommé : " + m + " | n=" + n);

        // réveiller les producteurs éventuels
        notifyAll();

        return m;
    }

    @Override
    public synchronized int nmsg() {
        return n;
    }

    @Override
    public synchronized int totmsg() {
        return totmsg;
    }
}
