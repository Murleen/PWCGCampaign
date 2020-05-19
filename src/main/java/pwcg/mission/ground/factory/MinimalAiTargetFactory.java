package pwcg.mission.ground.factory;

import pwcg.campaign.Campaign;
import pwcg.core.exception.PWCGException;
import pwcg.mission.ground.GroundUnitSize;
import pwcg.mission.ground.builder.AAAUnitBuilder;
import pwcg.mission.ground.org.IGroundUnitCollection;
import pwcg.mission.target.TargetDefinition;

public class MinimalAiTargetFactory
{
    private Campaign campaign;
    private TargetDefinition targetDefinition;
    
    public MinimalAiTargetFactory (Campaign campaign, TargetDefinition targetDefinition)
    {
        this.campaign = campaign;
        this.targetDefinition  = targetDefinition;
    }

    public IGroundUnitCollection createTroopConcentration () throws PWCGException
    {
        AAAUnitBuilder aaaUnitBuilder = new AAAUnitBuilder(campaign, targetDefinition); 
        return aaaUnitBuilder.createAAAArtilleryBattery(GroundUnitSize.GROUND_UNIT_SIZE_TINY);
    }
}
