package ata2014.main;

<<<<<<< Updated upstream
import edu.first.main.Constants;
import edu.first.module.actuators.Drivetrain;
import edu.first.module.actuators.VictorModule;
=======
import ata2014.commands.AddButtonBind;
import ata2014.commands.DisableModule;
import ata2014.commands.EnableModule;
import ata2014.commands.RemoveButtonBind;
import edu.first.commands.common.ReverseDualActionSolenoid;
import edu.first.commands.common.SetDualActionSolenoid;
import edu.first.commands.common.SetOutput;
import edu.first.identifiers.Function;
import edu.first.main.Constants;
import edu.first.module.actuators.DualActionSolenoid;
import edu.first.module.joysticks.BindingJoystick;
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
            .add(joystick1)
            .add(drivetrain).add(leftBack).add(leftFront).add(rightBack).add(rightFront)
=======
            .add(joysticks)
            .add(drivetrainSubsystem)
            .add(loader)
            .add(shooter)
            .add(drivingPID)
            .add(winchController)
            .add(compressor)
>>>>>>> Stashed changes
            .toSubsystem();

    public Robot() {
        super("2014 Robot");
    }

    public void init() {
        Logger.getLogger(this).warn("Robot is initializing");
<<<<<<< Updated upstream
=======
        File logFile = new File("Log.txt");
        TextFiles.writeAsFile(logFile, "Log File:");
        Logger.addLogToAll(new Logger.FileLog(logFile));

        // Apply function to driving algorithm
        joystick1.changeAxis(XboxController.LEFT_FROM_MIDDLE, new Function() {
            public double F(double in) {
                return drivingSensitivity * (MathUtils.pow(in, 3)) + (1 - drivingSensitivity) * in;
            }
        });

        joystick2.addDeadband(XboxController.TRIGGERS, DEADBAND);
        drivetrain.setReversedTurn(true);

>>>>>>> Stashed changes
        FULL_ROBOT.init();
        Logger.getLogger(this).warn("Robot is ready");
    }

    public void initAutonomous() {
    }

    public void initTeleoperated() {
        Logger.getLogger(this).info("Teleoperated starting...");

<<<<<<< Updated upstream
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
=======
        if (DriverstationInfo.getBatteryVoltage() < 12) {
            Logger.getLogger(DriverstationInfo.class).warn("Replace Battery <12V");
        }

        joysticks.enable();
        drivetrainSubsystem.enable();
        loader.enable();
        shooter.enable();
        compressor.enable();

        loaderPiston.set(DualActionSolenoid.Direction.LEFT);
        shifter.set(DualActionSolenoid.Direction.LEFT);

        // Driving
        if (Preferences.getInstance().getBoolean("DRIVINGPIDON", false)) {

        } else {
            joystick1.addAxisBind(drivetrain.getArcade(joystick1.getLeftDistanceFromMiddle(), joystick1.getRightX()));
        }

        joystick1.addWhenPressed(XboxController.B, new ReverseDualActionSolenoid(shifter));

        // Shoot
        joystick1.addWhenPressed(XboxController.A, new SetDualActionSolenoid(winchRelease, DualActionSolenoid.Direction.RIGHT));
        joystick1.addWhenPressed(XboxController.A, new SetDualActionSolenoid(loaderPiston, DualActionSolenoid.Direction.RIGHT));
        if (Preferences.getInstance().getBoolean("SHOOTINGNEUTRAL", false)) {
            // after shooting, default to neutral position
            joystick1.addWhenReleased(XboxController.A, new SetOutput(winchController, winchNeutralPosition));
        }

        joystick1.addWhenPressed(XboxController.X, new ReverseDualActionSolenoid(loaderPiston));
        joystick2.addWhenPressed(XboxController.X, new ReverseDualActionSolenoid(loaderPiston));

        // Move loader
        joystick2.addAxisBind(XboxController.TRIGGERS, loaderMotors);

        final BindingJoystick.ButtonBind winchOn = new BindingJoystick.WhilePressed(joystick2.getA(), new SetOutput(winchMotor, 1));
        final BindingJoystick.ButtonBind winchOff = new BindingJoystick.WhenReleased(joystick2.getA(), new SetOutput(winchMotor, 0));
        BindingJoystick.ButtonBind engageDog = new BindingJoystick.WhenPressed(joystick2.getA(),
                new SetDualActionSolenoid(winchRelease, DualActionSolenoid.Direction.LEFT));
        if (Preferences.getInstance().getBoolean("WINCHCONTROLLERON", false)) {
            winchController.enable();
            // Bring winch back
            joystick2.addWhenPressed(XboxController.A, new SetOutput(winchController, winchShootingPosition));

            // Turn on manual winch control
            joystick2.addWhenPressed(XboxController.RIGHT_BUMPER, new DisableModule(winchController));
            joystick2.addWhenPressed(XboxController.RIGHT_BUMPER, new AddButtonBind(joystick2, winchOn));
            joystick2.addWhenPressed(XboxController.RIGHT_BUMPER, new AddButtonBind(joystick2, winchOff));
            joystick2.addWhenPressed(XboxController.RIGHT_BUMPER, new AddButtonBind(joystick2, engageDog));
            // Turn off manual winch control
            joystick2.addWhenReleased(XboxController.RIGHT_BUMPER, new RemoveButtonBind(joystick2, winchOn));
            joystick2.addWhenReleased(XboxController.RIGHT_BUMPER, new RemoveButtonBind(joystick2, winchOff));
            joystick2.addWhenReleased(XboxController.RIGHT_BUMPER, new RemoveButtonBind(joystick2, engageDog));
            joystick2.addWhenReleased(XboxController.RIGHT_BUMPER, new EnableModule(winchController));
        } else {
            joystick2.addButtonBind(winchOn);
            joystick2.addButtonBind(winchOff);
            joystick2.addButtonBind(engageDog);
        }
>>>>>>> Stashed changes
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

    int x = 0;

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
