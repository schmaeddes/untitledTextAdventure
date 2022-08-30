package game.state;

import java.util.HashMap;
import java.util.Map;

public class GameState {
    private final Map<String, Entity> entities = new HashMap<>();

    public GameState() {

    }

    public Entity createEntity(String id) {
        Entity e = new Entity(id);
        this.entities.put(id, e);
        return e;
    }

    public Entity getEntityById(String id) {
        return this.entities.get(id);
    }
}