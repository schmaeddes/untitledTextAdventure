package game.logic.actionsystem;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import game.state.Entity;
import game.state.EntitySet;

/**
 * The action signature matcher matches a given set of entities against a
 * signature.
 */
public class ActionSignatureMatcher {
    private final ActionSignature signature;
    private final int signatureEntries[];
    private final Entity matchedEntities[];
    private EntitySet workingSet;

    /**
     * Constructor
     *
     * @param signature The signature
     */
    public ActionSignatureMatcher(ActionSignature signature) {
        this.signature = signature;
        this.signatureEntries = new int[signature.getTotalEntityCount()];
        this.matchedEntities = new Entity[signature.getTotalEntityCount()];
        int fillFromIdx = 0;
        for (int i = 0; i < signature.getEntryCount(); ++i) {
            int fillToIndex = fillFromIdx + signature.getEntrySize(i);
            Arrays.fill(this.signatureEntries, fillFromIdx, fillToIndex, i);
            fillFromIdx = fillToIndex;
        }
    }

    /**
     * Tries to match the given set of entities to the signature which was provided
     * during construction. If a match was found the matching entities are returned
     * in the order they match the signature.
     *
     * @param entities The set of entities to match
     * @return The matching entities in order of the signature or <code>null</code>
     *         if no match was found
     */
    public List<Entity> tryMatch(EntitySet entities) {
        int givenEntityCount = entities == null ? 0 : entities.getSize();
        if (givenEntityCount != this.signature.getTotalEntityCount()) {
            return null;
        }
        this.workingSet = new EntitySet(entities.getAll());
        return this.match(0) ? Arrays.asList(this.matchedEntities) : null;
    }

    /**
     * Recursive matching algorithm.
     *
     * @param idx The index to start the match
     * @return <code>true</code> if match was found
     */
    private boolean match(int idx) {
        if (idx == this.matchedEntities.length) {
            return true;
        }
        int currentEntryIndex = this.signatureEntries[idx];
        Set<Entity> allowedEntities = this.signature.getEntryEntitySet(currentEntryIndex);
        List<Entity> matchingEntities = this.workingSet.getAll().stream().filter(allowedEntities::contains).toList();
        for (Entity e : matchingEntities) {
            this.workingSet.remove(e);
            this.matchedEntities[idx] = e;
            if (this.match(idx + 1)) {
                return true;
            }
            this.workingSet.add(e);
        }
        return false;
    }
}
