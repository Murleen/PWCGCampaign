package pwcg.campaign.skin;

import java.util.Map;

import pwcg.campaign.context.AceManager;
import pwcg.campaign.context.PWCGContextManager;
import pwcg.campaign.squadmember.HistoricalAce;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.Logger;
import pwcg.core.utils.Logger.LogLevel;

public class AceSkinLoader
{
    private Map<String, SkinsForPlane> skinsForPlanes;
    
    public AceSkinLoader (Map<String, SkinsForPlane> skinsForPlanes)
    {
        this.skinsForPlanes = skinsForPlanes;
    }

    public void loadHistoricalAceSkins() throws PWCGException
    {
        AceManager aceManager = PWCGContextManager.getInstance().getAceManager();
        for (HistoricalAce ace : aceManager.getHistoricalAces())
        {
            registerHistoricalAceSkins(ace);
        }
        
    }

    private void registerHistoricalAceSkins(HistoricalAce ace)
    {
        for (Skin aceSkin : ace.getSkins())
        {
            SkinsForPlane skinsForPlane = skinsForPlanes.get(aceSkin.getPlane());
            if (skinsForPlane != null)
            {
                skinsForPlane.addAceSkin(aceSkin);
            }
            else
            {
                Logger.log(LogLevel.ERROR, "Invalid plane for HistoricalAce skin <" + ace.getSerialNumber() + "><"  + aceSkin.getSkinName() + ">" );
            }
        }
    }
}
