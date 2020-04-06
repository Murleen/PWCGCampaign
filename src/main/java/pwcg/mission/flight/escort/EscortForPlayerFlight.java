package pwcg.mission.flight.escort;

import pwcg.core.exception.PWCGException;
import pwcg.mission.flight.Flight;
import pwcg.mission.flight.FlightPayloadBuilder;
import pwcg.mission.flight.IFlight;
import pwcg.mission.flight.IFlightInformation;
import pwcg.mission.flight.initialposition.FlightPositionSetter;
import pwcg.mission.flight.waypoint.missionpoint.IMissionPointSet;
import pwcg.mission.flight.waypoint.missionpoint.MissionPointSetFactory;
import pwcg.mission.target.TargetDefinition;

public class EscortForPlayerFlight extends Flight implements IFlight
{
    private IFlight playerFlightThatNeedsEscort;

    public EscortForPlayerFlight(IFlightInformation flightInformation, TargetDefinition targetDefinition, IFlight playerFlightThatNeedsEscort)
    {
        super(flightInformation, targetDefinition);
        this.playerFlightThatNeedsEscort = playerFlightThatNeedsEscort;                
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
        IMissionPointSet flightActivate = MissionPointSetFactory.createFlightActivate(playerFlightThatNeedsEscort);
        this.getWaypointPackage().addMissionPointSet(flightActivate);

        EscortForPlayerWaypointFactory missionWaypointFactory = new EscortForPlayerWaypointFactory(this, playerFlightThatNeedsEscort);
        IMissionPointSet missionWaypoints = missionWaypointFactory.createWaypoints();
        this.getWaypointPackage().addMissionPointSet(missionWaypoints);
        
        IMissionPointSet flightEnd = MissionPointSetFactory.createFlightEndAtHomeField(playerFlightThatNeedsEscort);
        this.getWaypointPackage().addMissionPointSet(flightEnd);        
    }

    private void setFlightPayload() throws PWCGException
    {
        FlightPayloadBuilder flightPayloadHelper = new FlightPayloadBuilder(this);
        flightPayloadHelper.setFlightPayload();
    }
}
