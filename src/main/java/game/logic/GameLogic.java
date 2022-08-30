package game.logic;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import game.logic.actionsystem.PlayerAction;
import game.state.CircularLocationException;
import game.state.Entity;
import game.state.EntitySet;
import game.state.GameState;

public class GameLogic implements Closeable {
    private final Parser parser;
    private GameState state;
    private Entity player;
    private boolean discontinue = false;
    private Map<String, List<PlayerAction>> playerActions = new HashMap<>();

    public GameLogic(Parser parser) {
        this.parser = parser;
        this.state = new GameState();
    }

    public void loadGameState(String stateDescJsonFilePath) throws CircularLocationException {
        ////////////////////////////////////////////////////////////////////////
        // TODO setup code, load from json or so
        ////////////////////////////////////////////////////////////////////////

        Entity east = this.state.createEntity("east");
        Entity west = this.state.createEntity("west");
        Entity inside = this.state.createEntity("inside");
        Entity outside = this.state.createEntity("outside");
        EntitySet genericDirections = EntitySet.createPersistent("genericDirections");
        genericDirections.add(east, west);
        EntitySet houseDirections = EntitySet.createPersistent("houseDirections");
        houseDirections.add(inside, outside);

        Entity forestPath01 = this.state.createEntity("forest_path_01");
        Entity clearing = this.state.createEntity("clearing");
        Entity houseOutside = this.state.createEntity("house_outside");
        Entity houseInside = this.state.createEntity("house_inside");
        Entity houseMainDoor = this.state.createEntity("house_main_door");

        EntitySet locations = EntitySet.createPersistent("locations");
        locations.add(forestPath01, clearing, houseInside, houseOutside);

        forestPath01.connectBidirectional(east, west, clearing);
        forestPath01.connectBidirectional(west, east, houseOutside);
        houseOutside.connectBidirectional(inside, outside, houseInside);

        this.player = clearing.createContainedEntity(this, "player");
        Entity apple01 = forestPath01.createContainedEntity(this, "apple_01");
        Entity apple02 = forestPath01.createContainedEntity(this, "apple_02");

        EntitySet collectibles = EntitySet.createPersistent("collectibles");
        collectibles.add(apple01, apple02);

        collectibles.pushPlayerAction("take", (logic, args) -> {
            Entity collectible = args[0];
            if (logic.getPlayer().getLocation() == collectible.getLocation()) {
                try {
                    collectible.setLocation(logic.getPlayer());
                    logic.printRaw("Du nimmst %s, du Schuft.\n", collectible);
                } catch (CircularLocationException ex) {
                    // Should not happpen
                    ex.printStackTrace();
                }
            }
            return true;
        });

        // this.player becomes first argument by calling pushPlayerAction on it
        PlayerAction goAction = this.player.pushPlayerAction("go", (logic, args) -> {
            Entity character = args[0];
            Entity direction = args[1];
            Entity newLocation = character.getLocation().getConnectedEntity(direction);
            if (newLocation != null) {
                try {
                    logic.getPlayer().setLocation(newLocation);
                    logic.printRaw("Du gehst Richtung %s und landest hier: %s, du Lutscher!\n", direction, newLocation);
                } catch (CircularLocationException ex) {
                    ex.printStackTrace();
                }
            } else {
                logic.printRaw("Hier geht es nicht nach %s, du Nichtsnutz.\n", direction);
            }
            return true;
        });
        // second argument must be exactly 1 of the entites contained in genericDirections
        goAction.pushVaryingNeededEntites(genericDirections, 1);

        // again first argument becomes this.player by calling pushPlayerAction on it
        PlayerAction goHouseAction = this.player.pushPlayerAction("go", (logic, args) -> {
            Entity character = args[0];
            Entity direction = args[1];
            Entity newLocation = character.getLocation().getConnectedEntity(direction);
            if (newLocation != null && character.getLocation() == houseInside
                    || character.getLocation() == houseOutside) {
                if (houseMainDoor.getBoolAttribute("open")) {
                    try {
                        character.setLocation(newLocation);
                    } catch (CircularLocationException ex) {
                        ex.printStackTrace();
                    }
                    logic.printRaw("Du gehst durch die Tür, du Eumel.\n");
                } else {
                    logic.printRaw("Die Tür ist zu, du Dödel.\n");
                }
                return true;
            }
            return false;
        });
        // second argument must be exactly 1 of the entites contained in houseDirections
        goHouseAction.pushVaryingNeededEntites(houseDirections, 1);

        houseMainDoor.pushPlayerAction("open", (logic, args) -> {
            if (logic.getPlayer().getLocation() == houseInside || logic.getPlayer().getLocation() == houseOutside) {
                if (houseMainDoor.getBoolAttribute("open")) {
                    logic.printRaw("Die Tür ist schon offen, du Hammel.\n");
                } else {
                    houseMainDoor.setAttribute("open", true);
                    logic.printRaw("Du öffnest die Tür, du Dummbatz.\n");
                }
                return true;
            }
            return false;
        });

        houseMainDoor.pushPlayerAction("close", (logic, args) -> {
            if (logic.getPlayer().getLocation() == houseInside || logic.getPlayer().getLocation() == houseOutside) {
                if (!houseMainDoor.getBoolAttribute("open")) {
                    logic.printRaw("Die Tür ist schon geschlossen, du Mummenschanz.\n");
                } else {
                    houseMainDoor.setAttribute("open", false);
                    logic.printRaw("Du schließt die Tür, du Angsthase.\n");
                }
                return true;
            }
            return false;
        });
    }

    public GameState getState() {
        return this.state;
    }

    public Entity getPlayer() {
        return this.player;
    }

    public void mainLoop() {
        while (!this.discontinue) {
            this.parser.executeUserInput(this);
        }
    }

    public void printRaw(String rawMessage, Object... args) {
        System.out.printf(rawMessage, args);
    }

    public void registerPlayerAction(PlayerAction action) {
        List<PlayerAction> l = this.playerActions.get(action.getId());
        if (l == null) {
            this.playerActions.put(action.getId(), l = new LinkedList<>());
        }
        l.add(action);
    }

    /**
     * Searches for the first player action that does not use any entity and
     * executes it if one is found.
     *
     * @param id The action id
     * @return <code>true</code>, if an action was found and executed
     */
    public boolean tryExecutePlayerAction(String id) {
        return this.tryExecutePlayerAction(id, null);
    }

    /**
     * Searches for the first player action that can operate on the given set of
     * entities and executes it if one is found.
     *
     * @param id       The action id
     * @param entities The entities on which the action shoud operate
     * @return <code>true</code>, if an action was found and executed
     */
    public boolean tryExecutePlayerAction(String id, EntitySet entities) {
        List<PlayerAction> l = this.playerActions.get(id);
        if (l != null) {
            for (PlayerAction a : l) {
                if (a.tryExecute(null, entities, this)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void close() throws IOException {
        this.parser.close();
    }
}
