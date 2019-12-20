package pwcg.mission.flight.virtual;

import java.util.ArrayList;
import java.util.List;

import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.core.location.Orientation;
import pwcg.core.utils.MathUtils;
import pwcg.mission.flight.Flight;
import pwcg.mission.flight.plane.PlaneMCU;
import pwcg.mission.flight.waypoint.VirtualWayPointCoordinate;

public class VirtualWaypointPlotter
{
    private Flight flight;
    
    public VirtualWaypointPlotter(Flight flight)
    {
        this.flight = flight;
    }
    
    public List<VirtualWayPointCoordinate> plotCoordinates() throws PWCGException
    {
        List<VirtualWayPointCoordinate> plotCoordinates = plotAllCoordinates();
        int startVWP = VirtualWaypointStartFinder.determineStartVWP(flight, plotCoordinates);

        List<VirtualWayPointCoordinate> keptPlotCoordinates = new ArrayList<>();
        for (int i = startVWP; i < plotCoordinates.size(); ++i)
        {
            keptPlotCoordinates.add(plotCoordinates.get(i));
        }
        return keptPlotCoordinates;
    }

    private List<VirtualWayPointCoordinate> plotAllCoordinates() throws PWCGException 
    {        
        List<Coordinate> allMissionCoordinates = flight.getAllMissionCoordinates();
        if (allMissionCoordinates == null || allMissionCoordinates.size() == 0)
        {
            return this.generateVwpForNoWaypoints(flight);
        }
        else
        {
            return generateVwpForFlightPath(allMissionCoordinates);
        }
    }

    private List<VirtualWayPointCoordinate> generateVwpForFlightPath(List<Coordinate> allMissionCoordinates) throws PWCGException
    {
        List<VirtualWayPointCoordinate> flightPath = new ArrayList<VirtualWayPointCoordinate>();
        int wpIndex = 0;
        for (int i = 1; i < allMissionCoordinates.size(); ++i)
        {
            Coordinate legStartPosition = allMissionCoordinates.get(i-1);
            Coordinate legEndPosition = allMissionCoordinates.get(i);
            List<VirtualWayPointCoordinate> vwpForLeg = generateVwpSetForLeg(wpIndex, legStartPosition, legEndPosition);
            flightPath.addAll(vwpForLeg);
            ++wpIndex;
        }
        return flightPath;
    }

    private List<VirtualWayPointCoordinate> generateVwpSetForLeg(
                    int waypointIndex,
                    Coordinate legStartPosition,
                    Coordinate legEndPosition) throws PWCGException
    {
        List<VirtualWayPointCoordinate> flightPathForLeg = new ArrayList<VirtualWayPointCoordinate>();

        int numVirtualWpForLeg = calculateNumberOfVirtualWaypointsForLeg(legStartPosition, legEndPosition);
        double distanceBetweenVWP = calculateDistanceBetweenEachVirtualWaypoint(legStartPosition, legEndPosition, numVirtualWpForLeg);
        int waitInSecondsForVWP = calculateWaitTimeInSecondsForLeg(distanceBetweenVWP);
        double angle = MathUtils.calcAngle(legStartPosition, legEndPosition);
                
        for (int vwpIndex = 0; vwpIndex < numVirtualWpForLeg; ++vwpIndex)
        {
            double distanceToWP = distanceBetweenVWP * (vwpIndex+1);
            Coordinate nextCoordinate = MathUtils.calcNextCoord(legStartPosition, angle, distanceToWP);

            double altitudeDelta = (legStartPosition.getYPos() - legEndPosition.getYPos()) / numVirtualWpForLeg;
            double altitude = legStartPosition.getYPos() + (altitudeDelta * vwpIndex);
            nextCoordinate.setYPos(altitude);

            VirtualWayPointCoordinate virtualWayPointCoordinate = createVwpCoordinate(waypointIndex, legStartPosition, angle, waitInSecondsForVWP);
            flightPathForLeg.add(virtualWayPointCoordinate);
        }
        
        return flightPathForLeg;
    }
    
    private int calculateNumberOfVirtualWaypointsForLeg(Coordinate legStartPosition, Coordinate legEndPosition)
    {
        double distanceToWP = MathUtils.calcDist(legStartPosition, legEndPosition);
        Double numberOfLegs = new Double(distanceToWP / 3000.0);
        numberOfLegs += 1.0;
        return numberOfLegs.intValue();
    }

    private double calculateDistanceBetweenEachVirtualWaypoint(Coordinate legStartPosition, Coordinate legEndPosition, int numberOfLegs)
    {
        double distanceToWP = MathUtils.calcDist(legStartPosition, legEndPosition);
        return distanceToWP / numberOfLegs;
    }
    
    private int calculateWaitTimeInSecondsForLeg(double distanceBetweenVWP)
    {
        double cruiseSpeedKPH = flight.getFlightCruisingSpeed();
        double cruiseSpeedMetersPerSecond = cruiseSpeedKPH / 3600.0 * 1000;
        Double waitTime = distanceBetweenVWP / cruiseSpeedMetersPerSecond;
        waitTime += 1.0;
        return waitTime.intValue();
    }


    private List<VirtualWayPointCoordinate> generateVwpForNoWaypoints(Flight flight)
    {
        List<VirtualWayPointCoordinate> circlingFlightPath = new ArrayList<VirtualWayPointCoordinate>();
        PlaneMCU leadPlane = flight.getPlanes().get(0);
        for (int i = 0; i < 60; ++i)
        {
            VirtualWayPointCoordinate virtualWayPointCoordinate = createVwpCoordinate(0, leadPlane.getPosition(), 0.0, 7200);
            circlingFlightPath.add(virtualWayPointCoordinate);
        }
        
        return circlingFlightPath;
    }

    private VirtualWayPointCoordinate createVwpCoordinate(int wpIndex, Coordinate coordinate, double angle, int waitInSecondsForVWP)
    {
        VirtualWayPointCoordinate virtualWayPointCoordinate = new VirtualWayPointCoordinate();
        
        virtualWayPointCoordinate.setCoordinate(coordinate.copy());
        virtualWayPointCoordinate.setOrientation(new Orientation(angle));
        virtualWayPointCoordinate.setWaypointindex(wpIndex);
        virtualWayPointCoordinate.setWaypointWaitTimeSeconds(waitInSecondsForVWP);
        
        return virtualWayPointCoordinate;
    }
}
