package main.java.prodcons.app;

import main.java.prodcons.v1.ProdConsBufferDirect;

import java.io.FileInputStream;
import java.io.InputStream;
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

        IProdConsBuffer buffer = new ProdConsBufferDirect(bufSz);

        // Lancer producteurs et consommateurs dans un ordre mélangé
        for (int i = 0; i < Math.max(nProd, nCons); i++) {
            if (i < nProd && Math.random() < 0.5)
                new Producer(buffer, prodTime, minProd, maxProd);
            if (i < nCons)
                new Consumer(buffer, consTime);
        }
    }
}
