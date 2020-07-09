package pwcg.campaign;

import pwcg.campaign.context.PWCGContext;
import pwcg.core.exception.PWCGException;

public class CampaignFixer
{
    public static void fixCampaign(Campaign campaign) throws PWCGException
    {
        CampaignCoopConverter converter = new CampaignCoopConverter(campaign);
        converter.convertToV8Coop();

        CampaignCleaner cleaner = new CampaignCleaner(campaign);
        cleaner.cleanDataFiles();

        P47Adder p47Adder = new P47Adder(campaign);
        p47Adder.addP47D22();
        
        mergeAddedAces(campaign);
    }
    
    private static void mergeAddedAces(Campaign campaign) throws PWCGException
    {
        CampaignAces aces =  PWCGContext.getInstance().getAceManager().loadFromHistoricalAces(campaign.getDate());
        campaign.getPersonnelManager().getCampaignAces().mergeAddedAces(aces);
    }
}
