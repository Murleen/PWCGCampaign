package pwcg.mission.flight.balloonBust;

import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.mission.flight.IFlight;
import pwcg.mission.flight.waypoint.WaypointFactory;
import pwcg.mission.flight.waypoint.end.EgressWaypointGenerator;
import pwcg.mission.flight.waypoint.missionpoint.IMissionPointSet;
import pwcg.mission.flight.waypoint.missionpoint.MissionPointRouteSet;
import pwcg.mission.mcu.McuWaypoint;

public class BalloonBustWaypointFactory
{
    private IFlight flight;
    private MissionPointRouteSet missionPointSet = new MissionPointRouteSet();

    public BalloonBustWaypointFactory(IFlight flight) throws PWCGException
    {
        this.flight = flight;
    }

    public IMissionPointSet createWaypoints(McuWaypoint ingressWaypoint) throws PWCGException
    {
        McuWaypoint balloonBustWP = createTargetWaypoints();
        missionPointSet.addWaypoint(balloonBustWP);

        McuWaypoint egressWaypoint = EgressWaypointGenerator.createEgressWaypoint(flight, ingressWaypoint.getPosition());
        missionPointSet.addWaypoint(egressWaypoint);

        return missionPointSet;
    }
	
    private McuWaypoint createTargetWaypoints() throws PWCGException  
	{
		Coordinate coord = new Coordinate();
		coord.setXPos(flight.getFlightInformation().getTargetPosition().getXPos() + 50.0);
		coord.setZPos(flight.getFlightInformation().getTargetPosition().getZPos());
        coord.setYPos(flight.getFlightInformation().getAltitude());

		McuWaypoint balloonBustWP = WaypointFactory.createBalloonBustWaypointType();
		balloonBustWP.setTriggerArea(McuWaypoint.COMBAT_AREA);
		balloonBustWP.setSpeed(flight.getFlightPlanes().getFlightCruisingSpeed());
		balloonBustWP.setPosition(coord);	
		balloonBustWP.setTargetWaypoint(true);
        return balloonBustWP;
	}
}
