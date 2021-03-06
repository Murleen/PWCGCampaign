package pwcg.mission.target;

import pwcg.campaign.api.ICountry;
import pwcg.core.exception.PWCGException;
import pwcg.mission.flight.IFlightInformation;
import pwcg.mission.target.TargetRadius;

public class TargetTypeAvailabilityInputGenerator
{
    private TargetTypeAvailabilityInputs targetTypeAvailabilityInputs = new TargetTypeAvailabilityInputs();

    public TargetTypeAvailabilityInputs createTargetAvailabilityInputs(IFlightInformation flightInformation) throws PWCGException
    {
        ICountry enemyCountry = flightInformation.getSquadron().determineEnemyCountry(flightInformation.getCampaign(), flightInformation.getCampaign().getDate());

        TargetRadius targetRadius = new TargetRadius();
        targetRadius.calculateTargetRadius(flightInformation.getFlightType(), flightInformation.getMission().getMissionBorders().getAreaRadius());

        if (flightInformation.isPlayerRelatedFlight())
        {
            targetTypeAvailabilityInputs.setUseMinimalTargetSet(false);
        }
        else
        {
            targetTypeAvailabilityInputs.setUseMinimalTargetSet(true);
        }
        
        targetTypeAvailabilityInputs.setPreferredDistance(Double.valueOf(targetRadius.getInitialTargetRadius()).intValue());
        targetTypeAvailabilityInputs.setMaxDistance(Double.valueOf(targetRadius.getMaxTargetRadius()).intValue());
        targetTypeAvailabilityInputs.setDate(flightInformation.getCampaign().getDate());
        targetTypeAvailabilityInputs.setTargetGeneralLocation(flightInformation.getTargetSearchStartLocation());
        targetTypeAvailabilityInputs.setSide(enemyCountry.getSide());
        
        return targetTypeAvailabilityInputs;
    }
}
