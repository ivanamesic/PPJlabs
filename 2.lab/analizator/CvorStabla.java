

import java.util.ArrayList;
import java.util.List;

public class CvorStabla {
	private static final long serialVersionUID = 1L;

    private List<CvorStabla> djeca;
    private String znakCvora;

    public CvorStabla(String znakCvora) {
        this.znakCvora = znakCvora;
        this.djeca = new ArrayList<>();
    }

    public void dodajDijete(CvorStabla dijete) {
        djeca.add(dijete);
    }
    public List<CvorStabla> getDjeca() {
        return djeca;
    }

    public String getZnakCvora() {
        return znakCvora;
    }

    public String ispisiStablo(int razmak) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < razmak; i++) {
            sb.append(" ");
        }
        sb.append(this.znakCvora + "\n");
        if (djeca != null){
            /*
            for (CvorStabla c : djeca) {
                sb.append(c.ispisiStablo(razmak+1));
            }
             */
            for(int indx = djeca.size()-1; indx >= 0; indx--){
                sb.append(djeca.get(indx).ispisiStablo(razmak + 1));
            }
        }

        return sb.toString();
    }
}
