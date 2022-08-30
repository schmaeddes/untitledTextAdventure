package game.state;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import game.logic.GameLogic;
import game.logic.actionsystem.PlayerAction;
import game.logic.actionsystem.PlayerActionExecutor;

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
    public record EntityConnection(Entity to, Entity associatedEntity) {
    }

    private final String id;
    private final Set<String> attributes = new HashSet<>();
    private Entity location;
    private boolean closed = false;
    private final EntitySet contents;
    private final Map<String, EntityConnection> connections = new HashMap<>();
    private final Map<String, List<PlayerAction>> playerActions = new HashMap<>();
    final Set<EntitySet> containingPersistentSets = new HashSet<>();

    public Entity(String id, String... attributes) {
        this.id = id;
        this.attributes.addAll(Arrays.asList(attributes));
        this.contents = EntitySet.createPersistent(this.id + "::contents");
    }

    public String getId() {
        return this.id;
    }

    public Set<String> getAttributes() {
        return Collections.unmodifiableSet(this.attributes);
    }

    public boolean addAttribute(String attribute) {
        return this.attributes.add(attribute);
    }

    public boolean removeAttribute(String attribute) {
        return this.attributes.remove(attribute);
    }

    public void toggleAttribute(String attribute) {
        if (!this.attributes.remove(attribute)) {
            this.attributes.add(attribute);
        }
    }

    public void switchAttribute(String attr1, String attr2) {
        if (this.attributes.remove(attr1)) {
            this.attributes.add(attr2);
        } else if (this.attributes.remove(attr2)) {
            this.attributes.add(attr1);
        }
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

    public boolean isClosed() {
        return this.closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
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

    public void connectUnidirectional(String directionId, Entity to, Entity associatedEntity) {
        this.connections.put(directionId, new EntityConnection(to, associatedEntity));
    }

    public void connectBidirectional(String dirIdFromThisToOther, Entity associatedEntity, String dirIdFromOtherToThis,
            Entity to) {
        this.connections.put(dirIdFromThisToOther, new EntityConnection(to, associatedEntity));
        to.connections.put(dirIdFromOtherToThis, new EntityConnection(this, associatedEntity));
    }

    public void removeSingleConnection(String directionId) {
        this.connections.remove(directionId);
    }

    public void removeBidirectionalConnection(String dirIdFromThisToOther, String dirIdFromOtherToThis) {
        EntityConnection c = this.connections.remove(dirIdFromThisToOther);
        if (c != null) {
            c.to.connections.remove(dirIdFromOtherToThis);
        }
    }

    public EntityConnection getConnection(String directionId) {
        return this.connections.get(directionId);
    }

    public Set<String> getConnections() {
        return Collections.unmodifiableSet(this.connections.keySet());
    }

    public PlayerAction pushPlayerAction(String id, PlayerActionExecutor executor) {
        List<PlayerAction> l = this.playerActions.get(id);
        if (l == null) {
            this.playerActions.put(id, l = new LinkedList<>());
        }
        PlayerAction action = new PlayerAction(id, executor);
        action.pushNeededEntity(this);
        l.add(action);
        return action;
    }

    /**
     * Searches for the first player action that can operate on this entity and the
     * given secondary entities and executes it if one is found.
     *
     * @param id                The action id
     * @param secondaryEntities The set of secondary entities on which the action
     *                          should operate
     * @return <code>true</code>, if an action was found and executed
     */
    public boolean tryExecutePlayerAction(String id, EntitySet secondaryEntities, GameLogic logic) {
        if (!this.tryExecutePlayerAction(this.playerActions.get(id), secondaryEntities, logic)) {
            for (EntitySet set : this.containingPersistentSets) {
                if (this.tryExecutePlayerAction(set.getPlayerActions(id), secondaryEntities, logic)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Searches for the first player action that can operate on this entity and no
     * secondary entities executes it if one is found.
     *
     * @param id The action id
     * @return <code>true</code>, if an action was found and executed
     */
    public boolean tryExecutePlayerAction(String id, GameLogic logic) {
        return this.tryExecutePlayerAction(id, null, logic);
    }

    private boolean tryExecutePlayerAction(List<PlayerAction> actions, EntitySet secondaryEntities, GameLogic logic) {
        if (actions != null) {
            for (PlayerAction a : actions) {
                if (a.tryExecute(this, secondaryEntities, logic)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
