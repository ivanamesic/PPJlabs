

import java.io.Serializable;
import java.util.*;

public class DKA implements Serializable {
	private static final long serialVersionUID = 1L;

    public class DKAStanje implements Serializable {
    	private static final long serialVersionUID = 1L;

        private List<ENKAcvor> cvoroviUStanju;
        private int indeks;
        private Map<String, DKAStanje> iducaStanja;
        private Map<String, DKAStanje> akcije;


        public DKAStanje(List<ENKAcvor> prdoukcijeUStanju, int indeks, Map<String, DKAStanje> iducaStanja, Map<String, DKAStanje> akcije) {
            this.cvoroviUStanju = prdoukcijeUStanju;
            this.indeks = indeks;
            this.iducaStanja = iducaStanja;
            this.akcije = akcije;
        }

        public DKAStanje(int indeks) {
            cvoroviUStanju = new ArrayList<>();
            this.indeks = indeks;
            this.iducaStanja = new HashMap<>();
            this.akcije = new HashMap<>();
        }

        public List<ENKAcvor> getCvoroviUStanju() {
            return cvoroviUStanju;
        }

        public int getIndeks() {
            return indeks;
        }

        public Map<String, DKAStanje> getIducaStanja() {
            return iducaStanja;
        }

        public Map<String, DKAStanje> getAkcije() {
            return akcije;
        }

        public void setCvoroviUStanju(List<ENKAcvor> cvoroviUStanju) {
            this.cvoroviUStanju = cvoroviUStanju;
        }

        public void setIndeks(int indeks) {
            this.indeks = indeks;
        }

        public void setIducaStanja(Map<String, DKAStanje> iducaStanja) {
            this.iducaStanja = iducaStanja;
        }

        public void setAkcije(Map<String, DKAStanje> akcije) {
            this.akcije = akcije;
        }

        public boolean jeRedukcija() {
            //System.out.println(indeks + " ------------ " + indeks);
            for (ENKAcvor c : cvoroviUStanju) {
               // System.out.println(c.getProdukcija());
                if (c.getProdukcija().jeLiPotpuna()) {
                    return true;
                }
            }
            return false;

        }

