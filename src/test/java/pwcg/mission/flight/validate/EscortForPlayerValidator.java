package pwcg.mission.flight.validate;

import java.util.List;

import pwcg.core.exception.PWCGException;
import pwcg.mission.IUnit;
import pwcg.mission.flight.IFlight;
import pwcg.mission.flight.escort.EscortForPlayerFlight;
import pwcg.mission.flight.plane.PlaneMcu;
import pwcg.mission.flight.waypoint.WaypointGeneratorUtils;
import pwcg.mission.flight.waypoint.WaypointType;
import pwcg.mission.mcu.McuCover;
import pwcg.mission.mcu.McuForceComplete;
import pwcg.mission.mcu.McuTimer;
import pwcg.mission.mcu.McuWaypoint;

public class EscortForPlayerValidator 
{
    private IFlight playerFlight;
    private EscortForPlayerFlight escortForPlayerFlight;

    public EscortForPlayerValidator (IFlight playerFlight)
    {
        this.playerFlight = playerFlight;
    }
    
    public void validateEscortForPlayer() throws PWCGException
    {
        if (playerFlight.getMission().isNightMission())
        {
            validateNoEscortForPlayer();
        }
        else
        {
            validatePlayerEscortFlight();
        }
    }


    public void validateNoEscortForPlayer()
    {
        escortForPlayerFlight = playerFlight.getEscortForPlayer();
        assert(escortForPlayerFlight == null);        
    }

    private void validatePlayerEscortFlight() throws PWCGException
	{
		assert(playerFlight.getFlightData().getWaypointPackage().getWaypointsForLeadPlane().size() > 0);
        validateLinkedEscort();
        validateRendezvous();
        validateEscortLeadPlane();
        validateEscortFlags();
        validateEscortAltitude();
        validateWaypointLinkage();
	}

    private void validateRendezvous()
    {
        assert(escortForPlayerFlight != null);
   
        PlaneMcu leadEscortedPlane = playerFlight.getFlightData().getFlightPlanes().get(0);
        assert(escortForPlayerFlight.getCover().getTargets().get(0).equals(Integer.valueOf(leadEscortedPlane.getEntity().getIndex()).toString()));
   
        McuWaypoint ingressWP = WaypointGeneratorUtils.findWaypointByType(playerFlight.getFlightData().getWaypointPackage().getAllFlightWaypoints(), WaypointType.INGRESS_WAYPOINT.getName());
        PlaneMcu leadEscortPlane = escortForPlayerFlight.getFlightData().getFlightPlanes().get(0);
        assert(leadEscortPlane.getPosition().getXPos() == ingressWP.getPosition().getXPos());
        assert(leadEscortPlane.getPosition().getZPos() == ingressWP.getPosition().getZPos());
        assert(leadEscortPlane.getPosition().getYPos() > ingressWP.getPosition().getYPos());
    }

    private void validateEscortLeadPlane() throws PWCGException
    {
        PlaneMcu leadEscortPlane = escortForPlayerFlight.getFlightData().getFlightPlanes().get(0);
        assert(leadEscortPlane != null);
    }

    private void validateLinkedEscort()
    {
        for (IFlight linkedFlight : playerFlight.getFlightData().getLinkedFlights().getLinkedFlights())
        {
            assert(linkedFlight instanceof EscortForPlayerFlight);
            escortForPlayerFlight = (EscortForPlayerFlight)linkedFlight;    
        }
    }

    private void validateEscortFlags() throws PWCGException
    {
        assert(escortForPlayerFlight != null);
        assert(escortForPlayerFlight.getFlightData().getFlightInformation().isVirtual() == false);
        assert(escortForPlayerFlight.getFlightData().getFlightInformation().isAirStart() == true);
        assert(escortForPlayerFlight.getFlightData().getFlightInformation().isPlayerFlight() == false);
    }

    private void validateEscortAltitude()
    {
        for (PlaneMcu escortPlane : escortForPlayerFlight.getFlightData().getFlightPlanes().getPlanes())
        {
            assert(escortPlane.getPosition().getYPos() > 1000.0);
        }
    }

	private void validateWaypointLinkage() throws PWCGException 
	{
        McuWaypoint escortAirStartWP = escortForPlayerFlight.getFlightData().getWaypointPackage().getWaypointByType(WaypointType.AIR_START_WAYPOINT);
        McuWaypoint escortIngressWP = escortForPlayerFlight.getFlightData().getWaypointPackage().getWaypointByType(WaypointType.INGRESS_WAYPOINT);
        McuWaypoint escortReturnToBaseWP = escortForPlayerFlight.getFlightData().getWaypointPackage().getWaypointByType(WaypointType.RETURN_TO_BASE_WAYPOINT);

		assert(escortAirStartWP.getIndex() == escortForPlayerFlight.getFlightData().getWaypointPackage().getWaypointsForLeadPlane().get(0).getIndex());
		
        McuWaypoint playerIngressWP = playerFlight.getFlightData().getWaypointPackage().getWaypointByType(WaypointType.INGRESS_WAYPOINT);
        McuWaypoint playerEgressWP = playerFlight.getFlightData().getWaypointPackage().getWaypointByType(WaypointType.EGRESS_WAYPOINT);

		McuTimer coverTimer  = escortForPlayerFlight.getCoverTimer();
		McuCover cover = escortForPlayerFlight.getCover();
		
		McuTimer forceCompleteTimer = escortForPlayerFlight.getForceCompleteTimer();
		McuForceComplete forceComplete = escortForPlayerFlight.getForceCompleteEntity();

        assert(isIndexInTargetList(escortIngressWP.getIndex(), escortAirStartWP.getTargets()));
        assert(isIndexInTargetList(coverTimer.getIndex(), playerIngressWP.getTargets()));
        assert(isIndexInTargetList(cover.getIndex(), coverTimer.getTargets()));
        assert(isIndexInTargetList(playerFlight.getFlightData().getFlightPlanes().getFlightLeader().getEntity().getIndex(), cover.getTargets()));
		
        assert(isIndexInTargetList(forceCompleteTimer.getIndex(), playerEgressWP.getTargets()));
        assert(isIndexInTargetList(forceComplete.getIndex(), forceCompleteTimer.getTargets()));
        assert(isIndexInTargetList(escortReturnToBaseWP.getIndex(), forceCompleteTimer.getTargets()));
	}

	private boolean isIndexInTargetList(int targetIndexToFind, List<String>targetsFromMCU) 
	{
		boolean isIndexInTargetList = false;
		for (String targetIndex : targetsFromMCU)
		{
			if (targetIndex.equals(new String("" + targetIndexToFind)))
			{
				isIndexInTargetList = true;	
			}
		}
		return isIndexInTargetList;
	}
}
