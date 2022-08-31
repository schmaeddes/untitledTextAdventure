package game.state;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Entities are the building blocks of the game logic's world.
 *
 * They can represent objects, creatures, the player(s), locations, and more.
 * Each entity can also contain zero or more other entities, working as an
 * abstract container. It can be connected to other entities through keywords,
 * e.g. north, south, west, east. A connection can also be associated with
 * another entity to model doors or other types of portals. Finally, an entity
 * can store generic attributes.
 */
public class Entity {
    private final GameState gameState;
    private final String id;
    final Set<EntitySet> containingPersistentSets = new HashSet<>();
    private final Map<String, String> attributes = new HashMap<>();
    private Entity location;
    private final EntitySet contents;
    private final Map<Entity, Entity> connections = new HashMap<>();

    /**
     * Creates an entity and registers it to the given game state. DO NOT CALL THIS
     * CONSTRUCTOR DIRECTLY! Use {@link GameState#createEntity(String)},
     * {@link EntitySet#createEntity(String)}, or
     * {@link EntitySet#createEntity(String, Entity)} instead.
     *
     * @param state The game sate to register the entity to
     * @param id    The entity id
     */
    public Entity(GameState gameState, String id) {
        this.gameState = gameState;
        this.id = id;
        this.gameState.registerEntity(this);
        this.contents = this.gameState.createEntitySet(this.id + "::contents");
    }

    public String getId() {
        return this.id;
    }

    public String getAttribute(String key) {
        String value = this.attributes.get(key);
        return value == null ? "" : value;
    }

    public boolean getBoolAttribute(String key) {
        return Boolean.parseBoolean(this.getAttribute(key));
    }

    public void setAttribute(String key, Object value) {
        if (value == null) {
            this.attributes.remove(key);
        } else {
            this.attributes.put(key, value.toString());
        }
    }

    public void removeAttribute(String key) {
        this.setAttribute(key, null);
    }

    public void setLocation(Entity location) throws CircularLocationException {
        if (location == this) {
            throw new CircularLocationException("setLocation.self");
        } else if (this.contains(location, true)) {
            throw new CircularLocationException("setLocation.circular");
        } else if (this.location != location) {
            if (this.location != null) {
                this.location.contents.remove(this);
            }
            this.location = location;
            if (this.location != null) {
                this.location.contents.add(this);
            }
        }
    }

    public boolean contains(Entity other, boolean recursive) {
        return this.contents.getAll().stream().anyMatch(e -> e == other || (recursive && e.contains(other, true)));
    }

    public Entity getLocation() {
        return this.location;
    }

    public Set<Entity> getContents() {
        return this.contents.getAll();
    }

    public void connectUnidirectional(Entity direction, Entity to) {
        this.connections.put(direction, to);
    }

    public void connectBidirectional(Entity dirFromThisToOther, Entity dirFromOtherToThis, Entity other) {
        this.connections.put(dirFromThisToOther, other);
        other.connections.put(dirFromOtherToThis, this);
    }

    public void removeSingleConnection(Entity direction) {
        this.connections.remove(direction);
    }

    public void removeBidirectionalConnection(Entity dirFromThisToOther, Entity dirFromOtherToThis) {
        Entity other = this.connections.remove(dirFromThisToOther);
        if (other != null && other.connections.get(dirFromOtherToThis) == this) {
            other.connections.remove(dirFromOtherToThis);
        }
    }

    public Entity getConnectedEntity(Entity direction) {
        return this.connections.get(direction);
    }

    public Set<Entity> getConnectionDirections() {
        return Collections.unmodifiableSet(this.connections.keySet());
    }

    @Override
    public String toString() {
        return this.id;
    }
}
