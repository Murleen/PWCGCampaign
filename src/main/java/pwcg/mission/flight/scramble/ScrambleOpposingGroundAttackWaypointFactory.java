package pwcg.mission.flight.scramble;

import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.mission.flight.IFlight;
import pwcg.mission.flight.IFlightInformation;
import pwcg.mission.flight.attack.GroundAttackWaypointFactory;
import pwcg.mission.flight.waypoint.attack.GroundAttackWaypointHelper;
import pwcg.mission.flight.waypoint.begin.IIngressWaypoint;
import pwcg.mission.flight.waypoint.begin.IngressWaypointNearTarget;
import pwcg.mission.flight.waypoint.end.EgressWaypointGenerator;
import pwcg.mission.flight.waypoint.missionpoint.IMissionPointSet;
import pwcg.mission.flight.waypoint.missionpoint.MissionPointAttackWaypointSet;
import pwcg.mission.mcu.McuAttackArea.AttackAreaType;
import pwcg.mission.mcu.McuWaypoint;
import pwcg.mission.mcu.group.AirGroundAttackMcuSequence;

public class ScrambleOpposingGroundAttackWaypointFactory
{
    private IFlight flight;
    private MissionPointAttackWaypointSet missionPointSet = new MissionPointAttackWaypointSet();

    public ScrambleOpposingGroundAttackWaypointFactory(IFlight flight) throws PWCGException
    {
        this.flight = flight;
    }
    
    public IMissionPointSet createWaypoints() throws PWCGException
    {
        McuWaypoint ingressWaypoint = createIngressWaypoint(flight);
        missionPointSet.addWaypointBefore(ingressWaypoint);
        
        createTargetWaypoints(ingressWaypoint.getPosition());
        
        AirGroundAttackMcuSequence attackMcuSequence = createAttackArea();
        missionPointSet.setAttackSequence(attackMcuSequence);
        
        McuWaypoint egressWaypoint = EgressWaypointGenerator.createEgressWaypoint(flight, ingressWaypoint.getPosition());
        missionPointSet.addWaypointAfter(egressWaypoint);

        return missionPointSet;
    }

    private McuWaypoint createIngressWaypoint(IFlight flight) throws PWCGException  
    {
        IIngressWaypoint ingressWaypointGenerator = new IngressWaypointNearTarget(flight);
        McuWaypoint ingressWaypoint = ingressWaypointGenerator.createIngressWaypoint();
        return ingressWaypoint;
    }

    private void createTargetWaypoints(Coordinate ingressPosition) throws PWCGException  
    {
        IFlightInformation flightInformation = flight.getFlightData().getFlightInformation();
        GroundAttackWaypointHelper groundAttackWaypointHelper = new GroundAttackWaypointHelper(flight, ingressPosition, flightInformation.getAltitude());
        groundAttackWaypointHelper.createTargetWaypoints();
        for (McuWaypoint groundAttackWaypoint : groundAttackWaypointHelper.getWaypointsBefore())
        {
            missionPointSet.addWaypointBefore(groundAttackWaypoint);
        }
        for (McuWaypoint groundAttackWaypoint : groundAttackWaypointHelper.getWaypointsBefore())
        {
            missionPointSet.addWaypointAfter(groundAttackWaypoint);
        }
    }
    
    private AirGroundAttackMcuSequence createAttackArea() throws PWCGException 
    {
        IFlightInformation flightInformation = flight.getFlightData().getFlightInformation();
        AirGroundAttackMcuSequence attackMcuSequence = new AirGroundAttackMcuSequence(flightInformation);
        attackMcuSequence.createAttackArea(GroundAttackWaypointFactory.GROUND_ATTACK_TIME, AttackAreaType.GROUND_TARGETS);        
        return attackMcuSequence;
    }
}
