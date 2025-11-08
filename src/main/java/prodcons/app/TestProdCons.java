package main.java.prodcons.app;

import main.java.prodcons.v1.ProdConsBufferDirect;
import main.java.prodcons.v2.ProdConsBufferDirectTerm;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import main.java.prodcons.core.IProdConsBuffer;

public class TestProdCons {
    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        try (InputStream is = new FileInputStream("src/main/resources/prodcons/options.xml")) {
            properties.loadFromXML(is);
        }

        int nProd = Integer.parseInt(properties.getProperty("nProd"));
        int nCons = Integer.parseInt(properties.getProperty("nCons"));
        int bufSz = Integer.parseInt(properties.getProperty("bufSz"));
        int prodTime = Integer.parseInt(properties.getProperty("prodTime"));
        int consTime = Integer.parseInt(properties.getProperty("consTime"));
        int minProd = Integer.parseInt(properties.getProperty("minProd"));
        int maxProd = Integer.parseInt(properties.getProperty("maxProd"));

        IProdConsBuffer buffer = new ProdConsBufferDirectTerm(bufSz);

        int totalMessages = 0; // pour compter tous les messages produits

        List<Thread> producerThreads = new ArrayList<>();
        List<Thread> consumerThreads = new ArrayList<>();

        // Lancement des producteurs et consommateurs
        for (int i = 0; i < Math.max(nProd, nCons); i++) {
            if (i < nProd && Math.random() < 0.5) {
                int n = (int) (Math.random() * (maxProd - minProd) + minProd);
                totalMessages += n;

                Producer producer = new Producer(buffer, prodTime, n);
                Thread t = new Thread(producer);
                t.start();
                producerThreads.add(t);
            }

            if (i < nCons-1) { //je m'assure que le dernier thread créé soit un Consumer pour avoir la bonne valeur de totalMessages
                Consumer consumer = new Consumer(buffer, consTime, totalMessages);
                Thread t = new Thread(consumer);
                t.start();
                consumerThreads.add(t);
            }
        }

        Thread lastConsumer = new Thread(new Consumer(buffer, consTime, totalMessages));
        lastConsumer.start();
        consumerThreads.add(lastConsumer);

        // On attend que tous les producteurs aient fini
        for (Thread t : producerThreads) {
            t.join();
        }
        buffer.setProductionTerminee();

        // On attend que tous les consumers aient fini
        for (Thread t : consumerThreads) {
            t.join();
        }

        System.out.println("Tous les messages ont été consommés. Fin de l'application.");
    }
}
