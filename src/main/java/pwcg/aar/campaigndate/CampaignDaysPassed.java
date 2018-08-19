package pwcg.aar.campaigndate;

import java.util.Calendar;
import java.util.Date;

import pwcg.campaign.Campaign;
import pwcg.campaign.MissionSpread;
import pwcg.campaign.plane.Role;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.RandomNumberGenerator;

public class CampaignDaysPassed
{
    private Campaign campaign = null;
    
    public CampaignDaysPassed (Campaign campaign)
    {
        this.campaign = campaign;
    }

    public int calcDaysForMission() throws PWCGException 
    {
        int month = getCampaignMonth();
        MissionSpread missionSpread = new MissionSpread(campaign);
        int daysForMission = RandomNumberGenerator.getRandom(missionSpread.getMissionSpread(month));
        daysForMission = moreTimeForStrategicCampaigns(daysForMission);
        daysForMission = atLeastOneDay(daysForMission);
        
        return daysForMission;
    }

    private int getCampaignMonth()
    {
        Date date = campaign.getDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        return month;
    }

    private int moreTimeForStrategicCampaigns(int daysForMission) throws PWCGException
    {
        Role primaryRole = campaign.determineSquadron().determineSquadronPrimaryRole(campaign.getDate());
        if (primaryRole == Role.ROLE_STRAT_BOMB)
        {
            daysForMission *= 4;
        }
        
        return daysForMission;
    }

    private int atLeastOneDay(int daysForMission)
    {
        if (daysForMission < 1)
        {
            daysForMission = 1;
        }
        return daysForMission;
    }
}
