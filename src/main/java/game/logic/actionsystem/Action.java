package game.logic.actionsystem;

import java.util.List;

import game.logic.GameLogic;
import game.state.Entity;
import game.state.EntitySet;

/**
 * A player action represents a single action which operates on any number of
 * entities.
 *
 * It has an identifier, e.g. {@code "combine"}, a signature of needed entities,
 * and an executor. The signature consists of multiple entries, each one being
 * either a single entity or a group of entities. To execute an action a
 * {@link game.state.EntitySet} must be given which can be matched against its
 * signature.
 */
public class Action {
    private final String id;
    private final ActionExecutor executor;
    private final ActionSignatureMatcher signatureMatcher;

    /**
     * Constructs a player action by providing an id and an executor.
     *
     * @param id        The action's id
     * @param signature The action's signature
     * @param executor  The executor which is executed if a matching entity set is
     *                  provided to
     *                  {@link Action#tryExecute(Entity, EntitySet, GameLogic)}.
     */
    public Action(String id, ActionSignature signature, ActionExecutor executor) {
        this.id = id;
        this.signatureMatcher = new ActionSignatureMatcher(signature);
        this.executor = executor;
    }

    /**
     * Getter for the action's id.
     *
     * @return The action id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Tries to match the given entities to this action's signature and tries to
     * execute it if match was successful.
     *
     * @param actor    The actor which executes the action
     * @param entities A set of entities which will be matched against the signature
     *                 (excl. the first entry if <code>primaryEntity</code> was not
     *                 <code>null</code>)
     * @param logic    The game logic
     * @return <code>true</code>, if the given entities match the signature and the
     *         action was executed successfully
     */
    public boolean tryExecute(Entity actor, EntitySet entities, GameLogic logic) {
        List<Entity> matched = this.signatureMatcher.tryMatch(entities);
        return matched != null && this.executor.execute(logic, actor, matched.toArray(new Entity[0]));
    }
}
