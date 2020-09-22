package pwcg.mission.flight.validate;

import pwcg.core.exception.PWCGException;
import pwcg.mission.flight.IFlight;
import pwcg.mission.flight.plane.PlaneMcu;
import pwcg.mission.flight.waypoint.missionpoint.MissionPointFlightActivateVirtual;
import pwcg.mission.flight.waypoint.missionpoint.MissionPointSetType;
import pwcg.mission.mcu.McuTimer;

public class FlightActivateValidator
{
    public static void validate(IFlight flight) throws PWCGException
    {
        validateAttackLinkage(flight);
    }

    private static void validateAttackLinkage(IFlight flight) throws PWCGException
    {
        assert (flight.getFlightPlanes().getPlanes().size() > 0);
        
        MissionPointFlightActivateVirtual activateMissionPointSet = (MissionPointFlightActivateVirtual)flight.getWaypointPackage().getMissionPointSet(MissionPointSetType.MISSION_POINT_SET_ACTIVATE);
        McuTimer activationTimer = activateMissionPointSet.getMissionBeginVwpTimer();
        for (PlaneMcu plane : flight.getFlightPlanes().getPlanes())
        {
            assert(IndexLinkValidator.isIndexInTargetList(plane.getAttackTimer().getIndex(), activationTimer.getTargets()));
        }
    }
}
