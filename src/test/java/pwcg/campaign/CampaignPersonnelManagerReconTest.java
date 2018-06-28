package pwcg.campaign;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import pwcg.campaign.context.PWCGContextManager;
import pwcg.campaign.personnel.CampaignPersonnelFilter;
import pwcg.campaign.personnel.SquadronMemberFilterSpecification;
import pwcg.campaign.squadmember.SquadronMember;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;
import pwcg.testutils.CampaignCache;
import pwcg.testutils.CampaignCacheRoF;

@RunWith(MockitoJUnitRunner.class)
public class CampaignPersonnelManagerReconTest
{
	private Campaign campaign;

    @Before
    public void setup() throws PWCGException
    {
        PWCGContextManager.setRoF(true);
        campaign = CampaignCache.makeCampaign(CampaignCacheRoF.ESC_2_PROFILE);
    }

    @Test
    public void getSquadronMembersTest () throws PWCGException
    {                               
        SquadronMemberFilterSpecification filterSpecification = new SquadronMemberFilterSpecification();

        CampaignPersonnelFilter filter = new CampaignPersonnelFilter(campaign.getPersonnelManager().getSquadronPersonnel(campaign.getSquadronId()).getActiveSquadronMembers().getSquadronMembers());
        filterSpecification.setIncludePlayer(false);        
        filterSpecification.setIncludeAces(false);  
        filterSpecification.setSpecifySquadron(campaign.getSquadronId());
        
        Map<Integer, SquadronMember> squadronMembersNoPlayerNoAces = filter.getFilteredSquadronMembers(filterSpecification);
        assert (squadronMembersNoPlayerNoAces.size() == (Squadron.SQUADRON_SIZE - 1));
        
        filterSpecification.setIncludePlayer(false);        
        filterSpecification.setIncludeAces(true);        
        Map<Integer, SquadronMember> squadronMembersNoPlayerWithAces = filter.getFilteredSquadronMembers(filterSpecification);
        assert (squadronMembersNoPlayerWithAces.size() == squadronMembersNoPlayerNoAces.size());

        filterSpecification.setIncludePlayer(true);        
        filterSpecification.setIncludeAces(true);        
        Map<Integer, SquadronMember> squadronMembersWithPlayerWithAces = filter.getFilteredSquadronMembers(filterSpecification);
        assert (squadronMembersWithPlayerWithAces.size() == Squadron.SQUADRON_SIZE);
    }
}

