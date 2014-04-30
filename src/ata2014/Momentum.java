package ata2014;

import ata2014.commands.Shoot;
import ata2014.modules.CheesyVisionServer;
import edu.first.commands.ThreadedCommand;
import edu.first.commands.common.DisableModule;
import edu.first.commands.common.EnableModule;
import edu.first.commands.common.ReverseDualActionSolenoid;
import edu.first.commands.common.SetDualActionSolenoid;
import edu.first.commands.common.SetOutput;
import edu.first.commands.common.SetSpikeRelay;
import edu.first.commands.common.SetSwitch;
import edu.first.identifiers.Function;
import edu.first.identifiers.TransformedOutput;
import edu.first.main.Constants;
import edu.first.module.Module;
import edu.first.module.actuators.DualActionSolenoid;
import edu.first.module.actuators.SpikeRelay;
import edu.first.module.joysticks.XboxController;
import edu.first.module.subsystems.Subsystem;
import edu.first.robot.IterativeRobotAdapter;
import edu.first.util.DriverstationInfo;
import edu.first.util.File;
import edu.first.util.TextFiles;
import edu.first.util.dashboard.BooleanDashboard;
import edu.first.util.dashboard.NumberDashboard;
import edu.first.util.log.Logger;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Momentum - our 2014 robot. For information about this robot, see
 * http://www.thebluealliance.com/team/4334/2014
 *
 * @author Joel Gallant <joelgallant236@gmail.com>
 */
public class Momentum extends IterativeRobotAdapter implements Constants {

    static {
        // Let the drivers know that code is currently initializing
        Logger.displayLCDMessage("DO NOT ENABLE");
    }

    private final Subsystem TELEOP_MODULES = new Subsystem(new Module[]{
        joysticks, compressor, drive, shifters, winch, loader
    });
    private final Subsystem AUTO_MODULES = new Subsystem(new Module[]{
        CheesyVisionServer.getInstance(), drive, winch, loader
    });
    private final Subsystem ALL_MODULES = new Subsystem(new Module[]{TELEOP_MODULES, AUTO_MODULES,
        // Modules that are turned on conditionally
        winchBack, winchController,
        loaderController
    });

    private final BooleanDashboard winchLimitIndicator = new BooleanDashboard("Winch Limit", false);
    private final BooleanDashboard compressorIndicator = new BooleanDashboard("Compressor", false);
    private final NumberDashboard customSetpoint = new NumberDashboard("Custom Setpoint", LOADER_LOAD_SETPOINT.get());

    public Momentum() {
        super("Momentum");
    }

    public void init() {
        Logger.getLogger(this).info("Robot initializing...");
        TextFiles.writeAsFile(logFile, "--- Log file ---");
        Logger.addLogToAll(new Logger.FileLog(logFile));

        reloadSettings();
        if (!DRIVING_INVERSED) {
            drivetrain.setReversed(true);
        }
        drivetrain.setReversedTurn(true);

        ALL_MODULES.init();

        // initialise autonomous for performance
        Autonomous.getInstance();

        // add joystick binds
        addBinds();
        Logger.clearLCD();
        Logger.displayLCDMessage("Ready to enable");
    }

