package pwcg.mission;

import java.util.ArrayList;
import java.util.List;

import pwcg.campaign.Campaign;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.factory.PWCGFlightTypeAbstractFactory;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;
import pwcg.mission.flight.FlightTypes;
import pwcg.mission.flight.factory.IFlightTypeFactory;
import pwcg.mission.flight.factory.NightFlightTypeConverter;

public class PlayerFlightTypeBuilder
{
    public static List<FlightTypes> finalizePlayerFlightType(Campaign campaign, MissionHumanParticipants participatingPlayers, MissionProfile missionProfile) throws PWCGException
    {
        List<FlightTypes> playerFlightTypes = new ArrayList<>();
        for (Integer squadronId : participatingPlayers.getParticipatingSquadronIds())
        {
            Squadron playerSquadron = PWCGContext.getInstance().getSquadronManager().getSquadron(squadronId);
            FlightTypes requestedFlightType = determinePlayerFlightType(campaign, playerSquadron, participatingPlayers, missionProfile.isNightMission());
            playerFlightTypes.add(requestedFlightType);
        }
        return playerFlightTypes;
    }

    private static FlightTypes determinePlayerFlightType(Campaign campaign, Squadron squadron, MissionHumanParticipants participatingPlayers, boolean isNightMission) throws PWCGException
    {
        IFlightTypeFactory flightTypeFactory = PWCGFlightTypeAbstractFactory.createFlightFactory(campaign);
        boolean isPlayerFlight = true;
        FlightTypes flightType = flightTypeFactory.getFlightType(squadron, isPlayerFlight);
        flightType = NightFlightTypeConverter.getFlightType(flightType, isNightMission);
        return flightType;
    }
}
