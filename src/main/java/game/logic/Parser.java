package game.logic;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import game.logic.actionsystem.Action;
import game.logic.actionsystem.ActionParser;
import startup.Environment;
import util.Commands;
import util.TextColors;

public class Parser implements Closeable {
    private final Scanner scanner = new Scanner(System.in);
    private final List<ActionParser> actionParsers = new LinkedList<>();

    public Action readAction() {
        String greenPrompt = TextColors.BLUE.colorize(">");
        System.out.printf("%s ", greenPrompt);
        List<String> input = Arrays.stream(scanner.nextLine().split("\\s+")).map(String::toLowerCase).toList();

        for (ActionParser actionParser : this.actionParsers) {
            Action action = actionParser.parseAction(input);
            if (action != null) {
                return action;
            }
        }
        return null;
    }

    public void parse(List<String> parameter) {
        String command = parameter.get(0);

        switch (command) {
            case "go" -> Commands.go(Environment.instance.getAreaByString(parameter.get(1)));
            case "info" -> Commands.info();
        }
    }

    public void pushActionParser(ActionParser actionParser) {
        this.actionParsers.add(actionParser);
    }

    @Override
    public void close() throws IOException {
        this.scanner.close();
    }
}
