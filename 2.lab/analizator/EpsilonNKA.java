

import java.io.Serializable;
import java.util.*;

public class EpsilonNKA implements Serializable {
	private static final long serialVersionUID = 1L;


    private Map<String, List<Production>> gramatika = new HashMap<>();
    private String pocetnoStanje;
    private Map<String, List<String>> zapocinjeNezavrsni;
    private Map<Produkcija, ENKAcvor> obideniCvorovi = new LinkedHashMap<>();
    private List<String> epsilonNezavrsni = new ArrayList<>();
    private List<Produkcija> neObideneProdukcije = new ArrayList<>();

    public EpsilonNKA(Map<String, List<Production>> gramatika, String pocetnoStanje,
                      Map<String, List<String>> zapocinjeNezavrsni, List<String> epsilonNezavrsni) {
        this.gramatika = gramatika;
        this.pocetnoStanje = pocetnoStanje;
        this.zapocinjeNezavrsni = zapocinjeNezavrsni;
        this.epsilonNezavrsni = epsilonNezavrsni;
        generirajNKA();
        int i = 0;
        ENKAcvor cvor1 = null;
        ENKAcvor cvor2 = null;

        //System.out.println("Gotov sa eNKA.");
        //System.out.println("broj stanja: " + obideniCvorovi.size());
        //System.out.println("Sve okej s enka.");

        /*
       for (Map.Entry<Produkcija, ENKAcvor> cvor : obideniCvorovi.entrySet()) {
           if(i == 2){
               cvor1 = cvor.getValue().getEpsilonCvorovi().get(1);
           } else if( i == 9){
               cvor2 = cvor.getValue();
           }
           i++;
            System.out.println("######################");
           System.out.println(cvor.getValue());



            /*
            System.out.print("POCINJE OVDJE:  " + cvor.getValue().getProdukcija() + " IDE ZA ZNAK: " + cvor.getValue().getZnak() + " U ---> ");
            if(cvor.getValue().getIduciCvor() == null){
                System.out.println("null");
            } else {
                System.out.println( cvor.getValue().getIduciCvor().getProdukcija());
            }


            for(ENKAcvor cv : cvor.getValue().getEpsilonCvorovi()){
                System.out.println("#############################");
                System.out.println(cv);
            }


        }
        System.out.println(cvor1 == cvor2);
        */

    }

    public Map<String, List<Production>> getGramatika() {
        return gramatika;
    }

    /*private void prviPrijelaz() {
        List<String> left = gramatika.get(pocetnoStanje).get(0).getProdukti();
        Produkcija prvaProdukcija = new Produkcija(pocetnoStanje, left, 0, Arrays.asList("$"));
        Produkcija drugaProdukcija = new Produkcija(pocetnoStanje, left, left.size(), Arrays.asList("$"));
        List<Produkcija> epsiloni = napraviEpsilonPrijelaze(prvaProdukcija, left.get(0));

        ENKAcvor cvor2 = new ENKAcvor();
        cvor2.setProdukcija(drugaProdukcija);

        List<ENKAcvor> cvorovi = new ArrayList<>();
        for(Produkcija produkcija: )
        ENKAcvor prvi = new ENKAcvor(prvaProdukcija, left.get(0), cvor2, epsiloni);
    }*/


    private boolean tvoriPrazanNiz(List<String> lista, int pocetak) {
        String beta;
        boolean prazanNiz = true;
        for (int i = pocetak, size = lista.size(); i < size; i++) {
            beta = lista.get(i);
            if (!epsilonNezavrsni.contains(beta)) {
                prazanNiz = false;
                break;
            }
        }
        return prazanNiz;
    }

