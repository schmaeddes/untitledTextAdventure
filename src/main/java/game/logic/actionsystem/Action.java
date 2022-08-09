package game.logic.actionsystem;

import game.logic.GameLogic;

public abstract class Action {
    public enum Type {
        TAKE, DROP,
        COMBINE_WITH, USE,
        GIVE_TO, TAKE_FROM,
        PUT_ON,
        PUSH, PULL, ROLL, ROLL_TO,
        TRACE_RAY_TO,
        KILL, KILL_WITH,
        SEARCH, SEARCH_FOR,
        TALK_TO, TELL_TO, ANNOY,
        LOOK_AT, EXAMINE, READ, WRITE_ON_WITH,
        HIT, HIT_WITH,
        GO_TO,
    }

    public abstract void execute(GameLogic logic);
}
