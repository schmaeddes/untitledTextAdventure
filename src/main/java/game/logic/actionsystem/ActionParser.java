package game.logic.actionsystem;

import java.util.List;

public interface ActionParser {
    public Action parseAction(List<String> userInput);
}
