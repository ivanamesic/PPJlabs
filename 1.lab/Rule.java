package ppj;

import java.io.Serializable;
import java.util.List;

public class Rule implements Serializable {

    private State state;
    private String regularExpression;
    private List<String> actions;
    private ActionRule actionRule;

    public Rule(State state, String regularExpression, List<String> actions) {
        this.state = state;
        this.regularExpression = regularExpression;
        this.actions = actions;
    }

    public ActionRule getActionRule() {
        return actionRule;
    }

    public void setActionRule(ActionRule actionRule) {
        this.actionRule = actionRule;
    }

    public State getState() {
        return state;
    }

    public String getRegularExpression() {
        return regularExpression;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setRegularExpression(String regularExpression) {
        this.regularExpression = regularExpression;
    }

    public static class ActionRule implements  Serializable{
        private String lexem;
        private State state;
        private int backIndex;
        private boolean newRow;
        private Automat automat;

        public ActionRule() {
        }
        public ActionRule(String lexem, State state, int backIndex, boolean newRow, Automat automat){
            this.lexem = lexem;
            this.state = state;
            this.backIndex = backIndex;
            this.newRow = newRow;
            this.automat = automat;
        }

        @Override
        public String toString() {
            return "ActionRule{" +
                    "lexem='" + lexem + '\'' +
                    ", state=" + state +
                    ", backIndex=" + backIndex +
                    ", newRow=" + newRow +
                    ", automat=" + automat +
                    '}';
        }

        public String getLexem() {
            return lexem;
        }

        public State getState() {
            return state;
        }

        public int getBackIndex() {
            return backIndex;
        }

        public boolean isNewRow() {
            return newRow;
        }

        public Automat getAutomat() {
            return automat;
        }
    }

    @Override
    public String toString() {
        return "Rule{" +
                "state=" + state +
                ", regularExpression='" + regularExpression + '\'' +
                '}';
    }
}
