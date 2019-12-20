package pwcg.mission.flight.divebomb;

import java.util.ArrayList;
import java.util.List;

import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.mission.flight.Flight;
import pwcg.mission.flight.waypoint.approach.ApproachWaypointGenerator;
import pwcg.mission.flight.waypoint.attack.GroundAttackWaypointHelper;
import pwcg.mission.flight.waypoint.egress.EgressWaypointGenerator;
import pwcg.mission.flight.waypoint.ingress.IIngressWaypoint;
import pwcg.mission.flight.waypoint.ingress.IngressWaypointNearFront;
import pwcg.mission.flight.waypoint.initial.InitialWaypointGenerator;
import pwcg.mission.mcu.McuWaypoint;

public class DiveBombingWaypoints
{
    private Flight flight;
    private List<McuWaypoint> waypoints = new ArrayList<McuWaypoint>();

    public DiveBombingWaypoints(Flight flight) throws PWCGException
    {
        this.flight = flight;
    }

    public List<McuWaypoint> createWaypoints() throws PWCGException
    {
        InitialWaypointGenerator climbWaypointGenerator = new InitialWaypointGenerator(flight);
        List<McuWaypoint> initialWPs = climbWaypointGenerator.createInitialFlightWaypoints();
        waypoints.addAll(initialWPs);

        McuWaypoint ingressWaypoint = createIngressWaypoint(flight);
        waypoints.add(ingressWaypoint);
        
        List<McuWaypoint> targetWaypoints = createTargetWaypoints(ingressWaypoint.getPosition());
        waypoints.addAll(targetWaypoints);

        
        McuWaypoint egressWaypoint = EgressWaypointGenerator.createEgressWaypoint(flight, ingressWaypoint.getPosition());
        waypoints.add(egressWaypoint);
        
        McuWaypoint approachWaypoint = ApproachWaypointGenerator.createApproachWaypoint(flight);
        waypoints.add(approachWaypoint);

        return waypoints;
    }

    private McuWaypoint createIngressWaypoint(Flight flight) throws PWCGException  
    {
        IIngressWaypoint ingressWaypointGenerator = new IngressWaypointNearFront(flight);
        McuWaypoint ingressWaypoint = ingressWaypointGenerator.createIngressWaypoint();
        return ingressWaypoint;
    }

    private List<McuWaypoint> createTargetWaypoints(Coordinate ingressPosition) throws PWCGException
    {
        GroundAttackWaypointHelper groundAttackWaypointHelper = new GroundAttackWaypointHelper(flight, ingressPosition, flight.getFlightAltitude());        
        List<McuWaypoint> targetWaypoints = groundAttackWaypointHelper.createTargetWaypoints();
        return targetWaypoints;
    }
}
