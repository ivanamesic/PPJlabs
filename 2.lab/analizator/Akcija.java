

import java.io.Serializable;

public class Akcija implements Serializable {
	private static final long serialVersionUID = 1L;

    AkcijaEnum tip;
    int indeks;
    Production produkcija;

    public Akcija(AkcijaEnum tip, int indeks, Production produkcija) {
        this.tip = tip;
        this.indeks = indeks;
        this.produkcija = produkcija;
    }
}
