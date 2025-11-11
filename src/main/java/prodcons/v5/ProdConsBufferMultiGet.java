package main.java.prodcons.v5;

public class ProdConsBufferMultiGet implements IProdConsBuffer {

    private final Message[] buffer;  // tampon fixe
    private final int bufSz;         // taille max du buffer
    private int in = 0;              // indice d'insertion
    private int out = 0;             // indice de retrait
    private int n = 0;               // nombre actuel de messages dans le buffer
    private int totmsg = 0;          // total des messages produits depuis la création
    private boolean productionTerminee = false; //flag représentant l'état de vie des thread producer


    public ProdConsBufferMultiGet(int bufSz) {
        this.bufSz = bufSz;
        this.buffer = new Message[bufSz];
    }

    @Override
    public synchronized void put(Message m) throws InterruptedException {
        // attendre qu'il y ait une place libre
        while (n == bufSz) {
            System.out.println("[BUFFER PLEIN]");
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
            if (productionTerminee) {
            return null; // signal d’arrêt pour le consumer
        }
            System.out.println("[BUFFER VIDE]");
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
    public synchronized Message[] get(int k) throws InterruptedException {
        // attendre qu'il y ait au moins k messages
        while (n < k) {
            if (productionTerminee && n > 0) {
                // Si la production est terminée mais qu'il reste des messages,
                // on retourne tous les messages restants
                k = n;
                break;
            } else if (productionTerminee && n == 0) {
                return null; // signal d’arrêt pour le consumer
            }
            
            System.out.println("[PAS ASSEZ DE MESSAGES]");
            wait();
        }

        Message[] messages = new Message[k];
        for (int i = 0; i < k; i++) {
            messages[i] = buffer[out];
            buffer[out] = null;
            out = (out + 1) % bufSz;
            n--;
        }

        System.out.println("Nombre de places prises =" + n);

        // réveiller les producteurs et consumers éventuels
        notifyAll();

        return messages;
        
    }

    @Override
    public synchronized int nmsg() {
        return n;
    }

    @Override
    public synchronized int totmsg() {
        return totmsg;
    }

   public synchronized void setProductionTerminee() {
        productionTerminee = true;
        notifyAll(); // réveiller tous les consumers bloqués
}

 

    
}
