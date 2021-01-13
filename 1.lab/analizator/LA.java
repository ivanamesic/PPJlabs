package ppj.analizator;


import ppj.Automat;
import ppj.Rule;
import ppj.State;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class LA {
    private Analiser analiser;
    private String input;
    private Map<Automat, List<Rule>> automatMap = new HashMap<>();
    BufferedWriter output;
    List<Automat> goodAutos = new ArrayList<>();


    public LA() {
        this.analiser = new Analiser();
        try {
            this.output = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get("src/ppj/my.out"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            loadInputFile();
            makeAutomatMap();
            processFile();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*private void checkIfGood() throws IOException {
        List<String> testLines = Files.readAllLines(Paths.get("src/ppj/test.out"));
        List<String> myLines = Files.readAllLines(Paths.get("src/ppj/my.out"));
        if (testLines.size() != myLines.size()) {
            System.err.println("kriva duljina!");
        }
        for (int i = 0; i < testLines.size(); i++) {
            System.err.println("line " + i + myLines.get(i).equals(testLines.get(i)));
        }
    }*/


    public void makeAutomatMap(){
        for (Rule rule: analiser.getRules()) {
            if (automatMap.containsKey(rule.getActionRule().getAutomat())) {
                automatMap.get(rule.getActionRule().getAutomat()).add(rule);

            } else {
                List<Rule> rules = new ArrayList<>();
                rules.add(rule);
                automatMap.put(rule.getActionRule().getAutomat(), rules);
            }
        }
    }

    public void loadInputFile() throws IOException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        //BufferedReader stdin = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get("src/ppj/test.in"))));
        String line = "";
        StringBuilder sb = new StringBuilder();
        while (true) {
            line = stdin.readLine();
            if (line == null)
                break;
            sb.append(line + "\n");
        }
        stdin.close();
        input = sb.toString();
    }

    private Automat findBestAutomat(char sign) {
        goodAutos.clear();
        Automat goodOne = null;
        List<Automat> copied = new ArrayList<>();
        copied.addAll(availableAutomats);
        int counter = 0;
        int i = 1;
        for (Automat automat: copied) {

           Set<Integer> firstStates = automat.getCurrentStates();

            automat.goThroughTransitions(sign);

            Set<Integer> lastStates = automat.getCurrentStates();
            if (!automat.isAddedStates()) {
                availableAutomats.remove(automat);
            }
            if (automat.isAcceptable()){
                goodOne = automat;
                goodAutos.add(automat);
            }
        }
        return goodOne;
    }

    private void findLongestWord() {
        int maxIndex = 0;
        int a = currentIndex;
        int startIndex = glavniIndex;
        int endIndex = startIndex;
        boolean found = false;

        int i = 0;
        while(endIndex < input.length()){
            Automat automat = findBestAutomat(input.charAt(endIndex));
            if (!goodAutos.isEmpty()){
                automat = goodAutos.get(0);
            }

            if(automat != null){
                currentString = input.substring(startIndex, endIndex+1);
                currentBestAutomat = automat;
                found = true;
            }

            if(availableAutomats.isEmpty()){
                if(!found){
                    break;
                }
                glavniIndex = endIndex;
                break;
            }
            endIndex++;

        }
        if (!found) currentBestAutomat = null;


    }

    private int currentIndex;
    private String currentString ;
    private List<Automat> availableAutomats;
    private Automat currentBestAutomat;

    private static int glavniIndex;


    private void processFile() {
        int index = 0;
        currentIndex = 0;
        glavniIndex = 0;
        int rows = 1;
        State currentState = analiser.getStates().get(0);
        while(glavniIndex < input.length()-1) {
            char sign = input.charAt(glavniIndex);
            availableAutomats = analiser.getAutomatsForState(currentState);

            for (Automat automat: availableAutomats) {
                automat.setInitialConfig();
            }
            currentString = "";
            findLongestWord();

            if (currentBestAutomat == null) {
                glavniIndex++;
                continue;
            } else {
                index = currentIndex + 1;

            }
            List<Rule> rules = automatMap.get(currentBestAutomat);
            Rule rule2 = automatMap.get(currentBestAutomat).get(0);
            for (Rule r : rules){

                if (r.getState() != null && r.getState().equals(currentState)){
                    rule2 = r;
                }
            }
            Rule.ActionRule rule = rule2.getActionRule();
            currentBestAutomat = null;availableAutomats.clear();

            if (rule.getBackIndex() > -1) {
                glavniIndex = glavniIndex - currentString.length()+rule.getBackIndex();
                currentString = currentString.substring(0,rule.getBackIndex());
            }
            if (rule.isNewRow()) {
                rows++;
            }
            if (rule.getState() != null) {
                currentState = rule.getState();
            }
            if (rule.getLexem() != null) {
                if (!rule.getLexem().equals("-") && !currentString.isEmpty()){
                    System.out.println(rule.getLexem() + " " + rows + " " + currentString);
                }
            }
        }

    }



    public static void main(String[] args) {
       LA la = new LA();
    }
}

