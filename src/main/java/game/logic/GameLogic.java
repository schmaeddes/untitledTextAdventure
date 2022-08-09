package game.logic;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import game.logic.actionsystem.Action;
import game.logic.actionsystem.actions.GoDirection;
import game.logic.actionsystem.actions.Open;
import game.logic.actionsystem.actions.TakeFrom;
import game.state.CircularLocationException;
import game.state.Entity;
import game.state.EntitySet;
import game.state.GameState;

public class GameLogic implements Closeable {
    private final Parser parser;
    private GameState state;
    private Entity player;
    private boolean discontinue = false;

    public GameLogic(Parser parser) {
        this.parser = parser;
        this.state = new GameState();
    }

    public void loadGameState(String stateDescJsonFilePath) throws CircularLocationException {
        ////////////////////////////////////////////////////////////////////////
        // TODO setup code, load from json or so
        ////////////////////////////////////////////////////////////////////////

        // final String NORTH = "north";
        // final String SOUTH = "south";
        final String EAST = "east";
        final String WEST = "west";
        final String INSIDE = "inside";
        final String OUTSIDE = "outside";

        Entity forestPath01 = this.state.createEntity("forest_path_01");
        Entity clearing = this.state.createEntity("clearing");
        Entity houseOutside = this.state.createEntity("house_outside");
        Entity houseInside = this.state.createEntity("house_inside");
        Entity houseMainDoor = this.state.createEntity("house_main_door");
        houseMainDoor.setClosed(true);

        forestPath01.connectBidirectional(EAST, null, WEST, clearing);
        forestPath01.connectBidirectional(WEST, null, EAST, houseOutside);
        houseOutside.connectBidirectional(INSIDE, houseMainDoor, OUTSIDE, houseInside);

        this.player = this.state.createEntity("player");
        Entity apple01 = this.state.createEntity("apple_01", "green");
        Entity apple02 = this.state.createEntity("apple_02", "red");

        this.player.setLocation(clearing);
        apple01.setLocation(forestPath01);
        apple02.setLocation(forestPath01);

        ////////////////////////////////////////////////////////////////////////
        // TODO game specific action parsers
        ////////////////////////////////////////////////////////////////////////

        this.parser.pushActionParser(userInput -> userInput.get(0).equals("go")
                ? new GoDirection(userInput.size() < 2 ? null : userInput.get(1))
                : null);
        this.parser.pushActionParser(userInput -> {
            if (userInput.get(0).equals("take")) {
                int fromIdx = userInput.indexOf("from");
                List<EntityDescription> whatToTake = this
                        .parseDescriptionList(userInput.subList(1, fromIdx == -1 ? userInput.size() : fromIdx));
                List<EntityDescription> whereToTakeFrom = fromIdx == -1 ? null
                        : this.parseDescriptionList(userInput.subList(fromIdx + 1, userInput.size()));
                return new TakeFrom(whatToTake, whereToTakeFrom);
            } else {
                return null;
            }
        });
        this.parser.pushActionParser(userInput -> {
            if (userInput.get(0).equals("open")) {
                return new Open(this.parseDescriptionList(userInput.subList(1, userInput.size())));
            } else {
                return null;
            }
        });
    }

    public Entity getPlayer() {
        return this.player;
    }

    public EntitySet searchForEntity(EntityDescription description) {
        return this.state.searchForEntity(description);
    }

    public EntitySet searchForEntity(EntityDescription description, Predicate<Entity> acceptFunction) {
        return this.state.searchForEntity(description).getFiltered(acceptFunction);
    }

    public EntitySet searchForNearbyEntity(EntityDescription description) {
        return this.searchForEntity(description, e -> {
            if (this.player == null) {
                return false;
            } else if (this.player.contains(e, false)) {
                return true;
            } else if (this.player.getLocation() != null && this.player.getLocation().contains(e, false)) {
                return true;
            } else {
                for (Entity.EntityConnection c : this.player.getLocation().getConnections().stream()
                        .map(this.player.getLocation()::getConnection).toList()) {
                    if (c.associatedEntity() == e) {
                        return true;
                    }
                }
            }
            return false;
        });
    }

    public void mainLoop() {
        while (!this.discontinue) {
            Action action = this.parser.readAction();
            if (action != null) {
                action.execute(this);
            }
        }
    }

    private List<EntityDescription> parseDescriptionList(List<String> words) {
        List<EntityDescription> descriptions = new LinkedList<>();
        List<String> desc = new LinkedList<>();
        for (String word : words) {
            if (word.equals("and")) {
                descriptions.add(new EntityDescription(desc));
                desc = new LinkedList<>();
            } else {
                desc.add(word);
            }
        }
        if (!desc.isEmpty()) {
            descriptions.add(new EntityDescription(desc));
        }
        return descriptions;
    }

    public void printToUser(String messageId, Object... args) {
        System.out.print(messageId);
        for (int i = 0; i < args.length; ++i) {
            System.out.print(i == 0 ? " " : ", ");
            System.out.print(args[i]);
        }
        System.out.println();
    }

    public boolean canPlayerOpen(Entity entity) {
        // TODO
        return true;
    }

    public boolean canPlayerTake(Entity entity) {
        // TODO
        return true;
    }

    @Override
    public void close() throws IOException {
        this.parser.close();
    }
}
