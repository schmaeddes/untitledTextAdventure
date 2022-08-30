package game.state;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import game.logic.GameLogic;
import game.logic.actionsystem.PlayerAction;
import game.logic.actionsystem.PlayerActionExecutor;

public class EntitySet {
    public static EntitySet createTemporary(Entity... entities) {
        return new EntitySet(null, Arrays.asList(entities));
    }

    public static EntitySet createTemporary(Collection<Entity> entities) {
        return new EntitySet(null, entities);
    }

    public static EntitySet createPersistent(String name, Entity... entities) {
        return new EntitySet(name, Arrays.asList(entities));
    }

    public static EntitySet createPersistent(String name, Collection<Entity> entities) {
        return new EntitySet(name, entities);
    }

    private final String name;
    private final Set<Entity> entities;
    private final Map<String, List<PlayerAction>> playerActions = new HashMap<>();

    private EntitySet(String name, Collection<Entity> entities) {
        this.name = name;
        this.entities = new HashSet<>(entities);
        if (this.name != null) {
            this.entities.forEach(e -> e.containingPersistentSets.add(this));
        }
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

    public boolean add(Entity entity) {
        if (this.entities.add(entity)) {
            if (this.name != null) {
                entity.containingPersistentSets.add(this);
            }
            return true;
        }
        return false;
    }

    public boolean remove(Entity entity) {
        if(this.entities.remove(entity)) {
            if(this.name != null) {
                entity.containingPersistentSets.remove(this);
            }
            return true;
        }
        return false;
    }

    public boolean contains(Entity entity) {
        return this.entities.contains(entity);
    }

    public Entity collapse(GameLogic logic) {
        if (this.entities.size() <= 1) {
            return this.entities.stream().findAny().orElse(null);
        } else {
            // TODO if more than 1 candidate, ask user to specify
            return this.entities.stream().findAny().orElse(null);
        }
    }

    public EntitySet getFiltered(Predicate<Entity> acceptFunction) {
        return EntitySet.createTemporary(this.entities.stream().filter(acceptFunction).toList());
    }

    public PlayerAction pushPlayerAction(String id, PlayerActionExecutor executor) {
        List<PlayerAction> l = this.playerActions.get(id);
        if (l == null) {
            this.playerActions.put(id, l = new LinkedList<>());
        }
        PlayerAction action = new PlayerAction(id, executor);
        action.pushVaryingNeededEntites(this, 1);
        l.add(action);
        return action;
    }

    public List<PlayerAction> getPlayerActions(String id) {
        List<PlayerAction> l = this.playerActions.get(id);
        return l == null ? Collections.emptyList() : Collections.unmodifiableList(this.playerActions.get(id));
    }
}
