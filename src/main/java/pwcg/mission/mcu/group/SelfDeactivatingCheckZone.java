package pwcg.mission.mcu.group;

import java.io.BufferedWriter;
import java.io.IOException;

import pwcg.campaign.utils.IndexGenerator;
import pwcg.core.exception.PWCGIOException;
import pwcg.core.location.Coordinate;
import pwcg.core.utils.Logger;
import pwcg.mission.mcu.BaseFlightMcu;
import pwcg.mission.mcu.McuCheckZone;
import pwcg.mission.mcu.McuDeactivate;
import pwcg.mission.mcu.McuTimer;



public class SelfDeactivatingCheckZone 
{
	private String name = "Self Deactivating CZ";
	private String desc = "Self Deactivating CZ";
    private int index = IndexGenerator.getInstance().getNextIndex();;
	
    private McuTimer activateCZTimer = new McuTimer();
    private McuCheckZone checkZone = new McuCheckZone();
    
    private McuTimer deactivateCZTimer = new McuTimer();
    private McuDeactivate deactivateCZ = new McuDeactivate();
	

    public SelfDeactivatingCheckZone (Coordinate coordinate, int zone)
    {
        super();
        initialize(coordinate, zone);
    }

    private void initialize(Coordinate coordinate, int zone) 
    {
        checkZone.setZone(zone);
                        
        activateCZTimer.setPosition(coordinate.copy());
        checkZone.setPosition(coordinate.copy());

        deactivateCZTimer.setPosition(coordinate.copy());
        deactivateCZ.setPosition(coordinate.copy());
        
        activateCZTimer.setName("CZ Activate Timer");
        checkZone.setName("CZ");
        
        deactivateCZTimer.setName("CZ Deactivate Timer");
        deactivateCZ.setName("CZ Deactivate");

        activateCZTimer.setDesc("CZ Activate Timer");
        checkZone.setDesc("CZ");

        deactivateCZTimer.setDesc("VWP CZ Deactivate Timer");
        deactivateCZ.setDesc("CZ Deactivate");
        
        activateCZTimer.setTimer(0);
        deactivateCZTimer.setTimer(0);
    }

    public void linkTargets(BaseFlightMcu activateIn, BaseFlightMcu deactivatIn)
    {
        // 1. Link the incoming MCU to the activate timer
        activateIn.setTarget(activateCZTimer.getIndex());
        activateCZTimer.setTarget(checkZone.getIndex());

        // 2. If the CZ triggers, deactivate the CZ to avoid repeat triggers
        checkZone.setTarget(deactivateCZTimer.getIndex());
        deactivateCZTimer.setTarget(deactivateCZ.getIndex());
        
        // Deactivate both the CZ and the activate timer
        deactivateCZ.setTarget(checkZone.getIndex());        
        deactivateCZ.setTarget(activateCZTimer.getIndex());    
        
        // We may have an alternative deactivate
        if (deactivatIn != null)
        {
            deactivatIn.setTarget(deactivateCZTimer.getIndex());
        }
    }

    
    public void write(BufferedWriter writer) throws PWCGIOException
    {
        try
        {
            writer.write("Group");
            writer.newLine();
            writer.write("{");
            writer.newLine();
            
            writer.write("  Name = \"" + name + "\";");
            writer.newLine();
            writer.write("  Index = " + index + ";");
            writer.newLine();
            writer.write("  Desc = \"" +  desc + "\";");
            writer.newLine();
            writer.newLine();

            activateCZTimer.write(writer);
            checkZone.write(writer);
            deactivateCZTimer.write(writer);
            deactivateCZ.write(writer);

            writer.write("}");
            writer.newLine();
        }
        catch (IOException e)
        {
            Logger.logException(e);
            throw new PWCGIOException(e.getMessage());
        }
    }

    public void setZone(int zone)
    {
        checkZone.setZone(zone);
    }

    public void setCZTarget(int targetMcuIndex)
    {
        checkZone.setTarget(targetMcuIndex);
    }

    public void setCZObject(int objectMcuIndex)
    {
        checkZone.setObject(objectMcuIndex);
    }

    public McuCheckZone getCheckZone()
    {
        return checkZone;
    }

    public McuTimer getDeactivateCZTimer()
    {
        return deactivateCZTimer;
    }
    
    
}
