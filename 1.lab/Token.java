package ppj;

import java.io.Serializable;

public class Token implements Serializable {

    private String name;

    public Token(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
