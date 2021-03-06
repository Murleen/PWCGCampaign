package pwcg.campaign.io.json;

import java.io.File;
import java.util.List;

import pwcg.campaign.Campaign;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.personnel.PersonnelReplacementsService;
import pwcg.campaign.personnel.SquadronPersonnel;
import pwcg.campaign.squadmember.SquadronMembers;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.FileUtils;

public class CampaignPersonnelIOJson 
{
    public static  void writeJson(Campaign campaign) throws PWCGException
    {
        makePersonnelDir(campaign);
        writeSquadrons(campaign);
        writeReplacements(campaign);
    }
    
    private static void makePersonnelDir(Campaign campaign)
    {
        String campaignPersonnelDir = PWCGContext.getInstance().getDirectoryManager().getPwcgCampaignsDir() + campaign.getCampaignData().getName() + "\\Personnel\\";
        File personnelDirFile = new File(campaignPersonnelDir);
        if (!personnelDirFile.exists())
        {
            personnelDirFile.mkdir();
        }
        
        String campaignReplacementsDir = PWCGContext.getInstance().getDirectoryManager().getPwcgCampaignsDir() + campaign.getCampaignData().getName() + "\\Personnel\\Replacements\\";
        File replacementDirFile = new File(campaignReplacementsDir);
        if (!replacementDirFile.exists())
        {
            replacementDirFile.mkdir();
        }
    }

    private static void writeSquadrons(Campaign campaign) throws PWCGException
    {
        String campaignPersonnelDir = PWCGContext.getInstance().getDirectoryManager().getPwcgCampaignsDir() + campaign.getCampaignData().getName() + "\\Personnel\\";
        JsonWriter<SquadronMembers> jsonWriterSquadrons = new JsonWriter<>();
        for (SquadronPersonnel squadronPersonnel : campaign.getPersonnelManager().getAllSquadronPersonnel())
        {
            SquadronMembers squadronMembersToWrite = squadronPersonnel.getSquadronMembers();
            jsonWriterSquadrons.writeAsJson(squadronMembersToWrite, campaignPersonnelDir, squadronPersonnel.getSquadron().getSquadronId() + ".json");
        }
    }

    private static void writeReplacements(Campaign campaign) throws PWCGException
    {
        String campaignReplacementDir = PWCGContext.getInstance().getDirectoryManager().getPwcgCampaignsDir() + campaign.getCampaignData().getName() + "\\Personnel\\Replacements\\";
        JsonWriter<PersonnelReplacementsService> jsonWriterReplacements = new JsonWriter<>();
        for (PersonnelReplacementsService replacements : campaign.getPersonnelManager().getAllPersonnelReplacements())
        {
            jsonWriterReplacements.writeAsJson(replacements, campaignReplacementDir, replacements.getServiceId() + ".json");
        }
    }

    public static void readJson(Campaign campaign) throws PWCGException
    {
        readSquadrons(campaign);
        readReplacements(campaign);
    }

    private static void readSquadrons(Campaign campaign) throws PWCGException
    {
        String campaignPersonnelDir = PWCGContext.getInstance().getDirectoryManager().getPwcgCampaignsDir() + campaign.getCampaignData().getName() + "\\Personnel\\";
        FileUtils fileUtils = new FileUtils();
        List<File> jsonFiles = fileUtils.getFilesWithFilter(campaignPersonnelDir, ".json");
        for (File jsonFile : jsonFiles)
        {
            JsonObjectReader<SquadronMembers> jsoReader = new JsonObjectReader<>(SquadronMembers.class);
            SquadronMembers squadronMembers = jsoReader.readJsonFile(campaignPersonnelDir, jsonFile.getName());
            
            int squadronId = Integer.valueOf(FileUtils.stripFileExtension(jsonFile.getName()));
            Squadron squadron = PWCGContext.getInstance().getSquadronManager().getSquadron(squadronId);
            SquadronPersonnel squadronPersonnel = new SquadronPersonnel(campaign, squadron);
            squadronPersonnel.setSquadronMembers(squadronMembers);
            
            campaign.getPersonnelManager().addPersonnelForSquadron(squadronPersonnel);
        }
    }

    private static void readReplacements(Campaign campaign) throws PWCGException
    {
        String campaignReplacementDir = PWCGContext.getInstance().getDirectoryManager().getPwcgCampaignsDir() + campaign.getCampaignData().getName() + "\\Personnel\\Replacements\\";
        FileUtils fileUtils = new FileUtils();
        List<File> jsonFiles = fileUtils.getFilesWithFilter(campaignReplacementDir, ".json");
        for (File jsonFile : jsonFiles)
        {
            JsonObjectReader<PersonnelReplacementsService> jsoReader = new JsonObjectReader<>(PersonnelReplacementsService.class);
            PersonnelReplacementsService replacements = jsoReader.readJsonFile(campaignReplacementDir, jsonFile.getName());
            
            int serviceId = Integer.valueOf(FileUtils.stripFileExtension(jsonFile.getName()));
            campaign.getPersonnelManager().addPersonnelReplacementsService(serviceId, replacements);
        }
    }
}
