package pwcg.aar;

import java.util.Date;

import pwcg.aar.campaigndate.AARTimePassedAfterWounds;
import pwcg.aar.data.AARContext;
import pwcg.aar.data.ExtendedTimeReason;
import pwcg.aar.inmission.phase2.logeval.missionresultentity.LogPilot;
import pwcg.campaign.Campaign;
import pwcg.campaign.context.PWCGContextManager;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.DateUtils;

public class AARExtendedTimeHandler
{
    private Campaign campaign;
    private AARContext aarContext;
	
	public AARExtendedTimeHandler(Campaign campaign, AARContext aarContext)
	{
        this.campaign = campaign;
        this.aarContext = aarContext;
	}
	
	public void timePassedForWounds(LogPilot playerCrewMember) throws PWCGException
	{
        AARTimePassedAfterWounds newDateCalculator = new AARTimePassedAfterWounds(campaign);
        Date woundedDate = newDateCalculator.calcDateOfRecovery(playerCrewMember);
        playerCrewMember.setDateOfReturn(woundedDate);
	}

	public void advanceTimeForPlayerRecovery(Date woundedDate) throws PWCGException
    {
        aarContext.setReasonForExtendedTime(ExtendedTimeReason.WOUND);
        stepToNewDate(woundedDate);
        campaign.setCurrentMission(null);
    }

    public void timePassedForLeave(int timePassedDays) throws PWCGException
    {
        Date dateAfterLeave = DateUtils.advanceTimeDays(campaign.getDate(), timePassedDays);
        stepToNewDate(dateAfterLeave);
        aarContext.setReasonForExtendedTime(ExtendedTimeReason.LEAVE);
        campaign.setCurrentMission(null);
    }

    public void timePassedForTransfer(int timePassedDays) throws PWCGException
    {
        Date dateAfterLeave = DateUtils.advanceTimeDays(campaign.getDate(), timePassedDays);
        stepToNewDate(dateAfterLeave);
        aarContext.setReasonForExtendedTime(ExtendedTimeReason.TRANSFER);
        campaign.setCurrentMission(null);
    }

    public void timePassedForSquadronNotViable() throws PWCGException
    {
        Squadron squadron = PWCGContextManager.getInstance().getSquadronManager().getSquadron(campaign.getSquadronId());
        while (!squadron.isSquadronViable(campaign))
        {
            aarContext.setReasonForExtendedTime(ExtendedTimeReason.NO_PILOTS);
            AAROutOfMissionStepper stepper = new AAROutOfMissionStepper(campaign, aarContext);
            stepper.oneStep();
            campaign.setCurrentMission(null);
        }
    }

    private void stepToNewDate(Date newDate) throws PWCGException
    {
        AAROutOfMissionStepper stepper = new AAROutOfMissionStepper(campaign, aarContext);
        stepper.outOfMissionElapsedTime(newDate);
    }

}