    /*
    
     BINDS
    
     Joystick 1
     .... A - Shoot
     .... B - Shift gear
     .... X - Open arms
     .... Y - 
     .... Left Stick - Driving
     .... Right Stick - Driving
     .... Left Bumper - Gear 1
     .... Right Bumper - Gear 2
     .... Triggers - 
     .... Start - Photon Cannon ON
     .... Back - Photon Cannon OFF

     Joystick 2
     .... A - Bring winch back
     .... B - Stop winch
     .... X - Open arms
     .... Y - 
     .... Left Stick - 
     .... Right Stick - 
     .... Left Bumper - Loader up
     .... Right Bumper - Loader down
     .... Triggers - Arm control
     .... Start - Go to setpoint
     .... Back - Set setpoint

     */
    private void addBinds() {
        if (CONTROL_STYLE.equalsIgnoreCase("arcade")) {
            joystick1.changeAxis(XboxController.LEFT_FROM_MIDDLE, drivingAlgorithm);
            joystick1.addAxisBind(drivetrain.getArcade(joystick1.getLeftDistanceFromMiddle(), joystick1.getRightX()));
        } else if (CONTROL_STYLE.equalsIgnoreCase("tank")) {
            joystick1.changeAxis(XboxController.LEFT_FROM_MIDDLE, drivingAlgorithm);
            joystick1.changeAxis(XboxController.RIGHT_FROM_MIDDLE, drivingAlgorithm);
            joystick1.addAxisBind(drivetrain.getTank(joystick1.getLeftDistanceFromMiddle(), joystick1.getRightDistanceFromMiddle()));
        }
        joystick1.addWhenPressed(XboxController.A, new ThreadedCommand(new Shoot(loaderPiston, DualActionSolenoid.Direction.RIGHT,
                winchRelease, DualActionSolenoid.Direction.LEFT)));
        joystick1.addWhenPressed(XboxController.A, new SetSwitch(winchLimitIndicator, false));
        joystick1.addWhenPressed(XboxController.B, new ReverseDualActionSolenoid(shifters));
        joystick1.addWhenPressed(XboxController.X, new ReverseDualActionSolenoid(loaderPiston));
        if (MAC_MODE) {
            Logger.getLogger(this).warn("Mac mode ON");
            /*
             Y - Winch Back
             Left Bumper - Winch and store loader
             Right Bumper - Load position
             Triggers - Manual Control
             */
            joystick1.addWhenPressed(XboxController.Y, new EnableModule(winchBack));
            joystick1.addWhenPressed(XboxController.Y, new SetDualActionSolenoid(winchRelease, DualActionSolenoid.Direction.RIGHT));
            joystick1.addWhenPressed(XboxController.LEFT_BUMPER, new EnableModule(winchBack));
            joystick1.addWhenPressed(XboxController.LEFT_BUMPER, new SetDualActionSolenoid(winchRelease, DualActionSolenoid.Direction.RIGHT));
            joystick1.addWhenPressed(XboxController.LEFT_BUMPER, new SetDualActionSolenoid(loaderPiston, DualActionSolenoid.Direction.LEFT));
            joystick1.addWhenPressed(XboxController.LEFT_BUMPER, new EnableModule(loaderController));
            joystick1.addWhenPressed(XboxController.LEFT_BUMPER, new SetOutput(loaderController, LOADER_STORE_SETPOINT));
            joystick1.addWhenPressed(XboxController.RIGHT_BUMPER, new EnableModule(loaderController));
            joystick1.addWhenPressed(XboxController.RIGHT_BUMPER, new SetOutput(loaderController, LOADER_LOAD_SETPOINT));
            joystick1.addWhenPressed(XboxController.RIGHT_BUMPER, new SetDualActionSolenoid(loaderPiston, DualActionSolenoid.Direction.RIGHT));
            joystick1.addWhenPressed(joystick1.getRawAxisAsButton(XboxController.TRIGGERS, 0.15), new DisableModule(loaderController));
            joystick1.addAxisBind(XboxController.TRIGGERS, new TransformedOutput(new TransformedOutput(loaderMotors,
                    new Function.ProductFunction(LOADER_MAX_SPEED)),
                    new Function.OppositeFunction()));
        } else {
            joystick1.addWhenPressed(XboxController.LEFT_BUMPER, new SetDualActionSolenoid(shifters, DualActionSolenoid.Direction.RIGHT));
            joystick1.addWhenPressed(XboxController.RIGHT_BUMPER, new SetDualActionSolenoid(shifters, DualActionSolenoid.Direction.LEFT));
        }

        joystick1.addWhenPressed(XboxController.START, new SetSpikeRelay(photonCannon, SpikeRelay.Direction.FORWARDS));
        joystick1.addWhenPressed(XboxController.BACK, new SetSpikeRelay(photonCannon, SpikeRelay.Direction.OFF));
        joystick2.addWhenPressed(XboxController.A, new EnableModule(winchBack));
        joystick2.addWhenPressed(XboxController.A, new SetDualActionSolenoid(winchRelease, DualActionSolenoid.Direction.RIGHT));
        joystick2.addWhenPressed(XboxController.B, new DisableModule(winchBack));
        joystick2.addWhenPressed(XboxController.B, new SetOutput(winchMotor, 0));
        joystick2.addWhenPressed(XboxController.X, new ReverseDualActionSolenoid(loaderPiston));
        joystick2.addWhenPressed(XboxController.BACK, new EnableModule(loaderController));
        joystick2.addWhenPressed(XboxController.BACK, new SetOutput(loaderController, customSetpoint));
        joystick2.addWhenPressed(XboxController.START, new SetOutput(customSetpoint, loaderPosition));
        joystick2.addWhenPressed(XboxController.START, new SetOutput(loaderController, loaderPosition));
        joystick2.addWhenPressed(XboxController.START, new EnableModule(loaderController));
        if (!MAC_MODE) {
            joystick2.addWhenPressed(XboxController.LEFT_BUMPER, new EnableModule(loaderController));
            joystick2.addWhenPressed(XboxController.LEFT_BUMPER, new SetOutput(loaderController, LOADER_STORE_SETPOINT));
            joystick2.addWhenPressed(XboxController.RIGHT_BUMPER, new EnableModule(loaderController));
            joystick2.addWhenPressed(XboxController.RIGHT_BUMPER, new SetOutput(loaderController, LOADER_LOAD_SETPOINT));
            joystick2.addWhenPressed(joystick2.getRawAxisAsButton(XboxController.TRIGGERS, 0.15), new DisableModule(loaderController));
            joystick2.addAxisBind(XboxController.TRIGGERS, new TransformedOutput(new TransformedOutput(loaderMotors,
                    new Function.ProductFunction(LOADER_MAX_SPEED)),
                    new Function.OppositeFunction()));
        }
        Logger.getLogger(this).info("Binds added");
    }

