package pwcg.mission.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pwcg.campaign.group.airfield.Airfield;
import pwcg.core.exception.PWCGException;
import pwcg.mission.Mission;
import pwcg.mission.flight.IFlight;

public class MissionFlightValidator
{
    
    public static void validateMission(Mission mission) throws PWCGException
    {
        validateSquadronsUsedOnlyOnce(mission);
        validateAirfieldUsedOnlyOnce(mission);
    }

    private static void validateSquadronsUsedOnlyOnce(Mission mission)
    {
        Set<Integer> squadronsUsedInMission = new HashSet<>();
        for (IFlight flight: mission.getMissionFlights().getAllAerialFlights())
        {
            assert (!squadronsUsedInMission.contains(flight.getSquadron().getSquadronId()));
            squadronsUsedInMission.add(flight.getSquadron().getSquadronId());
        }
    }

    private static void validateAirfieldUsedOnlyOnce(Mission mission) throws PWCGException
    {
        Map<String, Airfield> includedAirfields = new HashMap<>();
        for (IFlight flight : mission.getMissionFlights().getAllAerialFlights())
        {
            Airfield airfield = flight.getSquadron().determineCurrentAirfieldAnyMap(mission.getCampaign().getDate());
            assert(!includedAirfields.containsKey(airfield.getName()));
            includedAirfields.put(airfield.getName(), airfield);
        }
    }
}