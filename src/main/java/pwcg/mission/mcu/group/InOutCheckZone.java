package pwcg.mission.mcu.group;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import pwcg.campaign.utils.IndexGenerator;
import pwcg.core.exception.PWCGException;
import pwcg.core.exception.PWCGIOException;
import pwcg.core.location.Coordinate;
import pwcg.core.utils.Logger;
import pwcg.mission.flight.Flight;
import pwcg.mission.mcu.Coalition;
import pwcg.mission.mcu.McuActivate;
import pwcg.mission.mcu.McuCheckZone;
import pwcg.mission.mcu.McuDeactivate;
import pwcg.mission.mcu.McuTimer;

public class InOutCheckZone {
    private String name = "In/Out CZ";
    private String desc = "In/Out CZ";
    private int index = IndexGenerator.getInstance().getNextIndex();;

    private McuCheckZone inZone = new McuCheckZone();
    private McuCheckZone outZone = new McuCheckZone();

    private McuActivate inActivate = new McuActivate();
    private McuActivate outActivate = new McuActivate();

    private McuDeactivate inDeactivate = new McuDeactivate();
    private McuDeactivate outDeactivate = new McuDeactivate();

    private McuTimer inTimer = new McuTimer();
    private McuTimer outTimer = new McuTimer();
    private McuTimer enableTimer = new McuTimer();
    private McuTimer disableTimer = new McuTimer();

    public InOutCheckZone()
    {
        inZone.setCloser(1);
        outZone.setCloser(0);

        inTimer.setTimer(1);
        outTimer.setTimer(1);
        enableTimer.setTimer(0);
        disableTimer.setTimer(0);

        setNames();
        setTargets();
    }

    private void setTargets() {
        inZone.setTarget(inDeactivate.getIndex());
        inZone.setTarget(outActivate.getIndex());
        inZone.setTarget(outTimer.getIndex());

        outZone.setTarget(inActivate.getIndex());
        outZone.setTarget(inTimer.getIndex());
        outZone.setTarget(outDeactivate.getIndex());

        inActivate.setTarget(inZone.getIndex());
        outActivate.setTarget(outZone.getIndex());

        inDeactivate.setTarget(inZone.getIndex());
        outDeactivate.setTarget(outZone.getIndex());

        inTimer.setTarget(inZone.getIndex());
        outTimer.setTarget(outZone.getIndex());

        enableTimer.setTarget(inActivate.getIndex());
        enableTimer.setTarget(inTimer.getIndex());
        enableTimer.setTarget(outDeactivate.getIndex());

        disableTimer.setTarget(inDeactivate.getIndex());
        disableTimer.setTarget(outDeactivate.getIndex());
    }

    private void setNames()
    {
        inZone.setName("In CZ");
        outZone.setName("Out CZ");
        inActivate.setName("In CZ Activate");
        outActivate.setName("Out CZ Activate");
        inDeactivate.setName("In CZ Deactivate");
        outDeactivate.setName("Out CZ Deactivate");
        inTimer.setName("In timer");
        outTimer.setName("Out timer");
        enableTimer.setName("Enable timer");
        disableTimer.setName("Disable timer");
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

            inZone.write(writer);
            outZone.write(writer);

            inActivate.write(writer);
            outActivate.write(writer);

            inDeactivate.write(writer);
            outDeactivate.write(writer);

            inTimer.write(writer);
            outTimer.write(writer);
            enableTimer.write(writer);
            disableTimer.write(writer);

            writer.write("}");
            writer.newLine();
        }
        catch (IOException e)
        {
            Logger.logException(e);
            throw new PWCGIOException(e.getMessage());
        }
    }

    public void setPosition(Coordinate coordinate)
    {
        inZone.setPosition(coordinate);
        outZone.setPosition(coordinate);
        inActivate.setPosition(coordinate);
        outActivate.setPosition(coordinate);
        inDeactivate.setPosition(coordinate);
        outDeactivate.setPosition(coordinate);
        enableTimer.setPosition(coordinate);
        disableTimer.setPosition(coordinate);
    }

    public void setZone(int zone)
    {
        inZone.setZone(zone);
        outZone.setZone(zone + 500);
    }

    public void setCylinder(int cylinder)
    {
        inZone.setCylinder(cylinder);
        outZone.setCylinder(cylinder);
    }

    public void setInTarget(int targetMcuIndex)
    {
        inZone.setTarget(targetMcuIndex);
    }

    public void setOutTarget(int targetMcuIndex)
    {
        outZone.setTarget(targetMcuIndex);
    }

    public void triggerCheckZoneByCoalitions (List<Coalition> coalitions)
    {
        inZone.triggerCheckZoneByCoalitions(coalitions);
        outZone.triggerCheckZoneByCoalitions(coalitions);
    }

    public void triggerCheckZoneByCoalition (Coalition coalition)
    {
        inZone.triggerCheckZoneByCoalition(coalition);
        outZone.triggerCheckZoneByCoalition(coalition);
    }

    public void triggerCheckZoneByFlight (Flight flight)
    {
        inZone.triggerCheckZoneByFlight(flight);
        outZone.triggerCheckZoneByFlight(flight);
    }

    public void triggerCheckZoneBySingleObject (int objectId)
    {
        inZone.triggerCheckZoneBySingleObject(objectId);
        outZone.triggerCheckZoneBySingleObject(objectId);
    }

    public void triggerCheckZoneByMultipleObjects(List<Integer> playerPlaneIds) throws PWCGException
    {
        inZone.triggerCheckZoneByMultipleObjects(playerPlaneIds);
        outZone.triggerCheckZoneByMultipleObjects(playerPlaneIds);
    }

    public McuTimer getEnableTimer()
    {
        return enableTimer;
    }

    public McuTimer getDisableTimer()
    {
        return disableTimer;
    }
}
