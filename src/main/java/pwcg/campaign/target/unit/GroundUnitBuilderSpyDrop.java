package pwcg.campaign.target.unit;

import pwcg.campaign.Campaign;
import pwcg.campaign.target.TargetDefinition;
import pwcg.core.exception.PWCGException;
import pwcg.mission.ground.GroundUnitCollection;
import pwcg.mission.ground.GroundUnitFactory;
import pwcg.mission.ground.factory.TroopConcentrationFactory;
import pwcg.mission.ground.unittypes.GroundUnit;
import pwcg.mission.ground.unittypes.SpotLightGroup;

public class GroundUnitBuilderSpyDrop
{
    private Campaign campaign;
    private TargetDefinition targetDefinition;
    private GroundUnitCollection groundUnitCollection = new GroundUnitCollection(GroundUnitCollectionType.INFANTRY_GROUND_UNIT_COLLECTION);

    public GroundUnitBuilderSpyDrop(Campaign campaign, TargetDefinition targetDefinition)
    {
        this.campaign = campaign;
        this.targetDefinition = targetDefinition;
    }

    public GroundUnitCollection createSpyDropTargets() throws PWCGException 
    {
        TroopConcentrationFactory groundUnitFactory = new TroopConcentrationFactory(campaign, targetDefinition);
        GroundUnit targetUnit = groundUnitFactory.createTroopConcentration();
        groundUnitCollection.addGroundUnit(GroundUnitType.INFANTRY_UNIT, targetUnit);
        
        addSpotLight();
        
        return groundUnitCollection;
    }

    private void addSpotLight() throws PWCGException 
    {
        GroundUnitFactory groundUnitFactory = new GroundUnitFactory(campaign, targetDefinition.getTargetPosition(), targetDefinition.getTargetCountry());
        SpotLightGroup spotLightGroup = groundUnitFactory.createSpotLightGroup();
        if (spotLightGroup != null)
        {
            groundUnitCollection.addGroundUnit(GroundUnitType.STATIC_UNIT, spotLightGroup);
        }
    }
}