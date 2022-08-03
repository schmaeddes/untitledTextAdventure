import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Main {

    public static Environment environment = new Environment();
    public static Parser parser = new Parser();

    public static void main(String[] args) {

        String greenPrompt = TextColors.BLUE.colorize(">");

        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.printf("%s ", greenPrompt);
            List<String> input = Arrays.stream(scanner.nextLine().split(" ")).map(o -> o.toLowerCase(Locale.ROOT)).toList();

            parser.parse(input);
        }

    }
}
