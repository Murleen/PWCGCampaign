package pwcg.campaign.io.json;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import pwcg.campaign.SkirmishProfile;
import pwcg.campaign.SkirmishProfileType;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.context.PWCGProduct;
import pwcg.core.exception.PWCGException;

@RunWith(MockitoJUnitRunner.class)
public class SkirmishProfileIOJsonTest
{
    @Test
    public void readJsonBoSTest() throws PWCGException
    {
        PWCGContext.setProduct(PWCGProduct.BOS);
        PWCGContext.getInstance();
        Map<SkirmishProfileType, SkirmishProfile> skirmishProfiles = SkirmishProfileIOJson.readJson();
        assert (skirmishProfiles.size() > 0);
        for (SkirmishProfile skirmishProfile : skirmishProfiles.values())
        {
            assert (skirmishProfile.getSkirmishProfileElements().size() > 0);
        }
    }
}
