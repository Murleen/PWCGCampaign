package pwcg.mission.flight.divebomb;

import java.io.BufferedWriter;
import java.util.List;

import pwcg.campaign.Campaign;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.config.ConfigItemKeys;
import pwcg.core.config.ConfigManagerCampaign;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.core.utils.RandomNumberGenerator;
import pwcg.mission.Mission;
import pwcg.mission.MissionBeginUnit;
import pwcg.mission.flight.FlightTypes;
import pwcg.mission.flight.GroundTargetAttackFlight;
import pwcg.mission.mcu.McuWaypoint;

public class DiveBombingFlight extends GroundTargetAttackFlight
{
    static private int DIVE_BOMB_ALT = 1700;
    static private int DIVE_BOMB_ATTACK_TIME = 180;

	public DiveBombingFlight() 
	{
		super (DIVE_BOMB_ATTACK_TIME);
	}

	public void initialize(
				Mission mission, 
				Campaign campaign, 
				FlightTypes flightType,
				Coordinate targetCoords, 
				Squadron squad, 
                MissionBeginUnit missionBeginUnit,
				boolean isPlayerFlight) throws PWCGException 
	{
		super.initialize (mission, campaign, flightType, targetCoords, squad, missionBeginUnit, isPlayerFlight);
	}

	public void createUnitMission() throws PWCGException  
	{
		super.createUnitMission();
		super.createAttackArea(DIVE_BOMB_ALT);
	}

	@Override
	public int calcNumPlanes() throws PWCGException 
	{
		ConfigManagerCampaign configManager = campaign.getCampaignConfigManager();
		
		int BombingMinimum = configManager.getIntConfigParam(ConfigItemKeys.BombingMinimumKey);
		int BombingAdditional = configManager.getIntConfigParam(ConfigItemKeys.BombingAdditionalKey) + 1;
		numPlanesInFlight = BombingMinimum + RandomNumberGenerator.getRandom(BombingAdditional);
		
        return modifyNumPlanes(numPlanesInFlight);
	}

	@Override
	public List<McuWaypoint> createWaypoints(Mission mission, Coordinate startPosition) throws PWCGException 
	{
	    List<McuWaypoint> waypointList = null;
	    
        DiveBombingWaypoints waypointGenerator = new DiveBombingWaypoints(
                startPosition, 
                targetCoords, 
                this,
                mission);

        waypointList = waypointGenerator.createWaypoints();

	    return waypointList;
	}

	@Override
	public void write(BufferedWriter writer) throws PWCGException 
	{
		super.write(writer);
	}
}
