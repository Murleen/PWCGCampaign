package pwcg.aar.tabulate.debrief;

import java.util.ArrayList;
import java.util.List;

import pwcg.aar.data.AARContext;
import pwcg.aar.ui.display.model.AARPromotionPanelData;
import pwcg.aar.ui.events.PromotionEventGenerator;
import pwcg.aar.ui.events.model.PromotionEvent;
import pwcg.campaign.Campaign;
import pwcg.core.exception.PWCGException;

public class PromotionPanelEventTabulator
{
    private Campaign campaign;
    private AARContext aarContext;

    private AARPromotionPanelData promotionPanelData = new AARPromotionPanelData();

    public PromotionPanelEventTabulator (Campaign campaign, AARContext aarContext)
    {
        this.campaign = campaign;
        this.aarContext = aarContext;
    }
        
    public AARPromotionPanelData tabulateForAARPromotionPanel() throws PWCGException
    {
        PromotionEventGenerator promotionEventGenerator = new PromotionEventGenerator(campaign);
        List<PromotionEvent> promotionEventsForCampaignMembersOutOfMission = promotionEventGenerator.createPilotPromotionEvents(aarContext.getReconciledOutOfMissionData().getPersonnelAwards().getPromotions());
        List<PromotionEvent> promotionEventsForCampaignMemberInMission = promotionEventGenerator.createPilotPromotionEvents(aarContext.getReconciledInMissionData().getPersonnelAwards().getPromotions());

        List<PromotionEvent> promotionEventsForCampaignMembers = new ArrayList<>();
        promotionEventsForCampaignMembers.addAll(promotionEventsForCampaignMembersOutOfMission);
        promotionEventsForCampaignMembers.addAll(promotionEventsForCampaignMemberInMission);
        
        promotionPanelData.setPromotionEventsDuringElapsedTime(promotionEventsForCampaignMembers);
                
        return promotionPanelData;
    }
}
