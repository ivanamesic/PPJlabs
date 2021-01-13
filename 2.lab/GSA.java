

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class GSA {

    List<String> nezavrsni = new ArrayList<>();
    List<String> zavrsni = new ArrayList<>();
    List<String> sinkronizacijski = new ArrayList<>();
    List<String> sviZnakovi = new ArrayList<>();
    Map<String, List<Production>> gramatika = new LinkedHashMap<>();
    Map<String, Map<String,Character>> beginsWithCharTable = new HashMap<>();
    List<String> epsilonNezavrsni = new ArrayList<>();

    Map<String, List<String>> zapocinjeNezavrsni = new HashMap<>();

    public List<String> getNezavrsni() {
        return nezavrsni;
    }

    public List<String> getZavrsni() {
        return zavrsni;
    }

    public GSA(BufferedReader br) {
        try{
            loadInput(br);
            addEpsilonNezavrsni();
            addInitialProduction();
            initBeginsWithCharTable();
            getZapocinjeNezavrsni();
           // System.out.println(gramatika);
            EpsilonNKA enka = new EpsilonNKA(gramatika, nezavrsni.get(0), zapocinjeNezavrsni, epsilonNezavrsni);
            DKA dka = new DKA(enka);
            Table table = new Table(zavrsni,nezavrsni,dka);
            //System.out.print(table.toString());
            writeObjects(table);
            //System.out.println(dka.toString());

        } catch(IOException e) {
        }
    }

    private void writeObjects(Table table) throws IOException {
        FileOutputStream fos =new FileOutputStream(getClass().getResource("analizator/GSAtable.ser").getPath());
        ObjectOutputStream out = new ObjectOutputStream(fos);
        out.writeObject(table);
        fos.close();
        //out.close();

        out = new ObjectOutputStream(new FileOutputStream(getClass().getResource("analizator/GSAsink.ser").getPath()));
        out.writeObject(sinkronizacijski);
        out.close();
    }
    private List<String> getZapocinjeProdukcije(String ... args) {
        List<String> zapocinje = new ArrayList<>();
        for (String s : args){
            if (!s.contains("<")){
                if(s.equals("$")) break;
                if (!zapocinje.contains(s))zapocinje.add(s);
                break;
            }
            zapocinje.addAll(zapocinjeNezavrsni.get(s).stream().filter((a)->!zapocinje.contains(a)).collect(Collectors.toList()));
        }

        return zapocinje;
    }

    private void addEpsilonNezavrsni() {
        for (Map.Entry<String, List<Production>> entry : gramatika.entrySet()){
           if (isEpsilonNezavrsni(entry.getValue())){
               epsilonNezavrsni.add(entry.getKey());
           }
        }

        while (true) {
            boolean goON = false;
            for (Map.Entry<String, List<Production>> entry : gramatika.entrySet()){
                if (epsilonNezavrsni.contains(entry.getKey())) continue;
                for (Production p : entry.getValue()){
                    boolean isEmpty = true;
                    for (String s : p.getProdukti()){
                        if (!nezavrsni.contains(s) || !epsilonNezavrsni.contains(s)) {
                            isEmpty = false;
                            break;
                        }
                    }

                    if (isEmpty && !epsilonNezavrsni.contains(p.getNezavrsni())) {
                        epsilonNezavrsni.add(p.getNezavrsni());
                        goON = true;
                    }
                }
            }
            if (!goON) break;
        }
    }

    private boolean isEpsilonNezavrsni(List<Production> productions) {
        for (Production p : productions){
            if (p.getProdukti().size() == 0) {
                return true;
            }
        }
        return false;
    }

    private void getZapocinjeNezavrsni() {
        for (Map.Entry<String, Map<String,Character>> entry : beginsWithCharTable.entrySet()){
            if (nezavrsni.contains(entry.getKey())) {
                List<String> zavrsniZnakovi = new ArrayList<>();
                for (Map.Entry<String,Character> entry2 : entry.getValue().entrySet()) {
                    if (zavrsni.contains(entry2.getKey()) && entry2.getValue() == '*') {
                        zavrsniZnakovi.add(entry2.getKey());
                    }
                }
                zapocinjeNezavrsni.put(entry.getKey(), zavrsniZnakovi);
                //System.out.println(entry.getKey() + ": " + zavrsniZnakovi);
            }
        }


    }

    private void loadInput(BufferedReader br) throws IOException {
        String line;
        List<String> lines = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            if (line == null) break;
            line = line.trim();

            if (line.startsWith("%V")){
                for (String s: line.substring(3).split(" ")){
                    nezavrsni.add(s);
                }
            } else if(line.startsWith("%T")){
                for (String s: line.substring(3).split(" ")){
                    zavrsni.add(s);
                }
            } else if(line.startsWith("%Syn")){
                for (String s: line.substring(5).split(" ")){
                    sinkronizacijski.add(s);
                }
                break;
            }
        }

        List<Production> productionsInMap = new ArrayList<>();
        String zadnjiNezavrsni = "";
        while (true) {
            line = br.readLine();
            if (line == null) {
                if (gramatika.containsKey(zadnjiNezavrsni)){
                    gramatika.get(zadnjiNezavrsni).addAll(productionsInMap);
                } else {
                    gramatika.put(zadnjiNezavrsni, productionsInMap);
                }
                break;
            }

            if (!line.startsWith(" ")){
                line = line.trim();
                if (zadnjiNezavrsni.isEmpty()){
                    zadnjiNezavrsni = line;
                } else {
                    if (gramatika.containsKey(zadnjiNezavrsni)){
                        gramatika.get(zadnjiNezavrsni).addAll(productionsInMap);
                        productionsInMap = new ArrayList<>();
                    } else {
                        gramatika.put(zadnjiNezavrsni, productionsInMap);
                        productionsInMap = new ArrayList<>();
                    }

                    zadnjiNezavrsni=line;
                }
            } else{
                Production production = new Production();
                production.setNezavrsni(zadnjiNezavrsni);
                if (line.trim().equals("$")) {
                    production.setProdukti(new ArrayList<>());
                } else {
                    production.setProdukti(Arrays.asList(line.substring(1).split(" ")));
                }
                productionsInMap.add(production);
            }
        }

        sviZnakovi.addAll(nezavrsni);
        sviZnakovi.addAll(zavrsni);
        sviZnakovi.addAll(sinkronizacijski);

        br.close();
    }

    private void addInitialProduction(){
        String pocetni = nezavrsni.get(0);
        String noviPocetni = pocetni.substring(0, pocetni.length()-1) + "'>";
        nezavrsni.add(0, noviPocetni);

        List<String> noviPocProd = new ArrayList<>();
        noviPocProd.add(pocetni);
        Production pocetnaProd = new Production(noviPocetni, noviPocProd);
        List<Production> noviPocetniProductions = new ArrayList<>();
        noviPocetniProductions.add(pocetnaProd);
        gramatika.put(noviPocetni, noviPocetniProductions);

        sviZnakovi.add(noviPocetni);
    }

    public void initBeginsWithCharTable() {
        for (String s : sviZnakovi){
            Map<String,Character> unutarnjaMapa = new HashMap<>();
            for (String s2 : sviZnakovi){
                unutarnjaMapa.put(s2,'-');
            }
            beginsWithCharTable.put(s,unutarnjaMapa);
        }
        createBeginingTable();
    }

    public void createBeginingTable() {
        for (Map.Entry<String, List<Production>>  entry : gramatika.entrySet()){
            for (Production p: entry.getValue()){
                for (int i = 0; i < p.getProdukti().size(); i++) {
                    beginsWithCharTable.get(p.getNezavrsni()).put(p.getProdukti().get(i), '*');
                    if (!epsilonNezavrsni.contains(p.getProdukti().get(i)))
                        break;
                }
            }
        }

        for (String s : sviZnakovi) {
            beginsWithCharTable.get(s).put(s, '*');
            /*if (zavrsni.contains(s)){
                continue;
            }*/

            List<String> toMarkNezavrsni = new ArrayList<>();
            HashSet<String> markedNezavrsni = new HashSet<>();

            for (String mapValue : nezavrsni) {
                if (beginsWithCharTable.get(s).get(mapValue) == '*') {
                    toMarkNezavrsni.add(mapValue);
                }
            }

            while (toMarkNezavrsni.size() > 0) {
                String toCheck = toMarkNezavrsni.remove(0);

                if (markedNezavrsni.contains(toCheck)){
                    continue;
                }

                beginsWithCharTable.get(s).put(toCheck, '*');
                markedNezavrsni.add(toCheck);

                for (String nextToMark : beginsWithCharTable.get(toCheck).keySet()) {
                    if (beginsWithCharTable.get(toCheck).get(nextToMark) == '*')
                          toMarkNezavrsni.add(nextToMark);
                }
            }
        }

    }




    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        GSA gsa = new GSA(br);
    }

}
