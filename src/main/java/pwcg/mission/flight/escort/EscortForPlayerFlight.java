package pwcg.mission.flight.escort;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import pwcg.campaign.api.IProductSpecificConfiguration;
import pwcg.campaign.factory.ProductSpecificConfigurationFactory;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.core.location.Orientation;
import pwcg.mission.Mission;
import pwcg.mission.MissionBeginUnit;
import pwcg.mission.flight.Flight;
import pwcg.mission.flight.FlightInformation;
import pwcg.mission.flight.plane.PlaneMCU;
import pwcg.mission.flight.waypoint.WaypointFactory;
import pwcg.mission.flight.waypoint.WaypointGeneratorUtils;
import pwcg.mission.flight.waypoint.WaypointType;
import pwcg.mission.mcu.McuCover;
import pwcg.mission.mcu.McuForceComplete;
import pwcg.mission.mcu.McuTimer;
import pwcg.mission.mcu.McuWaypoint;

public class EscortForPlayerFlight extends Flight
{
    protected McuCover cover = null;
    protected McuTimer coverTimer  = null;

    protected McuTimer forceCompleteTimer = null;
    protected McuForceComplete forceCompleteEntity = null;

    protected Coordinate rendevousCoord;
    
    protected Flight playerFlight;


    public EscortForPlayerFlight(FlightInformation flightInformation, MissionBeginUnit missionBeginUnit, Flight playerFlight) 
    {
        super(flightInformation, missionBeginUnit);
        this.playerFlight = playerFlight;                
    }

    @Override
    public void createUnitMission() throws PWCGException  
    {
        createWaypointPackage();
        createEscortPlaneInitialPosition();
        createWaypoints();
        createActivation();
        createFormation();
        setFlightPayload();
        createCover();
        createForceComplete();
    }

    @Override
    protected void createWaypoints() throws PWCGException
    {
        List<McuWaypoint> waypointList = createWaypoints(flightInformation.getMission(), playerFlight.getFlightInformation().getDepartureAirfield().getPosition());
        waypointPackage.setWaypoints(waypointList);
    }

    @Override
    protected void createFlightSpecificTargetAssociations()
    {
        linkWPToPlane(getLeadPlane(), waypointPackage.getWaypointsForLeadPlane());

        getMissionBeginUnit().linkToMissionBegin(getActivationTimer().getIndex());
        getActivationTimer().setTarget(getFormationTimer().getIndex());
        
        McuWaypoint ingressWP = WaypointGeneratorUtils.findWaypointByType(playerFlight.getAllWaypoints(), WaypointType.INGRESS_WAYPOINT.getName());
        ingressWP.setTarget(getCoverTimer().getIndex());

        McuWaypoint egressWP = WaypointGeneratorUtils.findWaypointByType(playerFlight.getAllWaypoints(), WaypointType.EGRESS_WAYPOINT.getName());
        egressWP.setTarget(forceCompleteTimer.getIndex());

    }

    public void createCover() throws PWCGException 
    {
        McuWaypoint ingressWP = WaypointGeneratorUtils.findWaypointByType(playerFlight.getAllWaypoints(), 
                        WaypointType.INGRESS_WAYPOINT.getName());

        Coordinate coverPosition = ingressWP.getPosition().copy();
        coverPosition.setYPos(coverPosition.getYPos() + 400);
        
        cover  = new McuCover();
        cover.setPosition(coverPosition);
        cover.setObject(planes.get(0).getEntity().getIndex());
        cover.setTarget(playerFlight.getPlanes().get(0).getEntity().getIndex());

        coverTimer  = new McuTimer();
        coverTimer.setName("Cover Timer for " + flightInformation.getSquadron().determineDisplayName(flightInformation.getCampaign().getDate()));
        coverTimer.setDesc("Cover " + playerFlight.getFlightInformation().getSquadron().determineDisplayName(flightInformation.getCampaign().getDate()));
        coverTimer.setPosition(coverPosition);
        coverTimer.setTarget(cover.getIndex());
        
    }

    protected void createForceComplete()
    {
        // Deactivate the cover entity
        forceCompleteEntity = new McuForceComplete();
        forceCompleteEntity.setName("Escort Cover Force Complete");
        forceCompleteEntity.setDesc("Escort Cover Force Complete");
        forceCompleteEntity.setOrientation(new Orientation());
        forceCompleteEntity.setPosition(flightInformation.getTargetCoords().copy());
        forceCompleteEntity.setObject(planes.get(0).getEntity().getIndex());
        
        forceCompleteTimer  = new McuTimer();
        forceCompleteTimer.setName("Escort Cover Force Complete Timer");
        forceCompleteTimer.setDesc("Escort Cover Force Complete Timer");
        forceCompleteTimer.setOrientation(new Orientation());
        forceCompleteTimer.setPosition(flightInformation.getTargetCoords().copy());
        forceCompleteTimer.setTimer(2);
        forceCompleteTimer.setTarget(forceCompleteEntity.getIndex());
        forceCompleteTimer.setTarget(getWaypointPackage().getWaypointsForLeadPlane().get(0).getIndex());

    }

