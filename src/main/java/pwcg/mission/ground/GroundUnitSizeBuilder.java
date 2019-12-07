package pwcg.mission.ground;

import pwcg.campaign.Campaign;
import pwcg.core.config.ConfigItemKeys;
import pwcg.core.config.ConfigManagerCampaign;
import pwcg.core.config.ConfigSimple;
import pwcg.core.exception.PWCGException;

public class GroundUnitSizeBuilder
{
    public static GroundUnitSize calcNumUnitsByConfig(Campaign campaign, Boolean isPlayerTarget) throws PWCGException 
    {
        if (isPlayerTarget)
        {
            ConfigManagerCampaign configManager = campaign.getCampaignConfigManager();
            String currentGroundSetting = configManager.getStringConfigParam(ConfigItemKeys.SimpleConfigGroundKey);
            if (currentGroundSetting.equals(ConfigSimple.CONFIG_LEVEL_HIGH))
            {
                return GroundUnitSize.GROUND_UNIT_SIZE_HIGH;
            }
            else if (currentGroundSetting.equals(ConfigSimple.CONFIG_LEVEL_MED))
            {
                return GroundUnitSize.GROUND_UNIT_SIZE_MEDIUM;
            }
            else
            {
                return GroundUnitSize.GROUND_UNIT_SIZE_LOW;
            }
        }
        else
        {
            return GroundUnitSize.GROUND_UNIT_SIZE_TINY;
        }
    }
}
