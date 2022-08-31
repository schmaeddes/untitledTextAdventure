package game.state;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import game.logic.GameLogic;

public class EntitySet {
    private final GameState gameState;
    private final String id;
    private final Set<Entity> entities = new HashSet<>();

    /**
     * Creates a persistent, named entity set. DO NOT CALL THIS CONSTRUCTOR
     * DIRECTLY. Use {@link GameState#createEntitySet(String)} instead.
     *
     * @param state The game state to register the entity set to
     * @param id    The entity set id
     */
    public EntitySet(GameState gameState, String id) {
        this.gameState = gameState;
        this.id = id;
        this.gameState.registerEntitySet(this);
    }

    /**
     * Creates a temporary entity set.
     *
     * @param entities Initial entities contained in the set
     */
    public EntitySet(Collection<Entity> entities) {
        this.id = null;
        this.gameState = null;
        this.entities.addAll(entities);
    }

    /**
     * Creates a temporary entity set.
     *
     * @param entities Initial entities contained in the set
     */
    public EntitySet(Entity... entities) {
        this(Arrays.asList(entities));
    }

    public String getId() {
        return this.id;
    }

    public boolean isPersistent() {
        return this.id != null;
    }

    public boolean isEmpty() {
        return this.entities.isEmpty();
    }

    public Set<Entity> getAll() {
        return Collections.unmodifiableSet(this.entities);
    }

    public int getSize() {
        return this.entities.size();
    }

    public void add(Collection<Entity> entities) {
        for (Entity e : entities) {
            if (this.entities.add(e) && this.id != null) {
                e.containingPersistentSets.add(this);
            }
        }
    }

    public void add(Entity... entities) {
        this.add(Arrays.asList(entities));
    }

    public void remove(Entity... entities) {
        for (Entity e : entities) {
            if (this.entities.remove(e) && this.id != null) {
                e.containingPersistentSets.remove(this);
            }
        }
    }

    public boolean contains(Entity entity) {
        return this.entities.contains(entity);
    }

    public Entity createEntity(String id) {
        Entity e = this.gameState.createEntity(id);
        this.add(e);
        return e;
    }

    public Entity createEntity(String id, Entity location) throws CircularLocationException {
        Entity e = this.createEntity(id);
        e.setLocation(location);
        return e;
    }

    public Entity collapse(GameLogic logic) {
        if (this.entities.size() <= 1) {
            return this.entities.stream().findAny().orElse(null);
        } else {
            // TODO if more than 1 candidate, ask user to specify
            return this.entities.stream().findAny().orElse(null);
        }
    }
}
