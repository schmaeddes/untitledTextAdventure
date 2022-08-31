package game.state;

import java.util.HashMap;
import java.util.Map;

public class GameState {
    private final Map<String, Entity> entities = new HashMap<>();
    private final Map<String, EntitySet> entitySets = new HashMap<>();

    public GameState(String savegameJsonPath) {
        try {
            /*
             * TODO: load from savegame JSON
             */
            EntitySet genericDirections = this.createEntitySet("genericDirections");
            Entity west = genericDirections.createEntity("west");
            Entity east = genericDirections.createEntity("east");

            EntitySet houseDirections = this.createEntitySet("houseDirections");
            Entity inside = houseDirections.createEntity("inside");
            Entity outside = houseDirections.createEntity("outside");

            EntitySet locations = this.createEntitySet("locations");
            Entity forestPath01 = locations.createEntity("forestPath01");
            Entity clearing = locations.createEntity("clearing");
            Entity houseOutside = locations.createEntity("houseOutside");
            Entity houseInside = locations.createEntity("houseInside");
            locations.createEntity("houseMainDoor");

            forestPath01.connectBidirectional(east, west, clearing);
            forestPath01.connectBidirectional(west, east, houseOutside);
            houseOutside.connectBidirectional(inside, outside, houseInside);

            EntitySet characters = this.createEntitySet("characters");
            characters.createEntity("player", clearing);

            EntitySet collectibles = this.createEntitySet("collectibles");
            collectibles.createEntity("apple01", forestPath01);
            collectibles.createEntity("apple02", forestPath01);
        } catch (CircularLocationException ex) {
            ex.printStackTrace();
        }
    }

    public Entity createEntity(String id) {
        if (this.entities.containsKey(id)) {
            throw new UnsupportedOperationException("duplicate entity id: " + id);
        }
        return new Entity(this, id);
    }

    public Entity getEntityById(String id) {
        return this.entities.get(id);
    }

    public EntitySet createEntitySet(String id) {
        if (this.entitySets.containsKey(id)) {
            throw new UnsupportedOperationException("duplicate entity set id: " + id);
        }
        return new EntitySet(this, id);
    }

    public EntitySet getEntitySetById(String id) {
        return this.entitySets.get(id);
    }

    /**
     * Registers an entity to the game state. DO NOT CALL THIS OUTSIDE OF THE ENTITY
     * CONSTRUCTOR!
     *
     * @param entity The entity to register
     */
    public void registerEntity(Entity entity) {
        this.entities.put(entity.getId(), entity);
    }

    /**
     * Registers an entity set to the game state. DO NOT CALL THIS OUTSIDE OF THE
     * ENTITY SET CONSTRUCTOR!
     *
     * @param entitySet The entity set to register
     */
    public void registerEntitySet(EntitySet entitySet) {
        this.entitySets.put(entitySet.getId(), entitySet);
    }
}