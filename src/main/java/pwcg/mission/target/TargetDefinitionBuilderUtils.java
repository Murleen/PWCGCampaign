package pwcg.mission.target;

import pwcg.campaign.api.ICountry;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.RandomNumberGenerator;
import pwcg.mission.flight.IFlightInformation;

public class TargetDefinitionBuilderUtils
{
    public static void chooseSides(IFlightInformation flightInformation) throws PWCGException
    {
        TargetDefinition targetDefinition = flightInformation.getTargetDefinition();
        
        int roll = RandomNumberGenerator.getRandom(100);
        if (roll < 60)
        {
            targetDefinition.setAttackingCountry(flightInformation.getSquadron().determineEnemyCountry(flightInformation.getCampaign(), flightInformation.getCampaign().getDate()));
            targetDefinition.setTargetCountry(flightInformation.getSquadron().determineSquadronCountry(flightInformation.getCampaign().getDate()));
        }
        else
        {
            targetDefinition.setTargetCountry(flightInformation.getSquadron().determineEnemyCountry(flightInformation.getCampaign(), flightInformation.getCampaign().getDate()));
            targetDefinition.setAttackingCountry(flightInformation.getSquadron().determineSquadronCountry(flightInformation.getCampaign().getDate()));
        }
    }

    public static String buildTargetName(ICountry targetCountry, TargetType targetType)
    {
        String nationality = targetCountry.getNationality();
        String name = nationality + " " + targetType.getTargetName();
        return name;
    }

}
