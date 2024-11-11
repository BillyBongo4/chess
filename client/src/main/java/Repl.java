import java.util.Scanner;

public class Repl {
    public Repl(String serverUrl) {
        System.out.println("ServerUrl: " + serverUrl);
    }

    public void run() {
        System.out.println("TEMP!");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            result = scanner.nextLine();
        }
    }
}
