package pwcg.mission;

import pwcg.campaign.Campaign;
import pwcg.campaign.context.PWCGContextManager;
import pwcg.core.exception.PWCGException;
import pwcg.mission.flight.FlightTypes;

public class MissionGenerator
{
    Campaign campaign = null;

    public MissionGenerator(MissionHumanParticipants participatingPlayers)
    {
        campaign = PWCGContextManager.getInstance().getCampaign();
    }

    public Mission makeMission(MissionHumanParticipants participatingPlayers) throws PWCGException 
    {
        Mission mission = makeSingleMission(participatingPlayers, FlightTypes.ANY);
        return mission;
    }

    public Mission makeLoneWolfMission(MissionHumanParticipants participatingPlayers) throws PWCGException 
    {
        FlightTypes flightType = FlightTypes.LONE_WOLF;
        Mission mission = makeSingleMission(participatingPlayers, flightType);
        return mission;
    }

    private Mission makeSingleMission(MissionHumanParticipants participatingPlayers, FlightTypes flightType) throws PWCGException 
    {
        campaign.setCurrentMission(null);        
        Mission mission = new Mission();
        campaign.setCurrentMission(mission);
        mission.initialize(campaign);
        mission.generate(participatingPlayers, flightType);
        
        return mission;
    }
}
