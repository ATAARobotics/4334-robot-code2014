package edu.ata.driving.modules;

import edu.wpi.first.wpilibj.SafePWM;
import edu.wpi.first.wpilibj.SpeedController;

/**
 * Module object representing a PWM controlled speed controller. Only applies to
 * {@link SpeedController} objects.
 *
 * <p> Is not 'enablable' or 'disablable'. Will always work.
 *
 * @author Joel Gallant
 */
public class SpeedControllerModule extends Module implements Module.Disableable {

    private final SpeedController speedController;
    private boolean enabled = false;

    /**
     * Creates the speed controller with a name and its associated
     * {@link SpeedController} object.
     *
     * @param name name of the module displayed to user
     * @param speedController speed controller object to use
     */
    public SpeedControllerModule(String name, SpeedController speedController) {
        super(name);
        this.speedController = speedController;
    }

    public final void enable() {
        enabled = true;
    }
    
    public final void disable() {
        enabled = false;
    }

    public final boolean isEnabled() {
        return enabled;
    }

    /**
     * Returns the speed controller object being used underneath this class.
     *
     * <p> <i> Will throw an {@link IllegalStateException} if the module has not
     * been enabled ({@link Module#enable()}) </i>
     *
     * @return speed controller being used
     */
    public final SpeedController getSpeedController() {
        if (isEnabled()) {
            return speedController;
        } else {
            throw new IllegalStateException("Speed Controller being accessed is not enabled - " + getName());
        }
    }

    /**
     * Sets the speed of the specific speed controller.
     *
     * <p> <i> Will throw an {@link IllegalStateException} if the module has not
     * been enabled ({@link Module#enable()}) </i>
     *
     * @see SpeedController#set(double)
     * @param speed speed from -1 to +1
     */
    public final void setSpeed(double speed) {
        getSpeedController().set(speed);
    }

    /**
     * Stops the motor. (Sets to 0)
     *
     * <p> <i> Will throw an {@link IllegalStateException} if the module has not
     * been enabled ({@link Module#enable()}) </i>
     */
    public final void stop() {
        setSpeed(0);
    }

    /**
     * Returns the current speed being set on the speed controller.
     *
     * <p> <i> Will throw an {@link IllegalStateException} if the module has not
     * been enabled ({@link Module#enable()}) </i>
     *
     * @see SpeedController#get()
     * @return speed of the motor
     */
    public final double getSpeed() {
        return getSpeedController().get();
    }

    /**
     * If the speed controller is a {@link SafePWM}, it will feed the motor
     * safety object. Otherwise does nothing.
     */
    public final void feed() {
        if (getSpeedController() instanceof SafePWM) {
            ((SafePWM) getSpeedController()).Feed();
        }
    }
}