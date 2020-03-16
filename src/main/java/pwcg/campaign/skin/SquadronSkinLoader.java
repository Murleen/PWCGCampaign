package pwcg.campaign.skin;

import java.util.Map;

import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.context.SquadronManager;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.PWCGLogger;
import pwcg.core.utils.PWCGLogger.LogLevel;

public class SquadronSkinLoader
{
    private Map<String, SkinsForPlane> skinsForPlanes;
    
    public SquadronSkinLoader (Map<String, SkinsForPlane> skinsForPlanes)
    {
        this.skinsForPlanes = skinsForPlanes;
    }

    public void loadSquadronSkins() throws PWCGException
    {
        SquadronManager squadronManager = PWCGContext.getInstance().getSquadronManager();
        for (Squadron squadron : squadronManager.getAllSquadrons())
        {
            registerSquadronSkins(squadron);
        }
        
    }

    private void registerSquadronSkins(Squadron squadron)
    {
        for (Skin squadronSkin : squadron.getSkins())
        {
            SkinsForPlane skinsForPlane = skinsForPlanes.get(squadronSkin.getPlane());
            if (skinsForPlane != null)
            {
                skinsForPlane.addSquadronSkin(squadronSkin);
            }
            else
            {
                PWCGLogger.log(LogLevel.ERROR, "Invalid plane for squadron skin <" + squadron.getSquadronId() 
                + "><"  + squadronSkin.getSkinName() + ">" );
            }
        }
    }
}
