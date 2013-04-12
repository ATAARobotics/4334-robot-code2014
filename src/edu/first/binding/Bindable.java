package edu.first.binding;

import edu.first.bindings.AxisBind;
import edu.first.command.Command;
import edu.first.identifiers.Function;
import edu.wpi.first.wpilibj.networktables2.util.List;

/**
 * Object representing a joystick (although not explictly) that can bind axises
 * and buttons to commands and outputs respectively. To check and perform the
 * binds given through the various methods, use {@link Bindable#doBinds()}.
 *
 * @author Joel Gallant
 */
public abstract class Bindable {

    protected final List binds = new List();

    public final void addBind(BindAction action) {
        binds.add(action);
    }

    public final void addWhenPressed(Button button, Command command) {
        addBind(new WhenPressed(button, command));
    }

    public final void addWhilePressed(Button button, Command command) {
        addBind(new WhilePressed(button, command));
    }

    public final void addWhenReleased(Button button, Command command) {
        addBind(new WhenReleased(button, command));
    }

    public final void addWhileReleased(Button button, Command command) {
        addBind(new WhileReleased(button, command));
    }

    public final void addAxis(Axis axis, AxisBind bind) {
        addBind(new SetAxis(bind, axis));
    }

    public final void addAxis(Axis axis, AxisBind bind, Function function) {
        addBind(new SetAxis(bind, axis, function));
    }

    public final void removeBind(BindAction action) {
        binds.remove(action);
    }

    public final void removeAllBinds() {
        binds.clear();
    }

    public final void doBinds() {
        for (int x = 0; x < binds.size(); x++) {
            if (binds.get(x) instanceof BindAction) {
                ((BindAction) binds.get(x)).doBind();
            }
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        for(int x = 0; x < binds.size(); x++) {
            buffer.append(binds.get(x).toString()).append("\n");
        }
        return buffer.toString();
    }

    private static class BindingObject {

        private final String id;

        public BindingObject(String id) {
            this.id = id;
        }

        public String toString() {
            return id;
        }
    }

    public static abstract class Button extends BindingObject {

        public Button(String id) {
            super(id);
        }

        public abstract boolean isPressed();
    }

    public static abstract class Axis extends BindingObject {

        public Axis(String id) {
            super(id);
        }

        public abstract double getValue();
    }

    public static abstract class BindAction {

        private BindAction() {
        }

        public abstract void doBind();
    }

    public static abstract class BindsActionWithButton extends BindAction {

        public final Button button;

        private BindsActionWithButton(Button button) {
            this.button = button;
        }
    }

    public static abstract class BindsActionWithAxis extends BindAction {

        public final Axis axis;

        public BindsActionWithAxis(Axis axis) {
            this.axis = axis;
        }
    }

    public static final class WhenPressed extends BindsActionWithButton {

        private final Command bind;
        private boolean wasPressed = false;

        public WhenPressed(Button button, Command bind) {
            super(button);
            this.bind = bind;
        }

        public void doBind() {
            if (button.isPressed() && !wasPressed) {
                bind.run();
                wasPressed = true;
            } else if (!button.isPressed()) {
                wasPressed = false;
            }
        }
    }

    public static final class WhilePressed extends BindsActionWithButton {

        private final Command bind;

        public WhilePressed(Button button, Command bind) {
            super(button);
            this.bind = bind;
        }

        public void doBind() {
            if (button.isPressed()) {
                bind.run();
            }
        }
    }

    public static final class WhenReleased extends BindsActionWithButton {

        private final Command bind;
        private boolean wasPressed = false;

        public WhenReleased(Button button, Command bind) {
            super(button);
            this.bind = bind;
        }

        public void doBind() {
            if (!button.isPressed() && wasPressed) {
                bind.run();
                wasPressed = false;
            } else if (button.isPressed()) {
                wasPressed = true;
            }
        }
    }

    public static final class WhileReleased extends BindsActionWithButton {

        private final Command bind;

        public WhileReleased(Button button, Command bind) {
            super(button);
            this.bind = bind;
        }

        public void doBind() {
            if (!button.isPressed()) {
                bind.run();
            }
        }
    }

    public static final class SetAxis extends BindsActionWithAxis {

        private Function function = new Function.DefaultFunction();
        private final AxisBind axisBind;

        public SetAxis(AxisBind axisBind, Axis axis) {
            super(axis);
            this.axisBind = axisBind;
        }

        public SetAxis(AxisBind axisBind, Axis axis, Function function) {
            super(axis);
            this.axisBind = axisBind;
            this.function = function;
        }

        public void doBind() {
            axisBind.set(function.apply(axis.getValue()));
        }
    }
}