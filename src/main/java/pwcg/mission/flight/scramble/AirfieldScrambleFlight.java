package pwcg.mission.flight.scramble;

import pwcg.core.exception.PWCGException;
import pwcg.mission.flight.Flight;
import pwcg.mission.flight.FlightPayloadBuilder;
import pwcg.mission.flight.IFlight;
import pwcg.mission.flight.IFlightInformation;
import pwcg.mission.flight.initialposition.FlightPositionSetter;
import pwcg.mission.flight.waypoint.begin.AirStartWaypointFactory.AirStartPattern;
import pwcg.mission.flight.waypoint.begin.IngressWaypointFactory;
import pwcg.mission.flight.waypoint.begin.IngressWaypointFactory.IngressWaypointPattern;
import pwcg.mission.flight.waypoint.missionpoint.IMissionPointSet;
import pwcg.mission.flight.waypoint.missionpoint.MissionPointSetFactory;
import pwcg.mission.mcu.McuWaypoint;

public class AirfieldScrambleFlight extends Flight implements IFlight
{
    public AirfieldScrambleFlight(IFlightInformation flightInformation)
    {
        super(flightInformation);
    }

    public void createFlight() throws PWCGException
    {
        initialize(this);
        createWaypoints();
        FlightPositionSetter.setFlightInitialPosition(this);
        setFlightPayload();
    }

    private void createWaypoints() throws PWCGException
    {
        McuWaypoint ingressWaypoint = IngressWaypointFactory.createIngressWaypoint(IngressWaypointPattern.INGRESS_AT_TARGET, this);

        IMissionPointSet flightActivate = MissionPointSetFactory.createFlightActivate(this);
        this.getWaypointPackage().addMissionPointSet(flightActivate);

        IMissionPointSet flightBegin = MissionPointSetFactory.createFlightBegin(this, flightActivate, AirStartPattern.AIR_START_NEAR_AIRFIELD, ingressWaypoint);
        this.getWaypointPackage().addMissionPointSet(flightBegin);

        ScrambleWaypointFactory missionWaypointFactory = new ScrambleWaypointFactory(this);
        IMissionPointSet missionWaypoints = missionWaypointFactory.createWaypoints(ingressWaypoint);
        this.getWaypointPackage().addMissionPointSet(missionWaypoints);
        
        IMissionPointSet flightEnd = MissionPointSetFactory.createFlightEndAtHomeField(this);
        this.getWaypointPackage().addMissionPointSet(flightEnd);        
    }

    private void setFlightPayload() throws PWCGException
    {
        FlightPayloadBuilder flightPayloadHelper = new FlightPayloadBuilder(this);
        flightPayloadHelper.setFlightPayload();
    }
}
