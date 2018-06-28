package pwcg.aar.campaign.update;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import pwcg.aar.AARTestSetup;
import pwcg.aar.campaign.update.CampaignAceUpdater;
import pwcg.campaign.Campaign;
import pwcg.campaign.squadmember.Ace;
import pwcg.campaign.squadmember.Victory;
import pwcg.core.exception.PWCGException;
import pwcg.testutils.CampaignCache;
import pwcg.testutils.CampaignCacheRoF;
import pwcg.testutils.VictoryMaker;

@RunWith(MockitoJUnitRunner.class)
public class CampaignAceUpdaterTest extends AARTestSetup
{
    @Before
    public void setup() throws PWCGException
    {
        setupAARMocks();
    }
    
    @Test
    public void testInSquadronAceUpdate() throws PWCGException 
    {
        Campaign campaign = CampaignCache.makeCampaign(CampaignCacheRoF.ESC_103_PROFILE);

        Map<Integer, List<Victory>> aceVictories = new HashMap<>();
        List<Victory> victories = VictoryMaker.makeMultipleAlliedVictories(1, campaign.getDate());
        victories.get(0).getVictor().setPilotName("Georges Guynemer");
        aceVictories.put(101064, victories);
        
        Ace aceInCampaign = campaign.getPersonnelManager().getCampaignAces().retrieveAceBySerialNumber(101064);   
        int aceVictoriesBefore = aceInCampaign.getVictories().size();
        
        CampaignAceUpdater updater = new CampaignAceUpdater(campaign, aceVictories);
        updater.updatesCampaignAces();

        int aceVictoriesAfter = aceInCampaign.getVictories().size();
        
        assertTrue (aceVictoriesAfter == (aceVictoriesBefore+1));
    }
    
    
    @Test
    public void testOutOfSquadronAceUpdate() throws PWCGException 
    {
        Campaign campaign = CampaignCache.makeCampaign(CampaignCacheRoF.ESC_103_PROFILE);

        Map<Integer, List<Victory>> aceVictories = new HashMap<>();
        List<Victory> victories = VictoryMaker.makeMultipleCentralVictories(1, campaign.getDate());
        victories.get(0).getVictor().setPilotName("Paul Baumer");
        aceVictories.put(101143, victories);
        
        Ace aceInCampaign = campaign.getPersonnelManager().getCampaignAces().retrieveAceBySerialNumber(101143);   
        int aceVictoriesBefore = aceInCampaign.getVictories().size();
        
        CampaignAceUpdater updater = new CampaignAceUpdater(campaign, aceVictories);
        updater.updatesCampaignAces();

        int aceVictoriesAfter = aceInCampaign.getVictories().size();
        
        assertTrue (aceVictoriesAfter == (aceVictoriesBefore+1));
    }
}
