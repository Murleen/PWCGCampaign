
package pwcg.mission.mcu;

import java.io.BufferedWriter;
import java.io.IOException;

import pwcg.core.exception.PWCGIOException;
import pwcg.core.utils.Logger;

public class McuLanding extends BaseFlightMcu
{
	private int goalType = 0;

	public McuLanding ()
	{
		super();

		name = "command Land";
		desc = "command Land";
	}

	public void write(BufferedWriter writer) throws PWCGIOException
	{
		try
        {
            writer.write("MCU_CMD_Land");
            writer.newLine();
            writer.write("{");
            writer.newLine();
            
            super.write(writer);

            writeMCUGoal(writer, goalType);

            writer.write("}");
            writer.newLine();
            writer.newLine();
            writer.newLine();
        }
        catch (IOException e)
        {
            Logger.logException(e);
            throw new PWCGIOException(e.getMessage());
        }
	}	
}
