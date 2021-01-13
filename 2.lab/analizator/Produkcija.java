

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Produkcija implements Serializable {
	private static final long serialVersionUID = 1L;

    private String lijevaStrana;

    private int indeks;
    private List<String> desnaStrana = new ArrayList<>();
    private List<String> moguciZnakovi = new ArrayList<>();

    public Produkcija(String lijevaStrana, List<String> desnaStrana, int indeks, List<String> moguciZnakovi) {
        this.lijevaStrana = lijevaStrana;
        this.moguciZnakovi.addAll(moguciZnakovi);
        this.indeks = indeks;
        this.desnaStrana.addAll(desnaStrana);
        this.desnaStrana.add(indeks, "$");
    }

    public List<String> getDesnaStrana() {
        return desnaStrana;
    }

    public Produkcija pomakniTocku() {
        List<String> novaDesna = new ArrayList<>();
        novaDesna.addAll(desnaStrana);

        novaDesna.remove(indeks);
        return new Produkcija(lijevaStrana, novaDesna, indeks+1, moguciZnakovi);
    }

    public String getPocetnaProdukcija() {
        return lijevaStrana;
    }

    public int getIndeks() {
        return indeks;
    }

    public List<String> getMoguciZnakovi() {
        return moguciZnakovi;
    }

    public boolean jeLiPotpuna() {
        return indeks == desnaStrana.size()-1;
    }

    public String iduciZnak() {
        return desnaStrana.get(indeks+1);
    }

    @Override
    public String toString() {
        return "Produkcija:" +
                lijevaStrana + "->" + desnaStrana + " {" + moguciZnakovi + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Produkcija that = (Produkcija) o;
        return indeks == that.indeks &&
                lijevaStrana.equals(that.lijevaStrana) &&
                desnaStrana.equals(that.desnaStrana) &&
                moguciZnakovi.equals(that.moguciZnakovi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lijevaStrana, indeks, desnaStrana, moguciZnakovi);
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<>(Arrays.asList("b", "B", "c"));
        //list.add()
        Produkcija p = new Produkcija("A",list , 0, Arrays.asList("$"));
        Produkcija p1 = p.pomakniTocku().pomakniTocku();

        //System.out.println(p1.jeLiPotpuna());
    }

}
