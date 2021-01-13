

import java.util.List;
import java.util.Stack;

public class LRParser {
    private Table table;
    private List<RedakUlaza> input;
    private Stack<Integer> stanjaParsera;
    private Stack<CvorStabla> znakovniStog;
    private List<String> sinkronizacijski;
    private CvorStabla vrhStabla;

    public LRParser(Table table, List<RedakUlaza> input, List<String> sinkronizacijski) {
        this.table = table;
        stanjaParsera = new Stack<>();
        znakovniStog = new Stack<>();
        this.stanjaParsera.push(0);
       // this.znakovniStog.push(new CvorStabla("$"));
        this.input = input;
        this.sinkronizacijski = sinkronizacijski;
        this.vrhStabla = null;
        parseEntry();
    }

    public CvorStabla getVrhStabla() {
        return vrhStabla;
    }

    private void parseEntry() {
        int i = 0;
        while (true){
            RedakUlaza line;
            if(i >= input.size()) {
                line =  new RedakUlaza("$", i, "$");
            } else {
                line = input.get(i);
            }

            Akcija akcija = table.getAkcija(stanjaParsera.peek(), line.getUniformniZnak());

            //System.out.println("Stanje Parsera: " + stanjaParsera.peek() + "   Znak: " + line.getUniformniZnak());
            /*if(akcija != null){
                System.out.println(akcija.tip);
            }*/


            if (akcija == null) {
                //System.out.println("null akcija");
                boolean imaSinkro = false;
                for (int j = i; j < input.size(); j++) {
                    if (sinkronizacijski.contains(input.get(i).getUniformniZnak())) {
                        imaSinkro = true;
                        break;
                    }
                    i++;
                }

                if (!imaSinkro) {
                    //System.err.println("EEEERRRRRROOOOOOORRRRRRR");
                    break;
                }


                line = input.get(i);
                while (true) {
                    Akcija sinkAkcija = table.getAkcija(stanjaParsera.peek(), line.getUniformniZnak());
                    if (znakovniStog.empty()) break;

                        if (sinkAkcija != null) {
                        vrhStabla = znakovniStog.peek();
                        break;
                    }
                    stanjaParsera.pop();
                    znakovniStog.pop();
                    if (stanjaParsera.size() == 0) break;
                }

                if (stanjaParsera.size() == 0) break;

            //dobar
            } else if (akcija.tip.equals(AkcijaEnum.Reduciraj)) {
                CvorStabla noviCvor = new CvorStabla(akcija.produkcija.getNezavrsni());

                if (akcija.produkcija.getProdukti().size() != 0) {
                    for (int j = 0; j < akcija.produkcija.getProdukti().size(); j++) {
                        noviCvor.dodajDijete(znakovniStog.pop());
                        stanjaParsera.pop();
                    }
                } else {
                    noviCvor.dodajDijete(new CvorStabla("$"));
                }

                vrhStabla = noviCvor;
                znakovniStog.push(noviCvor);
                Akcija novoStanje = table.getNovoStanje(stanjaParsera.peek(), akcija.produkcija.getNezavrsni());

                stanjaParsera.push(novoStanje.indeks);

            //dobar
            } else if (akcija.tip.equals(AkcijaEnum.Pomakni)) {
                i++;
                CvorStabla noviCvor = new CvorStabla(line.toString());
                stanjaParsera.push(akcija.indeks);
                znakovniStog.push(noviCvor);
            //dobar
            } else if (akcija.tip.equals(AkcijaEnum.Prihvati)) {
                break;
            }

        }
    }
}
