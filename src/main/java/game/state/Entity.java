package game.state;

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
    private final String id;
    final Set<EntitySet> containingPersistentSets = new HashSet<>();
    private final Map<String, String> attributes = new HashMap<>();
    private Entity location;
    private final EntitySet contents;
    private final Map<Entity, Entity> connections = new HashMap<>();
    private final Map<String, List<PlayerAction>> playerActions = new HashMap<>();

    public Entity(String id) {
        this.id = id;
        this.contents = EntitySet.createPersistent(this.id + "::contents");
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

    public Entity createContainedEntity(GameLogic logic, String id) {
        Entity e = logic.getState().createEntity(id);
        try {
            e.setLocation(this);
        } catch (CircularLocationException ex) {
            // should not happen
            ex.printStackTrace();
        }
        return e;
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
