package game.state;

import java.util.LinkedList;
import java.util.List;

import game.logic.EntityDescription;

public class GameState {
    private final List<Entity> entities = new LinkedList<>();

    public GameState() {

    }

    public Entity createEntity(String id, String... attributes) {
        Entity e = new Entity(id, attributes);
        this.entities.add(e);
        return e;
    }

    public EntitySet searchForEntity(EntityDescription description) {
        return EntitySet.createTemporary(this.entities.stream().filter(e -> e.getId().equals(description.getMainWord())
                && e.getAttributes().containsAll(description.getAttributes())).toList());
    }
}