package ata2014.modules;

import edu.first.module.actuators.TalonModule;
import edu.first.module.sensors.DigitalInput;
import edu.first.util.MathUtils;

/**
 *
 * @author Joel Gallant <joelgallant236@gmail.com>
 */
public class WinchMotor extends TalonModule {

    private final DigitalInput limit;

    public WinchMotor(DigitalInput limit, int channel) {
        super(channel);
        this.limit = limit;
    }

    public void set(double value) {
        if (limit.getPosition()) {
            super.set(adjust(value));
        } else {
            super.set(0);
        }
    }

    public void setRate(double rate) {
        if (limit.getPosition()) {
            super.setRate(adjust(rate));
        } else {
            super.setRate(0);
        }
    }

    public void setRawSpeed(int speed) {
        if (limit.getPosition()) {
            super.setRawSpeed((int) adjust(speed));
        } else {
            super.setRawSpeed(0);
        }
    }

    public void setSpeed(double speed) {
        if (limit.getPosition()) {
            super.setSpeed(adjust(speed));
        } else {
            super.setSpeed(0);
        }
    }

    private double adjust(double speed) {
        return -MathUtils.abs(speed);
    }
}
