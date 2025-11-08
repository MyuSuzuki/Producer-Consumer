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
            System.out.println("[BUFFER DIRECT PLEIN]");
            wait();
        }

        // insérer le message à la position 'in'
        buffer[in] = m;
        in = (in + 1) % bufSz; 
        n++;
        totmsg++;

        System.out.println("Nombre de place prises =" + n);

        notifyAll();
    }

    @Override
    public synchronized Message get() throws InterruptedException {
        // attendre qu'il y ait un message
        while (n == 0) {
            System.out.println("[BUFFER DIRECT VIDE]");
            wait();
        }

        // retirer le message à la position 'out'
        Message m = buffer[out];
        buffer[out] = null;
        out = (out + 1) % bufSz;
        n--;

        System.out.println("Nombre de places prises =" + n);

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