    private List<ENKAcvor> napraviEpsilonPrijelaze(Produkcija p1, String iduciNez) {
        String beta;
        List<String> zapocinjeOdBeta = new ArrayList<>();
        if (p1.getIndeks() + 2 >= p1.getDesnaStrana().size()){
            zapocinjeOdBeta.addAll(p1.getMoguciZnakovi());
        } else {
            int indeks = p1.getIndeks()+2;
            beta = p1.getDesnaStrana().get(indeks);
            //moguci znakovi su svi iz skupa ZAPOCINJE(beta)
            //System.out.println(zapocinjeNezavrsni);
            //System.out.println("beta: " + beta);
            if(zapocinjeNezavrsni.get(beta) == null){
                zapocinjeOdBeta.add(beta);
            } else {
                zapocinjeOdBeta.addAll(zapocinjeNezavrsni.get(beta));
            }




            //ako tvori prazan niz a nije jos ukljucen prazan niz u mogucce znakove
            if (tvoriPrazanNiz(p1.getDesnaStrana(), indeks) && !zapocinjeOdBeta.contains("$")) {
                zapocinjeOdBeta.add("$");
            }

            if(zapocinjeOdBeta.isEmpty()){
                zapocinjeOdBeta.add("$");
            }
        }

        List<ENKAcvor> noviCvorovi = new ArrayList<>();
        for (Production production : gramatika.get(iduciNez)) {
            Produkcija produkcija = new Produkcija(iduciNez, production.getProdukti(), 0, zapocinjeOdBeta);
            if (obideniCvorovi.containsKey(produkcija)) {
                noviCvorovi.add(obideniCvorovi.get(produkcija));
            } else {
                noviCvorovi.add(new ENKAcvor(produkcija));
            }
        }
        return noviCvorovi;
    }

    private void napraviPrijelaz(ENKAcvor cvor) {
        if (obideniCvorovi.containsKey(cvor.getProdukcija())) return;

        obideniCvorovi.put(cvor.getProdukcija(), cvor);
        if (cvor.getProdukcija().jeLiPotpuna()) return;

        String iduciZnak = cvor.getProdukcija().iduciZnak();
        if(gramatika.keySet().contains(iduciZnak)) {
            Produkcija novaProdukcija = cvor.getProdukcija().pomakniTocku();
            ENKAcvor novi = new ENKAcvor(novaProdukcija);
            cvor.setZnak(iduciZnak);
            if (obideniCvorovi.containsKey(novaProdukcija)){
                cvor.setIduciCvor(obideniCvorovi.get(novaProdukcija));
            } else {
                cvor.setIduciCvor(novi);
            }

            napraviPrijelaz(novi);

            List<ENKAcvor> epsiloni = napraviEpsilonPrijelaze(cvor.getProdukcija(), iduciZnak);
            for (ENKAcvor epsCvor : epsiloni){
                /*Produkcija p = epsCvor.getProdukcija();
                if (obideniCvorovi.containsKey(p)) {
                    obideniCvorovi.get(p).getEpsilonCvorovi().add(epsCvor);
                }*/
                napraviPrijelaz(epsCvor);
            }
            cvor.setEpsilonCvorovi(epsiloni);

        } else {
            ENKAcvor novi = new ENKAcvor(cvor.getProdukcija().pomakniTocku());
            if (obideniCvorovi.containsKey(novi.getProdukcija())) {
                cvor.setIduciCvor(obideniCvorovi.get(novi.getProdukcija()));
            } else {
                cvor.setIduciCvor(novi);
            }
            cvor.setZnak(iduciZnak);
            napraviPrijelaz(novi);
        }
    }

    private void generirajNKA() {
        List<String> left = gramatika.get(pocetnoStanje).get(0).getProdukti();
        Produkcija prvaProdukcija = new Produkcija(pocetnoStanje, left, 0, Arrays.asList("$"));
        Produkcija drugaProdukcija = new Produkcija(pocetnoStanje, left, left.size(), Arrays.asList("$"));

        List<ENKAcvor> epsilonCvorovi = napraviEpsilonPrijelaze(prvaProdukcija, left.get(0));

        ENKAcvor drugiCvor = new ENKAcvor(drugaProdukcija);
        ENKAcvor prviCvor = new ENKAcvor(prvaProdukcija);
        //prviCvor.setIduciCvor(drugiCvor);
        //prviCvor.setZnak(left.get(0));
        //prviCvor.setEpsilonCvorovi(epsilonCvorovi);

        napraviPrijelaz(prviCvor);
        zamjeniReference();
    }

    private void zamjeniReference() {

        for (Map.Entry<Produkcija, ENKAcvor> cvor : obideniCvorovi.entrySet()) {

            List<ENKAcvor> listaE = new ArrayList<>();
            for(ENKAcvor ecv : cvor.getValue().getEpsilonCvorovi()){
                listaE.add(obideniCvorovi.get(ecv.getProdukcija()));
            }

            cvor.getValue().setEpsilonCvorovi(listaE);

        }
    }

    public Map<Produkcija, ENKAcvor> getObideniCvorovi() {
        return obideniCvorovi;
    }
}
