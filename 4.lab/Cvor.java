import java.util.ArrayList;
import java.util.List;

public class Cvor {
    private List<Cvor> djeca;
    private String sadrzaj;
    private int razina;
    private boolean nezavrsni;
    private int redak = -1;
    private String vrijednost;
    private boolean jeKonstanta;

    private int velicinaNiza = -100;

    private String imeIDN = "";
    private String tipIDN = "";
    private boolean jeFunkcija;
    private boolean lIzraz;
    private boolean uPetlji;
    private boolean definirano;

    private List<String> imenaParametara;
    private List<String> tipoviParametara;

    private ArrayList<String> friscLinije;
    private String labela;



    public Cvor(String sadrzaj, int razina) {
        this.djeca = new ArrayList<>();
        this.sadrzaj = sadrzaj;
        this.razina = razina;
        this.friscLinije = new ArrayList<>();

        this.imenaParametara = new ArrayList<>();
        this.tipoviParametara = new ArrayList<>();
        if (sadrzaj.trim().startsWith("<")) {
            this.nezavrsni = true;
            this.vrijednost = sadrzaj.trim();
        } else {
            this.nezavrsni = false;
            this.redak = Integer.parseInt(this.sadrzaj.split(" ")[1]);
            this.vrijednost = this.sadrzaj.split(" ")[2];
        }
    }

    public boolean isDefinirano() {
        return definirano;
    }

    public void setDefinirano(boolean definirano) {
        this.definirano = definirano;
    }

    public boolean isuPetlji() {
        return uPetlji;
    }

    public void setuPetlji(boolean uPetlji) {
        this.uPetlji = uPetlji;
    }

    public boolean islIzraz(SemantickiAnalizator.Djelokrug djelokrug) {
        if (!sadrzaj.split(" ")[0].equals("IDN")){
            return lIzraz;
        } else {
            return (boolean) nadiUdjelokrugu(djelokrug, "lizraz");
        }
    }

    public void setlIzraz(boolean lIzraz) {
        this.lIzraz = lIzraz;
    }

    public Cvor() {
        djeca = new ArrayList<>();
    }

    public List<Cvor> getDjeca() {
        return djeca;
    }

    //pushhh

    public void setDjeca(List<Cvor> djeca) {
        this.djeca = djeca;
    }

    public String getSadrzaj() {
        return sadrzaj;
    }

    public void setSadrzaj(String sadrzaj) {
        this.sadrzaj = sadrzaj;
    }

    public int getRazina() {
        return razina;
    }

    public void setRazina(int razina) {
        this.razina = razina;
    }

    public void dodajDijete(Cvor dijete){
        this.djeca.add(dijete);
    }

    public String getVrijednost() {
        return vrijednost;
    }

    public boolean isNezavrsni() {
        return nezavrsni;
    }

    public int getRedak() {
        return redak;
    }

    public String getImeIDN() {
       if (imeIDN.isEmpty()){
            if (sadrzaj.split(" ").length > 1){
                return sadrzaj.split(" ")[2];
            }
            return sadrzaj;
        }
        return imeIDN;
    }

    public void setImeIDN(String imeIDN) {
        this.imeIDN = imeIDN;
    }

    private Object nadiUdjelokrugu(SemantickiAnalizator.Djelokrug djelokrug, String option){
        SemantickiAnalizator.Djelokrug node = new SemantickiAnalizator.Djelokrug();
        node = djelokrug;

        while (node != null) {
            for (Cvor deklaracija : node.getSveDefinirano()) {
                if (deklaracija.getImeIDN().equals(this.getImeIDN())){
                    switch(option){
                        case "tip":
                            return deklaracija.getTipIDN(null);
                        case "tipovi":
                            return deklaracija.getTipoviParametara(null);
                        case "lizraz":
                            return (deklaracija.getTipIDN(djelokrug).equals("KR_INT") || deklaracija.getTipIDN(djelokrug).equals("KR_CHAR")) &&
                                    !deklaracija.isJeFunkcija();
                    }
                }

            }
            node = node.getSkrbnik();
        }
        switch(option){
            case "tip":
                return getTipIDN(null);
            case "tipovi":
                return getTipoviParametara(null);
            case "lizraz":
                return islIzraz(null);
            default:
                return null;
        }
    }

    @SuppressWarnings("unchecked")
    public String getTipIDN(SemantickiAnalizator.Djelokrug djelokrug) {
        String[] parts = getSadrzaj().split(" ");
        if (djelokrug!= null && parts[0].equals("IDN")) {
            return  (String) nadiUdjelokrugu(djelokrug, "tip");
        }
        return tipIDN;
    }

    public void setTipIDN(String tipIDN) {
        this.tipIDN = tipIDN;
    }

    public boolean isJeFunkcija(){
        if (tipoviParametara.size() == 0) {
            return false;
        } else
            return true;
    }

    public void setJeFunkcija(boolean jeFunkcija) {
        this.jeFunkcija = jeFunkcija;
    }


    public List<String> getImenaParametara() {
        return imenaParametara;
    }

    public void setImenaParametara(List<String> imenaParametara) {
        this.imenaParametara = imenaParametara;
    }

    public List<String> getTipoviParametara(SemantickiAnalizator.Djelokrug djelokrug) {
        if (djelokrug!= null && this.getSadrzaj().split(" ")[0].equals("IDN")) {
            return (List<String>) nadiUdjelokrugu(djelokrug, "tipovi");
        }
        return this.tipoviParametara;
    }

    public void setTipoviParametara(List<String> tipoviParametara) {
        this.tipoviParametara = tipoviParametara;
    }

    public void dodajTipParametra(String tipParametra){
        this.tipoviParametara.add(tipParametra);
    }

    public void dodajImeParametra(String imeParametra) {
        this.imenaParametara.add(imeParametra);
    }

    public boolean isJeKonstanta() {
        return jeKonstanta;
    }

    public void setJeKonstanta(boolean jeKonstanta) {
        this.jeKonstanta = jeKonstanta;
    }

    public int getVelicinaNiza() {
        return velicinaNiza;
    }

    public void setVelicinaNiza(int velicinaNiza) {
        this.velicinaNiza = velicinaNiza;
    }

    public void dodajLinije(ArrayList<String> linije){
        this.friscLinije.addAll(linije);
    }

    public void dodajLiniju(String linija) {
        this.friscLinije.add(linija);
    }

    public ArrayList<String> getFriscLinije() {
        return friscLinije;
    }

    public void makniFriscLiniju(int x){
        friscLinije.remove(x);
    }

    public void setFriscLinije(ArrayList<String> friscLinije) {
        this.friscLinije = friscLinije;
    }

    public String getLabela() {
        return labela;
    }

    public void setLabela(String labela) {
        this.labela = labela;
    }
}
