package ata2014.modules;

import api.gordian.Arguments;
import api.gordian.storage.InternalNotFoundException;
import edu.first.module.sensors.AnalogInput;
import org.gordian.value.GordianList;
import org.gordian.value.GordianNumber;

/**
 *
 * @author Joel Gallant <joelgallant236@gmail.com>
 */
public class WinchPot extends AnalogInput {

    private final double[] range;

    public WinchPot(double[] range, int channel) {
        super(channel);
        this.range = range;
    }

    public WinchPot(GordianList range, int channel) {
        super(channel);
        double[] r;
        try {
            r = new double[((GordianNumber) range.methods().get("size").run(new Arguments())).getInt()];
        } catch (InternalNotFoundException ex) {
            throw new RuntimeException();
        }
        for (int x = 0; x < r.length; x++) {
            r[x] = ((GordianNumber) range.get(x)).getValue();
        }
        this.range = r;
    }

    public double get() {
        return 100 * (getFromRange(super.get()) / range.length);
    }

    public double getAverage() {
        return 100 * (getFromRange(super.getAverage()) / range.length);
    }

    public double getVoltage() {
        return super.getAverage();
    }

    private double getFromRange(double val) {
        for (int x = 0; x < range.length; x++) {
            if (val == range[x]) {
                return x;
            }
            if (range[0] < range[range.length - 1]) {
                if (val > range[x] && x + 1 < range.length && val < range[x + 1]) {
                    return x + .5;
                }
            } else {
                if (val < range[x] && x + 1 < range.length && val > range[x + 1]) {
                    return x + .5;
                }
            }
        }
        if (range[0] < range[range.length - 1]) {
            if (val < range[0]) {
                return 0;
            } else {
                return range.length - 1;
            }
        } else {
            if (val > range[0]) {
                return 0;
            } else {
                return range.length - 1;
            }
        }
    }
}
