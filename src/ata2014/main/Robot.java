package ata2014.main;

import edu.first.main.Constants;
import edu.first.module.actuators.Drivetrain;
import edu.first.module.actuators.VictorModule;
import edu.first.module.joysticks.XboxController;
import edu.first.module.subsystems.Subsystem;
import edu.first.module.subsystems.SubsystemBuilder;
import edu.first.robot.IterativeRobotAdapter;
import edu.first.util.log.Logger;
import edu.first.module.joysticks.BindingJoystick;
import edu.first.util.MathUtils;
/**
 * Team 4334's main robot code starting point. Everything that happens is
 * derived from this class.
 *
 * @author Joel Gallant <joelgallant236@gmail.com>
 */
public final class Robot extends IterativeRobotAdapter implements Constants {

    private final XboxController joystick1 = new XboxController(1);
    private final VictorModule leftBack = new VictorModule(LEFT_BACK),
            leftFront = new VictorModule(LEFT_FRONT),
            rightBack = new VictorModule(RIGHT_BACK),
            rightFront = new VictorModule(RIGHT_FRONT);
    private final Drivetrain drivetrain = new Drivetrain(leftFront, leftBack, rightFront, rightBack);
    private final Subsystem FULL_ROBOT = new SubsystemBuilder()
            .add(joystick1)
            .add(drivetrain).add(leftBack).add(leftFront).add(rightBack).add(rightFront)
            .toSubsystem();

    public Robot() {
        super("2014 Robot");
    }

    public void init() {
        Logger.getLogger(this).warn("Robot is initializing");
        FULL_ROBOT.init();
        Logger.getLogger(this).warn("Robot is ready");
    }

    public void initAutonomous() {
    }

    public void initTeleoperated() {
        Logger.getLogger(this).info("Teleoperated starting...");

        FULL_ROBOT.enable();

        joystick1.addAxisBind(new BindingJoystick.DualAxisBind(joystick1.getLeftY(), joystick1.getRightX()) {

            public void doBind(double x, double axis2) {
                if (MathUtils.abs(x) < 0.1) {
                    x = 0;
                }
                if (MathUtils.abs(axis2) < 0.1) {
                    axis2 = 0;
                }
                
                boolean neg = x < 0;
                x = MathUtils.abs(x);
                x = (-2.398 * MathUtils.pow(x, 3) + 3.597 * MathUtils.pow(x, 2) - 0.199 * x - 4.18560022E-05);
                x = neg ? -x : x;
                
                drivetrain.arcadeDrive(x, axis2);
            }
        });
    }

    public void initDisabled() {
        Logger.getLogger(this).info("Disabled starting...");
        FULL_ROBOT.disable();
    }

    public void initTest() {
        Logger.getLogger(this).info("Test starting...");
    }

    public void periodicAutonomous() {
    }

    public void periodicTeleoperated() {
        joystick1.doBinds();
    }

    public void periodicDisabled() {
    }

    public void periodicTest() {
    }

    public void endAutonomous() {
        Logger.getLogger(this).warn("Autonomous finished");
    }

    public void endTeleoperated() {
        joystick1.clearBinds();

        Logger.getLogger(this).warn("Teleoperated finished");
    }
}
