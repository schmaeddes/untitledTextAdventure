import java.util.List;

public class Parser {

    public void parse(List<String> parameter) {
        String command = parameter.get(0);

            switch (command) {
                case "go" -> Commands.go(Main.environment.getAreaByString(parameter.get(1)));
                case "info" -> Commands.info();
            }

    }
}
