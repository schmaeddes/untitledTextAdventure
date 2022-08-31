package game.logic.actionsystem;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import game.state.Entity;
import game.state.EntitySet;

/**
 * An action signature describes which entities are needed for an action to
 * execute. Note that even if the signature matches the execution itself can
 * still fail.
 */
public class ActionSignature {
    private record SignatureEntry(EntitySet entities, int size) {
    }

    private final List<SignatureEntry> entries = new LinkedList<>();
    private int totalEntityCount = 0;

    /**
     * Pushes a new entry to the signature consisting of a single entity which is
     * needed for execution.
     *
     * @param entity The needed entity to push
     */
    public void pushEntry(Entity entity) {
        this.pushEntry(new EntitySet(entity), 1);
    }

    /**
     * Pushes a new entry to the signature consisting of a set of entities of which
     * a specific count is needed for execution.
     *
     * @param entities The set of needed entities
     * @param size     How many entities of the given set are needed for execution
     */
    public void pushEntry(EntitySet entities, int size) {
        this.entries.add(new SignatureEntry(entities, size));
        this.totalEntityCount += size;
    }

    /**
     * The total entity count is the sum of all entry sizes.
     *
     * @return The total entity count
     */
    public int getTotalEntityCount() {
        return this.totalEntityCount;
    }

    /**
     * The total number of entries pushed to the signature.
     *
     * @return The entry count
     */
    public int getEntryCount() {
        return this.entries.size();
    }

    /**
     * The size of an entry is the size paramter given when the entry was pushed to
     * the signature.
     *
     * @param entryIndex The index of the entry
     * @return The size of the entry
     */
    public int getEntrySize(int entryIndex) {
        return this.entries.get(entryIndex).size();
    }

    /**
     * The allowed entities of an entry were given when the entry was pushed to the
     * signature.
     *
     * @param entryIndex The entry's index
     * @return The allowed entities of an entry
     */
    public Set<Entity> getEntryEntitySet(int entryIndex) {
        return this.entries.get(entryIndex).entities().getAll();
    }
}
