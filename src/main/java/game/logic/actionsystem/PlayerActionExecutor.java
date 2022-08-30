package game.logic.actionsystem;

import game.logic.GameLogic;
import game.state.Entity;

/**
 * A player action executor provides specific instructions how to manipulate the
 * game logic.
 */
public interface PlayerActionExecutor {

    /**
     * Executes the game logic manupulation.
     *
     * @param logic The game logic
     * @param args  Arguments
     */
    public boolean execute(GameLogic logic, Entity... args);
}
