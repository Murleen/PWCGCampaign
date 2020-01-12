package pwcg.mission.flight.waypoint.missionpoint;

import java.io.BufferedWriter;
import java.util.List;

import pwcg.core.exception.PWCGException;
import pwcg.mission.flight.plane.PlaneMcu;

public interface IMissionPointSet extends IMissionPointSetWaypoints
{
    void write(BufferedWriter writer) throws PWCGException;

    void setLinkToNextTarget(int nextTargetIndex) throws PWCGException;
    
    int getEntryPoint() throws PWCGException;

    List<MissionPoint> getFlightMissionPoints() throws PWCGException;

    void finalize(PlaneMcu plane) throws PWCGException;
    
    void disableLinkToNextTarget();

    boolean isLinkToNextTarget();
}
