package pwcg.mission.flight.validate;

import pwcg.core.exception.PWCGException;
import pwcg.mission.flight.IFlight;
import pwcg.mission.flight.plane.PlaneMcu;
import pwcg.mission.flight.waypoint.WaypointAction;
import pwcg.mission.flight.waypoint.WaypointType;
import pwcg.mission.flight.waypoint.missionpoint.IMissionPointSet;
import pwcg.mission.flight.waypoint.missionpoint.MissionPointFlightActivateReal;
import pwcg.mission.flight.waypoint.missionpoint.MissionPointFlightActivateVirtual;
import pwcg.mission.flight.waypoint.missionpoint.MissionPointFlightBeginAirStart;
import pwcg.mission.flight.waypoint.missionpoint.MissionPointSetType;
import pwcg.mission.mcu.McuTimer;
import pwcg.mission.mcu.McuWaypoint;

public class BombingAirStartFlightValidator 
{
    public void validateBomberFlight(IFlight flight) throws PWCGException
    {
        assert(flight.getWaypointPackage().getAllWaypoints().size() > 0);
        validateWaypointLinkage(flight);
        validateWaypointTypes(flight);
        validateAirStart(flight);
        verifyStartAltltude(flight);
    }

    private void validateAirStart(IFlight flight)
    {
        for (PlaneMcu plane : flight.getFlightPlanes().getPlanes())
        {
            assert(plane.getPosition().getYPos() > 300);
        }
    }

    private void validateWaypointLinkage(IFlight flight) throws PWCGException 
    {
        McuWaypoint prevWaypoint = null;
        for (McuWaypoint waypoint : flight.getWaypointPackage().getAllWaypoints())
        {
            if (prevWaypoint != null)
            {
                if (prevWaypoint.getWaypointType() != WaypointType.TARGET_FINAL_WAYPOINT)
                {
                    boolean isNextWaypointLinked = IndexLinkValidator.isIndexInTargetList(waypoint.getIndex(), prevWaypoint.getTargets());
                    assert(isNextWaypointLinked);
                }
            }
            
            prevWaypoint = waypoint;
        }
        
        verifyActivateLinkedToFormation(flight);

        assert(flight.getWaypointPackage().getAllWaypoints().get(0).getWaypointType() == WaypointType.AIR_START_WAYPOINT);
        assert(flight.getWaypointPackage().getAllWaypoints().get(1).getWaypointType() == WaypointType.RENDEZVOUS_WAYPOINT);
    }
    
    private void verifyStartAltltude(IFlight flight) throws PWCGException 
    {
        McuWaypoint airStartWaypoint = flight.getWaypointPackage().getAllWaypoints().get(0);
        for (PlaneMcu plane : flight.getFlightPlanes().getPlanes())
        {
            double planeStartAltitude = plane.getEntity().getPosition().getYPos();
            double airStartAltitude = airStartWaypoint.getPosition().getYPos();
            double altDifference = Math.abs(planeStartAltitude - airStartAltitude);
            assert(altDifference < 1000.0);
        }
    }


    private void validateWaypointTypes(IFlight flight) 
    {
        boolean targetFinalFound = false;

        WaypointPriorityValidator.validateWaypointTypes(flight);

        for (McuWaypoint waypoint : flight.getWaypointPackage().getAllWaypoints())
        {
            if (waypoint.getWpAction().equals(WaypointAction.WP_ACTION_TARGET_FINAL))
            {
                targetFinalFound = true;
            }
        }
        
        assert(targetFinalFound);
    }
        
    public void verifyActivateLinkedToFormation(IFlight flight) throws PWCGException
    {        
        IMissionPointSet activateMissionPointSet = flight.getWaypointPackage().getMissionPointSet(MissionPointSetType.MISSION_POINT_SET_ACTIVATE);
        McuTimer missionBeginTimer = null;
        if (activateMissionPointSet instanceof  MissionPointFlightActivateVirtual)
        {
            MissionPointFlightActivateVirtual missionPointFlightActivateVirtual = (MissionPointFlightActivateVirtual)activateMissionPointSet;
            missionBeginTimer =  missionPointFlightActivateVirtual.getMissionBeginTimer();

        }
        else if (activateMissionPointSet instanceof  MissionPointFlightActivateReal)
        {
            MissionPointFlightActivateReal missionPointFlightActivateReal = (MissionPointFlightActivateReal)activateMissionPointSet;
            missionBeginTimer =  missionPointFlightActivateReal.getMissionBeginTimer();
        }
        
        MissionPointFlightBeginAirStart airStartMissionPointSet = (MissionPointFlightBeginAirStart)flight.getWaypointPackage().getMissionPointSet(MissionPointSetType.MISSION_POINT_SET_BEGIN_AIR);
        int airStartEntryIndex = airStartMissionPointSet.getEntryPoint();

        boolean isActivateLinked = IndexLinkValidator.isIndexInTargetList(airStartEntryIndex, missionBeginTimer.getTargets());
        assert(isActivateLinked);
    }
}
