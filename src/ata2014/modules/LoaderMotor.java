package ata2014.modules;

import edu.first.identifiers.Function;
import edu.first.module.actuators.TalonModule;

/**
 *
 * @author Joel Gallant <joelgallant236@gmail.com>
 */
public class LoaderMotor extends TalonModule {

    private final Function function;

    public LoaderMotor(Function function, int channel) {
        super(channel);
        this.function = function;
    }

    public LoaderMotor(int channel) {
        super(channel);
        this.function = new Function.DefaultFunction();
    }

    public void set(double value) {
        super.set(function.F(value));
    }

    public void setRate(double rate) {
        super.setRate(function.F(rate));
    }

    public void setRawSpeed(int speed) {
        super.setRawSpeed((int) function.F(speed));
    }

    public void setSpeed(double speed) {
        super.setSpeed(function.F(speed));
    }
}
