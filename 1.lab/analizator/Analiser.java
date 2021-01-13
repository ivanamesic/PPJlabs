package ppj.analizator;

import ppj.Automat;
import ppj.Rule;
import ppj.State;
import ppj.Token;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Analiser {
    private List<Rule> rules;
    private List<State> states;
    private List<Token> tokens;

    public Analiser() {
        try {
            loadEveryThing();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadEveryThing() throws IOException, ClassNotFoundException {
        ObjectInputStream stream1 = new ObjectInputStream(new BufferedInputStream(new FileInputStream("src/ppj/resources/GLArules.ser")));
        rules = (ArrayList<Rule>) stream1.readObject();

        ObjectInputStream stream2 = new ObjectInputStream(new BufferedInputStream(new FileInputStream("src/ppj/resources/GLAstates.ser")));
        states = (ArrayList<State>) stream2.readObject();

        ObjectInputStream stream3 = new ObjectInputStream(new BufferedInputStream(new FileInputStream("src/ppj/resources/GLAtokens.ser")));
        tokens = (ArrayList<Token>) stream3.readObject();
    }


    public List<Rule> getRules() {
        return rules;
    }

    public List<State> getStates() {
        return states;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public List<Automat> getAutomatsForState(State state) {
        List<Automat> automats = new ArrayList<>();
        for (Rule rule: rules) {
            if (state.equals(rule.getState())) {
                automats.add(rule.getActionRule().getAutomat());
            }
        }
        return automats;
    }

    public Rule.ActionRule getActionsForAutomat(Automat automat){
        for (Rule rule: rules) {
            if (rule.getActionRule().getAutomat().equals(automat)) {
                return rule.getActionRule();
            }

        }
        return  null;
    }
}
