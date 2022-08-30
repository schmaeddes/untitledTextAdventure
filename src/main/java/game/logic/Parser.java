package game.logic;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import game.state.Entity;
import game.state.EntitySet;
import startup.Environment;
import util.Commands;
import util.TextColors;

public class Parser implements Closeable {
    private final Scanner scanner = new Scanner(System.in);

    public void executeUserInput(GameLogic logic) {
        // Big TODO
        // currently just reads an action id followed by entity ids

        String greenPrompt = TextColors.BLUE.colorize(">");
        System.out.printf("%s ", greenPrompt);
        List<String> input = Arrays.stream(scanner.nextLine().split("\\s+")).map(String::toLowerCase).toList();
        if (!input.isEmpty()) {
            String actionId = input.get(0);
            List<Entity> args = new ArrayList<>(input.size() - 1);
            for (int i = 1; i < input.size(); ++i) {
                Entity e = logic.getState().getEntityById(input.get(i));
                if (e == null) {
                    logic.printRaw("Keine Ahnung, was du mit %s meinst, du Tölpel.\n", input.get(i));
                    return;
                }
                args.add(e);
            }
            Entity primaryEntity = this.getPrimaryEntity(logic, actionId, args);
            if (primaryEntity != null) {
                primaryEntity.tryExecutePlayerAction(actionId, EntitySet.createTemporary(args), logic);
            } else if(logic.tryExecutePlayerAction(actionId, EntitySet.createTemporary(args))) {
                logic.printRaw("Das geht doch so nicht.\n", args);
            }
        }
    }

    private Entity getPrimaryEntity(GameLogic logic, String actionId, List<Entity> arguments) {
        return switch (actionId) {
            case "go" -> logic.getPlayer();
            case "take", "open", "close" -> arguments.remove(0);
            default -> null;
        };
    }

    public void parse(List<String> parameter) {
        String command = parameter.get(0);

        switch (command) {
            case "go" -> Commands.go(Environment.instance.getAreaByString(parameter.get(1)));
            case "info" -> Commands.info();
        }
    }

    @Override
    public void close() throws IOException {
        this.scanner.close();
    }
}
