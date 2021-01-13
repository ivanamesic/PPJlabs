package ppj;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;

public class Automat implements Serializable {

    public class Transition implements  Serializable{
        private int startState;
        private int endState;
        private char sign;

        public Transition(int startState, int endState, char sign) {
            this.startState = startState;
            this.endState = endState;
            this.sign = sign;
        }

        @Override
        public String toString() {
            return "Transition{" +
                    "startState=" + startState +
                    ", endState=" + endState +
                    ", sign=" + sign +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Transition that = (Transition) o;
            return startState == that.startState &&
                    endState == that.endState &&
                    sign == that.sign;
        }

        @Override
        public int hashCode() {
            return Objects.hash(startState, endState, sign);
        }

        public int getStartState() {
            return startState;
        }

        public int getEndState() {
            return endState;
        }

        public char getSign() {
            return sign;
        }
    }

    private int startState;
    private int acceptableState;
    private int numOfStates;
    private List<Transition> transitions;
    private List<Transition> epsilonTransitions;

    private boolean active;
    private Set<Integer> currentStates = new HashSet<>();

    public Automat() {
        numOfStates = 0;
        transitions = new ArrayList<>();
        epsilonTransitions = new ArrayList<>();
    }

    public void setStartState(int startState) {
        this.startState = startState;
    }

    public void setAcceptableState(int acceptableState) {
        this.acceptableState = acceptableState;
    }

    public int addState(){
        return ++numOfStates;
    }

    public int getNumOfStates() {
        return numOfStates;
    }

    public int getStartState() {
        return startState;
    }

    public int getAcceptableState() {
        return acceptableState;
    }

    public List<Transition> getTransitions() {
        return transitions;
    }

    public void addTransition(int a, int b, char c){
        boolean isnew = true;

        for (Transition t: transitions){
            if (t.getStartState() == a && t.getEndState() == b && t.getSign() == c){
                isnew = false;
                break;
            }
        }

        if (isnew) {
            transitions.add(new Transition(a, b, c));
        }
    }

    public void addEpsilonTransition(int startState, int endState){
        boolean isnew = true;
        for (Transition t: epsilonTransitions){
            if (t.getStartState() == startState && t.getEndState() == endState){
                isnew = false;
                break;
            }
        }

        if (isnew) {
            epsilonTransitions.add(new Transition(startState, endState, '$'));
        }
    }



    public Set<Integer> getCurrentStates() {
        return currentStates;
    }

    private boolean addedStates;
    public void goThroughTransitions(char sign) {
        boolean a = false;
        List<Integer> endStates = new ArrayList<>();
        for (Transition transition: transitions) {
            if (currentStates.contains(transition.getStartState()) && transition.getSign() == sign) {
                endStates.add(transition.getEndState());
                a = true;
            }
        }
        addedStates = a;
        if (a) {
            currentStates = new HashSet<>(endStates);
        } else {
            currentStates.clear();
        }
        goEpsilon();
    }

    public void goEpsilon() {
        List<Integer> endStates = new ArrayList<>();
        boolean a = false;
        for (Transition transition: epsilonTransitions) {
            if (currentStates.contains(transition.getStartState()) && !currentStates.contains(transition.getEndState())) {
                endStates.add(transition.getEndState());
                //System.out.println("$: " + transition.getStartState() +" -> " + transition.getEndState());
                a = true;
            }
        }
        if(a){
            currentStates.addAll(endStates);
            goEpsilon();
        }
        addedStates = addedStates ? true : a;
        //System.out.println("<goEpsilon> *prvi prijelaz* epsilon"+Arrays.toString(currentStates.toArray()));
    }

    public boolean isAddedStates() {
        return addedStates;
    }

    public void setInitialConfig() {
        currentStates.add(getStartState());
        for(int i = 0; i < numOfStates; i++){
            goEpsilon();
        }


    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Automat automat = (Automat) o;
        return startState == automat.startState &&
                acceptableState == automat.acceptableState &&
                Objects.equals(transitions, automat.transitions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startState, acceptableState, transitions);
    }

    public boolean isAcceptable() {
        return currentStates.contains(acceptableState);
    }

    @Override
    public String toString() {
        return "Automat{" +
                "startState=" + startState +
                ", acceptableState=" + acceptableState +
                ", numOfStates=" + numOfStates +
                '}';
    }

    public String printTransitions() {
        StringBuilder sb = new StringBuilder();
        sb.append("Prijelazi: \n");
        for (Transition t : transitions) {
            sb.append(t.getSign() + ": " + t.getStartState() + "->" + t.getEndState()+ "\n");
        }
        sb.append("Epsilon prijelazi: \n");
        for (Transition t : epsilonTransitions) {
            sb.append(t.getStartState() + "->" + t.getEndState() + "\n");
        }
        return sb.toString();
    }
}
