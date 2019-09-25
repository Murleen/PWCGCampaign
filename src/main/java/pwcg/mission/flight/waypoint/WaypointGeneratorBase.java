package pwcg.mission.flight.waypoint;

import java.util.ArrayList;
import java.util.List;

import pwcg.campaign.Campaign;
import pwcg.campaign.api.Side;
import pwcg.campaign.context.FrontLinePoint;
import pwcg.campaign.context.FrontLinesForMap;
import pwcg.campaign.context.PWCGContextManager;
import pwcg.core.config.ConfigItemKeys;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.core.utils.MathUtils;
import pwcg.core.utils.RandomNumberGenerator;
import pwcg.mission.mcu.McuWaypoint;

public class WaypointGeneratorBase 
{
    public static int INGRESS_DISTANCE_FROM_FRONT = 10000;
	

    public static double getWaypointAltitude(Campaign campaign, Coordinate previousPosition, Coordinate nextPosition, double waypointSpeed, double desiredAltitude) 
                    throws PWCGException 
    {
        double distanceBetweenWP =  MathUtils.calcDist(previousPosition, nextPosition);
        double minutesToNextWP = (distanceBetweenWP / 1000.0 / waypointSpeed) * 60;
        double maxAcheivableAltitudeGain = minutesToNextWP * 400.0;

        double maxAcheivableAltitude = previousPosition.getYPos() + maxAcheivableAltitudeGain;
        if (maxAcheivableAltitude < (desiredAltitude * .8))
        {
            maxAcheivableAltitude = desiredAltitude * .8;
        }

        double wpAltitude = desiredAltitude;
        if (maxAcheivableAltitude < desiredAltitude)
        {
            wpAltitude = maxAcheivableAltitude;
        }

        // Never too low
        int InitialWaypointAltitude = campaign.getCampaignConfigManager().getIntConfigParam(ConfigItemKeys.InitialWaypointAltitudeKey);
        if (wpAltitude < InitialWaypointAltitude)
        {
            wpAltitude = InitialWaypointAltitude;
        }
        
        return wpAltitude;
    }

	public static McuWaypoint findWaypointByType(List<McuWaypoint> waypointList, String type)
	{
		McuWaypoint theWP = null;
		for (McuWaypoint waypoint : waypointList)
		{
			if (waypoint.getName().equals(type))
			{
				theWP = waypoint;
				break;
			}
		}
		
		return theWP;
	}

	public static boolean goNorth(Campaign campaign, int startFrontIndex, Side side) throws PWCGException 
    {
        final int closestToEdge = 10;

        boolean goNorth = true;
        
        // At northern edge - go south
        FrontLinesForMap frontLineMarker =  PWCGContextManager.getInstance().getCurrentMap().getFrontLinesForMap(campaign.getDate());
        List<FrontLinePoint> frontLines = frontLineMarker.getFrontLines(side);
        if (startFrontIndex < closestToEdge)
        {
            goNorth = false;
        }
        // at southern edge - go north
        else if (startFrontIndex > (frontLines.size() - closestToEdge) )
        {
            goNorth = true;
        }
        // in the middle - either direction
        else
        {
            int goNorthInt = RandomNumberGenerator.getRandom(100);
            if (goNorthInt < 50)
            {
                goNorth = false;
            }
        }
        
        return goNorth;
    }

	public static int getNextFrontIndex(Campaign campaign, int startFrontIndex, boolean goNorth, int numToAdvance, Side side) throws PWCGException
    {
        int frontIndex;
        if (goNorth)
        {
            frontIndex = startFrontIndex - numToAdvance;
        }
        else
        {
            frontIndex = startFrontIndex + numToAdvance;
        }
        
        // Don't go too far North
        if (frontIndex < 3)
        {
            frontIndex = 2;
        }
        
        // Don't go too far South
        FrontLinesForMap frontLineMarker =  PWCGContextManager.getInstance().getCurrentMap().getFrontLinesForMap(campaign.getDate());
        List<FrontLinePoint> frontLines = frontLineMarker.getFrontLines(side);
        if (frontIndex > frontLines.size())
        {
            frontIndex = frontLines.size()-2;
        }

        
        return frontIndex;
    }

	public static boolean isEdgeOfMap(Campaign campaign, int frontIndex, boolean goNorth, Side side) throws PWCGException
    {
        if (goNorth && frontIndex < 4)
        {
            return true;
        }
        
        FrontLinesForMap frontLineMarker =  PWCGContextManager.getInstance().getCurrentMap().getFrontLinesForMap(campaign.getDate());
        List<FrontLinePoint> frontLines = frontLineMarker.getFrontLines(side);
        if (!goNorth && (frontIndex > (frontLines.size() - 4)))
        {
            return true;
        }
        
        return false;
    }

	public static void setWaypointsNonFighterPriority(List<McuWaypoint> waypoints)
	{
		for (McuWaypoint waypoint : waypoints)
		{
			if (waypoint.getPriority().getPriorityValue() < WaypointPriority.PRIORITY_MED.getPriorityValue())
			{
				waypoint.setPriority(WaypointPriority.PRIORITY_MED);
			}
		}
	}

	public static List<McuWaypoint> getTargetWaypoints(List<McuWaypoint> playerWaypoints)
    {
        List<McuWaypoint> selectedWaypoints = new ArrayList <McuWaypoint>();
        
        for (int i = 0; i < playerWaypoints.size(); ++i)
        {
            McuWaypoint playerWaypoint = playerWaypoints.get(i);
            if (playerWaypoint.getWpAction() == WaypointAction.WP_ACTION_PATROL ||
                playerWaypoint.getWpAction() == WaypointAction.WP_ACTION_RECON)
                            
            {
                selectedWaypoints.add(playerWaypoint);
            }
        }

        return selectedWaypoints;
    }
}
