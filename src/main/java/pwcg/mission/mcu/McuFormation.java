package pwcg.mission.mcu;

import java.io.BufferedWriter;
import java.io.IOException;

import pwcg.core.exception.PWCGIOException;
import pwcg.core.utils.Logger;

public class McuFormation extends BaseFlightMcu
{
	public static int FORMATION_NONE = 0;
	public static int FORMATION_V = 1;
	public static int FORMATION_LEFT = 2;
	public static int FORMATION_RIGHT = 3;
	public static int FORMATION_ON_ROAD_COLUMN = 4;
	public static int FORMATION_OFF_ROAD_COLUMN = 5;
	public static int FORMATION_OFF_ROAD_USE_POSITION = 6;
	public static int FORMATION_VEHICLE_FORWARD = 7;
	public static int FORMATION_VEHICLE_BACKWARDS = 8;
	public static int FORMATION_VEHICLE_STOP = 9;
	public static int FORMATION_VEHICLE_PANIC_STOP = 10;
	public static int FORMATION_VEHICLE_CONTINUE_MOVING = 11;
	public static int FORMATION_VEHICLE_SET_DIRECTION = 12;
	
	public static int FORMATION_DENSITY_TIGHT = 0;
	public static int FORMATION_DENSITY_MED = 1;
	public static int FORMATION_DENSITY_LOOSE = 2;
	
	private int formationType = FORMATION_RIGHT;
	private int formationDensity = FORMATION_DENSITY_LOOSE;

 	public McuFormation ()
	{
 		super();
 		
  		setName("Command Formation");
 		formationType = FORMATION_RIGHT;
	}
	

	public void write(BufferedWriter writer) throws PWCGIOException
	{
		try
        {
            writer.write("MCU_CMD_Formation");
            writer.newLine();
            writer.write("{");
            writer.newLine();
            
            super.write(writer);
            
            writer.write("  FormationType = " + formationType + ";");
            writer.newLine();
            writer.write("  FormationDensity = " + formationDensity + ";");
            writer.newLine();

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


	public int getFormationType() {
		return formationType;
	}


	public void setFormationType(int formationType) {
		this.formationType = formationType;
	}


	public int getFormationDensity() {
		return formationDensity;
	}


	public void setFormationDensity(int formationDensity) {
		this.formationDensity = formationDensity;
	}
}
