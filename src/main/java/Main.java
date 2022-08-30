import java.io.IOException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;

import game.logic.GameLogic;
import game.logic.Parser;
import game.state.CircularLocationException;
import game.state.Entity;
import startup.Environment;
import startup.LoadStuff;

public class Main {
    public static void main(String[] args) throws IOException {

        LoadStuff loadStuff = new LoadStuff();
        loadStuff.load(Environment.instance);

        ScriptEngine jse = GraalJSScriptEngine.create(
                Engine.newBuilder().option("engine.WarnInterpreterOnly", "false").build(),
                Context.newBuilder("js").allowHostAccess(HostAccess.ALL).allowHostClassLookup(s -> true));
        try {
            Entity t = new Entity("test");
            jse.put("test", t);
            jse.eval("console.log(test.toString());");
        } catch (ScriptException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.exit(0);
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
