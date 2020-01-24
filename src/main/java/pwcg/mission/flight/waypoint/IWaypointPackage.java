package pwcg.mission.flight.waypoint;

import java.io.BufferedWriter;
import java.util.List;

import pwcg.core.exception.PWCGException;
import pwcg.core.exception.PWCGIOException;
import pwcg.mission.flight.plane.PlaneMcu;
import pwcg.mission.flight.waypoint.missionpoint.IMissionPointSet;
import pwcg.mission.flight.waypoint.missionpoint.MissionPoint;
import pwcg.mission.mcu.BaseFlightMcu;
import pwcg.mission.mcu.McuWaypoint;

public interface IWaypointPackage
{    
    void addMissionPointSet(IMissionPointSet missionPointSet);

    List<McuWaypoint> getAllWaypoints();

    List<MissionPoint> getFlightMissionPoints() throws PWCGException;
    
    List<BaseFlightMcu> getAllFlightPoints();

    MissionPoint getMissionPointByAction(WaypointAction action) throws PWCGException;

    void updateWaypoints(List<McuWaypoint> waypointsInBriefing) throws PWCGException;
    
    void write(BufferedWriter writer) throws PWCGIOException, PWCGException;

    IWaypointPackage duplicate(int positionInFormation) throws PWCGException;

    void triggerOnFlightActivation(int index) throws PWCGException;

    void finalize(PlaneMcu plane) throws PWCGException;

}