

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Production implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nezavrsni;
    private List<String> produkti;

    public void setNezavrsni(String nezavrsni) {
        this.nezavrsni = nezavrsni;
    }

    public void setProdukti(List<String> produkti) {
        this.produkti = produkti;
    }
    public Production(){}

    public Production(String nezavrsni, List<String> produkti) {
        this.nezavrsni = nezavrsni;
        this.produkti = produkti;
    }

    public String getNezavrsni() {
        return nezavrsni;
    }

    public List<String> getProdukti() {
        return produkti;
    }

    @Override
    public String toString() {
        return "Production{" +
                "nezavrsni='" + nezavrsni + '\'' +
                ", produkti=" + produkti +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Production that = (Production) o;
        return Objects.equals(nezavrsni, that.nezavrsni) &&
                Objects.equals(produkti, that.produkti);
    }



    @Override
    public int hashCode() {
        return Objects.hash(nezavrsni, produkti);
    }

    public void addAll(List<String> otherProducts){
        this.produkti.addAll(otherProducts);
    }
}
