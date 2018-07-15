package pwcg.mission.flight.spy;

import java.io.BufferedWriter;
import java.util.List;

import pwcg.campaign.Campaign;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.mission.Mission;
import pwcg.mission.MissionBeginUnit;
import pwcg.mission.flight.Flight;
import pwcg.mission.flight.FlightTypes;
import pwcg.mission.flight.waypoint.WaypointType;
import pwcg.mission.mcu.McuTimer;
import pwcg.mission.mcu.McuWaypoint;

public class SpyExtractFlight extends Flight
{
	protected McuTimer spyDropTimer = null;

	public SpyExtractFlight() 
	{
		super ();
	}

	public void initialize(
				Mission mission, 
				Campaign campaign, 
				Coordinate targetCoords, 
				Squadron squad, 
                MissionBeginUnit missionBeginUnit,
				boolean isPlayerFlight) throws PWCGException 
	{
		super.initialize (mission, campaign, FlightTypes.SPY_EXTRACT, targetCoords, squad, missionBeginUnit, isPlayerFlight);
	}

	@Override
	public List<McuWaypoint> createWaypoints(Mission mission, Coordinate startPosition) throws PWCGException 
	{
		SpyExtractWaypoints waypointGenerator = new SpyExtractWaypoints(
					startPosition, 
		       		targetCoords, 
		       		this,
		       		mission);

		List<McuWaypoint> waypointList = waypointGenerator.createWaypoints();
		
		McuWaypoint targetWP = null;
		for (McuWaypoint wp : waypointList)
		{
			if (wp.getName().equals(WaypointType.SPY_EXTRACT_WAYPOINT.getName()))
			{
				targetWP = wp;
				break;
			}
		}

		createSpyDrop(targetWP);
		
		return waypointList;
	}

	public void createSpyDrop(McuWaypoint targetWP) throws PWCGException 
	{
		Coordinate landCoords = targetCoords.copy();
		landCoords.setYPos(0.0);
		
		spyDropTimer = new McuTimer();
		spyDropTimer.setName(getName() + ": Activation Timer");		
		spyDropTimer.setDesc("Activation Timer for " + getName());
		spyDropTimer.setPosition(landCoords.copy());	
		spyDropTimer.setTimer(120);
	}

	@Override
    protected void createFlightSpecificTargetAssociations() throws PWCGException
	{
        linkWPToPlane(getLeadPlane(), waypointPackage.getWaypointsForLeadPlane());

        McuWaypoint prevWP = null;
        for (McuWaypoint nextWP : waypointPackage.getWaypointsForLeadPlane())
        {
            if (prevWP != null)
            {
                if (prevWP.getName().equals(WaypointType.RECON_WAYPOINT.getName()))
                {
                    prevWP.setTarget(spyDropTimer.getIndex());
                    spyDropTimer.setTarget(nextWP.getIndex());
                }
                else
                {
                    prevWP.setTarget(nextWP.getIndex());
                }
            }
            
			prevWP = nextWP;
		}
	}

	@Override
	public void write(BufferedWriter writer) throws PWCGException 
	{
		super.write(writer);
		spyDropTimer.write(writer);
	}

	@Override
	protected int calcNumPlanes() 
	{
		return 1;
	}

	public String getMissionObjective() throws PWCGException 
	{
        String objective = "Extract our spy at the specified location" + formMissionObjectiveLocation(targetCoords.copy()) + ".  Don't get caught!";       
		
		return objective;
	}

}
