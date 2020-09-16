package pwcg.mission.mcu.group.virtual;

import java.io.BufferedWriter;

import pwcg.core.exception.PWCGException;
import pwcg.mission.flight.waypoint.virtual.VirtualWayPointCoordinate;
import pwcg.mission.mcu.McuTimer;

public final class VirtualWaypointStartNextVwp 
{       
    private VirtualWayPointCoordinate vwpCoordinate;

    private McuTimer startNextWaypointTimer = new McuTimer();
    private McuTimer startnextWaypointTriggeredTimer = new McuTimer();
    
    public VirtualWaypointStartNextVwp(VirtualWayPointCoordinate vwpCoordinate)
    {
        this.vwpCoordinate = vwpCoordinate; 
    }

    public void build() throws PWCGException 
    {
        buildMcus();
        setTargetAssociations();
    }

    public void linkToNextVwp(int nextVwpIndex)
    {
        startnextWaypointTriggeredTimer.setTarget(nextVwpIndex);
    }

    private void buildMcus()
    {
        startNextWaypointTimer.setPosition(vwpCoordinate.getPosition().copy());
        startNextWaypointTimer.setName("VWP Start next VWP Timer");
        startNextWaypointTimer.setDesc("VWP Start next VWP Timer");
        startNextWaypointTimer.setTimer(vwpCoordinate.getWaypointWaitTimeSeconds());

        startnextWaypointTriggeredTimer.setPosition(vwpCoordinate.getPosition().copy());
        startnextWaypointTriggeredTimer.setName("VWP Start next VWP Triggered Timer");
        startnextWaypointTriggeredTimer.setDesc("VWP Start next VWP Triggered Timer");
        startnextWaypointTriggeredTimer.setTimer(vwpCoordinate.getWaypointWaitTimeSeconds());
    }

    private void setTargetAssociations() throws PWCGException
    {
        startNextWaypointTimer.setTarget(startnextWaypointTriggeredTimer.getIndex());
    }

    public void write(BufferedWriter writer) throws PWCGException
    {
        startNextWaypointTimer.write(writer);
        startnextWaypointTriggeredTimer.write(writer);
    }

    public int getDisbablePoint()
    {
        return startNextWaypointTimer.getIndex();
    }

    public McuTimer getStartNextWaypointTimer()
    {
        return startNextWaypointTimer;
    }

    public McuTimer getStartNextWaypointTriggeredTimer()
    {
        return startnextWaypointTriggeredTimer;
    }
}
