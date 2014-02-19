package ata2014.subsystems;

import ata2014.main.Ports;
import ata2014.main.Preferences;
import ata2014.modules.LoaderMotor;
import edu.first.identifiers.Function;
import edu.first.module.actuators.DualActionSolenoidModule;
import edu.first.module.actuators.SpeedController;
import edu.first.module.actuators.SpeedControllerGroup;
import edu.first.module.actuators.TalonModule;
import edu.first.module.sensors.DigitalInput;
import edu.first.module.subsystems.Subsystem;
import edu.first.module.subsystems.SubsystemBuilder;

/**
 *
 * @author ata
 */
public interface Loader extends Ports {

    DigitalInput leftLoaderSwitch = new DigitalInput(LEFT_LOADER_SWITCH),
            rightLoaderSwitch = new DigitalInput(RIGHT_LOADER_SWITCH);
    TalonModule leftLoaderMotor = new LoaderMotor(new Function() {
        double limit = Preferences.getInstance().getDouble("LeftArmLimit", 0.5);

        public double F(double in) {
            return in * limit;
        }
    }, LEFT_LOADER_MOTOR),
            rightLoaderMotor = new LoaderMotor(new Function.CompoundFunction(new Function[]{new Function.OppositeFunction(), new Function() {
                double limit = Preferences.getInstance().getDouble("RightArmLimit", 0.5);

                public double F(double in) {
                    return in * limit;
                }
            }}), RIGHT_LOADER_MOTOR);
    SpeedControllerGroup loaderMotors = new SpeedControllerGroup(new SpeedController[]{leftLoaderMotor, rightLoaderMotor});
    DualActionSolenoidModule loaderPiston = new DualActionSolenoidModule(LOADER_PISTON_IN, LOADER_PISTON_OUT);

    Subsystem loader = new SubsystemBuilder()
            .add(leftLoaderSwitch).add(rightLoaderSwitch)
            .add(leftLoaderMotor).add(rightLoaderMotor)
            .add(loaderPiston)
            .toSubsystem();
}
