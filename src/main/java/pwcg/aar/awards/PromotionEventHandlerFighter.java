package pwcg.aar.awards;

import pwcg.campaign.ArmedService;
import pwcg.campaign.Campaign;
import pwcg.campaign.api.IRankHelper;
import pwcg.campaign.factory.RankFactory;
import pwcg.campaign.squadmember.SquadronMember;
import pwcg.core.exception.PWCGException;

public class PromotionEventHandlerFighter
{
	public static int PilotRankMedVictories = 1;          // Number of victories to advance pilot Rank from low to medium
    public static int PilotRankHighMinVictories = 3;      // Number of victories to advance pilot Rank from medium to high
    public static int PilotRankExecVictories = 5;         // Number of victories to advance pilot Rank from high to exec
    public static int PilotRankCommandVictories = 10;     // Number of victories to advance pilot Rank from exec to command

    public static int PilotRankMedMinMissions = 10;       // Number of missions to advance pilot Rank from low to medium
    public static int PilotRankHighMinMissions = 25;      // Number of victories to advance pilot Rank from medium to high
    public static int PilotRankExecMinMissions = 40;      // Number of victories to advance pilot Rank from medium to exec
    public static int PilotRankCommandMinMissions = 60;   // Number of victories to advance pilot Rank from exec to command


    public String determineScoutPromotion(Campaign campaign, SquadronMember pilot) throws PWCGException 
    {
        String promotion = PromotionEventHandler.NO_PROMOTION;
        
        ArmedService service = pilot.determineService(campaign.getDate());
        int numPilotVictories = pilot.getSquadronMemberVictories().getAirToAirVictories();
        int numMissions = pilot.getMissionFlown();        
        IRankHelper rankObj = RankFactory.createRankHelper();
        int rankPosBeforePromotion = rankObj.getRankPosByService(pilot.getRank(), service);

        if (rankPosBeforePromotion > 3)
        {
            if (numPilotVictories >= PilotRankMedVictories && numMissions >= PilotRankMedMinMissions)
            {
                promotion = rankObj.getRankByService(3, service);
            }
        }
        else if (rankPosBeforePromotion == 3)
        {
            if (numPilotVictories >= PilotRankHighMinVictories && numMissions >= PilotRankHighMinMissions)
            {
                promotion = rankObj.getRankByService(2, service);
            }
        }
        else if (rankPosBeforePromotion == 2)
        {
            if (numPilotVictories >= PilotRankExecVictories && numMissions >= PilotRankExecMinMissions)
            {
                promotion = rankObj.getRankByService(1, service);
            }
        }
        else if (rankPosBeforePromotion == 1)
        {
            if (pilot.isPlayer())
            {
                if (numPilotVictories >= PilotRankCommandVictories && numMissions >= PilotRankCommandMinMissions)
                {
                    promotion = rankObj.getRankByService(0, service);
                }
            }
        }
        
        return promotion;
    }

}