    protected void createEscortPlaneInitialPosition() throws PWCGException 
    {
        PlaneMCU flightLeader = getFlightLeader();

        // Initial position is at the ingress WP
        McuWaypoint ingressWP = WaypointGeneratorUtils.findWaypointByType(playerFlight.getAllWaypoints(), 
                        WaypointType.INGRESS_WAYPOINT.getName());

        Coordinate coverPosition = ingressWP.getPosition().copy();
        coverPosition.setYPos(coverPosition.getYPos() + 400);

        int i = 0;
        for (PlaneMCU plane : planes)
        {
            Coordinate planeCoords = coverPosition.copy();
            
            IProductSpecificConfiguration productSpecificConfiguration = ProductSpecificConfigurationFactory.createProductSpecificConfiguration();
            int AircraftSpacingHorizontal = productSpecificConfiguration.getAircraftSpacingHorizontal();
            planeCoords.setXPos(coverPosition.getXPos() - (i * AircraftSpacingHorizontal));
            planeCoords.setZPos(coverPosition.getZPos() - (i * AircraftSpacingHorizontal));

            int AircraftSpacingVertical = productSpecificConfiguration.getAircraftSpacingVertical();            
            planeCoords.setYPos(coverPosition.getYPos() + (i * AircraftSpacingVertical));
            plane.setPosition(planeCoords);

            Orientation orient = new Orientation();
            orient.setyOri(ingressWP.getOrientation().getyOri());
            plane.setOrientation(orient);

            // This must be done last
            plane.populateEntity(this, flightLeader);
            ++i;
        }
    }

    public String getMissionObjective() throws PWCGException 
    {
        String objective = "Escort to the specified location and accompany them until they cross our lines.";
        String objectiveName =  formMissionObjectiveLocation(flightInformation.getTargetCoords().copy());
        if (!objectiveName.isEmpty())
        {
            objective = "Escort to the location" + objectiveName + 
                    ".  Accompany them until they cross our lines.";
        }
        
        return objective;
    }

    @Override
    protected List<McuWaypoint> createWaypoints(Mission mission, Coordinate startPosition) throws PWCGException
    {
        Coordinate rtbCoords = flightInformation.getDepartureAirfield().getPosition().copy();
        rtbCoords.setYPos(4000.0);

        Orientation orient = new Orientation();
        orient.setyOri(flightInformation.getDepartureAirfield().getOrientation().getyOri());

        List<McuWaypoint> waypoints = new ArrayList<>();
        
		McuWaypoint rtbWP = WaypointFactory.createReturnToBaseWaypointType();
        rtbWP.setTriggerArea(McuWaypoint.START_AREA);
        rtbWP.setSpeed(250);
        rtbWP.setPosition(rtbCoords);
        rtbWP.setOrientation(orient);
        
        waypoints.add(rtbWP);
        
        return waypoints;
    }

    @Override
    public void write(BufferedWriter writer) throws PWCGException 
    {
        super.write(writer);
        
        coverTimer.write(writer);
        cover.write(writer);
        
        forceCompleteTimer.write(writer);
        forceCompleteEntity.write(writer);
    }


    public McuCover getCover()
    {
        return cover;
    }


    public void setCover(McuCover cover)
    {
        this.cover = cover;
    }


    public McuTimer getCoverTimer()
    {
        return coverTimer;
    }


    public void setCoverTimer(McuTimer coverTimer)
    {
        this.coverTimer = coverTimer;
    }


    public McuTimer getForceCompleteTimer()
    {
        return forceCompleteTimer;
    }


    public void setForceCompleteTimer(McuTimer forceCompleteTimer)
    {
        this.forceCompleteTimer = forceCompleteTimer;
    }


    public McuForceComplete getForceCompleteEntity()
    {
        return forceCompleteEntity;
    }


    public void setForceCompleteEntity(McuForceComplete forceCompleteEntity)
    {
        this.forceCompleteEntity = forceCompleteEntity;
    }


    public Coordinate getRendevousCoord()
    {
        return rendevousCoord;
    }


    public void setRendevousCoord(Coordinate rendevousCoord)
    {
        this.rendevousCoord = rendevousCoord;
    }


    public Flight getPlayerFlight()
    {
        return playerFlight;
    }


    public void setPlayerFlight(Flight playerFlight)
    {
        this.playerFlight = playerFlight;
    }

    
}
