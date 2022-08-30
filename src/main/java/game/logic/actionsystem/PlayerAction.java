package game.logic.actionsystem;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import game.logic.GameLogic;
import game.state.Entity;
import game.state.EntitySet;

/**
 * A player action represents a single action which operates on any number of
 * entities.
 *
 * It has an identifier, e.g. <code>"combine"</code>, a signature of needed
 * entities, and an executor. The signature consists multiple entries, each one
 * being either a single entity or a group of entities. To execute a player
 * action an <a href="#EntitySet"><code>EntitySet</code></a> must be given which
 * can be matched against its signature.
 */
public class PlayerAction {
    private record SignatureEntry(EntitySet entities, int count) {
    }

    private final String id;
    private final PlayerActionExecutor executor;
    private final List<SignatureEntry> signatureEntries = new LinkedList<>();
    private int neededEntitiesTotalCount = 0;

    /**
     * Constructs a player action by providing an id and an executor.
     *
     * @param id       The action id
     * @param executor The executor which is called when
     *                 <a href="#tryExecute">tryExecute</a> is called with an entity
     *                 set which can be matched against the signature.
     */
    public PlayerAction(String id, PlayerActionExecutor executor) {
        this.id = id;
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
     * Pushes a new entry to this action' signature consisting of a single entity
     * which is needed for execution.
     *
     * @param entity The needed entity to push
     */
    public void pushNeededEntity(Entity entity) {
        this.signatureEntries.add(new SignatureEntry(EntitySet.createTemporary(entity), 1));
        ++this.neededEntitiesTotalCount;
    }

    /**
     * Pushes a new entry to this action's signature consisting of a set of entities
     * of which a specific count is needed for execution.
     *
     * @param entities The set of needed entities
     * @param count    How many entities of the given set are needed for execution
     */
    public void pushVaryingNeededEntites(EntitySet entities, int count) {
        this.signatureEntries.add(new SignatureEntry(entities, count));
        this.neededEntitiesTotalCount += count;
    }

    /**
     * Tries to match the given entities to this action's signature and executes it
     * if successful.
     *
     * @param primaryEntity     An optional primary entity which will be matched
     *                          against the first signature entry.
     * @param secondaryEntities A set of entities which will be matched against the
     *                          signature (excl. the first entry if
     *                          <code>primaryEntity</code> was not
     *                          <code>null</code>)
     * @param logic             The game logic
     * @return <code>true</code>, if given entities match the signature and the
     *         action was executed
     */
    public boolean tryExecute(Entity primaryEntity, EntitySet secondaryEntities, GameLogic logic) {
        if ((primaryEntity == null ? 0 : 1)
                + (secondaryEntities == null ? 0 : secondaryEntities.getSize()) != this.neededEntitiesTotalCount) {
            return false;
        }
        int entityCounts[] = new int[this.signatureEntries.size()];
        Entity entitiesToUse[] = new Entity[this.neededEntitiesTotalCount];
        if (primaryEntity != null) {
            if (this.signatureEntries.isEmpty() || !this.signatureEntries.get(0).entities().contains(primaryEntity)) {
                return false;
            }
            entitiesToUse[entityCounts[0]++] = primaryEntity;
        }
        if (secondaryEntities != null) {
            for (Entity e : secondaryEntities.getAll()) {
                int idx = 0;
                for (int i = 0; i < this.signatureEntries.size(); ++i) {
                    SignatureEntry signatureEntry = this.signatureEntries.get(i);
                    if (entityCounts[i] != signatureEntry.count() && signatureEntry.entities().contains(e)) {
                        entitiesToUse[idx + entityCounts[i]++] = e;
                        break;
                    }
                    idx += signatureEntry.count();
                }
            }
        }
        if (Arrays.stream(entitiesToUse).anyMatch(Objects::isNull)) {
            return false;
        }
        return this.executor.execute(logic, entitiesToUse);
    }
}
