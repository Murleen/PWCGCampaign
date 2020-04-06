package pwcg.mission.flight.factory;

import pwcg.core.exception.PWCGException;
import pwcg.mission.flight.FlightTypes;

public class NightFlightTypeConverter
{    
    public static FlightTypes getFlightType(FlightTypes flightType, boolean isNightMission) throws PWCGException
    {
        if (isNightMission)
        {
            if (flightType == FlightTypes.ESCORT                    ||
                flightType == FlightTypes.ARTILLERY_SPOT            ||
                flightType == FlightTypes.BALLOON_BUST              ||
                flightType == FlightTypes.BALLOON_DEFENSE           ||
                flightType == FlightTypes.OFFENSIVE                 ||
                flightType == FlightTypes.DIVE_BOMB)
            {
                return FlightTypes.GROUND_ATTACK;
            }
    
            if (flightType == FlightTypes.ANTI_SHIPPING_DIVE_BOMB)
            {
                return FlightTypes.ANTI_SHIPPING_ATTACK;
            }
        }

        return flightType;
    }
 }
