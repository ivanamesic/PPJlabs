package ppj;

public class RegularDefinition {
    private String name;
    private String regularExpression;

    public RegularDefinition(String name, String regularExpression) {
        checkArgs(name, regularExpression);
        this.name = name;
        this.regularExpression = regularExpression;
    }

    public RegularDefinition(){}

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " " + regularExpression;
    }

    public String getRegularExpression() {
        return regularExpression;
    }

    public void setRegularExpression(String regularExpression) {
        this.regularExpression = regularExpression;
    }

    public static void checkArgs(String name, String regularExpression) {

    }




}
