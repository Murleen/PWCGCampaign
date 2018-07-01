package pwcg.mission.flight;

import java.util.List;

import pwcg.campaign.squadmember.SquadronMember;
import pwcg.core.config.ConfigItemKeys;
import pwcg.core.config.ConfigManagerCampaign;
import pwcg.core.exception.PWCGException;
import pwcg.core.exception.PWCGMissionGenerationException;
import pwcg.core.location.Coordinate;
import pwcg.core.location.Orientation;
import pwcg.core.utils.MathUtils;
import pwcg.mission.MissionSkinGenerator;
import pwcg.mission.Unit;
import pwcg.mission.flight.plane.PlaneMCU;
import pwcg.mission.flight.waypoint.ActualWaypointPackage;
import pwcg.mission.flight.waypoint.VirtualWaypointPackage;
import pwcg.mission.mcu.BaseFlightMcu;
import pwcg.mission.mcu.McuMessage;
import pwcg.mission.mcu.McuWaypoint;
import pwcg.mission.mcu.group.PlaneRemover;
import pwcg.mission.mcu.group.VirtualWayPoint;

public class FlightFinalizer 
{
    protected int flightId = -1;

     protected Flight flight;

    public FlightFinalizer(Flight flight)
    {
        this.flight = flight;
    }

    public void finalizeFlight() throws PWCGException 
    {
        setWaypointOrientation();
        createWaypointTargetAssociations();
        setFlightLeaderLinks();

        // Special finalization logic for player flight
        if (flight.isPlayerFlight())
        {
            finalizePlayerFlight();
        }
        else if (flight.isVirtual())
        {
            finalizeVirtualFlight();
        }
        else
        {
            finalizeNonVirtualAiFlight();
        }

        // Finalize linked flights
        for (Unit unit : flight.getLinkedUnits())
        {
            if (unit instanceof Flight)
            {
                Flight flight = (Flight) unit;
                flight.finalizeFlight();
            }
        }

        // Initialize the attack entity to make the AI do something useful
        linkAttackEntity();

        createTargetAssociationsMissionBegin();

        assignSkinsForFlight();
    }

    private void assignSkinsForFlight() throws PWCGException
    {
        MissionSkinGenerator skinGenerator = new MissionSkinGenerator();
        if (flight.isPlayerFlight())
        {
            for (PlaneMCU plane : flight.getPlanes())
            {
                SquadronMember squadronMember = plane.getPilot();
                skinGenerator.setSkinForPlayerSquadron(squadronMember, flight.getSquadron(), plane, flight.getCampaign().getDate());
            }
        }
        else
        {
            for (PlaneMCU plane : flight.getPlanes())
            {
                skinGenerator.setAISkin(flight.getSquadron(), plane, flight.getCampaign().getDate());
            }
        }
    }

    private void finalizePlayerFlight() throws PWCGException 
    {
        // Add take off notification.
        // This is needed to trigger the first WP
        if (!flight.isAirstart())
        {
            if (flight.getTakeoff() != null)
            {
                BaseFlightMcu wpEntryMcu = flight.getWaypointPackage().getEntryMcu();

                flight.getLeadPlane().getEntity().setOnMessages(
                                McuMessage.ONTAKEOFF,
                                flight.getTakeoff().getIndex(),
                                wpEntryMcu.getIndex());
            }
        }
        else
        {
            // The mission begin timer triggers the formation timer.
            // For airstart, link the formation timer to the WP timer
            BaseFlightMcu wpEntryMcu = flight.getWaypointPackage().getEntryMcu();
            if (wpEntryMcu != null)
            {
                flight.getFormationTimer().setTarget(wpEntryMcu.getIndex());
            }
        }

        // Reset the player flight for air starts
        if (flight.isAirstart())
        {
            resetPlaneInitialPositionForAirStarts();
        }
        
        flight.enableNonVirtualFlight();
    }

