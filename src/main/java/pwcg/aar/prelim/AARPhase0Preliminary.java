package pwcg.aar.prelim;

import java.util.List;

import pwcg.aar.inmission.phase1.parse.AARMissionFileLogResultMatcher;
import pwcg.campaign.Campaign;
import pwcg.campaign.squadmember.SquadronMembers;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.DirectoryReader;

public class AARPhase0Preliminary
{
    private Campaign campaign;
    private AARPreliminaryData aarPreliminarytData = new AARPreliminaryData();

    public AARPhase0Preliminary(Campaign campaign)
    {
        this.campaign = campaign;
    }
    
    public AARPreliminaryData createAARPreliminaryData() throws PWCGException
    {        
        readPwcgMissionData();        
        tabulateClaimPanelData();   
        determineActiveCampaignMembersInMission();
        determineActiveCampaignMembersNotInMission();
        
        return aarPreliminarytData;
    }

    private void readPwcgMissionData() throws PWCGException
    {        
    	AARPwcgMissionFinder pwcgMissionFinder = new AARPwcgMissionFinder(campaign);
    	List<PwcgMissionData> sortedPwcgMissionDataForCampaign = pwcgMissionFinder.getSortedPwcgMissionsForCampaign();
    
    	DirectoryReader directoryReader = new DirectoryReader();
    	AARLogSetFinder logSetFinder = new AARLogSetFinder(directoryReader);
    	List<String> sortedLogSets = logSetFinder.getSortedLogFileSets();
    	
    	AARMostRecentLogSetFinder mostRecentLogSetFinder = createAARMostRecentLogSetFinder(campaign);
        mostRecentLogSetFinder.getMostRecentAARLogFileMissionDataSetForCampaign(sortedLogSets, sortedPwcgMissionDataForCampaign);     
                
        if (mostRecentLogSetFinder.getAarLogFileMissionFile() == null)
        {
        	throw new PWCGException ("Failed to find most recent log file data set");
        }

        aarPreliminarytData.setMissionLogFileSet(mostRecentLogSetFinder.getAarLogFileMissionFile());
        aarPreliminarytData.setPwcgMissionData(mostRecentLogSetFinder.getPwcgMissionData());
    }
    
    private void determineActiveCampaignMembersInMission() throws PWCGException
    {
        CampaignMembersInMissionFinder campaignMembersInMissionHandler = new CampaignMembersInMissionFinder();
        SquadronMembers campaignMembersInMission = campaignMembersInMissionHandler.determineCampaignMembersInMission(campaign, aarPreliminarytData.getPwcgMissionData());
        aarPreliminarytData.setCampaignMembersInMission(campaignMembersInMission);
    }
    
    private void determineActiveCampaignMembersNotInMission() throws PWCGException
    {
        CampaignMembersOutOfMissionFinder campaignMembersOutOfMissionHandler = new CampaignMembersOutOfMissionFinder();        
        SquadronMembers campaignMembersOutOfMission = campaignMembersOutOfMissionHandler.getCampaignMembersNotInMission(campaign, aarPreliminarytData.getCampaignMembersInMission());
        aarPreliminarytData.setCampaignMembersOutOfMission(campaignMembersOutOfMission);
    }

    private AARMostRecentLogSetFinder createAARMostRecentLogSetFinder(Campaign campaign) throws PWCGException
    {        
        AARHeaderParser aarHeaderParser = new AARHeaderParser();        
    	AARMissionFileLogResultMatcher matcher = new AARMissionFileLogResultMatcher(campaign, aarHeaderParser);
        return new AARMostRecentLogSetFinder(matcher);
    }

    private void tabulateClaimPanelData() throws PWCGException
    {
        AARClaimPanelEventTabulator claimPanelEventTabulator = new AARClaimPanelEventTabulator(campaign, aarPreliminarytData);
        AARClaimPanelData claimPanelData = claimPanelEventTabulator.tabulateForAARClaimPanel();
        aarPreliminarytData.setClaimPanelData(claimPanelData);
    }

}
