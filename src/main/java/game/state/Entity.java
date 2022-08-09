package game.state;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Entity {
    public record EntityConnection(Entity to, Entity associatedEntity) {

    }

    private final String id;
    private final Set<String> attributes = new HashSet<>();
    private Entity location;
    private boolean closed = false;
    private final EntitySet contents = new EntitySet();
    private final Map<String, EntityConnection> connections = new HashMap<>();

    public Entity(String id, String... attributes) {
        this.id = id;
        this.attributes.addAll(Arrays.asList(attributes));
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

    public void connectBidirectional(String dirIdFromThisToOther, Entity associatedEntity, String dirIdFromOtherToThis, Entity to) {
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

    @Override
    public String toString() {
        return this.id;
    }
}
