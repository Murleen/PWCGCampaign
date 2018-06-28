package pwcg.aar.campaign.update;

import java.util.ArrayList;
import java.util.List;

import pwcg.campaign.Campaign;
import pwcg.campaign.context.PWCGContextManager;
import pwcg.campaign.context.SquadronManager;
import pwcg.campaign.personnel.InitialSquadronStaffer;
import pwcg.campaign.personnel.SquadronPersonnel;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;

public class CampaignUpdateNewSquadronStaffer
{
    private Campaign campaign;
    private List<Integer> squadronsAdded = new ArrayList<>();
    
    public CampaignUpdateNewSquadronStaffer (Campaign campaign) 
    {
        this.campaign = campaign;
    }

    public List<Integer> staffNewSquadrons() throws PWCGException
    {
        SquadronManager squadronManager = PWCGContextManager.getInstance().getSquadronManager();
        for (Squadron squadron : squadronManager.getAllActiveSquadrons(campaign.getDate()))
        {
            if (campaign.getPersonnelManager().getSquadronPersonnel(squadron.getSquadronId()) == null)
            {
                InitialSquadronStaffer squadronStaffer = new InitialSquadronStaffer(campaign, squadron);
                SquadronPersonnel squadronPersonnel = squadronStaffer.generatePersonnel();
                campaign.getPersonnelManager().addPersonnelForSquadron(squadronPersonnel);
                squadronsAdded.add(squadron.getSquadronId());
            }
        }
        
        return squadronsAdded;
    }
}
