package pwcg.mission.flight.waypoint.intercept;

import java.util.ArrayList;
import java.util.List;

import pwcg.campaign.Campaign;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.core.location.Orientation;
import pwcg.core.utils.MathUtils;
import pwcg.mission.flight.Flight;
import pwcg.mission.flight.waypoint.WaypointAction;
import pwcg.mission.flight.waypoint.WaypointFactory;
import pwcg.mission.flight.waypoint.WaypointPriority;
import pwcg.mission.flight.waypoint.WaypointType;
import pwcg.mission.mcu.McuWaypoint;

/**
 * Create a creeping search pattern. 
 * A creeping search pattern looks like a sideways bar graph
 * 
 * @author U071098
 *
 */
public class CreepingLinePattern 
{       
    private static final int MAX_CREEP_SEGMENTS = 3;
    
    private Campaign campaign;
    List<McuWaypoint> creepingWPs = new ArrayList<McuWaypoint>();
    private Flight flight;
    private int wpTriggerArea = 1000;
    private WaypointType waypointType;
    private WaypointAction waypointAction;
    private int legsInCreeping = MAX_CREEP_SEGMENTS;
    
    public CreepingLinePattern(Campaign campaign, Flight flight, WaypointType waypointType, WaypointAction waypointAction, int wpTriggerArea, int legsInCreeping) throws PWCGException 
    {
        this.campaign = campaign;
        this.flight = flight;
        this.wpTriggerArea = wpTriggerArea;
        this.waypointType = waypointType;
        this.waypointAction = waypointAction;
        this.legsInCreeping = legsInCreeping;
    }

    /**
     * Recursively generate creeping, altering altitude per the request
     * 
     * @param missionWPs
     * @throws PWCGException 
     */
    public List<McuWaypoint> generateCreepingWPSegments(McuWaypoint lastWP, double legDistance, double connectSegmentDistance) throws PWCGException
    {
        int legCount = 0;
                        
        generateCreepingWPSegment(lastWP, lastWP.getOrientation().getyOri(), legDistance, connectSegmentDistance, legCount);
        
        return creepingWPs;
    }

    /**
     * Recursively generate creeping segments.  
     * A creeping pattern looks like a  bar graph
     * 
     * @param missionWPs
     * @throws PWCGException 
     */
    private void generateCreepingWPSegment(McuWaypoint lastWP, double initialOrientation, double legDistance, double connectSegmentDistance, int legCount) throws PWCGException
    {
        // A leg is four waypoints
        lastWP = generateCreepingWP(lastWP, connectSegmentDistance, initialOrientation);
        lastWP = generateCreepingWP(lastWP, legDistance, MathUtils.adjustAngle(initialOrientation, 90));
        lastWP = generateCreepingWP(lastWP, connectSegmentDistance, initialOrientation);
        lastWP = generateCreepingWP(lastWP, legDistance, MathUtils.adjustAngle(initialOrientation, -90));
        
        ++legCount;
        if (legCount < legsInCreeping)
        {
            generateCreepingWPSegment(lastWP, initialOrientation, legDistance, connectSegmentDistance, legCount);
        }
    }
    
    

    /**
     * Generate a single waypoint in the creeping segment
     * 
     * @param missionWPs
     * @throws PWCGException 
     */
    private McuWaypoint generateCreepingWP(McuWaypoint lastWP, double legDistance, double orientation) throws PWCGException
    {
        McuWaypoint nextCreepingWP = WaypointFactory.createDefinedWaypointType(waypointType, waypointAction);
        
        nextCreepingWP.setTriggerArea(wpTriggerArea);
        nextCreepingWP.setDesc(flight.getSquadron().determineDisplayName(campaign.getDate()), waypointType.getName());

        nextCreepingWP.setSpeed(lastWP.getSpeed());
        nextCreepingWP.setPriority(WaypointPriority.PRIORITY_LOW);          
        
        Orientation creepingWPOrientation = new Orientation();
        creepingWPOrientation.setyOri(orientation);
        nextCreepingWP.setOrientation(creepingWPOrientation);

        Coordinate creepingCoords = MathUtils.calcNextCoord(lastWP.getPosition().copy(), orientation, legDistance);
        
        creepingCoords.setYPos(lastWP.getPosition().getYPos());
        nextCreepingWP.setPosition(creepingCoords);
        
        creepingWPs.add(nextCreepingWP);
        
        return nextCreepingWP;
    }
}
