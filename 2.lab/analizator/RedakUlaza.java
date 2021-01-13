

public class RedakUlaza {
    private String uniformniZnak;
    private int brRetka;
    private String nizZnakova;

    public RedakUlaza(String uniformniZnak, int brRetka, String nizZnakova) {
        this.uniformniZnak = uniformniZnak;
        this.brRetka = brRetka;
        this.nizZnakova = nizZnakova;
    }

    public String getUniformniZnak() {
        return uniformniZnak;
    }

    public void setUniformniZnak(String uniformniZnak) {
        this.uniformniZnak = uniformniZnak;
    }

    public int getBrRetka() {
        return brRetka;
    }

    public void setBrRetka(int brRetka) {
        this.brRetka = brRetka;
    }

    public String getNizZnakova() {
        return nizZnakova;
    }

    public void setNizZnakova(String nizZnakova) {
        this.nizZnakova = nizZnakova;
    }

    public String toString() {
        return uniformniZnak + " " + brRetka + " " + nizZnakova;
    }
}
