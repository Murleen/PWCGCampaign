package pwcg.mission.target;

import pwcg.campaign.api.ICountry;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.core.location.Orientation;
import pwcg.mission.flight.FlightTypes;
import pwcg.mission.flight.IFlightInformation;
import pwcg.mission.target.locator.TargetLocatorAir;

public class TargetDefinitionBuilderAirToAir implements ITargetDefinitionBuilder
{
    private IFlightInformation flightInformation;
    private TargetDefinition targetDefinition = new TargetDefinition();

    public TargetDefinitionBuilderAirToAir (IFlightInformation flightInformation)
    {
    	this.flightInformation = flightInformation;
    }

    public TargetDefinition buildTargetDefinition () throws PWCGException
    {
        createBasicTargetDefinition();
        createTargetRadius();        
        createTargetLocation();
        return targetDefinition;
    }

    private void createTargetRadius() throws PWCGException
    {
        TargetRadius targetRadius = new TargetRadius();
        targetRadius.calculateTargetRadius(flightInformation.getFlightType(), flightInformation.getMission().getMissionBorders().getAreaRadius());
        targetDefinition.setPreferredRadius(Double.valueOf(targetRadius.getInitialTargetRadius()).intValue());
        targetDefinition.setMaximumRadius(Double.valueOf(targetRadius.getMaxTargetRadius()).intValue());
    }

    private void createBasicTargetDefinition() throws PWCGException
    {
        targetDefinition.setTargetType(TargetType.TARGET_AIR);
        targetDefinition.setAttackingSquadron(flightInformation.getSquadron());

        targetDefinition.setAttackingCountry(flightInformation.getSquadron().determineSquadronCountry(flightInformation.getCampaign().getDate()));
        targetDefinition.setCountry(flightInformation.getSquadron().determineEnemyCountry(flightInformation.getCampaign(), flightInformation.getCampaign().getDate()));
        targetDefinition.setTargetName(buildTargetName(targetDefinition.getCountry(), TargetType.TARGET_AIR));

        targetDefinition.setDate(flightInformation.getCampaign().getDate());
        targetDefinition.setPlayerTarget((Squadron.isPlayerSquadron(flightInformation.getCampaign(), flightInformation.getSquadron().getSquadronId())));
    }

    private void createTargetLocation() throws PWCGException
    {
        Coordinate targetLocation = getTargetLocationForFlightType();
        targetDefinition.setPosition(targetLocation);
        targetDefinition.setTargetOrientation(new Orientation());
    }

    private Coordinate getTargetLocationForFlightType() throws PWCGException
    {
        TargetLocatorAir targetLocatorAir = new TargetLocatorAir(flightInformation);
        
        if (flightInformation.getFlightType() == FlightTypes.PATROL || 
            flightInformation.getFlightType() == FlightTypes.LOW_ALT_PATROL ||
            flightInformation.getFlightType() == FlightTypes.LOW_ALT_CAP ||
            flightInformation.getFlightType() == FlightTypes.LONE_WOLF)
        {
            return targetLocatorAir.getFrontCoordinate();
        }
        else if (flightInformation.getFlightType() == FlightTypes.INTERCEPT || flightInformation.getFlightType() == FlightTypes.RECON)
       {
           return targetLocatorAir.getInterceptCoordinate();
       }
        else if (flightInformation.getFlightType() == FlightTypes.STRATEGIC_INTERCEPT)
       {
            return flightInformation.getMission().getMissionBorders().getCenter();
       }
        else if (flightInformation.getFlightType() == FlightTypes.OFFENSIVE || flightInformation.getFlightType() == FlightTypes.SPY_EXTRACT)
        {
            return targetLocatorAir.getEnemyTerritoryPatrolCoordinate();
        }
        else if (flightInformation.getFlightType() == FlightTypes.TRANSPORT)
        {
            return targetLocatorAir.getTransportAirfieldCoordinate();
        }
        else if (flightInformation.getFlightType() == FlightTypes.SCRAMBLE)
        {
            return targetLocatorAir.getScrambleCoordinate();
        }
        else if (flightInformation.getFlightType() == FlightTypes.ESCORT)
        {
            if (flightInformation.isPlayerFlight())
            {
                return targetLocatorAir.getPlayerEscortRendezvousCoordinate();
            }
            else
            {
                return targetLocatorAir.getEscortForPlayerRendezvousCoordinate();
            }
        }

        throw new PWCGException("No target locations for flight type " + flightInformation.getFlightType());
    }

    private String buildTargetName(ICountry targetCountry, TargetType targetType)
    {
        String nationality = targetCountry.getNationality();
        String name = nationality + " " + targetType.getTargetName();
        return name;
    }

}
