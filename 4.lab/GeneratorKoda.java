import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GeneratorKoda {

    public static void main(String[] args) throws IOException {
        /*String[] tests = {"01_ret_broj", "02_ret_global", "03_veliki_broj", "04_neg_broj", "05_plus",
                "06_plus_signed", "07_minus", "08_bitor", "09_bitand", "10_bitxor", "11_fun1", "12_fun2", "13_fun3",
                "13_scope1", "14_scope2", "15_scope3", "16_scope4", "17_char", "18_init_izraz", "19_if1", "20_if2",
                "21_if3", "22_if4", "23_niz1", "24_niz2", "25_niz3", "26_niz4", "27_rek", "28_rek_main",
                "29_for", "30_while", "31_inc", "32_gcd", "33_short", "34_izraz", "35_params", "36_params2", "37_funcloop"};
*//*
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
*//*

        *//*BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get("a.frisc"))));
        SemantickiAnalizator sa = new SemantickiAnalizator(br);
        GeneratorKoda gk = new GeneratorKoda();
        gk.printall(sa, bw);*//*

        String line = "src/lab4/test/test/";

        for (String t : tests) {
            System.out.println("-------------- " + t + " -----------------");
            BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(line + t + "/test.in"))));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(line + t + "/myout.out"))));
            SemantickiAnalizator sa = new SemantickiAnalizator(br);
            GeneratorKoda.printall(sa, bw);
            if (t.equals("24_niz2")) {
                System.out.println(sa.getKorijen().getFriscLinije());
                sa.getListaLabela().forEach(b -> System.out.println(b.getIme() + b.isJePrazna()));
                System.out.println("************************");
            }
            bw.close();
        }*/
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get("a.frisc"))));
        SemantickiAnalizator sa = new SemantickiAnalizator(br);
        GeneratorKoda gk = new GeneratorKoda();
        gk.printall(sa, bw);
        bw.close();
    }






    private void printall(SemantickiAnalizator sa, BufferedWriter bw) throws IOException {
        boolean el = false;
        for (String line : sa.getKorijen().getFriscLinije()){
            try {
                if (line.startsWith("TRUE_") || line.startsWith("FALSE_") || line.startsWith("ELSE_") || line.startsWith("ENDIF_")){
                    bw.write(line + "\n");
                    continue;
                }
                /*if (el){
                    el = false;
                    bw.write(line+"\n");
                    continue;
                }*/
                bw.write((line.startsWith("THEN_") || line.startsWith("F_") || line.startsWith("G_")) ? line + "\n" : "\t\t" + line + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        List<Labela> nova = new ArrayList<>();
        for (Labela la : sa.getListaLabela()){
            if (!nova.contains(la)){
                nova.add(la);
            }
        }
        for (Labela l : nova){
            if (!l.isJeFunkcija()){
                /*System.out.print(l.getIme() + "\t\t");
                System.out.println(l.getCvor().getFriscLinije());*/
                if (l.isJePrazna()){
                    bw.write(l.getIme() + "\tDW %D 0\n");
                    continue;
                }
                String ime = "";
                try{
                    Integer.parseInt(l.getCvor().getImeIDN());
                    ime = l.getCvor().getImeIDN();
                } catch (Exception e){
                    char c = l.getCvor().getImeIDN().charAt(1);
                    int i = (int) c;
                    ime = String.valueOf(i);
                }
                bw.write(l.getIme() + "\t\t" + "DW %D " + ime + "\n");
            }
        }
    }


}
