import java.util.Objects;

public class Labela {

    private String ime;
    private Cvor cvor;
    private boolean jeFunkcija;
    private boolean jePrazna;

    public Labela(String ime, Cvor cvor) {
        this.ime = ime;
        this.cvor = cvor;
        jeFunkcija = false;
        jePrazna = false;
    }

    public Labela(String ime, Cvor cvor, boolean jelFunkcija){
        this.ime = ime;
        this.cvor = cvor;
        jeFunkcija = jelFunkcija;
        jePrazna = false;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public Cvor getCvor() {
        return cvor;
    }

    public void setCvor(Cvor cvor) {
        this.cvor = cvor;
    }

    public boolean isJeFunkcija() {
        return jeFunkcija;
    }

    public void setJeFunkcija(boolean jeFunkcija) {
        this.jeFunkcija = jeFunkcija;
    }

    public boolean isJePrazna() {
        return jePrazna;
    }

    public void setJePrazna(boolean jePrazna) {
        this.jePrazna = jePrazna;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Labela labela = (Labela) o;
        return Objects.equals(ime, labela.ime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ime);
    }
}
