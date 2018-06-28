package pwcg.mission.flight.waypoint;

import pwcg.core.location.Coordinate;
import pwcg.core.location.Orientation;

/**
 * A VWP Coordinate contains the position of a VWP and the associated action point (WP, ATtackArea, etc) 
 * that the flight will transition to.  It is used to create a VWP group for the mission file.
 * 
 * @author Patrick Wilson
 *
 */
public class VirtualWayPointCoordinate
{
    private Coordinate coordinate = new Coordinate();
    private Orientation orientation = new Orientation();
    private int waypointindex = 0;
    
    public Coordinate getCoordinate()
    {
        return coordinate;
    }
    
    public void setCoordinate(Coordinate coordinate)
    {
        this.coordinate = coordinate;
    }
    
    public Orientation getOrientation()
    {
        return orientation;
    }
    
    public void setOrientation(Orientation orientation)
    {
        this.orientation = orientation;
    }

    public int getWaypointindex()
    {
        return waypointindex;
    }

    public void setWaypointindex(int waypointindex)
    {
        this.waypointindex = waypointindex;
    }
    
    
}
