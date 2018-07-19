package pwcg.mission.flight.balloonBust;

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
import pwcg.mission.mcu.McuWaypoint;

public class BalloonBustFlight extends Flight
{
    public BalloonBustFlight(FlightInformation flightInformation, MissionBeginUnit missionBeginUnit)
    {
        super (flightInformation, missionBeginUnit);
    }

	@Override
	public int calcNumPlanes() throws PWCGException 
	{
		ConfigManagerCampaign configManager = getCampaign().getCampaignConfigManager();
		
		int BalloonBustMinimum = configManager.getIntConfigParam(ConfigItemKeys.BalloonBustMinimumKey);
		int BalloonBustAdditional = configManager.getIntConfigParam(ConfigItemKeys.BalloonBustAdditionalKey) + 1;
		numPlanesInFlight = BalloonBustMinimum + RandomNumberGenerator.getRandom(BalloonBustAdditional);
		
		return numPlanesInFlight;

	}

	@Override
	public List<McuWaypoint> createWaypoints(Mission mission, Coordinate startPosition) throws PWCGException 
	{
		BalloonBustWaypoints waypointGenerator = new BalloonBustWaypoints(startPosition, 
			       getTargetCoords(), 
			       this,
			       mission);

        List<McuWaypoint> waypointList = waypointGenerator.createWaypoints();
        
        return waypointList;
	}

	public String getMissionObjective() throws PWCGException 
	{
        String objective = "Destroy the enemy balloon" + formMissionObjectiveLocation(getTargetCoords().copy()) + ".";       

		return objective;
	}

    @Override
    protected void createFlightSpecificTargetAssociations() throws PWCGException
    {
        this.createSimpleTargetAssociations();
    }
}
