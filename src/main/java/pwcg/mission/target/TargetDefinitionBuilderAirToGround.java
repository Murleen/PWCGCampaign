package pwcg.mission.target;

import pwcg.campaign.squadron.Squadron;
import pwcg.campaign.target.locator.targettype.TargetTypeAttackGenerator;
import pwcg.campaign.target.locator.targettype.TargetTypeAvailabilityInputGenerator;
import pwcg.campaign.target.locator.targettype.TargetTypeAvailabilityInputs;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.mission.flight.FlightInformation;
import pwcg.mission.flight.FlightTypes;
import pwcg.mission.target.locator.TargetLocatorAttack;

public class TargetDefinitionBuilderAirToGround implements ITargetDefinitionBuilder
{
    private FlightInformation flightInformation;
    private TargetDefinition targetDefinition = new TargetDefinition();

    public TargetDefinitionBuilderAirToGround (FlightInformation flightInformation)
    {
        this.flightInformation = flightInformation;
    }

    public TargetDefinition buildTargetDefinition () throws PWCGException
    {        
        TacticalTarget targetType = determinePredefinedTacticalTarget(flightInformation.getFlightType());
        if (targetType == TacticalTarget.TARGET_ANY)
        {
            Coordinate targetSearchStartLocation = flightInformation.getTargetSearchStartLocation();
            TargetTypeAvailabilityInputs targetTypeAvailabilityInputs = createTargetingInputs(targetSearchStartLocation);
            targetType = createTargetType(targetTypeAvailabilityInputs);
        }
        buildTargetDefinitionForTacticalFlight(targetType);          
        
        return targetDefinition;
    }

    private TacticalTarget determinePredefinedTacticalTarget(FlightTypes flightType) 
    {
        if (flightType == FlightTypes.LOW_ALT_BOMB || flightType == FlightTypes.LOW_ALT_CAP || 
            flightType == FlightTypes.LOW_ALT_PATROL || flightType == FlightTypes.CONTACT_PATROL ||
            flightType == FlightTypes.PARATROOP_DROP)
        {
            return TacticalTarget.TARGET_ASSAULT;
        }
        else if (flightType == FlightTypes.BALLOON_BUST || flightType == FlightTypes.BALLOON_DEFENSE)
        {
            return TacticalTarget.TARGET_BALLOON;
        }
        else if (flightType == FlightTypes.ANTI_SHIPPING_BOMB || flightType == FlightTypes.ANTI_SHIPPING_ATTACK || flightType == FlightTypes.ANTI_SHIPPING_DIVE_BOMB)
        {
            return TacticalTarget.TARGET_SHIPPING;
        }
        else if (flightType == FlightTypes.SPY_EXTRACT)
        {
            return TacticalTarget.TARGET_TROOP_CONCENTRATION;
        }
        return TacticalTarget.TARGET_ANY;
    }    

    private void buildTargetDefinitionForTacticalFlight (TacticalTarget targetType) throws PWCGException
    {
        targetDefinition.setTargetType(targetType);
        targetDefinition.setAttackingSquadron(flightInformation.getSquadron());
        targetDefinition.setTargetName(TargetDefinitionBuilderUtils.buildTargetName(
                flightInformation.getSquadron().determineSquadronCountry(flightInformation.getCampaign().getDate()), targetType));

        determineAttackingAndDefendingCountries(targetType);
        
        targetDefinition.setDate(flightInformation.getCampaign().getDate());
        targetDefinition.setPlayerTarget((Squadron.isPlayerSquadron(flightInformation.getCampaign(), flightInformation.getSquadron().getSquadronId())));
        
        TargetRadius targetRadius = new TargetRadius();
        targetRadius.calculateTargetRadius(flightInformation.getFlightType(), flightInformation.getMission().getMissionBorders().getAreaRadius());
        targetDefinition.setPreferredRadius(new Double(targetRadius.getInitialTargetRadius()).intValue());
        targetDefinition.setMaximumRadius(new Double(targetRadius.getMaxTargetRadius()).intValue());
        
        TargetLocatorAttack targetLocator = new TargetLocatorAttack(targetDefinition,flightInformation.getMission().getMissionBorders().getCenter());
        targetLocator.locateTarget();
        targetDefinition.setTargetPosition(targetLocator.getTargetLocation());
        targetDefinition.setTargetOrientation(targetLocator.getTargetOrientation());
    }

    private void determineAttackingAndDefendingCountries(TacticalTarget targetType) throws PWCGException
    {
        targetDefinition.setAttackingCountry(flightInformation.getSquadron().determineSquadronCountry(flightInformation.getCampaign().getDate()));
        targetDefinition.setTargetCountry(flightInformation.getSquadron().determineEnemyCountry(flightInformation.getCampaign(), flightInformation.getCampaign().getDate()));

        if (flightInformation.getFlightType() == FlightTypes.BALLOON_DEFENSE)
        {
            targetDefinition.setAttackingCountry(flightInformation.getSquadron().determineEnemyCountry(flightInformation.getCampaign(), flightInformation.getCampaign().getDate()));
            targetDefinition.setTargetCountry(flightInformation.getSquadron().determineSquadronCountry(flightInformation.getCampaign().getDate()));
        }
        else if (targetType == TacticalTarget.TARGET_ASSAULT)
        {
            TargetDefinitionBuilderUtils.chooseSides(flightInformation);
        }
        else
        {
            targetDefinition.setAttackingCountry(flightInformation.getSquadron().determineSquadronCountry(flightInformation.getCampaign().getDate()));
            targetDefinition.setTargetCountry(flightInformation.getSquadron().determineEnemyCountry(flightInformation.getCampaign(), flightInformation.getCampaign().getDate()));
        }
    }

    private TargetTypeAvailabilityInputs createTargetingInputs(Coordinate targetSearchStartLocation) throws PWCGException
    {
        TargetTypeAvailabilityInputGenerator targetTypeAvailabilityInputGenerator = new TargetTypeAvailabilityInputGenerator();
        TargetTypeAvailabilityInputs targetTypeAvailabilityInputs = targetTypeAvailabilityInputGenerator.createTargetAvailabilityInputs(flightInformation);
        return targetTypeAvailabilityInputs;
    }

    private TacticalTarget createTargetType(TargetTypeAvailabilityInputs targetTypeAvailabilityInputs) throws PWCGException
    {
        TargetTypeAttackGenerator targetTypeGenerator = new TargetTypeAttackGenerator(targetTypeAvailabilityInputs);
        TacticalTarget targetType = targetTypeGenerator.createTargetType();
        return targetType;
    }
}
