import java.io.IOException;

import game.logic.GameLogic;
import game.logic.Parser;
import game.state.CircularLocationException;
import startup.Environment;
import startup.LoadStuff;

public class Main {
    public static void main(String[] args) throws IOException {

        LoadStuff loadStuff = new LoadStuff();
        loadStuff.load(Environment.instance);

        Parser parser = new Parser();
        try (GameLogic logic = new GameLogic(parser)) {
            logic.loadGameState("games/damnCoolTextAdventureFTW.json");
            logic.mainLoop();
        } catch (CircularLocationException ex) {
            System.err.println("You messed up you game state: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
