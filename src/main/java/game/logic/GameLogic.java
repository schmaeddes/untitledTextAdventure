package game.logic;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import game.logic.actionsystem.Action;
import game.logic.actionsystem.ActionExecutor;
import game.logic.actionsystem.ActionSignature;
import game.state.CircularLocationException;
import game.state.Entity;
import game.state.EntitySet;
import game.state.GameState;

public class GameLogic implements Closeable {
    private final Parser parser;
    private GameState gameState;
    private Entity player;
    private boolean discontinue = false;
    private Map<String, List<Action>> playerActions = new HashMap<>();

    public GameLogic(Parser parser) {
        this.parser = parser;
    }

    public void loadGameState(String gameDescriptionJsonPath, String savegameJsonPath) throws CircularLocationException {
        this.gameState = new GameState(savegameJsonPath);

        /*
         * TODO: load from game specific JSON
         */
        EntitySet collectibles = this.gameState.getEntitySetById("collectibles");
        EntitySet genericDirections = this.gameState.getEntitySetById("genericDirections");
        EntitySet houseDirections = this.gameState.getEntitySetById("houseDirections");
        this.player = this.gameState.getEntityById("player");
        Entity houseInside = this.gameState.getEntityById("houseInside");
        Entity houseOutside = this.gameState.getEntityById("houseOutside");
        Entity houseMainDoor = this.gameState.getEntityById("houseMainDoor");

        ActionSignature takeSignature = new ActionSignature();
        takeSignature.pushEntry(collectibles, 1);
        this.createAction("take", takeSignature, (logic, actor, args) -> {
            Entity collectible = args[0];
            if (actor.getLocation() == collectible.getLocation()) {
                try {
                    collectible.setLocation(actor);
                    logic.printRaw("%s: Du nimmst %s, du Schuft.\n", actor, collectible);
                    return true;
                } catch (CircularLocationException ex) {
                    // Should not happpen
                    ex.printStackTrace();
                }
            }
            return false;
        });

        ActionSignature goActionSignature = new ActionSignature();
        goActionSignature.pushEntry(genericDirections, 1);
        this.createAction("go", goActionSignature, (logic, actor, args) -> {
            Entity direction = args[0];
            Entity newLocation = actor.getLocation().getConnectedEntity(direction);
            if (newLocation != null) {
                try {
                    logic.getPlayer().setLocation(newLocation);
                    logic.printRaw("%s: Du gehst Richtung %s und landest hier: %s, du Lutscher!\n", actor, direction,
                            newLocation);
                } catch (CircularLocationException ex) {
                    ex.printStackTrace();
                }
            } else {
                logic.printRaw("%s: Hier geht es nicht nach %s, du Nichtsnutz.\n", actor, direction);
            }
            return true;
        });

        ActionSignature goHouseActionSignature = new ActionSignature();
        goHouseActionSignature.pushEntry(houseDirections, 1);
        this.createAction("go", goHouseActionSignature, (logic, actor, args) -> {
            Entity direction = args[0];
            Entity newLocation = actor.getLocation().getConnectedEntity(direction);
            if (newLocation != null && actor.getLocation() == houseInside || actor.getLocation() == houseOutside) {
                if (houseMainDoor.getBoolAttribute("open")) {
                    try {
                        actor.setLocation(newLocation);
                    } catch (CircularLocationException ex) {
                        ex.printStackTrace();
                    }
                    logic.printRaw("%s: Du gehst durch %s, du Eumel.\n", actor, houseMainDoor);
                } else {
                    logic.printRaw("%s: %s ist zu, du Dödel.\n", actor, houseMainDoor);
                }
                return true;
            }
            return false;
        });

        ActionSignature openCloseSignature = new ActionSignature();
        openCloseSignature.pushEntry(houseMainDoor);

        this.createAction("open", openCloseSignature, (logic, actor, args) -> {
            Entity door = args[0];
            if (actor.getLocation() == houseInside || actor.getLocation() == houseOutside) {
                if (door.getBoolAttribute("open")) {
                    logic.printRaw("%s: Die Tür ist schon offen, du Hammel.\n", actor);
                } else {
                    door.setAttribute("open", true);
                    logic.printRaw("%s: Du öffnest die Tür, du Dummbatz.\n", actor);
                }
                return true;
            }
            return false;
        });

        this.createAction("close", openCloseSignature, (logic, actor, args) -> {
            Entity door = args[0];
            if (actor.getLocation() == houseInside || actor.getLocation() == houseOutside) {
                if (!door.getBoolAttribute("open")) {
                    logic.printRaw("%s: Die Tür ist schon geschlossen, du Mummenschanz.\n", actor);
                } else {
                    door.setAttribute("open", false);
                    logic.printRaw("%s: Du schließt die Tür, du Angsthase.\n", actor);
                }
                return true;
            }
            return false;
        });
    }

    public GameState getState() {
        return this.gameState;
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

    public void createAction(String id, ActionSignature signature, ActionExecutor executor) {
        List<Action> l = this.playerActions.get(id);
        if (l == null) {
            this.playerActions.put(id, l = new LinkedList<>());
        }
        l.add(new Action(id, signature, executor));
    }

    /**
     * Searches for the first player action that does not use any entity and
     * executes it if one is found.
     *
     * @param id    The action id
     * @param actor The actor who should execute the action
     * @return <code>true</code>, if an action was found and executed
     */
    public boolean tryExecuteAction(String id, Entity actor) {
        return this.tryExecuteAction(id, actor, null);
    }

    /**
     * Searches for the first player action that can operate on the given set of
     * entities and executes it if one is found.
     *
     * @param id       The action id
     * @param actor    The actor who should execute the action
     * @param entities The entities on which the action shoud operate
     * @return <code>true</code>, if an action was found and executed
     */
    public boolean tryExecuteAction(String id, Entity actor, EntitySet entities) {
        List<Action> l = this.playerActions.get(id);
        if (l != null) {
            for (Action a : l) {
                if (a.tryExecute(actor, entities, this)) {
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
