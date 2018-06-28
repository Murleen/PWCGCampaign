package pwcg.campaign.skin;

import java.util.Map;

import org.junit.Test;

import pwcg.campaign.context.PWCGContextManager;
import pwcg.core.exception.PWCGException;

public class SkinLoaderTest
{
    @Test
    public void skinLoaderRoFTest() throws PWCGException
    {
        PWCGContextManager.setRoF(true);
        SkinLoader skinLoader = new SkinLoader();
        Map<String, SkinsForPlane> skinsForPlanes = skinLoader.loadPwcgSkins();
        
        assert(skinsForPlanes != null);
        assert (skinsForPlanes.get("fokkerd7").getAceSkins().getSkins().size() > 10);
        assert (skinsForPlanes.get("fokkerd7").getSquadronSkins().getSkins().size() > 10);
        assert (skinsForPlanes.get("fokkerd7").getConfiguredSkins().getSkins().size() > 100);
        assert (skinsForPlanes.get("fokkerd7").getDoNotUse().getSkins().size() > 50);
    }

    @Test
    public void skinLoaderBoSTest() throws PWCGException
    {
        PWCGContextManager.setRoF(false);
        SkinLoader skinLoader = new SkinLoader();
        Map<String, SkinsForPlane> skinsForPlanes = skinLoader.loadPwcgSkins();
        
        assert(skinsForPlanes != null);
    }

}