    private void finalizeNonVirtualAiFlight() throws PWCGException 
    {
        // The mission begin timer triggers the formation timer.
        // For airstart, link the formation timer to the WP timer
    	if (flight.isEscortedByPlayerFlight())
        {
            // Flights escorted by the player circle until rendezvous
        }
    	else
    	{
	        BaseFlightMcu wpEntryMcu = flight.getWaypointPackage().getEntryMcu();
	        if (wpEntryMcu != null)
	        {
	            flight.getFormationTimer().setTarget(wpEntryMcu.getIndex());
	        }
    	}

        resetPlaneInitialPositionForAirStarts();
        flight.enableNonVirtualFlight();
    }

    private void finalizeVirtualFlight() throws PWCGException 
    {
        ConfigManagerCampaign configManager = flight.getCampaign().getCampaignConfigManager();

        // Create virtual waypoints
        if (flight.getWaypointPackage() instanceof VirtualWaypointPackage)
        {
            VirtualWaypointGenerator virtualWaypointGenerator = new VirtualWaypointGenerator(flight);
            List<VirtualWayPoint> virtualWaypoints = virtualWaypointGenerator.createVirtualWaypoints();

            VirtualWaypointPackage virtualWaypointPackage = (VirtualWaypointPackage) flight.getWaypointPackage();
            virtualWaypointPackage.setVirtualWaypoints(virtualWaypoints);

            // After we have the VWPs in place, link to the waypoint package
            BaseFlightMcu wpEntryMcu = flight.getWaypointPackage().getEntryMcu();
            if (wpEntryMcu != null)
            {
                flight.getMissionBeginUnit().linkToMissionBegin(wpEntryMcu.getIndex());
            }

            // Plane Remover
            if (!(configManager.getIntConfigParam(ConfigItemKeys.UsePlaneDeleteKey) == 0))
            {
                for (int i = 0; i < flight.getPlanes().size(); ++i)
                {
                    PlaneMCU plane = flight.getPlanes().get(i);
                    if (!plane.isPlayerPlane(flight.getCampaign().getPlayer().getSerialNumber()))
                    {
                        plane.createPlaneRemover(flight, flight.getMission().getMissionFlightBuilder().getPlayerFlight().getPlayerPlane());
                    }
                    
                    for (VirtualWayPoint virtualWaypoint : ((VirtualWaypointPackage) flight.getWaypointPackage()).getVirtualWaypoints())
                    {
                        // For virtual WP flights, enable the plane remover when
                        // the flight becomes active.
                        // Only one virtual WP will ever trigger, so the plane
                        // remover will be instantiated only once.
                        PlaneRemover planeRemover = plane.getPlaneRemover();
                        if (planeRemover != null)
                        {
                            virtualWaypoint.onTriggerAddTarget(plane, planeRemover.getEntryPoint().getIndex());
                        }
                    }
                }
            }
        }
        else
        {
            throw new PWCGMissionGenerationException("Non virtual AI flight");
        }
    }

    private void linkAttackEntity()
    {
        for (int index = 0; index < flight.getPlanes().size(); ++index)
        {
            PlaneMCU plane = flight.getPlanes().get(index);
            
            plane.initializeAttackEntity(index);
            if (flight.getWaypointPackage() instanceof VirtualWaypointPackage)
            {
                VirtualWaypointPackage virtualWaypointPackage = (VirtualWaypointPackage)flight.getWaypointPackage();
                for (VirtualWayPoint vwp : virtualWaypointPackage.getVirtualWaypoints())
                {
                    vwp.onTriggerAddTarget(plane, plane.getOnSpawnTimer().getIndex());
                }
            }
            else
            {
                ActualWaypointPackage actualWaypointPackage = (ActualWaypointPackage)flight.getWaypointPackage();
                actualWaypointPackage.onTriggerAddTarget(plane.getOnSpawnTimer().getIndex());
            }
        }
    }

