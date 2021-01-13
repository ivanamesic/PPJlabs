

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ENKAcvor implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Produkcija produkcija;
    private String znak;
    private ENKAcvor iduciCvor;
    private List<ENKAcvor> epsilonCvorovi;

    public ENKAcvor(Produkcija produkcija, String znak, ENKAcvor iduciCvor, List<ENKAcvor> epsilonCvorovi) {
        this.produkcija = produkcija;
        this.znak = znak;
        this.iduciCvor = iduciCvor;
        this.epsilonCvorovi = epsilonCvorovi;
    }

    public ENKAcvor(Produkcija produkcija) {
        this.produkcija = produkcija;
        epsilonCvorovi = new ArrayList<>();;
    }

    public ENKAcvor() {

    }

    public Produkcija getProdukcija() {
        return produkcija;
    }

    public void setProdukcija(Produkcija produkcija) {
        this.produkcija = produkcija;
    }

    public String getZnak() {
        return znak;
    }

    public void setZnak(String znak) {
        this.znak = znak;
    }

    public ENKAcvor getIduciCvor() {
        return iduciCvor;
    }

    public void setIduciCvor(ENKAcvor iduciCvor) {
        this.iduciCvor = iduciCvor;
    }

    public List<ENKAcvor> getEpsilonCvorovi() {
        return epsilonCvorovi;
    }

    public void setEpsilonCvorovi(List<ENKAcvor> epsilonCvorovi) {
        this.epsilonCvorovi = epsilonCvorovi;
    }

    public ENKAcvor copy() {
        return new ENKAcvor(this.produkcija, this.znak, this.iduciCvor, this.epsilonCvorovi);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ENKAcvor enkAcvor = (ENKAcvor) o;
        return Objects.equals(produkcija, enkAcvor.produkcija);
    }

    @Override
    public int hashCode() {
        return Objects.hash(produkcija);
    }

    @Override
    public String toString() {
        String epsiloni = "";
        for (ENKAcvor cvor : epsilonCvorovi){
            epsiloni += cvor.getProdukcija() + ", ";
        }
        String s = "ENKA cvor: " + produkcija + "\n";
        s += znak + "->" + "iduciCvor: " + (iduciCvor == null ? "" : iduciCvor.getProdukcija()) + "\n";
        s += "epsilonCvorovi: " + epsilonCvorovi + "\n";
        s+= "--------------------------------------------------\n";
        return s;
    }
}
