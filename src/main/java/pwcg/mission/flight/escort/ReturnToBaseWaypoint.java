package pwcg.mission.flight.escort;

import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.core.location.Orientation;
import pwcg.mission.flight.IFlight;
import pwcg.mission.flight.waypoint.WaypointFactory;
import pwcg.mission.mcu.McuWaypoint;

public class ReturnToBaseWaypoint
{

    public static McuWaypoint createReturnToBaseWaypoint(IFlight escortFlight) throws PWCGException
    {
        Coordinate returnToBaseCoords = escortFlight.getFlightData().getFlightInformation().getDepartureAirfield().getPosition().copy();
        returnToBaseCoords.setYPos(2000.0);

        Orientation orient = new Orientation();
        orient.setyOri(escortFlight.getFlightData().getFlightInformation().getDepartureAirfield().getOrientation().getyOri());

        McuWaypoint rtbWP = WaypointFactory.createReturnToBaseWaypointType();
        rtbWP.setTriggerArea(McuWaypoint.START_AREA);
        rtbWP.setSpeed(escortFlight.getFlightData().getFlightPlanes().getFlightCruisingSpeed());
        rtbWP.setPosition(returnToBaseCoords);
        rtbWP.setOrientation(orient);
        return rtbWP;
    }

}
