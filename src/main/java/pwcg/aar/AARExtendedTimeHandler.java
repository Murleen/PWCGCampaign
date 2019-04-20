package pwcg.aar;

import java.util.Date;

import pwcg.aar.data.AARContext;
import pwcg.aar.data.ExtendedTimeReason;
import pwcg.campaign.Campaign;
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

    private void stepToNewDate(Date newDate) throws PWCGException
    {
        AAROutOfMissionStepper stepper = new AAROutOfMissionStepper(campaign, aarContext);
        stepper.outOfMissionElapsedTime(newDate);
    }

}
