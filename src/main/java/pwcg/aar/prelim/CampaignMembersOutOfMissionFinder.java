package pwcg.aar.prelim;

import java.util.Map;

import pwcg.campaign.Campaign;
import pwcg.campaign.squadmember.SquadronMember;
import pwcg.campaign.squadmember.SquadronMembers;
import pwcg.core.exception.PWCGException;

public class CampaignMembersOutOfMissionFinder
{
    public static SquadronMembers getAllCampaignMembersNotInMission(Campaign campaign, SquadronMembers campaignMembersInMission) throws PWCGException
    {
        Map<Integer, SquadronMember> allCampaignMembers = campaign.getPersonnelManager().getAllNonAceCampaignMembers();  
        SquadronMembers campaignMembersOutOfMission = new SquadronMembers();
        for (SquadronMember pilot : allCampaignMembers.values())
        {
            if (!campaignMembersInMission.getSquadronMemberCollection().containsKey(pilot.getSerialNumber()))
            {
                campaignMembersOutOfMission.addToSquadronMemberCollection(pilot);
            }
        }
        
        return campaignMembersOutOfMission;
    }
    
    public static SquadronMembers getActiveCampaignMembersNotInMission(Campaign campaign, SquadronMembers campaignMembersInMission) throws PWCGException
    {
        Map<Integer, SquadronMember> allCampaignMembers = campaign.getPersonnelManager().getAllActiveNonAceCampaignMembers();  
        SquadronMembers activeCampaignMembersOutOfMission = new SquadronMembers();
        for (SquadronMember pilot : allCampaignMembers.values())
        {
            if (!campaignMembersInMission.getSquadronMemberCollection().containsKey(pilot.getSerialNumber()))
            {
                activeCampaignMembersOutOfMission.addToSquadronMemberCollection(pilot);
            }
        }
        
        return activeCampaignMembersOutOfMission;
    }
}
