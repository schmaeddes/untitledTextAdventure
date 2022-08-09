package game.state;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import game.logic.GameLogic;

public class EntitySet {
    private final Set<Entity> entities;

    public EntitySet(Entity... entities) {
        this(Arrays.asList(entities));
    }

    public EntitySet(Collection<Entity> entities) {
        this.entities = new HashSet<>(entities);
    }

    public boolean isEmpty() {
        return this.entities.isEmpty();
    }

    public Set<Entity> getAll() {
        return Collections.unmodifiableSet(this.entities);
    }

    public boolean add(Entity entity) {
        return this.entities.add(entity);
    }

    public boolean remove(Entity entity) {
        return this.entities.remove(entity);
    }

    public boolean contains(Entity entity) {
        return this.entities.contains(entity);
    }

    public Entity collapse(GameLogic logic) {
        if(this.entities.size() <= 1) {
            return this.entities.stream().findAny().orElse(null);
        } else {
            // TODO if more than 1 candidate, ask user to specify
            return this.entities.stream().findAny().orElse(null);
        }
    }

    public EntitySet getFiltered(Predicate<Entity> acceptFunction) {
        return new EntitySet(this.entities.stream().filter(acceptFunction).toList());
    }
}
