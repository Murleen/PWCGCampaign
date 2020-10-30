package pwcg.mission;

import java.util.Arrays;
import java.util.List;

import pwcg.campaign.Campaign;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.CoordinateBox;
import pwcg.mission.flight.FlightTypes;

public class MissionGenerator
{
    private Campaign campaign = null;

    public MissionGenerator(Campaign campaign)
    {
        this.campaign = campaign;
    }

    public Mission makeMission(MissionHumanParticipants participatingPlayers) throws PWCGException 
    {
        MissionProfile missionProfile = generateProfile(participatingPlayers);
        
        List<FlightTypes> playerFlightTypes = Arrays.asList(FlightTypes.OFFENSIVE);
        //List<FlightTypes> playerFlightTypes = PlayerFlightTypeBuilder.finalizePlayerFlightTypes(campaign, participatingPlayers, missionProfile);
        Mission mission = buildMission(participatingPlayers, playerFlightTypes, missionProfile);
        return mission;
    }

    public Mission makeLoneWolfMission(MissionHumanParticipants participatingPlayers) throws PWCGException 
    {
        List<FlightTypes> playerFlightTypes = Arrays.asList(FlightTypes.LONE_WOLF);
        Mission mission = buildMission(participatingPlayers, playerFlightTypes, MissionProfile.DAY_TACTICAL_MISSION);
        return mission;
    }

    public Mission makeTestSingleMissionFromFlightType(MissionHumanParticipants participatingPlayers, FlightTypes playerFlightType, MissionProfile missionProfile) throws PWCGException 
    {
        List<FlightTypes> playerFlightTypes = Arrays.asList(playerFlightType);
        Mission mission = buildMission(participatingPlayers, playerFlightTypes, missionProfile);
        return mission;
    }

    public Mission makeTestCoopMissionFromFlightType(MissionHumanParticipants participatingPlayers, List<FlightTypes> playerFlightTypes, MissionProfile missionProfile) throws PWCGException 
    {
        Mission mission = buildMission(participatingPlayers, playerFlightTypes, missionProfile);
        return mission;
    }

    private MissionProfile generateProfile(MissionHumanParticipants participatingPlayers) throws PWCGException 
    {
        MissionProfileGenerator missionProfileGenerator = new MissionProfileGenerator(campaign, participatingPlayers);
        MissionProfile missionProfile = missionProfileGenerator.generateMissionProfile();
        return missionProfile;
    }

    private Mission buildMission(MissionHumanParticipants participatingPlayers, List<FlightTypes> playerFlightTypes, MissionProfile missionProfile) throws PWCGException 
    {
        campaign.setCurrentMission(null);        
        CoordinateBox missionBorders = buildMissionBorders(missionProfile, participatingPlayers);
        Mission mission = new Mission(campaign, missionProfile, participatingPlayers, missionBorders);
        campaign.setCurrentMission(mission);
        mission.generate(playerFlightTypes);
        
        return mission;
    }

    private CoordinateBox buildMissionBorders(MissionProfile missionProfile, MissionHumanParticipants participatingPlayers) throws PWCGException
    {
        MissionBorderBuilder missionBorderBuilder = new MissionBorderBuilder(campaign, participatingPlayers);
        CoordinateBox missionBorders = missionBorderBuilder.buildCoordinateBox();
        return missionBorders;
    }
}
