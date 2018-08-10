package pwcg.mission;

import pwcg.campaign.Campaign;
import pwcg.core.config.ConfigItemKeys;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.DateUtils;
import pwcg.core.utils.RandomNumberGenerator;
import pwcg.mission.flight.Flight;

public class MaxFighterFlightCalculator
{
    private Campaign campaign;
    private Flight playerFlight;
    
    public MaxFighterFlightCalculator (Campaign campaign, Flight playerFlight)
    {
        this.campaign = campaign;
        this.playerFlight = playerFlight;
    }
    
    public int getMaxFighterFlightsForMission() throws PWCGException
    {
        int maxFighterToKeepIfGroundCampaign = campaign.getCampaignConfigManager().getIntConfigParam(ConfigItemKeys.AiFighterFlightsForGroundCampaignMaxKey);
        int maxFighterToKeepIfFighterCampaign = campaign.getCampaignConfigManager().getIntConfigParam(ConfigItemKeys.AiFighterFlightsForFighterCampaignMaxKey);
        int numFighterFlightsToKeep = 0;
        if (campaign.isFighterCampaign() && playerFlight.isFighterFlight())
        {
            numFighterFlightsToKeep =  RandomNumberGenerator.getRandom(maxFighterToKeepIfFighterCampaign)+1;
        }
        else
        {
            numFighterFlightsToKeep = RandomNumberGenerator.getRandom(maxFighterToKeepIfGroundCampaign+1);
        }
        
        return reduceFlightsForPeriodOfLowActivity(numFighterFlightsToKeep);
    }

    private int reduceFlightsForPeriodOfLowActivity(int numFighterFlightsToKeep) throws PWCGException
    {
        if (numFighterFlightsToKeep == 0)
        {
            return numFighterFlightsToKeep;
        }
        
        if(campaign.getDate().before(DateUtils.getDateYYYYMMDD("19161001")))
        {
            int oddsNoFighterEarly = RandomNumberGenerator.getRandom(100);
            if (oddsNoFighterEarly < 70)
            {
                numFighterFlightsToKeep = 0;
            }
            else if (oddsNoFighterEarly < 90)
            {
                numFighterFlightsToKeep = numFighterFlightsToKeep - 1;
            }
        }
        return numFighterFlightsToKeep;
    }
}
