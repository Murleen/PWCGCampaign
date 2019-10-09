package pwcg.campaign.group;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import pwcg.campaign.Campaign;
import pwcg.campaign.api.IAirfield;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.context.PWCGMap.FrontMapIdentifier;
import pwcg.campaign.context.PWCGProduct;
import pwcg.core.exception.PWCGException;
import pwcg.mission.flight.Flight;
import pwcg.testutils.CampaignCache;
import pwcg.testutils.SquadrontTestProfile;

public class AirfieldManagerTest
{
    Campaign campaign;
    
    @Mock
    Flight flight;

    @Before
    public void setup() throws PWCGException
    {
        PWCGContext.setProduct(PWCGProduct.ROF);
        campaign = CampaignCache.makeCampaign(SquadrontTestProfile.JASTA_11_PROFILE);
        PWCGContext.getInstance().setCampaign(campaign);
    }

	@Test
	public void airfieldValidityCheckFranceTest() throws PWCGException 
	{
        PWCGContext.getInstance().changeContext(FrontMapIdentifier.FRANCE_MAP);
        PWCGContext.getInstance().setTestUseMovingFront(false);
        AirfieldManager airfieldManager = PWCGContext.getInstance().getMapByMapId(FrontMapIdentifier.FRANCE_MAP).getAirfieldManager();
        for (IAirfield airfield : airfieldManager.getAllAirfields().values())
        {
            assert (airfield.getTakeoffLocation() != null);
            assert (airfield.getLandingLocation() != null);
        }
	}

}
