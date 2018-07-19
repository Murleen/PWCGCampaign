package pwcg.mission.flight.scramble;

import java.io.BufferedWriter;
import java.util.List;

import pwcg.core.config.ConfigItemKeys;
import pwcg.core.config.ConfigManagerCampaign;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.core.utils.RandomNumberGenerator;
import pwcg.mission.Mission;
import pwcg.mission.MissionBeginUnit;
import pwcg.mission.flight.Flight;
import pwcg.mission.flight.FlightInformation;
import pwcg.mission.flight.plane.PlaneMCU;
import pwcg.mission.mcu.McuWaypoint;

public class ScrambleOpposingFlight extends Flight
{
	Coordinate startCoords = null;
	
    public ScrambleOpposingFlight(FlightInformation flightInformation, MissionBeginUnit missionBeginUnit, Coordinate startCoords)
    {
        super (flightInformation, missionBeginUnit);
        this.startCoords = startCoords;
    }

	public void createUnitMission() throws PWCGException  
	{
		super.createUnitMission();

		for (PlaneMCU plane : planes)
		{
			plane.setFuel(.6);
		}
	}

	@Override
	public int calcNumPlanes() throws PWCGException 
	{
		ConfigManagerCampaign configManager = getCampaign().getCampaignConfigManager();
		
		int GroundAttackMinimum = configManager.getIntConfigParam(ConfigItemKeys.GroundAttackMinimumKey);
		int GroundAttackAdditional = configManager.getIntConfigParam(ConfigItemKeys.GroundAttackAdditionalKey) + 1;
		numPlanesInFlight = GroundAttackMinimum + RandomNumberGenerator.getRandom(GroundAttackAdditional);
		
        return modifyNumPlanes(numPlanesInFlight);
	}

	@Override
	public List<McuWaypoint> createWaypoints(Mission mission, Coordinate startPosition) throws PWCGException 
	{
		ScrambleOpposingWaypoints waypointGenerator = new ScrambleOpposingWaypoints(
					startPosition, 
					getTargetCoords(), 
		       		this,
		       		mission);

        List<McuWaypoint> waypointList = waypointGenerator.createWaypoints();
        
        return waypointList;
	}

	@Override
	public void write(BufferedWriter writer) throws PWCGException 
	{
		super.write(writer);
	}

	public String getMissionObjective() 
	{
		String objective = "";
		return objective;
	}

    @Override
    protected void createFlightSpecificTargetAssociations() throws PWCGException
    {
        this.createSimpleTargetAssociations();
    }
}
