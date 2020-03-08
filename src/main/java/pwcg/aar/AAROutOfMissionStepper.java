package pwcg.aar;

import java.util.Date;

import pwcg.aar.campaign.update.CampaignUpdater;
import pwcg.aar.campaigndate.CampaignDaysPassed;
import pwcg.aar.data.AARContext;
import pwcg.aar.data.CampaignUpdateData;
import pwcg.aar.data.ui.UIDebriefData;
import pwcg.aar.outofmission.AARCoordinatorOutOfMission;
import pwcg.aar.tabulate.campaignupdate.AARCampaignUpdateTabulator;
import pwcg.aar.tabulate.debrief.AARDebriefTabulateCoordinator;
import pwcg.campaign.Campaign;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.DateUtils;

public class AAROutOfMissionStepper
{
	private Campaign campaign;
    private AARContext aarContext;

	public AAROutOfMissionStepper (Campaign campaign, AARContext aarContext)
	{
        this.campaign = campaign;
        this.aarContext = aarContext;
	}
	
	public void outOfMissionElapsedTime(Date targetCampaignDate) throws PWCGException
	{
	    do 
	    {
	        aarContext.resetContextForNextTimeIncrement();
	        setIncrementDate(targetCampaignDate);	        
            outOfMission();
            tabulateFlightDebriefUI();
	        updateCampaign();	        
	    }
	    while(campaign.getDate().before(targetCampaignDate));
	}

	public void oneStep() throws PWCGException
	{
        aarContext.resetContextForNextTimeIncrement();
        Date targetCampaignDate = DateUtils.advanceTimeDays(campaign.getDate(), calculateDaysForMission());
        setIncrementDate(targetCampaignDate);	        
        outOfMission();
        tabulateFlightDebriefUI();
        updateCampaign();	        
	}

    private Date setIncrementDate(Date targetCampaignDate) throws PWCGException
    {
        Date incrementalDate;
        incrementalDate = makeIncrementalCampaignDate(targetCampaignDate);
        aarContext.setNewDate(incrementalDate);
        return incrementalDate;
    }

    private Date makeIncrementalCampaignDate(Date targetCampaignDate) throws PWCGException
	{
		Date newDate;
		int daysToIncrement = calculateDaysForMission();
		newDate = DateUtils.advanceTimeDays(campaign.getDate(), daysToIncrement);

		if (newDate.after(targetCampaignDate))
		{
			newDate = targetCampaignDate;
		}
		return newDate;
	}

    private int calculateDaysForMission() throws PWCGException 
    {
        CampaignDaysPassed campaignDaysPassed = new CampaignDaysPassed(campaign);
        int daysAfterMission = campaignDaysPassed.calcDaysForMission();
        return daysAfterMission;
    }

	private void outOfMission() throws PWCGException
	{
	    AARCoordinatorOutOfMission coordinatorOutOfMission = new AARCoordinatorOutOfMission(campaign, aarContext);
	    coordinatorOutOfMission.coordinateOutOfMissionAAR();
	}
    
    private void tabulateFlightDebriefUI() throws PWCGException 
    {
        AARDebriefTabulateCoordinator uiDebriefTabulator = new AARDebriefTabulateCoordinator(campaign, aarContext);
        UIDebriefData uiDebriefDataForTimeStep = uiDebriefTabulator.tabulateForDebriefUI();
        aarContext.mergeUiDebriefData(uiDebriefDataForTimeStep);
    }

    private void updateCampaign() throws PWCGException
    {
        AARCampaignUpdateTabulator campaignUpdateTabulator = new AARCampaignUpdateTabulator(campaign, aarContext);
        CampaignUpdateData campaignUpdateData = campaignUpdateTabulator.tabulateAARForCampaignUpdate();
        aarContext.setCampaignUpdateData(campaignUpdateData);

        CampaignUpdater campaignUpdater = new CampaignUpdater(campaign, aarContext);
        campaignUpdater.updateCampaign();
    }
}
