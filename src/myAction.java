import java.util.List;

// Enum to define the type of action
enum ActionType {
    ADD, REMOVE, MODIFY, GROUP, UNGROUP, COPY, PASTE
}

// Action class to represent an operation
class Action {
    List<baseObj> originalState;
    List<baseObj> newState;
    ActionType type;

    Action(List<baseObj> originalState, List<baseObj> newState, ActionType type) {
        this.originalState = originalState;
        this.newState = newState;
        this.type = type;
    }
}