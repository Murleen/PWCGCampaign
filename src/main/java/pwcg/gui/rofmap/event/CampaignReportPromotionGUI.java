package pwcg.gui.rofmap.event;

import pwcg.aar.ui.events.model.PromotionEvent;
import pwcg.campaign.ArmedService;
import pwcg.campaign.Campaign;
import pwcg.campaign.api.ICountry;
import pwcg.campaign.context.PWCGContextManager;
import pwcg.campaign.factory.CountryFactory;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.DateUtils;

public class CampaignReportPromotionGUI extends CampaignDocumentGUI
{
	private static final long serialVersionUID = 1L;
	private PromotionEvent promotionEvent = null;

	public CampaignReportPromotionGUI(PromotionEvent promotionEvent)
	{
		super();
		
		this.promotionEvent = promotionEvent;
		
		makePanel();		
	}

    protected String getHeaderText() throws PWCGException
    {
        // Promotion text
        Campaign campaign = PWCGContextManager.getInstance().getCampaign();
        String promotionHeaerText = promotionEvent.getPilot().determineService(campaign.getDate()).getName() + "\n\n";

        promotionHeaerText += "To all who shall see these presents, greeting: \n\n";
        
        return promotionHeaerText;
    }

    protected String getBodyText() throws PWCGException
	{
        Campaign campaign = PWCGContextManager.getInstance().getCampaign();
        
        String promotionText = "Know ye that reposing special trust and confidence in the fidelity and abilities of ";
        promotionText += "NAME I do hereby appoint him RANK of the SERVICE of NATION, ";
        promotionText += "to rank as such from DATE.  He is carefully and diligently to discharge the duty of ";
        promotionText += "RANK by doing and performing all manner of things threrunto belonging.  And I do ";
        promotionText += "strictly require all Soldiers under his command to be obedient to his orders as RANK ";
        promotionText += "as he shall receive from his Superior Officers set over him, according to the rules ";
        promotionText += "and discipline of war. ";

        promotionText += "\n\nGiven under the hand of OFFICER on DATE";
        
        promotionText = promotionText.replaceAll("NAME", promotionEvent.getOldRank() + " " + promotionEvent.getPilot().getName());
        promotionText = promotionText.replaceAll("RANK", promotionEvent.getNewRank());
        promotionText = promotionText.replaceAll("DATE", DateUtils.getDateStringPretty(campaign.getDate()));
        
        ArmedService service = promotionEvent.getPilot().determineService(campaign.getDate());
        
        promotionText = promotionText.replaceAll("SERVICE", service.getName());
        ICountry country = CountryFactory.makeCountryByCountry(promotionEvent.getPilot().getCountry());
        promotionText = promotionText.replaceAll("NATION", country.getCountryName());

        promotionText = promotionText.replaceAll("OFFICER", promotionEvent.getPilot().determineService(campaign.getDate()).getGeneralRankForService() + " " + promotionEvent.getPromotingGeneral());

        return promotionText;
	}

    @Override
    public void finished()
    {
    }
}
