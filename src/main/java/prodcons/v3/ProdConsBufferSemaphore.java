package main.java.prodcons.v3;

import java.util.concurrent.Semaphore;

public class ProdConsBufferSemaphore implements IProdConsBuffer {

    private final Message[] buffer;
    private final int bufSz;
    private int in = 0;      // index d'insertion
    private int out = 0;     // index de retrait
    private int n = 0;       // nombre courant de messages
    private int totmsg = 0;  // nombre total de messages produits
    private boolean productionTerminee = false;

    // Sémaphores
    private final Semaphore empty; // nombre de cases libres
    private final Semaphore full;  // nombre de messages prêts
    private final Semaphore mutex; // exclusion mutuelle
    private final int nCons; // nombre de consommateurs

    public ProdConsBufferSemaphore(int bufSz, int nCons) {
        this.bufSz = bufSz;
        this.nCons = nCons;
        this.buffer = new Message[bufSz];
        this.empty = new Semaphore(bufSz); // toutes les cases sont libres au début
        this.full = new Semaphore(0);      // aucun message prêt au départ
        this.mutex = new Semaphore(1);     // accès exclusif
    }

    @Override
    public void put(Message m) throws InterruptedException {
        empty.acquire();   // attendre une case libre
        mutex.acquire();   // entrer en section critique

        buffer[in] = m;
        in = (in + 1) % bufSz;
        n++;
        totmsg++;

        mutex.release();   // sortir de la section critique
        full.release();    // signaler qu’un message est prêt
    }

    @Override
    public Message get() throws InterruptedException {
        full.acquire();    // attendre un message disponible
        mutex.acquire();   // entrer en section critique

        if (n == 0 && productionTerminee) {
            mutex.release();   // sortir de la section critique
            full.release();    // remettre le sémaphore pour les autres consommateurs
            return null;       // plus de messages possibles
        }

        Message m = buffer[out];
        out = (out + 1) % bufSz;
        n--;

        mutex.release();   // sortir de la section critique
        empty.release();   // signaler qu’une case s’est libérée
        return m;
    }

    @Override
    public int nmsg() {
        try {
            mutex.acquire();
            int res = n;
            mutex.release();
            return res;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return -1;
        }
    }

    @Override
    public int totmsg() {
        try {
            mutex.acquire();
            int res = totmsg;
            mutex.release();
            return res;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return -1;
        }
    }

    @Override
    public void setProductionTerminee() {
        try {
            mutex.acquire();
            productionTerminee = true;
            full.release(nCons);
            mutex.release();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
