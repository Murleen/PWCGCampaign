package pwcg.mission;

import pwcg.campaign.Campaign;
import pwcg.campaign.factory.PWCGFlightFactory;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;
import pwcg.mission.flight.Flight;
import pwcg.mission.flight.FlightTypes;
import pwcg.mission.flight.factory.FlightFactory;
import pwcg.mission.flight.plane.PlaneMCU;
import pwcg.mission.ground.unittypes.infantry.GroundPillBoxFlareUnit;
import pwcg.mission.mcu.McuCheckZone;
import pwcg.mission.mcu.group.FlareSequence;

public class PlayerFlightBuilder
{
    private Campaign campaign;
    private Mission mission;

    private Flight playerFlight;
 
    public PlayerFlightBuilder(Campaign campaign, Mission mission)
    {
        this.campaign = campaign;
        this.mission = mission;
    }
    
    public Flight createPlayerFlight(FlightTypes flightType, Squadron squadron) throws PWCGException 
    {
        // get plane to allow determination of role
        FlightFactory flightFactory = PWCGFlightFactory.createFlightFactory(campaign);
        if (flightType == FlightTypes.ANY)
        {
            flightType = flightFactory.buildFlight(squadron, true);
        }
        
        if (flightType == FlightTypes.FERRY)
        {
            playerFlight = flightFactory.buildFerryFlight(mission, squadron, true);
        }
        else
        {
            playerFlight = flightFactory.buildFlight(mission, squadron, flightType, true);
        }
        
        triggerLinkedUnitCZFromMyFlight(playerFlight);
        
        validatePlayerFlight();
        
        return playerFlight;
    }

    private void validatePlayerFlight() throws PWCGException
    {
        boolean playerIsInFlight = false;
        for (PlaneMCU plane : playerFlight.getPlanes())
        {
            if (plane.getPilot().isPlayer())
            {
                playerIsInFlight = true;
            }
        }
        
        if (!playerIsInFlight)
        {
            throw new PWCGException("No plane assigned to player");
        }
    }

    private void triggerLinkedUnitCZFromMyFlight(Unit parent) throws PWCGException 
    {
        for (Unit unit : parent.linkedUnits)
        {
            MissionBeginUnit mbu = unit.getMissionBeginUnit();
            if (mbu instanceof MissionBeginUnitCheckZone)
            {
                MissionBeginUnitCheckZone mbucz = (MissionBeginUnitCheckZone) mbu;
                McuCheckZone checkZone = mbucz.getSelfDeactivatingCheckZone().getCheckZone();
                checkZone.triggerCheckZoneByFlight(playerFlight);
            }
            
            if (unit instanceof GroundPillBoxFlareUnit)
            {
                GroundPillBoxFlareUnit flareUnit = (GroundPillBoxFlareUnit) unit;
                FlareSequence flareSequence = flareUnit.getFlares();
                MissionBeginUnitCheckZone mbucz = flareSequence.getMissionBeginUnit();
                McuCheckZone checkZone = mbucz.getSelfDeactivatingCheckZone().getCheckZone();
                checkZone.triggerCheckZoneByFlight(playerFlight);
            }

            triggerLinkedUnitCZFromMyFlight(unit);
        }
    }
}
