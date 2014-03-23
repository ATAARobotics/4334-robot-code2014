package ata2014.commands;

import edu.first.commands.CommandGroup;
import edu.first.commands.common.SetDualActionSolenoid;
import edu.first.commands.common.WaitCommand;
import edu.first.module.actuators.DualActionSolenoid;

/**
 *
 * @author Joel Gallant <joelgallant236@gmail.com>
 */
public class Shoot extends CommandGroup {

    public Shoot(DualActionSolenoid arms, DualActionSolenoid.Direction armDir,
            DualActionSolenoid shooter, DualActionSolenoid.Direction shooterDir) {
        addSequential(new SetDualActionSolenoid(arms, armDir));
        addSequential(new WaitCommand(150));
        addSequential(new SetDualActionSolenoid(shooter, shooterDir));
    }
}
