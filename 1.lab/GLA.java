package ppj;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GLA {

    private List<State> states = new ArrayList<>();
    private List<Rule> rules = new ArrayList<>();
    private List<Token> tokens = new ArrayList<>();
    private List<RegularDefinition> regularDefinitions = new ArrayList<>();
    private Map<String, String> regDefinitionsMap = new HashMap<>();

    public GLA(BufferedReader br) {
        try {
            parse(br);
            initializeList();
            convertMapDef();
            parseRules();
            writeObjects();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //ak nekaj ne valja mozda je fileoutputstream problem jer nije klouzan
    private void writeObjects() throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("src/ppj/resources/GLAstates.ser"));
        out.writeObject(states);
        out.close();

        out = new ObjectOutputStream(new FileOutputStream("src/ppj/resources/GLArules.ser"));
        out.writeObject(rules);
        out.close();

        out = new ObjectOutputStream(new FileOutputStream("src/ppj/resources/GLAtokens.ser"));
        out.writeObject(tokens);
        out.close();

    }

    private void initializeList() {
        for (Map.Entry<String, String> entry: regDefinitionsMap.entrySet()){
            regularDefinitions.add(new RegularDefinition(entry.getKey(), entry.getValue()));
        }
    }

    private void parseRules() {
        List<Rule.ActionRule> actionRules = new ArrayList<>();
        for (Rule r: rules){
            Automat automat = new Automat();
            String converted = convertRegDef(r.getRegularExpression());
            r.setRegularExpression(converted);
            StatePair result = convert(r.getRegularExpression(), automat);
            automat.setStartState(result.getLeftState());
            automat.setAcceptableState(result.getRightState());

            State state = null;
            int backIndex = -1;
            String lexem = null;
            boolean newRow = false;
            for (String action: r.getActions()){
                action.trim();
                if (action.startsWith(SpecialArguments.VRATI_SE)){
                    backIndex = Integer.parseInt(action.split(" ")[1]);
                } else if (action.startsWith(SpecialArguments.UDJI_U_STANJE)){
                    state = new State(action.split(" ")[1]);
                } else if(action.startsWith(SpecialArguments.NOVI_REDAK)) {
                    newRow = true;
                } else {
                    lexem = action;
                }
            }
            r.setActionRule(new Rule.ActionRule(lexem, state, backIndex, newRow, automat));
        }
    }


    private void parse(BufferedReader br) throws IOException {
        String line;
        List<String> lines = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            if (line == null) break;
            line = line.trim();
            if (line.startsWith("{")) {
                String[] splitted = line.split(" ");
                regDefinitionsMap.put(splitted[0], splitted[1]);
            } else if (line.startsWith("%X")) {
                String[] splitted2 = line.split(" ");
                for (int i = 1; i < splitted2.length; i++) {
                    states.add(new State(splitted2[i]));
                }

            } else if (line.startsWith("%L")) {
                String[] splitted2 = line.split(" ");
                for (int i = 1; i < splitted2.length; i++) {
                    tokens.add(new Token(splitted2[i]));
                }
                break;
            }
        }

        while ((line = br.readLine()) != null) {
            if (line == null) break;
            line = line.trim();
            if (line.startsWith("<")) {
                String state = line.substring(1, line.indexOf('>'));
                String regular = line.substring(line.indexOf('>') + 1, line.length());
                regular = returnRegularExpression(regular);
                br.readLine();
                List<String> list = new ArrayList<>();
                while (!(line = br.readLine()).equals("}")) {
                    list.add(line);
                }
                rules.add(new Rule(new State(state), regular, list));
            }
        }

    }

    private String returnRegularExpression(String regDefName) {
        for (RegularDefinition regDef : regularDefinitions){
            if (regDefName.contains(regDef.getName())) {
                regDefName.replace(regDef.getName(), regDefinitionsMap.get(regDef.getName()));
            }
        }
        return regDefName;
    }

    /**
     * Vraća regularni izraz bez reference na regularne definicije, mijenja ih s njihovim vrijednostima iz regDefinitionsMap
     */
    private String convertRegDef(String regularDefinition) {

        for (String s : regDefinitionsMap.keySet()) {
            if (regularDefinition.contains(s)) {
                regularDefinition = regularDefinition.replace(s, "(" + regDefinitionsMap.get(s) + ")");
            }
        }
        return  regularDefinition;
    }

    /**
     * Mijenja vrijednosti u mapi tako da zamjenjuje reference regularnih definicija s njihovim vrijednostima
     */
    private void convertMapDef() {
        for (String s : regDefinitionsMap.keySet()) {
            RegularDefinition regularDefinition = new RegularDefinition(s, regDefinitionsMap.get(s));
            for (String key : regDefinitionsMap.keySet()) {
                if (regDefinitionsMap.get(s).contains(key)) {
                    regularDefinition.setRegularExpression(regDefinitionsMap.get(s).replace(key, "(" + regDefinitionsMap.get(key) + ")"));
                    regDefinitionsMap.put(s, regularDefinition.getRegularExpression());
                }
            }
        }
    }

    private int newState(Automat automat) {
        return automat.addState();
    }

    /**
     * Provjerava ima li izraz značenje operatora ili je prefiksiran s \\
     * @param expression
     * @param i
     * @return
     */
    private boolean isOperator(String expression, int i) {
        int br = 0;
        while (i - 1 >= 0 && expression.charAt(i - 1) == '\\') {
            br++;
            i--;
        }
        return br % 2 == 0;
    }

    private StatePair convert(String expression, Automat automat){
        int noBrackets = 0;
        int index = 0;
        List<String> choices = new ArrayList<>();

        for (int i = 0; i < expression.length(); i++){
            if (expression.charAt(i)=='(' && isOperator(expression, i)){
                noBrackets++;
            } else if (expression.charAt(i)==')' && isOperator(expression, i)){
                noBrackets--;
            } else if (noBrackets == 0 && expression.charAt(i) == '|' && isOperator(expression, i)){
                choices.add(expression.substring(index, i));
                index=i+1;
            }
        }

        if (index != 0){
            choices.add(expression.substring(index, expression.length()));
        }

        int leftState = newState(automat);
        int rightState = newState(automat);

        if (index!=0){
            for (int i = 0; i < choices.size(); i++){
                StatePair temp = convert(choices.get(i), automat);
                addEpsilonTransition(automat, leftState, temp.getLeftState());
                addEpsilonTransition(automat, temp.getRightState(), rightState);
            }
            return new StatePair(leftState, rightState);
        } else {
            boolean prefixed = false;
            int lastState = leftState;
         a:   for (int i = 0; i < expression.length(); i++) {
                int a, b;
                if (prefixed) {
                    prefixed = false;
                    char transitionChar;
                    switch (expression.charAt(i)){
                        case 't':
                            transitionChar = '\t';
                            break;
                        case 'n':
                            transitionChar = '\n';
                            break;
                        case '_':
                            transitionChar = ' ';
                            break;
                        default:
                            transitionChar = expression.charAt(i);
                    }

                    a = newState(automat);
                    b = newState(automat);
                    addTransition(automat, a, b, transitionChar);
                } else {
                    if (expression.charAt(i)=='\\') {
                            prefixed = true;
                            continue a;
                    } else if (expression.charAt(i) != '('){
                        a = newState(automat);
                        b = newState(automat);
                        if (expression.charAt(i) == '$') {
                            addEpsilonTransition(automat, a, b);
                        } else {
                            addTransition(automat, a, b, expression.charAt(i));
                        }
                    } else {
                        int j = findClosedBracket(expression, i);

                        StatePair temporary = convert(expression.substring(i+1, j), automat);
                        a = temporary.getLeftState();
                        b = temporary.getRightState();
                        i = j;
                    }
                }

                if (i+1 < expression.length() && expression.charAt(i+1)=='*'){
                    int x = a;
                    int y = b;
                    a = newState(automat);
                    b = newState(automat);
                    addEpsilonTransition(automat, a, x);
                    addEpsilonTransition(automat, y, b);
                    addEpsilonTransition(automat, a, b);
                    addEpsilonTransition(automat, y, x);
                    i++;
                }

                addEpsilonTransition(automat, lastState, a);
                lastState = b;
            }

            addEpsilonTransition(automat, lastState, rightState);
        }

        return new StatePair(leftState, rightState);
    }
    private int findClosedBracket(String expression, int index) {
        int counter = 1;
        for (int i = index+1; i < expression.length(); i++) {
            if (expression.charAt(i) == '(') {
                counter++;
            } else if (expression.charAt(i) == ')') {
                counter--;
            }

            if (counter == 0) {
                return i;
            }
        }
        return -1;
    }
    private void addTransition(Automat automat, int a, int b, char transitionChar) {
        automat.addTransition(a, b, transitionChar);
    }

    private void addEpsilonTransition(Automat automat, int state, int tempState){
        automat.addEpsilonTransition(state, tempState);
    }

    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        /*BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get("src/ppj/test.lan"))));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        GLA gla = new GLA(br);


    }
}

