package pwcg.mission.flight.balloondefense;

import pwcg.core.exception.PWCGException;
import pwcg.mission.flight.Flight;
import pwcg.mission.flight.FlightPayloadBuilder;
import pwcg.mission.flight.IFlight;
import pwcg.mission.flight.IFlightInformation;
import pwcg.mission.flight.initialposition.FlightPositionSetter;
import pwcg.mission.flight.waypoint.begin.AirStartWaypointFactory.AirStartPattern;
import pwcg.mission.flight.waypoint.begin.FlightWaypointGroupFactory;
import pwcg.mission.flight.waypoint.begin.IngressWaypointFactory;
import pwcg.mission.flight.waypoint.begin.IngressWaypointFactory.IngressWaypointPattern;
import pwcg.mission.flight.waypoint.missionpoint.IMissionPointSet;
import pwcg.mission.flight.waypoint.missionpoint.MissionPointSetType;
import pwcg.mission.ground.org.IGroundUnitCollection;
import pwcg.mission.mcu.McuWaypoint;

public class BalloonDefenseFlight extends Flight implements IFlight
{
	protected IGroundUnitCollection balloonUnit = null;

    public BalloonDefenseFlight(IFlightInformation flightInformation, IGroundUnitCollection balloonUnit)
    {
        super (flightInformation);
        this.balloonUnit = balloonUnit;     
    }

    public void createFlight() throws PWCGException
    {
        flightData.initialize(this);
        createWaypoints();
        FlightPositionSetter.setFlightInitialPosition(this);
        setFlightPayload();
    }
    
    private void createWaypoints() throws PWCGException
    {
        McuWaypoint ingressWaypoint = IngressWaypointFactory.createIngressWaypoint(IngressWaypointPattern.INGRESS_NEAR_TARGET, this);

        IMissionPointSet flightBegin = FlightWaypointGroupFactory.createFlightBegin(this, AirStartPattern.AIR_START_NEAR_INGRESS, ingressWaypoint);
        flightData.getWaypointPackage().addMissionPointSet(MissionPointSetType.MISSION_POINT_SET_FLIGHT_BEGIN, flightBegin);

        BalloonDefenseWaypointFactory missionWaypointFactory = new BalloonDefenseWaypointFactory(this);
        IMissionPointSet missionWaypoints = missionWaypointFactory.createWaypoints(ingressWaypoint);
        flightData.getWaypointPackage().addMissionPointSet(MissionPointSetType.MISSION_POINT_SET_MISSION_COVER, missionWaypoints);
        
        IMissionPointSet flightEnd = FlightWaypointGroupFactory.createFlightEndAtHomeField(this);
        flightData.getWaypointPackage().addMissionPointSet(MissionPointSetType.MISSION_POINT_SET_FLIGHT_END, flightEnd);        
    }

    private void setFlightPayload() throws PWCGException
    {
        FlightPayloadBuilder flightPayloadHelper = new FlightPayloadBuilder(this);
        flightPayloadHelper.setFlightPayload();
    }

    @Override
    public void finalize() throws PWCGException
    {
        flightData.getWaypointPackage().finalize();
    }
}	