    private void resetPlaneInitialPositionForAirStarts() throws PWCGException 
    {
        PlaneMCU flightLeader = flight.getFlightLeader();

        int i = 0;
        Coordinate flightLeaderPos = null;
        Orientation flightLeaderOrient = null;
        for (PlaneMCU plane : flight.getPlanes())
        {
            if (i == 0)
            {
                flightLeaderPos = flightLeader.getPosition().copy();
                flightLeaderOrient = flightLeader.getOrientation().copy();
                ++i;
                continue;
            }

            Coordinate planeCoords = new Coordinate();

            ConfigManagerCampaign configManager = flight.getCampaign().getCampaignConfigManager();

            // Since we always face east, subtract from z to get your mates
            // behind you
            int AircraftSpacingHorizontal = configManager.getIntConfigParam(ConfigItemKeys.AircraftSpacingHorizontalKey);
            planeCoords.setXPos(flightLeaderPos.getXPos() - (i * AircraftSpacingHorizontal));
            planeCoords.setZPos(flightLeaderPos.getZPos() - (i * AircraftSpacingHorizontal));

            int AircraftSpacingVertical = configManager.getIntConfigParam(ConfigItemKeys.AircraftSpacingVerticalKey);
            planeCoords.setYPos(flightLeaderPos.getYPos() + (i * AircraftSpacingVertical));
            plane.setPosition(planeCoords);

            plane.setOrientation(flightLeaderOrient.copy());

            ++i;
        }
    }

    private void setFlightLeaderLinks()
    {
        int flightLeaderIndex = flight.getFlightLeader().getEntity().getIndex();
        if (flight.getActivationEntity() != null)
        {
            flight.getActivationEntity().setObject(flightLeaderIndex);
        }

        // only the player takes off
        if (flight.getTakeoff() != null)
        {
            flight.getTakeoff().setObject(flightLeaderIndex);
        }

        // Landing
        if (flight.getLanding() != null)
        {
            flight.getLanding().setObject(flightLeaderIndex);
        }

        // Formation
        if (flight.getFormationEntity() != null)
        {
            flight.getFormationEntity().setObject(flightLeaderIndex);
        }
    }

    private void setWaypointOrientation() throws PWCGException 
    {
        // TL each waypoint to the next one
        McuWaypoint prevWP = null;
        for (McuWaypoint nextWP : flight.getWaypointPackage().getWaypointsForLeadPlane())
        {
            if (prevWP != null)
            {
                Coordinate prevPos = prevWP.getPosition().copy();
                Coordinate nextPos = nextWP.getPosition().copy();
    
                Orientation orient = new Orientation();
                orient.setyOri(MathUtils.calcAngle(prevPos, nextPos));
    
                prevWP.setOrientation(orient);
            }
            
            prevWP = nextWP;
        }
    }

    private void createWaypointTargetAssociations() throws PWCGException
    {
        flight.createTargetAssociationsForFlight();
    }

    private void createTargetAssociationsMissionBegin()
    {
        if (flight.isPlayerFlight())
        {
            flight.getMissionBeginUnit().linkToMissionBegin(flight.getFormationTimer().getIndex());
        }
        else if (flight.isVirtual)
        {
            flight.getMissionBeginUnit().linkToMissionBegin(flight.getActivationTimer().getIndex());
            flight.getActivationTimer().setTarget(flight.getFormationTimer().getIndex());
        }
        else if (flight.isVirtual)
        {
            flight.getMissionBeginUnit().linkToMissionBegin(flight.getActivationTimer().getIndex());
            flight.getActivationTimer().setTarget(flight.getFormationTimer().getIndex());
        }
        else
        {
            flight.getMissionBeginUnit().linkToMissionBegin(flight.getActivationTimer().getIndex());
            flight.getActivationTimer().setTarget(flight.getFormationTimer().getIndex());
        }
    }
}
