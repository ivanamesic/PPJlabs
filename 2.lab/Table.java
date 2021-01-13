

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table implements Serializable {
	private static final long serialVersionUID = 1L;

    DKA dka;
    HashMap<Integer,HashMap<String,Akcija >> akcija;
    HashMap<Integer,HashMap<String, Akcija>> novoStanje;
    List<String> zavrsni;
    List<String> nezavrsni;

    public Table (List<String> zavrsni,List<String> nezavrsni, DKA dka){
        akcija = new HashMap<>();
        novoStanje = new HashMap<>();
        this.dka = dka;
        this.zavrsni = zavrsni;
        this.nezavrsni = nezavrsni;
        generateTable();
    }

	@SuppressWarnings("unchecked")
    private void generateTable() {
        List<DKA.DKAStanje> stanja = dka.getStanja();
        for (DKA.DKAStanje s : stanja){

          /*  System.err.println("[" + s.getIndeks() + "]");
            for(ENKAcvor cv : s.getCvoroviUStanju()){
                System.err.println(cv.getProdukcija());
            }*/


            //dodavanje prihvati
            if (s.getCvoroviUStanju().size() == 1 && s.getCvoroviUStanju().get(0).getProdukcija().getPocetnaProdukcija().equals(nezavrsni.get(0)) && s.getCvoroviUStanju().get(0).getProdukcija().jeLiPotpuna()){
                HashMap<String, Akcija> unos = new HashMap<>();
                unos.put("$", new Akcija(AkcijaEnum.Prihvati,-1,null));
                akcija.put(s.getIndeks(), unos);
                //System.out.println("prihvati " + s.getCvoroviUStanju());
                continue;
            }

            if(s.getCvoroviUStanju().get(0).getProdukcija().getPocetnaProdukcija().equals(nezavrsni.get(0)) && s.getCvoroviUStanju().get(0).getProdukcija().jeLiPotpuna()) {
                HashMap<String, Akcija> unos = new HashMap<>();
                unos.put("$", new Akcija(AkcijaEnum.Prihvati,-1,null));
                akcija.put(s.getIndeks(), unos);
                //System.out.println("prihvati " + s.getCvoroviUStanju());

            } else if (s.jeRedukcija()){
                //System.out.println("IMAAAMOO REDUKCIJUUU:  " + s.getIndeks());
                HashMap<String, Akcija> unos = new HashMap<>();
                Map<String, List<Production>> moguceRedukcije = s.vratiRedukciju();
                for (String moguciZnak : moguceRedukcije.keySet()) {
                    unos.put(moguciZnak, new Akcija(AkcijaEnum.Reduciraj, -1, moguceRedukcije.get(moguciZnak).get(0)));
                }
                akcija.put(s.getIndeks(), unos);
                //continue;
            }



            for (Map.Entry<String, DKA.DKAStanje> s2 : s.getIducaStanja().entrySet()){

                /*if(s.getIndeks() == 4){
                    System.out.println(zavrsni.contains(s2.getKey()));
                }*/

                if (zavrsni.contains(s2.getKey())) {
                    
                    if (akcija.get(s.getIndeks()) == null) {
                        HashMap<String, Akcija> unos = new HashMap<>();
                        unos.put(s2.getKey(), new Akcija(AkcijaEnum.Pomakni,s2.getValue().getIndeks(),null)); //dodaj u akcija
                        akcija.put(s.getIndeks(), unos);
                    } else {
                        akcija.get(s.getIndeks()).put(s2.getKey(), new Akcija(AkcijaEnum.Pomakni,s2.getValue().getIndeks(),null));//dodaj u akcija
                    }
                }else if (nezavrsni.contains(s2.getKey())){
                    if (novoStanje.get(s.getIndeks()) == null) {
                        HashMap<String, Akcija> unos = new HashMap<>();
                        unos.put(s2.getKey(), new Akcija(AkcijaEnum.Stavi,s2.getValue().getIndeks(),null)); //dodaj u akcija
                        novoStanje.put(s.getIndeks(), unos);
                    } else {
                        novoStanje.get(s.getIndeks()).put(s2.getKey(), new Akcija(AkcijaEnum.Stavi,s2.getValue().getIndeks(),null));//dodaj u akcija
                    }
                }
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        List<DKA.DKAStanje> kaka = dka.getStanja();
        for (DKA.DKAStanje s : kaka) {
            sb.append("[" + s.getIndeks() + "]\n");
            for (ENKAcvor sise : s.getCvoroviUStanju()){
                sb.append(sise.getProdukcija() + "\n");
            }
        }
        sb.append("*************AKCIJA**********************\n");
        for (Map.Entry<Integer, HashMap<String,Akcija>> a : akcija.entrySet()){
            sb.append(a.getKey() + ":  ");
            for (Map.Entry<String, Akcija> b : a.getValue().entrySet()){
                sb.append("{" + b.getKey() + "-" + b.getValue().tip + "(" + b.getValue().indeks+")}    ");
            }
            sb.append("\n");
        }

        sb.append("*************NOVO_SRANJE**********************\n");
        for (Map.Entry<Integer, HashMap<String,Akcija>> a : novoStanje.entrySet()){
            sb.append(a.getKey() + ":  ");
            for (Map.Entry<String, Akcija> b : a.getValue().entrySet()){
                sb.append("{" + b.getKey() + "-" + b.getValue().tip + "(" + b.getValue().indeks+")}    ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public HashMap<Integer, HashMap<String, Akcija>> getAkcijaTablica() {
        return akcija;
    }

    public Akcija getAkcija(Integer stanje, String znak) {
        return akcija.get(stanje).get(znak);
    }

    public HashMap<Integer, HashMap<String, Akcija>> getNovoStanjeTablica() {
        return novoStanje;
    }

    public Akcija getNovoStanje(Integer stanje, String znak) {
        return novoStanje.get(stanje).get(znak);
    }
}
