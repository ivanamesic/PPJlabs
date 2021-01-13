
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class SemantickiAnalizator {

    public static class Djelokrug{
        private Djelokrug skrbnik;
        private List<Cvor> sveDefinirano;

        public boolean isUpetlji() {
            return upetlji;
        }

        public void setUpetlji(boolean upetlji) {
            this.upetlji = upetlji;
        }

        private boolean upetlji;

        public Djelokrug(Djelokrug skrbnik) {
            this.skrbnik = skrbnik;
            this.sveDefinirano = new ArrayList<>();
        }

        public Djelokrug() {
            skrbnik = null;
            this.sveDefinirano = new ArrayList<>();
        }


        public Djelokrug getSkrbnik() {
            return skrbnik;
        }

        public List<Cvor> getSveDefinirano() {
            return sveDefinirano;
        }

        public void dodajDeklarirano(Cvor cvor){
            this.sveDefinirano.add(cvor);
        }

        public void setSveDefinirano(List<Cvor> sveDefinirano){
            this.sveDefinirano = sveDefinirano;
        }
    }

    private Djelokrug djelokrug;
    private List<String> deklariraneFunkcije;
    private List<Cvor> dekl;
    private List<Cvor> def;
    private List<String> definiraneFunkcije;
    private boolean greska = false;
    private boolean imaMain = false;

    public SemantickiAnalizator(BufferedReader br) {
        try {
            Cvor korijen = readInput(br);

            inicijaliziraj();
            prijevodnaJedinica(korijen);

            if (!greska) {
                if (!imaMainFunkciju()) {
                  //  System.out.println("MOJ ISPIS: " + "main");
                    writeToFile("main");
                    return;
                }

                if (!sveFunkcijeDefinirane()){
                 //   System.out.println("MOJ ISPIS: " + "funkcija");
                    /*System.out.println("DEFINIRANE: " + definiraneFunkcije);
                    System.out.println("DEKLARIRANE: " + deklariraneFunkcije);*/

                    writeToFile("funkcija");
                    return;
                }
                writeToFile("");
            }

        } catch (IOException e) {

        }
    }

    private void inicijaliziraj() {
        djelokrug = new Djelokrug();
        dekl = new ArrayList<>();
        def = new ArrayList<>();
        definiraneFunkcije = new ArrayList<>();
        deklariraneFunkcije = new ArrayList<>();
    }

    private Cvor readInput(BufferedReader br) throws IOException {
        String line;
        Cvor korijen = null;
        Stack<Cvor> stog = new Stack<>();
        while ((line = br.readLine()) != null) {
            if (line == null) break;

            int razina = odrediRazinu(line);
            Cvor cvor = new Cvor(line.trim(), razina);

            if (stog.isEmpty()) {
                stog.add(cvor);
                korijen = cvor;
                continue;
            }

            if (stog.peek().getRazina() == cvor.getRazina()) {
                stog.pop();
                stog.peek().dodajDijete(cvor);
                stog.push(cvor);
            } else if (stog.peek().getRazina() > cvor.getRazina()){
                while(stog.peek().getRazina() != cvor.getRazina()-1){
                    stog.pop();
                }
                stog.peek().dodajDijete(cvor);
                stog.push(cvor);
            } else {
                stog.peek().dodajDijete(cvor);
                stog.push(cvor);
            }
        }

        return korijen;
    }

/*    public void ispisi(Cvor cvor){
        for (int i = 0; i < cvor.getRazina(); i++){
            System.out.print(" ");
        }
        System.out.println(cvor.getSadrzaj());
        for (Cvor c : cvor.getDjeca()){
            ispisi(c);
        }
    }*/

    private int odrediRazinu(String line){
        int count = 0;
        for (char a : line.toCharArray()){
            if (a != ' ') break;
            count++;
        }
        return count;
    }

    private void prijevodnaJedinica(Cvor cvor){
        if (cvor.getDjeca().size()>1) {
            prijevodnaJedinica(cvor.getDjeca().get(0));
            if (greska) {
                return;
            }
            vanjskaDeklaracija(cvor.getDjeca().get(1));
        } else {
            vanjskaDeklaracija(cvor.getDjeca().get(0));
        }
    }

    private void vanjskaDeklaracija(Cvor cvor) {
        if (cvor.getDjeca().get(0).getSadrzaj().startsWith("<definicija_funkcije>")) {
            definicijaFunkcije(cvor.getDjeca().get(0));
        } else {
            deklaracijaFunkcije(cvor.getDjeca().get(0));
        }
    }

    private void deklaracijaFunkcije(Cvor cvor) {
        imeTipa(cvor.getDjeca().get(0));
        if (greska) return;
        cvor.getDjeca().get(1).setJeKonstanta(cvor.getDjeca().get(0).isJeKonstanta());
        cvor.getDjeca().get(1).setuPetlji(cvor.isuPetlji());
        cvor.getDjeca().get(1).setTipIDN(cvor.getDjeca().get(0).getTipIDN(djelokrug));
        cvor.getDjeca().get(1).setJeKonstanta(cvor.getDjeca().get(0).isJeKonstanta());
        cvor.getDjeca().get(1).setuPetlji(cvor.isuPetlji());
        listaInitDeklaratora(cvor.getDjeca().get(1));
    }

    private void listaInitDeklaratora(Cvor cvor) {
        if (cvor.getDjeca().size() == 1) {
            cvor.getDjeca().get(0).setTipIDN(cvor.getTipIDN(djelokrug));
            cvor.getDjeca().get(0).setJeKonstanta(cvor.isJeKonstanta());
            cvor.getDjeca().get(0).setuPetlji(cvor.isuPetlji());

            initDeklarator(cvor.getDjeca().get(0));
            if (greska) return;
        } else {
            cvor.getDjeca().get(0).setTipIDN(cvor.getTipIDN(djelokrug));
            cvor.getDjeca().get(0).setuPetlji(cvor.isuPetlji());

            cvor.getDjeca().get(0).setJeKonstanta(cvor.isJeKonstanta());
            listaInitDeklaratora(cvor.getDjeca().get(0));
            if (greska) return;
            cvor.getDjeca().get(2).setTipIDN(cvor.getTipIDN(djelokrug));
            cvor.getDjeca().get(2).setJeKonstanta(cvor.isJeKonstanta());
            cvor.getDjeca().get(2).setuPetlji(cvor.isuPetlji());

            initDeklarator(cvor.getDjeca().get(2));
        }
    }

    private void initDeklarator(Cvor cvor) {
        List<Cvor>  djeca = cvor.getDjeca();
        djeca.get(0).setTipIDN(cvor.getTipIDN(djelokrug));
        djeca.get(0).setJeKonstanta(cvor.isJeKonstanta());
        djeca.get(0).setuPetlji(cvor.isuPetlji());
        izravniDeklarator(cvor.getDjeca().get(0));
        if (greska) return;
        if (djeca.size() == 1) {
            if (djeca.get(0).isJeKonstanta()) {
                ispisiError(cvor);
                return;
            }
        } else {
            inicijalizator(cvor.getDjeca().get(2));
            if (greska) return;
            String tip = cvor.getDjeca().get(0).getTipIDN(djelokrug);

            if(tip.startsWith("KR_INT") || tip.startsWith("KR_CHAR")) {
                if(djeca.get(2).isJeFunkcija() || !mozeSeCastat(djeca.get(2).getTipIDN(djelokrug), tip)){
                    ispisiError(cvor);
                    return;
                }

            } else if(tip.startsWith("niz")) {
                String tipUNizu = tip.substring(4, tip.length()-1);
                if(djeca.get(2).getVelicinaNiza() > djeca.get(0).getVelicinaNiza()){
                    ispisiError(cvor);
                    return;
                }
                for (String tipp : djeca.get(2).getTipoviParametara(djelokrug)){
                    if(!mozeSeCastat(tipp, tipUNizu)){
                        ispisiError(cvor);
                        return;
                    }
                }
            }
           /* if (tip.startsWith("niz")) {


            } else {
                cvor.getDjeca().get(2).setTipIDN(cvor.getDjeca().get(0).getTipIDN(djelokrug));
                cvor.getDjeca().get(2).setJeKonstanta(false);
            }*/
        }


    }

    private void inicijalizator(Cvor cvor) {
        List<Cvor>  djeca = cvor.getDjeca();

        if (djeca.size() == 1){
            izrazPridruzivanja(djeca.get(0));
            if(greska) return;
            if (stvaraNiz(djeca.get(0))) {
                cvor.setVelicinaNiza(velicinaNiza(cvor));
                for (int i = 0; i < cvor.getVelicinaNiza(); ++i) {
                    cvor.dodajTipParametra("KR_CHAR");
                }
                cvor.setImeIDN(cvor.getDjeca().get(0).getImeIDN());
            } else {
                cvor.setTipIDN(djeca.get(0).getTipIDN(djelokrug));
                cvor.setTipoviParametara(djeca.get(0).getTipoviParametara(djelokrug));
                cvor.setImeIDN(cvor.getDjeca().get(0).getImeIDN());

            }


        }else {
            listaIzrazaPridruzivanja(djeca.get(1));

            if(greska) return;
            cvor.setVelicinaNiza(djeca.get(1).getVelicinaNiza());
            cvor.setTipoviParametara(djeca.get(1).getTipoviParametara(djelokrug));
            cvor.setImeIDN(cvor.getDjeca().get(1).getImeIDN());

        }
    }

    private int velicinaNiza(Cvor cvor) {
        while (cvor.getDjeca().size() > 0) {
            cvor = cvor.getDjeca().get(0);
        }
        int res = cvor.getSadrzaj().split(" ")[2].length();
        return res - 2;

    }

    private boolean stvaraNiz(Cvor cvor) {
        while (cvor.getDjeca().size() != 0) {
            if (cvor.getDjeca().size() > 1) {
                return false;
            }
            cvor = cvor.getDjeca().get(0);
        }
        return cvor.getSadrzaj().startsWith("NIZ_ZNAKOVA");
    }

    private void izravniDeklarator(Cvor cvor) {
        int a;
        List<Cvor>  djeca = cvor.getDjeca();
        if(djeca.size() == 1){
            if (cvor.getDjeca().get(0).getTipIDN(djelokrug).equals("KR_VOID")) {
                ispisiError(cvor);
                return;
            }

            for(Cvor c : djelokrug.getSveDefinirano()){
                if(c.getImeIDN().equals(cvor.getDjeca().get(0).getImeIDN())){
                    ispisiError(cvor);
                    return;
                }
            }

            cvor.setImeIDN(djeca.get(0).getImeIDN());
            djelokrug.dodajDeklarirano(cvor);
        } else {
            if(djeca.get(2).getSadrzaj().startsWith("BROJ")){
                if (cvor.getDjeca().get(0).getTipIDN(djelokrug).equals("KR_VOID")) {
                    ispisiError(cvor);
                    return;
                }

                for(Cvor c : djelokrug.getSveDefinirano()){
                    if(c.getSadrzaj().startsWith("IDN") && c.getImenaParametara().equals(cvor.getDjeca().get(0).getVrijednost())){
                        ispisiError(cvor);
                        return;
                    }
                }
                int br = 10000000;
                try {
                    br = Integer.parseInt(djeca.get(2).getSadrzaj().split(" ")[2]);
                } catch(Exception e){
                    ispisiError(cvor);
                    return;
                }

                if(br <= 0 || br > 1024){
                    ispisiError(cvor);
                    return;
                }

                // NISMO SIGURNI ZAS OVO RADI I KAKO OVO RADI
                cvor.setImeIDN(djeca.get(0).getImeIDN());
                cvor.setTipIDN("niz(" + cvor.getTipIDN(djelokrug) + ")");
                cvor.setVelicinaNiza(br);
                djelokrug.dodajDeklarirano(cvor);


            } else if(djeca.get(2).getSadrzaj().startsWith("KR_VOID")) {

                for(Cvor c : djelokrug.getSveDefinirano()){
                    if(c.getImeIDN().equals(djeca.get(0).getImeIDN())){
                        if(c.getTipoviParametara(djelokrug).size() != 1 || !c.getTipoviParametara(djelokrug).get(0).equals("KR_VOID")) {
                            ispisiError(cvor);
                            return;
                        }
                        cvor.setImeIDN(djeca.get(0).getImeIDN());
                        cvor.dodajTipParametra("KR_VOID");
                        return;
                    }
                }

                cvor.setImeIDN(djeca.get(0).getImeIDN());
                cvor.dodajTipParametra("KR_VOID");
                djelokrug.dodajDeklarirano(cvor);
                //deklariraneFunkcije.add(cvor.getImeIDN());

            }  else if(djeca.get(2).getSadrzaj().startsWith("<lista_parametara>")){
                listaParametara(djeca.get(2));
                for(Cvor c : djelokrug.getSveDefinirano()){
                    if(c.getImeIDN().equals(djeca.get(0).getTipIDN(djelokrug))){
                        if(c.getTipoviParametara(djelokrug).size() != djeca.get(2).getTipoviParametara(djelokrug).size()) {
                            ispisiError(cvor);
                            return;
                        }

                        int brojac = 0;
                        for(String parametar : c.getTipoviParametara(djelokrug)){
                            if(!djeca.get(2).getTipoviParametara(djelokrug).get(brojac).equals(parametar)){
                                ispisiError(cvor);
                                return;
                            }
                            ++brojac;

                        }

                        cvor.setImeIDN(djeca.get(0).getImeIDN());
                        cvor.setTipoviParametara(djeca.get(2).getTipoviParametara(djelokrug));
                        return;
                    }
                }

                cvor.setImeIDN(djeca.get(0).getImeIDN());
                cvor.setTipoviParametara(djeca.get(2).getTipoviParametara(djelokrug));
                djelokrug.dodajDeklarirano(cvor);
                deklariraneFunkcije.add(cvor.getImeIDN());
                dekl.add(cvor);

            }
        }




        boolean deklarirano = false;

    }

    private void listaParametara(Cvor cvor){
        if (cvor.getDjeca().size() == 1) {
            deklaracijaParametra(cvor.getDjeca().get(0));
            if (greska) return;
            cvor.dodajImeParametra(cvor.getDjeca().get(0).getImeIDN());
            cvor.dodajTipParametra(cvor.getDjeca().get(0).getTipIDN(djelokrug));
        } else {
            listaParametara(cvor.getDjeca().get(0));
            if (greska) return;
            deklaracijaParametra(cvor.getDjeca().get(2));
            if (greska) return;
            if (cvor.getDjeca().get(0).getImenaParametara().contains(cvor.getDjeca().get(2).getImeIDN())) {
                ispisiError(cvor);
                return;
            }

            List<String> imena = new ArrayList<>();
            imena.addAll(cvor.getDjeca().get(0).getImenaParametara());
            imena.add(cvor.getDjeca().get(2).getImeIDN());
            cvor.setImenaParametara(imena);

            List<String> tipovi = new ArrayList<>();
            tipovi.addAll(cvor.getDjeca().get(0).getTipoviParametara(djelokrug));
            tipovi.add(cvor.getDjeca().get(2).getTipIDN(djelokrug));
            cvor.setTipoviParametara(tipovi);
        }
    }

    private void deklaracijaParametra(Cvor cvor) {
        imeTipa(cvor.getDjeca().get(0));
        if (cvor.getDjeca().get(0).getTipIDN(djelokrug).equals("KR_VOID")) {
            ispisiError(cvor);
            return;
        }

        if (cvor.getDjeca().size() == 2) {
            cvor.setTipIDN(cvor.getDjeca().get(0).getTipIDN(djelokrug));
        } else {
            cvor.setTipIDN("niz("+cvor.getDjeca().get(0).getTipIDN(djelokrug)+")");
        }
        cvor.setImeIDN(cvor.getDjeca().get(1).getImeIDN());
    }

    private void listaDeklaracija(Cvor cvor) {
        if (cvor.getDjeca().size() == 1) {
            cvor.getDjeca().get(0).setuPetlji(cvor.isuPetlji());
            deklaracijaFunkcije(cvor.getDjeca().get(0));
            if (greska) return;
        } else {
            cvor.getDjeca().get(0).setuPetlji(cvor.isuPetlji());
            cvor.getDjeca().get(1).setuPetlji(cvor.isuPetlji());

            listaDeklaracija(cvor.getDjeca().get(0));
            if (greska) return;
            deklaracijaFunkcije(cvor.getDjeca().get(1));
            if (greska) return;
        }
    }

    private void definicijaFunkcije(Cvor cvor) {
        List<Cvor>  djeca = cvor.getDjeca();
        imeTipa(djeca.get(0));
        if (djeca.get(0).isJeKonstanta()){
            ispisiError(cvor);
            return;
        }


        if(definiraneFunkcije.contains(djeca.get(1).getImeIDN())) {
            ispisiError(cvor);
            return;
        }

        if (djeca.get(3).getSadrzaj().startsWith("KR_VOID")){
            Djelokrug globalni = nadiGlobalni();
            for (Cvor dc : globalni.getSveDefinirano()) {
                if (dc.isJeFunkcija() && dc.getImeIDN().equals(djeca.get(1).getImeIDN())) {
                    if(!(dc.getTipIDN(djelokrug).equals(djeca.get(0).getTipIDN(djelokrug)) && dc.getTipoviParametara(djelokrug).get(0).equals("KR_VOID"))){
                        ispisiError(cvor);
                        return;
                    }
                }
            }

            cvor.dodajTipParametra("KR_VOID");
            /*slozenaNaredba(cvor.getDjeca().get(5));
            if (greska) return;*/
            // SVETI DIO KODA OD PETRE
            //NESTO S MAINOM

            if (cvor.getDjeca().get(1).getImeIDN().equals("main") && cvor.getDjeca().get(0).getTipIDN(djelokrug).equals("KR_INT")){
                imaMain = true;
            }
            //NESTO S ERROROM
            // SVETI DIO KODA OD PETRE

        } else if(djeca.get(3).getSadrzaj().equals("<lista_parametara>")) {
            listaParametara(djeca.get(3));
            Djelokrug globalni = nadiGlobalni();
            // PETRA RADI DRUGACIJE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            for (Cvor dc : globalni.getSveDefinirano()) {
                if (dc.isJeFunkcija() && dc.getImeIDN().equals(djeca.get(1).getImeIDN())) {
                    List<String> ocekivaniParametri = djeca.get(3).getTipoviParametara(djelokrug);
                    if(!(dc.getTipIDN(djelokrug).equals(djeca.get(0).getTipIDN(djelokrug)) && dc.getTipoviParametara(djelokrug).equals(ocekivaniParametri))){
                        ispisiError(cvor);
                        return;
                    }
                }
            }

            cvor.setTipoviParametara(djeca.get(3).getTipoviParametara(djelokrug));
            cvor.setImenaParametara(djeca.get(3).getImenaParametara());
            Cvor CvorSlozenaNaredba = djeca.get(5);
            CvorSlozenaNaredba.setTipoviParametara(djeca.get(3).getTipoviParametara(djelokrug));
            CvorSlozenaNaredba.setImenaParametara(djeca.get(3).getImenaParametara());
        }

        cvor.setTipIDN(djeca.get(0).getTipIDN(djelokrug));
        cvor.setImeIDN(djeca.get(1).getImeIDN());
        cvor.setDefinirano(true);
        djelokrug.dodajDeklarirano(cvor);
        definiraneFunkcije.add(cvor.getImeIDN());
        def.add(cvor);
        dekl.add(cvor);
        deklariraneFunkcije.add(cvor.getImeIDN());
        slozenaNaredba(djeca.get(5));
        if (greska) return;
    }

    private void slozenaNaredba(Cvor cvor) {


        Djelokrug copyOfScope = new Djelokrug(djelokrug.getSkrbnik());
        copyOfScope.setSveDefinirano(djelokrug.getSveDefinirano());
        if (djelokrug.upetlji)
            copyOfScope.setUpetlji(true);

        Djelokrug newScope = new Djelokrug(copyOfScope);
        if (cvor.isuPetlji() || djelokrug.upetlji){
            newScope.setUpetlji(true);
        }
        djelokrug = newScope;

        if (cvor.isuPetlji()){
            djelokrug.setUpetlji(true);

        }
        for (int i = 0; i < cvor.getTipoviParametara(djelokrug).size(); ++i) {
            Cvor newNode = new Cvor("<" + cvor.getImenaParametara().get(i), -1);
            newNode.setTipIDN(cvor.getTipoviParametara(djelokrug).get(i));
            if (cvor.isuPetlji()){
                newNode.setuPetlji(true);
            }
            newNode.setImeIDN(cvor.getImenaParametara().get(i));
            djelokrug.dodajDeklarirano(newNode);
        }

        if (cvor.getDjeca().size() == 3) {
            listaNaredbi(cvor.getDjeca().get(1));
            if (greska) return;
        } else {
            listaDeklaracija(cvor.getDjeca().get(1));
            if (greska) return;
            listaNaredbi(cvor.getDjeca().get(2));
            if (greska) return;
        }
        djelokrug = djelokrug.getSkrbnik();
    }

    private void listaNaredbi(Cvor cvor) {
        if (cvor.getDjeca().size() == 1) {
            naredba(cvor.getDjeca().get(0));
            if (greska) return;
        } else {
            listaNaredbi(cvor.getDjeca().get(0));
            if (greska) return;
            naredba(cvor.getDjeca().get(1));
            if (greska) return;
        }
    }

    private void naredba(Cvor cvor) {
        Cvor dijete = cvor.getDjeca().get(0);
        String naziv = dijete.getSadrzaj();

        if (cvor.isuPetlji()){
            dijete.setuPetlji(true);
        }
        switch (naziv) {
            case "<slozena_naredba>":
                slozenaNaredba(dijete);
                break;
            case "<izraz_naredba>":
                izrazNaredba(dijete);
                break;
            case "<naredba_grananja>":
                naredbaGrananja(dijete);
                break;
            case "<naredba_petlje>":
                naredbaPetlje(dijete);
                break;
            case "<naredba_skoka>":
                naredbaSkoka(dijete);
                break;
        }
    }




    private void izrazNaredba(Cvor cvor){
        if (cvor.getDjeca().get(0).getSadrzaj().startsWith("TOCKAZAREZ")) {
            cvor.setTipIDN("KR_INT");
        } else {
            izraz(cvor.getDjeca().get(0));
            if (greska) return;
            cvor.setTipIDN(cvor.getDjeca().get(0).getTipIDN(djelokrug));
            cvor.setImeIDN(cvor.getDjeca().get(0).getImeIDN());
            cvor.setTipoviParametara(cvor.getDjeca().get(0).getTipoviParametara(djelokrug));
        }
    }


    private void naredbaGrananja(Cvor cvor) {
        izraz(cvor.getDjeca().get(2));
        if (greska) return;

        if (cvor.getDjeca().get(2).isJeFunkcija() || !mozeSeCastat(cvor.getDjeca().get(2).getTipIDN(djelokrug), "KR_INT")){
            ispisiError(cvor);
            return;
        }

        if (cvor.getDjeca().size() == 5) {
            naredba(cvor.getDjeca().get(4));
            if (greska) return;
        } else {
            naredba(cvor.getDjeca().get(4));
            if (greska) return;
            naredba(cvor.getDjeca().get(6));
            if (greska) return;
        }
    }

    private void naredbaSkoka(Cvor cvor) {
        if (cvor.getDjeca().size() == 2){
            String sadrzaj = cvor.getDjeca().get(0).getSadrzaj();
            if (sadrzaj.startsWith("KR_RETURN")) {
                String tipfunk = getTypeOfCurrentFunction();
                if (!tipfunk.equals("KR_VOID")) {
                    ispisiError(cvor);
                    return;
                }
            } else {
                if (!djelokrug.upetlji){
                    ispisiError(cvor);
                    return;
                }
            }
        } else {
            izraz(cvor.getDjeca().get(1));
            if (greska) return;
            String type = getTypeOfCurrentFunction();
            if (cvor.getDjeca().get(1).isJeFunkcija() || !mozeSeCastat(cvor.getDjeca().get(1).getTipIDN(djelokrug), type)) {
                ispisiError(cvor);
                return;
            }
        }
        if (djelokrug.upetlji) {
            djelokrug.setUpetlji(false);
        }
    }

    //ovo je kopirano, prepraviti
    private String getTypeOfCurrentFunction() {
        Djelokrug temp = new Djelokrug();
        temp = djelokrug;
        while (temp != null) {
            for (int i = temp.getSveDefinirano().size() - 1; i >= 0; --i) {
                Cvor declaration = temp.getSveDefinirano().get(i);
                if (declaration.isJeFunkcija() && declaration.isDefinirano()) {
                    return declaration.getTipIDN(temp);
                }
            }
            temp = temp.getSkrbnik();
        }
        return "";
    }

    private void naredbaPetlje(Cvor cvor) {
        if (cvor.getDjeca().size() == 5) {
            izraz(cvor.getDjeca().get(2));
            if (greska) return;

            if (cvor.getDjeca().get(2).isJeFunkcija() || !mozeSeCastat(cvor.getDjeca().get(2).getTipIDN(djelokrug), "KR_INT")){
                ispisiError(cvor);
                return;
            }
            cvor.getDjeca().get(4).setuPetlji(true);

            naredba(cvor.getDjeca().get(4));
            if (greska) return;
        } else  {
            izrazNaredba(cvor.getDjeca().get(2));
            if (greska) return;

            izrazNaredba(cvor.getDjeca().get(3));

            if (greska) return;
            if (cvor.getDjeca().get(3).isJeFunkcija() || !mozeSeCastat(cvor.getDjeca().get(3).getTipIDN(djelokrug), "KR_INT")){
                ispisiError(cvor);
                return;
            }

            if (cvor.getDjeca().size() == 6){
                cvor.getDjeca().get(5).setuPetlji(true);
                naredba(cvor.getDjeca().get(5));
                if (greska) return;
            } else {
                izraz(cvor.getDjeca().get(4));
                if (greska) return;

                cvor.getDjeca().get(6).setuPetlji(true);
                naredba(cvor.getDjeca().get(6));
                if (greska) return;
            }
        }
    }

    private Djelokrug nadiGlobalni() {
        Djelokrug globalni = djelokrug.getSkrbnik();
        if (globalni == null) return djelokrug;
        while (globalni.getSkrbnik() != null) {
            globalni = globalni.getSkrbnik();
        }
        return globalni;
    }

    private void ispisiError(Cvor cvor)  {
        StringBuilder sb = new StringBuilder();
        sb.append(cvor.getSadrzaj()+" ::= ");
        for (Cvor c : cvor.getDjeca()){
            String[] parts = c.getSadrzaj().split(" ");
            if (parts.length > 1){
                if (parts.length == 3){
                    sb.append(parts[0] + "(" + parts[1] + "," + parts[2] +")");
                } else{
                    sb.append(parts[0] + "(" + parts[1] + ",");
                    int indeks = c.getSadrzaj().indexOf(parts[2]);
                    sb.append(c.getSadrzaj().substring(indeks) + ")");
                }
            } else{
                sb.append(parts[0]);
            }
            sb.append(" ");
        }
        sb.deleteCharAt(sb.length()-1);
        //System.out.println("MOJ ISPIS: " + sb.toString());
        writeToFile(sb.toString());

        greska = true;
    }

    private void writeToFile(String s) {
        System.out.println(s);
        /*try{
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get("src/lab3/tvoj_output/"+indeks+".out"))));
            //BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get("src/lab3/primjeri/"+indeks+"/test.out"))));

            if (s.isEmpty()){
                bw.write("");
            }
            //System.out.println(s);
            bw.write(s);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private void imeTipa(Cvor cvor) {
        if (cvor.getDjeca().size() == 1){
            specifikatorTipa(cvor.getDjeca().get(0));
            if (greska) return;
            cvor.setTipIDN(cvor.getDjeca().get(0).getTipIDN(djelokrug));
        } else {
            specifikatorTipa(cvor.getDjeca().get(1));
            if (greska) return;

            cvor.setJeKonstanta(true);
            cvor.setTipIDN(cvor.getDjeca().get(1).getTipIDN(djelokrug));
            if (cvor.getDjeca().get(1).getTipIDN(djelokrug).equals("KR_VOID")) {
                ispisiError(cvor);
                return;
            }
        }
    }

    private void specifikatorTipa(Cvor cvor) {
        cvor.setTipIDN(cvor.getDjeca().get(0).getSadrzaj().split(" ")[0]);
    }

    private void primarniIzraz(Cvor cvor){
        Cvor dijete = cvor.getDjeca().get(0);
        String znak = dijete.getSadrzaj();
        if (znak.startsWith("IDN")) {
            if (!jeDeklararirano(dijete.getImeIDN(), djelokrug)) {
                ispisiError(cvor);
                return;
            }
            cvor.setImeIDN(dijete.getImeIDN());
            cvor.setTipoviParametara(dijete.getTipoviParametara(djelokrug));
            cvor.setTipIDN(dijete.getTipIDN(djelokrug));
            cvor.setlIzraz(dijete.islIzraz(djelokrug));
        } else if(znak.startsWith("BROJ")){
            BigInteger bigInteger = new BigInteger(dijete.getVrijednost());
            if (bigInteger.bitCount() > 32) {
                ispisiError(cvor);
                return;
            }
            long lon = Long.parseLong(dijete.getVrijednost());
            if (lon > 2147483647){
                ispisiError(cvor);
                return;
            }
            cvor.setlIzraz(false);
            cvor.setTipIDN("KR_INT");
        } else if(znak.startsWith("ZNAK")){
            if (!provjeriChar(dijete.getImeIDN())) {
                ispisiError(cvor);
                return;
            }
            cvor.setlIzraz(false);
            cvor.setTipIDN("KR_CHAR");
        } else if(znak.startsWith("NIZ_ZNAKOVA")){
            String niz = "";
            if (dijete.getSadrzaj().split(" ").length > 3){
                for (int i = 2; i < dijete.getSadrzaj().split(" ").length; i++){
                    niz+=dijete.getSadrzaj().split(" ")[i];
                }
            } else {
                niz = dijete.getSadrzaj().split(" ")[2];
            }
                if (!isNizZnakova(niz)) {
                ispisiError(cvor);
                return;
            }
            cvor.setJeKonstanta(true);
            cvor.setTipIDN("niz(KR_CHAR)");
            cvor.setlIzraz(false);
        } else if(znak.startsWith("L_ZAGRADA ")) {
            izraz(cvor.getDjeca().get(1));
            if (greska) return;
            cvor.setTipIDN(cvor.getDjeca().get(1).getTipIDN(djelokrug));
            cvor.setlIzraz(cvor.getDjeca().get(1).islIzraz(djelokrug));
        }
    }

    private boolean isNizZnakova(String imeIDN) {
        StringBuilder sb = new StringBuilder();
        int len = imeIDN.length();
        for (int i = 1; i < len- 1; ++i) {
            if (imeIDN.charAt(i) == '\\') {
                sb.append("'" +  imeIDN.charAt(i) + imeIDN.charAt(i + 1) + "'");
                if (!provjeriChar(sb.toString())) return false;
                sb.delete(0, sb.length());
                i++;
            } else {
                if (!provjeriChar("'" + imeIDN.charAt(i) + "'")) return false;
            }
        }
        return true;
    }

    private void izraz(Cvor cvor) {
        if (cvor.getDjeca().size() == 1) {
            izrazPridruzivanja(cvor.getDjeca().get(0));
            if (greska) return;
            cvor.setlIzraz(cvor.getDjeca().get(0).islIzraz(djelokrug));
            cvor.setTipoviParametara(cvor.getDjeca().get(0).getTipoviParametara(djelokrug));
            cvor.setTipIDN(cvor.getDjeca().get(0).getTipIDN(djelokrug));
            cvor.setImeIDN(cvor.getDjeca().get(0).getImeIDN());

        } else {
            izraz(cvor.getDjeca().get(0));
            if (greska) return;
            izrazPridruzivanja(cvor.getDjeca().get(2));
            if (greska) return;
            cvor.setlIzraz(false);
            cvor.setTipIDN(cvor.getDjeca().get(2).getTipIDN(djelokrug));
        }
    }

    private void izrazPridruzivanja(Cvor cvor) {
        if (cvor.getDjeca().size() == 1) {
            viseIzraza(cvor.getDjeca().get(0), "logIliIzraz");
            if (greska) return;
            cvor.setlIzraz(cvor.getDjeca().get(0).islIzraz(djelokrug));
            cvor.setImeIDN(cvor.getDjeca().get(0).getImeIDN());
            cvor.setTipoviParametara(cvor.getDjeca().get(0).getTipoviParametara(djelokrug));
            cvor.setTipIDN(cvor.getDjeca().get(0).getTipIDN(djelokrug));
        } else {
            postfiksIzraz(cvor.getDjeca().get(0));
            if (greska) return;
            if (!cvor.getDjeca().get(0).islIzraz(djelokrug)) {
                ispisiError(cvor);
                return;
            }
            izrazPridruzivanja(cvor.getDjeca().get(2));
            if (greska) return;
            if (!mozeSeCastat(cvor.getDjeca().get(2).getTipIDN(djelokrug), cvor.getDjeca().get(0).getTipIDN(djelokrug))) {
                ispisiError(cvor);
                return;
            }

            cvor.setlIzraz(false);
            cvor.setTipIDN(cvor.getDjeca().get(0).getTipIDN(djelokrug));
        }
    }

    private void postfiksIzraz(Cvor cvor) {
        if (cvor.getDjeca().size() == 1) {
            primarniIzraz(cvor.getDjeca().get(0));
            if(greska) return;

            cvor.setImeIDN(cvor.getDjeca().get(0).getImeIDN());
            cvor.setTipIDN(cvor.getDjeca().get(0).getTipIDN(djelokrug));
            cvor.setTipoviParametara(cvor.getDjeca().get(0).getTipoviParametara(djelokrug));
            cvor.setlIzraz(cvor.getDjeca().get(0).islIzraz(djelokrug));
        } else if (cvor.getDjeca().get(1).getSadrzaj().startsWith("L_UGL_ZAGRADA")){
            postfiksIzraz(cvor.getDjeca().get(0));
            if (greska) return;
            if (!cvor.getDjeca().get(0).getTipIDN(djelokrug).startsWith("niz")) {
                ispisiError(cvor);
                return;
            }
            String tipX = cvor.getDjeca().get(0).getTipIDN(djelokrug).replace("niz(", "");
            tipX = tipX.replace(")", "");
            izraz(cvor.getDjeca().get(2));
            if (greska) return;

            if (!mozeSeCastat(cvor.getDjeca().get(2).getTipIDN(djelokrug), "KR_INT")) {
                ispisiError(cvor);
                return;
            }
            cvor.setTipIDN(tipX);
            cvor.setlIzraz(!cvor.getDjeca().get(0).isJeKonstanta());
        }  else if (cvor.getDjeca().size() == 2) {
            postfiksIzraz(cvor.getDjeca().get(0));
            if (greska) return;

            if (!(cvor.getDjeca().get(0).islIzraz(djelokrug) && mozeSeCastat(cvor.getDjeca().get(0).getTipIDN(djelokrug), "KR_INT"))) {
                ispisiError(cvor);
                return;
            }

            cvor.setTipIDN("KR_INT");
            cvor.setlIzraz(false);
        } else if (cvor.getDjeca().get(2).getSadrzaj().startsWith("D_ZAGRADA")) {
            postfiksIzraz(cvor.getDjeca().get(0));
            if (greska) return;

            if (!(cvor.getDjeca().get(0).isJeFunkcija() && cvor.getDjeca().get(0).getTipoviParametara(djelokrug).get(0).equals("KR_VOID"))) {
                ispisiError(cvor);
                return;
            }
            cvor.setImeIDN(cvor.getDjeca().get(0).getImeIDN());
            cvor.setTipIDN(cvor.getDjeca().get(0).getTipIDN(djelokrug));
            cvor.setlIzraz(false);

        } else if (cvor.getDjeca().get(3).getSadrzaj().startsWith("D_ZAGRADA")) {
            postfiksIzraz(cvor.getDjeca().get(0));
            if (greska) return;

            listaArgumenata(cvor.getDjeca().get(2));
            if (greska) return;

            if (!cvor.getDjeca().get(0).isJeFunkcija()) {
                ispisiError(cvor);
                return;
            }

            Cvor pIzraz = cvor.getDjeca().get(0);
            Cvor argumenti = cvor.getDjeca().get(2);
            if (pIzraz.getTipoviParametara(djelokrug).size() != argumenti.getTipoviParametara(djelokrug).size()) {
                ispisiError(cvor);
                return;
            }

            for (int i = 0; i < pIzraz.getTipoviParametara(djelokrug).size(); ++i) {
                if (!mozeSeCastat(argumenti.getTipoviParametara(djelokrug).get(i), pIzraz.getTipoviParametara(djelokrug).get(i))) {
                    ispisiError(cvor);
                    return;
                }
            }
            cvor.setTipIDN(pIzraz.getTipIDN(djelokrug));
            cvor.setlIzraz(false);

        }
    }

    private void listaArgumenata(Cvor cvor) {
        if (cvor.getDjeca().size() == 1) {
            izrazPridruzivanja(cvor.getDjeca().get(0));
            if (greska) return;

            cvor.dodajTipParametra(cvor.getDjeca().get(0).getTipIDN(djelokrug));
        } else {
            listaArgumenata(cvor.getDjeca().get(0));
            if (greska) return;
            izrazPridruzivanja(cvor.getDjeca().get(2));

            List<String> tipovi = new ArrayList<>();
            tipovi.addAll(cvor.getDjeca().get(0).getTipoviParametara(djelokrug));
            tipovi.add(cvor.getDjeca().get(2).getTipIDN(djelokrug));
            cvor.setTipoviParametara(tipovi);
        }
    }


    private void viseIzraza(Cvor cvor, String operacija) {
        if (cvor.getDjeca().size() == 1) {
            switch(operacija){
                case "binIIzraz":
                    jednakosniIzraz(cvor.getDjeca().get(0));
                    break;
                case "binXIliIzraz":
                    viseIzraza(cvor.getDjeca().get(0), "binIIzraz");
                    break;
                case "binIliIzraz":
                    viseIzraza(cvor.getDjeca().get(0), "binXIliIzraz");
                    break;
                case "logIIzraz":
                    viseIzraza(cvor.getDjeca().get(0), "binIliIzraz");
                    break;
                case "logIliIzraz":
                    viseIzraza(cvor.getDjeca().get(0), "logIIzraz");
                    break;
                case "multiplikativniIzraz":
                    castIzraz(cvor.getDjeca().get(0));
                    break;
                case "aditivniIzraz":
                    viseIzraza(cvor.getDjeca().get(0), "multiplikativniIzraz");
                    break;
                case "odnosniIzraz":
                    viseIzraza(cvor.getDjeca().get(0), "aditivniIzraz");
                    break;
            }
            if (greska) return;

            cvor.setImeIDN(cvor.getDjeca().get(0).getImeIDN());
            cvor.setTipIDN(cvor.getDjeca().get(0).getTipIDN(djelokrug));
            cvor.setlIzraz(cvor.getDjeca().get(0).islIzraz(djelokrug));
            cvor.setTipoviParametara(cvor.getDjeca().get(0).getTipoviParametara(djelokrug));
        } else {
            viseIzraza(cvor.getDjeca().get(0), operacija);
            if (greska) return;
            if (!mozeSeCastat(cvor.getDjeca().get(0).getTipIDN(djelokrug), "KR_INT")) {
                ispisiError(cvor);
                return;
            }

            switch(operacija) {
                case "binIIzraz":
                    jednakosniIzraz(cvor.getDjeca().get(2));
                    break;
                case "binXIliIzraz":
                    viseIzraza(cvor.getDjeca().get(2), "binIIzraz");
                    break;
                case "binIliIzraz":
                    viseIzraza(cvor.getDjeca().get(2), "binXIliIzraz");
                    break;
                case "logIIzraz":
                    viseIzraza(cvor.getDjeca().get(2), "binIliIzraz");
                    break;
                case "logIliIzraz":
                    viseIzraza(cvor.getDjeca().get(2), "logIIzraz");
                    break;
                case "multiplikativniIzraz":
                    castIzraz(cvor.getDjeca().get(2));
                    break;
                case "aditivniIzraz":
                    viseIzraza(cvor.getDjeca().get(2), "multiplikativniIzraz");
                    break;
                case "odnosniIzraz":
                    viseIzraza(cvor.getDjeca().get(2), "aditivniIzraz");
                    break;
            }
            if (greska) return;
            if (!mozeSeCastat(cvor.getDjeca().get(2).getTipIDN(djelokrug), "KR_INT")) {
                ispisiError(cvor);
                return;
            }

            cvor.setlIzraz(false);
            cvor.setTipIDN("KR_INT");
        }
    }

    private void jednakosniIzraz(Cvor cvor){
        if (cvor.getDjeca().size() == 1){
            viseIzraza(cvor.getDjeca().get(0), "odnosniIzraz");
            if (greska) return;

            cvor.setImeIDN(cvor.getDjeca().get(0).getImeIDN());
            cvor.setTipIDN(cvor.getDjeca().get(0).getTipIDN(djelokrug));
            cvor.setlIzraz(cvor.getDjeca().get(0).islIzraz(djelokrug));
            cvor.setTipoviParametara(cvor.getDjeca().get(0).getTipoviParametara(djelokrug));
        } else {
            jednakosniIzraz(cvor.getDjeca().get(0));
            if (greska) return;
            if (cvor.getDjeca().get(0).isJeFunkcija() || !mozeSeCastat(cvor.getDjeca().get(0).getTipIDN(djelokrug), "KR_INT")) {
                ispisiError(cvor);
                return;
            }
            viseIzraza(cvor.getDjeca().get(2), "odnosniIzraz");
            if (greska) return;
            if (cvor.getDjeca().get(2).isJeFunkcija() || !mozeSeCastat(cvor.getDjeca().get(2).getTipIDN(djelokrug), "KR_INT")) {
                ispisiError(cvor);
                return;
            }
            cvor.setlIzraz(false);
            cvor.setTipIDN("KR_INT");
        }
    }

    private void unarniIzraz(Cvor cvor) {
        String sadrzaj = cvor.getDjeca().get(0).getSadrzaj();
        if (cvor.getDjeca().size() == 1) {
            postfiksIzraz(cvor.getDjeca().get(0));
            if (greska) return;
            cvor.setTipIDN(cvor.getDjeca().get(0).getTipIDN(djelokrug));
            cvor.setImeIDN(cvor.getDjeca().get(0).getImeIDN());
            cvor.setlIzraz(cvor.getDjeca().get(0).islIzraz(djelokrug));
            cvor.setTipoviParametara(cvor.getDjeca().get(0).getTipoviParametara(djelokrug));
        } else {
            if (sadrzaj.startsWith("OP_INC")  || sadrzaj.startsWith("OP_DEC")) {
                unarniIzraz(cvor.getDjeca().get(1));
                if (greska) return;
                if (!(cvor.getDjeca().get(1).islIzraz(djelokrug) && mozeSeCastat(cvor.getDjeca().get(1).getTipIDN(djelokrug), "KR_INT"))) {
                    ispisiError(cvor);
                    return;
                }
            } else {
                castIzraz(cvor.getDjeca().get(1));
                if (greska) return;
                if (!mozeSeCastat(cvor.getDjeca().get(1).getTipIDN(djelokrug), "KR_INT")) {
                    ispisiError(cvor);
                    return;
                }
            }

            cvor.setTipIDN("KR_INT");
            cvor.setlIzraz(false);
        }
    }

    private void castIzraz(Cvor cvor) {
        if (cvor.getDjeca().size() == 1) {
            unarniIzraz(cvor.getDjeca().get(0));
            if (greska) return;
            cvor.setTipIDN(cvor.getDjeca().get(0).getTipIDN(djelokrug));
            cvor.setlIzraz(cvor.getDjeca().get(0).islIzraz(djelokrug));
            cvor.setImeIDN(cvor.getDjeca().get(0).getImeIDN());
            cvor.setTipoviParametara(cvor.getDjeca().get(0).getTipoviParametara(djelokrug));
        } else {
            imeTipa(cvor.getDjeca().get(1));
            if (greska) return;
            castIzraz(cvor.getDjeca().get(3));
            if (greska) return;

            if (!mozeEksplicitanCast(cvor.getDjeca().get(3), cvor.getDjeca().get(1))) {
                ispisiError(cvor);
                return;
            }

            cvor.setlIzraz(false);
            cvor.setTipIDN(cvor.getDjeca().get(1).getTipIDN(djelokrug));
        }
    }

    private boolean mozeEksplicitanCast(Cvor cvor1, Cvor cvor2){
        String tip1 = cvor1.getTipIDN(djelokrug);
        String tip2 = cvor2.getTipIDN(djelokrug);

        if (!mozeSeCastat(tip2, tip1) && !(tip2.equals("KR_INT") && tip1.equals("KR_CHAR"))){
            return false;
        }
        if (cvor1.isJeFunkcija() || cvor2.isJeFunkcija()){
            return false;
        }
        return true;
    }

    private boolean mozeSeCastat(String tipIDN, String kr_int) {
        //Moze
        boolean moze = false;
        if(tipIDN.equals(kr_int)){
            return true;
        }else if(tipIDN.equals("KR_CHAR")) {
            if(kr_int.equals("KR_INT")){
                moze = true;
            }
        }

        return moze;

    }



    private boolean provjeriChar(String imeIDN) {
        if (imeIDN.length() == 3) {
            return true;
        } else if (imeIDN.charAt(1) == '\\'){
            char sign = imeIDN.charAt(2);
            List<Character> dopusteno = Arrays.asList('t', 'n', '0', '\\', '"', '\'');
            if (dopusteno.contains(sign)) return true;
            else return false;
        }
        return false;
    }

    private boolean jeDeklararirano(String ime, Djelokrug temp) {
        Djelokrug node = temp;
        while (node != null) {
            for (Cvor cvor : node.getSveDefinirano()) {
                if (ime.equals(cvor.getImeIDN()))
                    return true;
            }
            node = node.getSkrbnik();
        }
        return false;
    }

    private void listaIzrazaPridruzivanja(Cvor cvor) {
        if (cvor.getDjeca().size() == 1) {
            izrazPridruzivanja(cvor.getDjeca().get(0));
            if (greska) return;

            cvor.setVelicinaNiza(1);
            cvor.setTipoviParametara(Arrays.asList(cvor.getDjeca().get(0).getTipIDN(djelokrug)));
        } else {
            listaIzrazaPridruzivanja(cvor.getDjeca().get(0));
            if (greska) return;

            izrazPridruzivanja(cvor.getDjeca().get(2));
            if (greska) return;

            List<String> tipovi = new ArrayList<>();
            tipovi.addAll(cvor.getDjeca().get(0).getTipoviParametara(djelokrug));
            tipovi.add(cvor.getDjeca().get(2).getTipIDN(djelokrug));
            cvor.setTipoviParametara(tipovi);

            cvor.setVelicinaNiza(cvor.getDjeca().get(2).getVelicinaNiza()+1);
        }
    }

    public boolean imaMainFunkciju(){
        return imaMain;
    }

    public boolean sveFunkcijeDefinirane(){
        for (String dekl : deklariraneFunkcije){
            if (!definiraneFunkcije.contains(dekl)) {
                return false;
            }
        }
        for (Cvor d : dekl){
            for (Cvor df : def){
                if (df.getImeIDN().equals(d.getImeIDN())){
                    if (!df.getTipIDN(djelokrug).equals(d.getTipIDN(djelokrug))){
                        return false;
                    }
                    if (!df.getTipoviParametara(djelokrug).equals(d.getTipoviParametara(djelokrug))){
                        return false;
                    }
                }
            }
        }
        return true;
    }


    public static void main(String[] args) throws IOException {
        //BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get("src/lab3/primjeri/07_nedef_fun/test.in"))));
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));


        SemantickiAnalizator sa = new SemantickiAnalizator(br);
        //pushhh
    }


}
