package game.logic.actionsystem;

import game.logic.GameLogic;
import game.state.Entity;

/**
 * An action executor provides specific instructions how to manipulate the game
 * logic.
 */
public interface ActionExecutor {

    /**
     * Executes the game logic manupulation.
     *
     * @param logic The game logic
     * @param actor The actor
     * @param args  Arguments
     */
    public boolean execute(GameLogic logic, Entity actor, Entity... args);
}
