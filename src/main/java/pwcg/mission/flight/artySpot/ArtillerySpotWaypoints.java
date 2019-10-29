package pwcg.mission.flight.artySpot;

import java.util.ArrayList;
import java.util.List;

import pwcg.campaign.Campaign;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.core.utils.MathUtils;
import pwcg.core.utils.RandomNumberGenerator;
import pwcg.mission.flight.Flight;
import pwcg.mission.flight.waypoint.ApproachWaypointGenerator;
import pwcg.mission.flight.waypoint.ClimbWaypointGenerator;
import pwcg.mission.flight.waypoint.EgressWaypointGenerator;
import pwcg.mission.flight.waypoint.IIngressWaypoint;
import pwcg.mission.flight.waypoint.IngressWaypointNearFront;
import pwcg.mission.flight.waypoint.WaypointFactory;
import pwcg.mission.mcu.McuWaypoint;

public class ArtillerySpotWaypoints
{
    private Flight flight;
    private Campaign campaign;
    private List<McuWaypoint> waypoints = new ArrayList<McuWaypoint>();

    protected boolean escortedByPlayer = false;

    public ArtillerySpotWaypoints(Flight flight) throws PWCGException
    {
        this.flight = flight;
        this.campaign = flight.getCampaign();
    }

    public List<McuWaypoint> createWaypoints() throws PWCGException
    {
        if (flight.isPlayerFlight())
        {
            ClimbWaypointGenerator climbWaypointGenerator = new ClimbWaypointGenerator(campaign, flight);
            List<McuWaypoint> climbWPs = climbWaypointGenerator.createClimbWaypoints(flight.getFlightInformation().getAltitude());
            waypoints.addAll(climbWPs);
        }

        McuWaypoint ingressWaypoint = createIngressWaypoint(flight);
        waypoints.add(ingressWaypoint);
        
        createTargetWaypoints(ingressWaypoint.getPosition());
        
        McuWaypoint egressWaypoint = EgressWaypointGenerator.createEgressWaypoint(flight, ingressWaypoint.getPosition());
        waypoints.add(egressWaypoint);
        
        McuWaypoint approachWaypoint = ApproachWaypointGenerator.createApproachWaypoint(flight);
        waypoints.add(approachWaypoint);

        return waypoints;
    }
    

    protected McuWaypoint createIngressWaypoint(Flight flight) throws PWCGException  
    {
        IIngressWaypoint ingressWaypointGenerator = new IngressWaypointNearFront(flight);
        McuWaypoint ingressWaypoint = ingressWaypointGenerator.createIngressWaypoint();
        return ingressWaypoint;
    }

	protected void createTargetWaypoints(Coordinate startPosition) throws PWCGException  
	{
		McuWaypoint artillerySpotInitialWaypoint = WaypointFactory.createArtillerySpotWaypointType();

		artillerySpotInitialWaypoint.setTriggerArea(McuWaypoint.TARGET_AREA);
		artillerySpotInitialWaypoint.setSpeed(flight.getFlightCruisingSpeed());
        artillerySpotInitialWaypoint.setTargetWaypoint(true);

		Coordinate initialArtillerySpotCoord = flight.getTargetCoords();
		initialArtillerySpotCoord.setYPos(flight.getFlightAltitude());
		artillerySpotInitialWaypoint.setPosition(initialArtillerySpotCoord);	

		waypoints.add(artillerySpotInitialWaypoint);		
		
		int iterNum = 1;
		
		double angle = RandomNumberGenerator.getRandom(360);	
		
		createNextWaypoint(artillerySpotInitialWaypoint, iterNum, angle);
	}

	private void createNextWaypoint(McuWaypoint lastWP, int iterNum, double angle) throws PWCGException  
	{
		McuWaypoint artillerySpotAdditionalWaypoint = WaypointFactory.createArtillerySpotWaypointType();
		++ iterNum;

		artillerySpotAdditionalWaypoint.setTriggerArea(McuWaypoint.TARGET_AREA);
		artillerySpotAdditionalWaypoint.setSpeed(flight.getFlightCruisingSpeed());

		double distance = 2000.0;
		Coordinate nextArtillerySpotCoordinate = MathUtils.calcNextCoord(lastWP.getPosition(), angle, distance);
        nextArtillerySpotCoordinate.setYPos(flight.getFlightAltitude());
		artillerySpotAdditionalWaypoint.setPosition(nextArtillerySpotCoordinate);	
		artillerySpotAdditionalWaypoint.setTargetWaypoint(true);

		waypoints.add(artillerySpotAdditionalWaypoint);
		
		if (iterNum == 20)
		{
			return;
		}

		angle = MathUtils.adjustAngle (angle, 45);		
		
		createNextWaypoint(artillerySpotAdditionalWaypoint, iterNum, angle);
	}	
}