    public void initTeleoperated() {
        TELEOP_MODULES.enable();

        reloadSettings();

        if (WINCH_CONTROL.getPosition()) {
            winchPosition.enable();
            winchController.enable();
        }

        drivetrain.setSafetyEnabled(true);
        loaderPiston.set(DualActionSolenoid.Direction.LEFT);
        shifters.set(DualActionSolenoid.Direction.LEFT);

        Logger.getLogger(this).info("Teleop started");
    }

    public void periodicTeleoperated() {
        joystick1.doBinds();
        joystick2.doBinds();

        compressorIndicator.set(compressorController.getDirection() != SpikeRelay.Direction.OFF);
        if (winchMotor.atLimit()) {
            winchLimitIndicator.set(true);
        }
        if (DriverstationInfo.getBatteryVoltage() < 7) {
            compressorController.disable();
        } else {
            compressorController.enable();
        }
        SmartDashboard.putNumber("Loader", loaderPosition.get());
        SmartDashboard.putBoolean("Gear", shifters.get() == DualActionSolenoid.Direction.LEFT);
    }

    public void endTeleoperated() {
        TELEOP_MODULES.disable();
        Logger.getLogger(this).info("Teleop finished");
    }

    public void initAutonomous() {
        Logger.getLogger(this).info("Autonomous starting...");
        AUTO_MODULES.enable();

        reloadSettings();

        drivetrain.setSafetyEnabled(false);
        String file = TextFiles.getTextFromFile(new File(settings.getString("AutoFile", "Autonomous.txt")));
        Logger.getLogger(this).info("Script starting...");
        try {
            Autonomous.getInstance().run(file);
        } catch (RuntimeException killScript) {
            Logger.getLogger(Autonomous.getInstance()).error("Script was killed", killScript);
        }
    }

    public void endAutonomous() {
        AUTO_MODULES.disable();
    }

    public void initDisabled() {
        Logger.getLogger(this).info("Disabled starting...");
        ALL_MODULES.disable();
    }

    public void endDisabled() {
    }

    public void reloadSettings() {
        settings.reload();

        loaderController.setTolerance(LOADER_TOLERANCE.get());
        loaderController.setP(LOADER_P.get());
        loaderController.setI(LOADER_I.get());
        loaderController.setD(LOADER_D.get());
    }
}