        public  Map<String, List<Production>> vratiRedukciju(){
            Map<String,List<Production>> gramatika = nka.getGramatika();
            Map<String, List<Production>> moguceRedukcije = new LinkedHashMap<>();
            for (Map.Entry<String,List<Production>> g : gramatika.entrySet()){
                for (ENKAcvor c : cvoroviUStanju){
                    if (c.getProdukcija().getPocetnaProdukcija().equals(g.getKey())) {
                        List<String> desnaStrana = new ArrayList<>();
                        desnaStrana.addAll(c.getProdukcija().getDesnaStrana());
                        desnaStrana.remove(desnaStrana.size()-1);
                        for (Production p : g.getValue()) {
                            if (p.getProdukti().equals(desnaStrana)) {
                                for (String moguciZnak : c.getProdukcija().getMoguciZnakovi()) {
                                    if (moguceRedukcije.containsKey(moguciZnak)) {
                                        moguceRedukcije.get(moguciZnak).add(p);
                                    } else {
                                        moguceRedukcije.put(moguciZnak, new ArrayList<>(Arrays.asList(p)));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return  moguceRedukcije;
        }

        @Override
        public String toString() {
            return "DKAStanje{" +
                    "produkcijeUStanju=" + cvoroviUStanju +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DKAStanje dkaStanje = (DKAStanje) o;
            return cvoroviUStanju.equals(dkaStanje.cvoroviUStanju);
        }

        @Override
        public int hashCode() {
            return Objects.hash(cvoroviUStanju);
        }
    }

    private Map<Integer, DKAStanje> prijelazi = new HashMap<>();
    private List<ENKAcvor> obideniNKACvorovi = new ArrayList<>();
    private EpsilonNKA nka;
    private static int dkaCounter = 0;


    private ArrayList<DKAStanje> cvoroviZaObradu = new ArrayList<>();
    private ArrayList<DKAStanje> stanja = new ArrayList<>();

    public DKA(EpsilonNKA nka) {
        this.nka = nka;

        dkaCounter = 0;
        postaviPocetno();
        postaviSveOstale();
        /*for (DKAStanje cs : stanja){

            System.out.println("["+cs.getIndeks()+"]");
            for (ENKAcvor cv : cs.getCvoroviUStanju()){
                System.out.println(cv.getProdukcija());
            }
        }*/
       // napraviPrijelazeDKA();
    }

    private void postaviSveOstale() {

        while(!cvoroviZaObradu.isEmpty()){
            //System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            DKAStanje trenutnoStanje = cvoroviZaObradu.get(0);

            //System.out.println("Kolicina stanja na repertoaru:  " + stanja.size());
            //System.out.println("Broj stanja: " + trenutnoStanje.getCvoroviUStanju().size());

            cvoroviZaObradu.remove(trenutnoStanje);


            HashMap<String, List<ENKAcvor>> prijelaziIzCvora = new HashMap<>();



            for(ENKAcvor cvor : trenutnoStanje.getCvoroviUStanju()){
                //System.out.println("cvor: " + cvor.getProdukcija() + "   za znak: " + cvor.getZnak() + "   ide u --> " + cvor.getIduciCvor());
                if(cvor.getIduciCvor() != null && !cvor.getZnak().isEmpty()){
                    if(prijelaziIzCvora.containsKey(cvor.getZnak())){
                        List<ENKAcvor> list = prijelaziIzCvora.get(cvor.getZnak());
                        list.add(cvor.getIduciCvor());
                        prijelaziIzCvora.put(cvor.getZnak(),list);
                    } else {
                        ArrayList<ENKAcvor> listaCvorova = new ArrayList<ENKAcvor>();
                        listaCvorova.add(cvor.getIduciCvor());
                        prijelaziIzCvora.put(cvor.getZnak(), listaCvorova);
                    }

                }
            }



            for(Map.Entry<String, List<ENKAcvor>> pri : prijelaziIzCvora.entrySet()){

                DKAStanje novoStanje = new DKAStanje(dkaCounter++);
                postaviStanje(novoStanje, pri.getValue());

                //if(dkaCounter == 5){
                    //for(ENKAcvor o : pri.getValue()){
                      //  System.out.println("OVDJE GLEDAJ:  " + o.getProdukcija());
                    //}
                   // System.out.println("OVDJE GLEDAJ:  " + pri.getValue());
                //}

                if(!cvoroviZaObradu.contains(novoStanje) && !stanja.contains(novoStanje)){
                    cvoroviZaObradu.add(novoStanje);
                }

                if(stanja.contains(novoStanje)){
                    novoStanje = stanja.get(stanja.indexOf(novoStanje));
                    dkaCounter--;
                } else {
                    stanja.add(novoStanje);
                }

                trenutnoStanje.getIducaStanja().put(pri.getKey(), novoStanje);
            }
        }
    }

    /*
    private void postaviStanje(DKAStanje novoStanje, List<ENKAcvor> cvors) {
        
        for(ENKAcvor cvor : cvors){
            // System.out.println("Jel radis stanje nutra?");
            if(!novoStanje.getCvoroviUStanju().contains(cvor)){
                novoStanje.getCvoroviUStanju().add(cvor);
            }
            napraviStanje(novoStanje, cvor);
        }

    }

     */

    private void postaviStanje(DKAStanje novoStanje, List<ENKAcvor> cvors) {
        for(ENKAcvor cvor : cvors){
            // System.out.println("Jel radis stanje nutra?");
            if(!novoStanje.getCvoroviUStanju().contains(cvor)){
                ENKAcvor novi = new ENKAcvor();
                novi = cvor.copy();
                novoStanje.getCvoroviUStanju().add(novi);
            }

            boolean ima = true;
            Map<Produkcija, ENKAcvor> mapaEnka = nka.getObideniCvorovi();
            int i = 0;
            while(ima){

                ima = false;

                //System.out.println(i++);

                List<ENKAcvor> pomocnaLista = new ArrayList<>();
                for(ENKAcvor cvr : novoStanje.getCvoroviUStanju()){
                    for(ENKAcvor cvrE : cvr.getEpsilonCvorovi()){
                        if(!novoStanje.getCvoroviUStanju().contains(cvrE) && !pomocnaLista.contains(cvrE)){
                            ima = true;
                            ENKAcvor novi2 = cvrE.copy();
                            pomocnaLista.add(novi2);
                        }
                    }
                }

                novoStanje.getCvoroviUStanju().addAll(pomocnaLista);

                /*
                for(Map.Entry<Produkcija, ENKAcvor> entri : mapaEnka.entrySet()){
                    if(novoStanje.getCvoroviUStanju().contains(entri.getValue()) && ){
                        List<ENKAcvor> aa = entri.getValue().getEpsilonCvorovi();
                        ima = true;
                        for(ENKAcvor c : aa){
                            ENKAcvor novi2 = c.copy();
                            novoStanje.getCvoroviUStanju().add(novi2);
                        }
                    }

                }

                 */

            }


        }

    }

    private void postaviPocetno() {
        HashSet<ENKAcvor> cvorovi = new HashSet<>();

        DKAStanje pocStanje = new DKAStanje(0);

        ArrayList<ENKAcvor> cvori = new ArrayList<>();
        cvori.addAll(nka.getObideniCvorovi().values());
        prijelazi.put(dkaCounter,pocStanje);
        dkaCounter++;
        pocStanje.getCvoroviUStanju().add(cvori.get(0));

        napraviStanje(pocStanje, cvori.get(0));

        stanja.add(pocStanje);
        cvoroviZaObradu.add(pocStanje);




    }

    private void napraviPrijelazeDKA() {
        int i = 0;
        for (ENKAcvor nkaCvor : nka.getObideniCvorovi().values()) {
            if (!obideniNKACvorovi.contains(nkaCvor)) {
                DKAStanje stanje = new DKAStanje(i);
                prijelazi.put(i,stanje);
                i++;
                stanje.getCvoroviUStanju().add(nkaCvor);
                napraviStanje(stanje, nkaCvor);


            }
        }



    }

    private void napraviStanje(DKAStanje stanje, ENKAcvor cvor) {
        //System.out.println(cvor);

        if(!obideniNKACvorovi.contains(cvor)) {
            obideniNKACvorovi.add(cvor);
        }

        //obideniNKACvorovi.add(cvor);
        if (cvor.getEpsilonCvorovi() == null || cvor.getEpsilonCvorovi().size() == 0) return;
        if(cvor.getEpsilonCvorovi().contains(cvor)) return;
        for (ENKAcvor c : cvor.getEpsilonCvorovi()){
            stanje.getCvoroviUStanju().add(c);
            napraviStanje(stanje, c);
        }
    }


    public Map<Integer, DKAStanje> getPrijelazi() {
        return prijelazi;
    }

    public List<ENKAcvor> getObideniNKACvorovi() {
        return obideniNKACvorovi;
    }

    public EpsilonNKA getNka() {
        return nka;
    }

    public static int getDkaCounter() {
        return dkaCounter;
    }

    public ArrayList<DKAStanje> getCvoroviZaObradu() {
        return cvoroviZaObradu;
    }

    public ArrayList<DKAStanje> getStanja() {
        return stanja;
    }

    @Override
    public String toString() {
        return "DKA{" +
                "prijelazi=" + prijelazi +
                '}';
    }

}
