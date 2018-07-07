package pwcg.aar.outofmission.phase2.resupply;

import pwcg.campaign.Campaign;
import pwcg.campaign.squadron.Squadron;

public class SquadronNeedFactory
{
    public enum SquadronNeedType
    {
        PERSONNEL,
        EQUIPMENT
    };
    
    SquadronNeedType squadronNeedType = SquadronNeedType.PERSONNEL;
    
    public SquadronNeedFactory(SquadronNeedType squadronNeedType)
    {
        this.squadronNeedType = squadronNeedType;
    }
    
    public ISquadronNeed buildSquadronNeed(Campaign campaign, Squadron squadron)
    {
        if (squadronNeedType == SquadronNeedType.PERSONNEL)
        {
            return new SquadronPersonnelNeed(campaign, squadron);
        }
        else
        {
            return new SquadronEquipmentNeed(campaign, squadron);
        }
    }
}
