package pwcg.aar.inmission.prelim;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import pwcg.aar.prelim.CampaignMembersInMissionFinder;
import pwcg.aar.prelim.PwcgMissionData;
import pwcg.campaign.Campaign;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.context.PWCGProduct;
import pwcg.campaign.squadmember.SerialNumber;
import pwcg.campaign.squadmember.SquadronMember;
import pwcg.campaign.squadmember.SquadronMembers;
import pwcg.core.exception.PWCGException;
import pwcg.mission.data.PwcgGeneratedMissionPlaneData;
import pwcg.testutils.CampaignCache;
import pwcg.testutils.SquadronTestProfile;

@RunWith(MockitoJUnitRunner.class)
public class CampaignMembersInMissionTest
{    
    @Mock
    private PwcgMissionData pwcgMissionData;
    
    private Campaign campaign;
    private Map<Integer, PwcgGeneratedMissionPlaneData> missionPlanes  = new HashMap<>();

    @Before
    public void setup() throws PWCGException
    {
        PWCGContext.setProduct(PWCGProduct.BOS);
        campaign = CampaignCache.makeCampaign(SquadronTestProfile.REGIMENT_503_PROFILE);
    }


    @Test
    public void testAceRetrieval() throws PWCGException
    {
        missionPlanes.clear();
        for (int i = 0; i < 50; ++i)
        {
            PwcgGeneratedMissionPlaneData planeData = new PwcgGeneratedMissionPlaneData();
            planeData.setPilotSerialNumber(SerialNumber.AI_STARTING_SERIAL_NUMBER + (i * 2) + 1);
            missionPlanes.put(planeData.getPilotSerialNumber(), planeData);
        }
        
        Mockito.when(pwcgMissionData.getMissionPlanes()).thenReturn(missionPlanes);
        
        CampaignMembersInMissionFinder campaignMembersInMissionHandler = new CampaignMembersInMissionFinder();
        SquadronMembers squadronMembersInMission = campaignMembersInMissionHandler.determineCampaignMembersInMission(campaign, pwcgMissionData);

        assert(squadronMembersInMission.getActiveCount(campaign.getDate()) == 50);
        
        for (SquadronMember squadronMember : squadronMembersInMission.getSquadronMemberCollection().values())
        {
            assert((squadronMember.getSerialNumber() % 2) == 1);
            assert(squadronMember.getSerialNumber() > SerialNumber.AI_STARTING_SERIAL_NUMBER);
            assert(squadronMember.getSerialNumber() < SerialNumber.AI_STARTING_SERIAL_NUMBER + 102);
        }
    }

}
