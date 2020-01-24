package pwcg.mission.flight.escort;

import pwcg.core.exception.PWCGException;
import pwcg.mission.flight.Flight;
import pwcg.mission.flight.FlightPayloadBuilder;
import pwcg.mission.flight.IFlight;
import pwcg.mission.flight.IFlightInformation;
import pwcg.mission.flight.initialposition.FlightPositionSetter;
import pwcg.mission.flight.waypoint.begin.AirStartWaypointFactory.AirStartPattern;
import pwcg.mission.flight.waypoint.begin.IngressWaypointFactory.IngressWaypointPattern;
import pwcg.mission.flight.waypoint.missionpoint.IMissionPointSet;
import pwcg.mission.flight.waypoint.missionpoint.MissionPointSetFactory;
import pwcg.mission.mcu.McuWaypoint;

public class EscortForPlayerFlight extends Flight implements IFlight
{
    private IFlight playerFlight;
   
    public EscortForPlayerFlight(IFlightInformation flightInformation, IFlight playerFlight)
    {
        super(flightInformation);
        this.playerFlight = playerFlight;                
    }

    public void createFlight() throws PWCGException
    {
        flightData.initialize(this);
        createWaypoints();
        FlightPositionSetter.setFlightInitialPosition(this);
        setFlightPayload();
    }

    @Override
    public IMissionPointSet createFlightSpecificWaypoints(McuWaypoint ingressWaypoint) throws PWCGException
    {
        EscortForPlayerWaypointFactory missionWaypointFactory = new EscortForPlayerWaypointFactory(this, playerFlight);
        IMissionPointSet missionWaypoints = missionWaypointFactory.createWaypoints(ingressWaypoint);
        return missionWaypoints;
    }

    private void createWaypoints() throws PWCGException
    {
        MissionPointSetFactory.createStandardMissionPointSet(this, AirStartPattern.AIR_START_NEAR_INGRESS, IngressWaypointPattern.INGRESS_NEAR_TARGET);
    }

    private void setFlightPayload() throws PWCGException
    {
        FlightPayloadBuilder flightPayloadHelper = new FlightPayloadBuilder(this);
        flightPayloadHelper.setFlightPayload();
    }
}
