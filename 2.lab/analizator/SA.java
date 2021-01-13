


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SA {
    private List<RedakUlaza> input;
    private Table table;
    private List<String> sinkronizacijski;

    public SA() {
        try {
            loadInputFile();
            loadEverything();
            LRParser lr = new LRParser(table, input, sinkronizacijski);
            //jeliMozdaTocno(lr.getVrhStabla().ispisiStablo(0));
            System.out.print(lr.getVrhStabla().ispisiStablo(0));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void loadInputFile() throws IOException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        //BufferedReader stdin = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get("src/ppj/test.in"))));
        String line = "";
        List<RedakUlaza> input = new ArrayList<>();
        while (true) {
            line = stdin.readLine();
            if (line == null)
                break;
            String[] parts = line.split(" ");
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < parts.length; i++) {
                sb.append(parts[i]+" ");
            }
            sb.deleteCharAt(sb.length()-1);
            input.add(new RedakUlaza(parts[0], Integer.parseInt(parts[1]), sb.toString()));
        }
        stdin.close();
        this.input = input;
    }

	@SuppressWarnings("unchecked")
    private void loadEverything() throws IOException, ClassNotFoundException {
        ObjectInputStream stream1 = new ObjectInputStream(new BufferedInputStream(new FileInputStream(getClass().getResource("GSAtable.ser").getPath())));
        table = (Table) stream1.readObject();

        ObjectInputStream stream2 = new ObjectInputStream(new BufferedInputStream(new FileInputStream(getClass().getResource("GSAsink.ser").getPath())));
        sinkronizacijski = (List<String>) stream2.readObject();
    }

    public static void main(String[] args) {
        SA sa = new SA();
    }
}
