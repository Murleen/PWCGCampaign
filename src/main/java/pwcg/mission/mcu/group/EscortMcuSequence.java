package pwcg.mission.mcu.group;

import java.io.BufferedWriter;

import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.core.location.Orientation;
import pwcg.mission.flight.IFlight;
import pwcg.mission.flight.plane.PlaneMcu;
import pwcg.mission.flight.waypoint.WaypointGeneratorUtils;
import pwcg.mission.flight.waypoint.WaypointType;
import pwcg.mission.mcu.McuCover;
import pwcg.mission.mcu.McuDeactivate;
import pwcg.mission.mcu.McuEvent;
import pwcg.mission.mcu.McuForceComplete;
import pwcg.mission.mcu.McuProximity;
import pwcg.mission.mcu.McuTimer;
import pwcg.mission.mcu.McuWaypoint;

public class EscortMcuSequence
{
    private McuTimer escortProximityTimer;
    private McuProximity escortProximity;
    private McuDeactivate escortProximityDeactivate;
    private McuCover cover = null;
    private McuTimer coverTimer  = null;
    private McuTimer forceCompleteTimer;
    private McuForceComplete forceComplete;
    private McuTimer escortCompleteTimer;
    
    private IFlight escortFlight;
    private IFlight escortedFlight;
    
    public EscortMcuSequence(IFlight escortedFlight, IFlight escortFlight) throws PWCGException
    {
        this.escortedFlight = escortedFlight;
        this.escortFlight = escortFlight;
    }
    
    public void createEscortSequence() throws PWCGException
    {
        createProximity();
        createCover();
        createForceComplete();
    }
    
    public void finalize() throws PWCGException
    {
        createTargetAssociations();
    }

    private void createProximity()
    {
        PlaneMcu flightLeader = escortFlight.getFlightPlanes().getFlightLeader();
        PlaneMcu escortedLeader = escortedFlight.getFlightPlanes().getFlightLeader();
        Coordinate rendevousPosition = getCoverPosition();

        escortProximityTimer = new McuTimer();
        escortProximityTimer.setName("Escort proximity timer");
        escortProximityTimer.setPosition(rendevousPosition);

        escortProximity = new McuProximity();
        escortProximity.setName("Escort proximity check");
        escortProximity.setDesc("Escort proximity check");
        escortProximity.setPosition(rendevousPosition);
        escortProximity.setCloser(1);
        escortProximity.setDistance(3000);
        escortProximity.setObject(flightLeader.getEntity().getIndex());
        escortProximity.setObject(escortedLeader.getEntity().getIndex());

        escortProximityDeactivate = new McuDeactivate();
        escortProximityDeactivate.setName("Escort proximity deactivate");
        escortProximityDeactivate.setPosition(rendevousPosition);

        McuEvent onTookOffEvent = new McuEvent();
        onTookOffEvent.setType(McuEvent.ONPLANETOOKOFF);
        onTookOffEvent.setTarId(escortProximityTimer.getIndex());
        flightLeader.getEntity().addEvent(onTookOffEvent);
}

    public void createCover() throws PWCGException 
    {
        PlaneMcu flightLeader = escortFlight.getFlightPlanes().getFlightLeader();
        Coordinate rendevousPosition = getCoverPosition();
        
        cover  = new McuCover();
        cover.setPosition(rendevousPosition);
        cover.setObject(flightLeader.getEntity().getIndex());
        cover.setTarget(escortedFlight.getFlightPlanes().getFlightLeader().getEntity().getIndex());

        coverTimer  = new McuTimer();
        coverTimer.setName("Cover Timer");
        coverTimer.setDesc("Cover");
        coverTimer.setPosition(rendevousPosition);
    }

    private Coordinate getCoverPosition()
    {
        McuWaypoint rendezvousWP = WaypointGeneratorUtils.findWaypointByType(escortedFlight.getWaypointPackage().getAllWaypoints(), 
                WaypointType.RENDEZVOUS_WAYPOINT.getName());

        Coordinate coverPosition = rendezvousWP.getPosition().copy();
        coverPosition.setYPos(coverPosition.getYPos() + 400);
        return coverPosition;
    }

    private void createForceComplete() throws PWCGException
    {
        PlaneMcu flightLeader = escortFlight.getFlightPlanes().getFlightLeader();
        Coordinate rendevousPosition = getCoverPosition();

        forceCompleteTimer = new McuTimer();
        forceCompleteTimer.setName("Escort Cover Force Complete Timer");
        forceCompleteTimer.setDesc("Escort Cover Force Complete Timer");
        forceCompleteTimer.setOrientation(new Orientation());
        forceCompleteTimer.setPosition(rendevousPosition);
        forceCompleteTimer.setTimer(1);

        forceComplete = new McuForceComplete();
        forceComplete.setName("Escort Cover Force Complete");
        forceComplete.setDesc("Escort Cover Force Complete");
        forceComplete.setOrientation(new Orientation());
        forceComplete.setPosition(rendevousPosition);
        forceComplete.setObject(flightLeader.getEntity().getIndex());

        escortCompleteTimer = new McuTimer();
        escortCompleteTimer.setName("Escort Complete Timer");
        escortCompleteTimer.setDesc("Escort Complete Timer");
        escortCompleteTimer.setOrientation(new Orientation());
        escortCompleteTimer.setPosition(rendevousPosition);
        escortCompleteTimer.setTimer(1);
    }
    
    private void createTargetAssociations()
    {
        McuWaypoint rtbWP = WaypointGeneratorUtils.findWaypointByType(escortFlight.getWaypointPackage().getAllWaypoints(), 
                WaypointType.EGRESS_WAYPOINT.getName());

        escortProximityTimer.setTarget(escortProximity.getIndex());
        escortProximity.setTarget(coverTimer.getIndex());
        escortProximityDeactivate.setTarget(escortProximity.getIndex());

        coverTimer.setTarget(cover.getIndex());
        coverTimer.setTarget(escortProximityDeactivate.getIndex());
        
        forceCompleteTimer.setTarget(forceComplete.getIndex());
        forceCompleteTimer.setTarget(escortCompleteTimer.getIndex());
        escortCompleteTimer.setTarget(rtbWP.getIndex());
    }
    
    public void write(BufferedWriter writer) throws PWCGException 
    {
        coverTimer.write(writer);
        cover.write(writer);
        escortProximityTimer.write(writer);
        escortProximity.write(writer);
        escortProximityDeactivate.write(writer);
        forceCompleteTimer.write(writer);
        forceComplete.write(writer);
        // write escort complete timer?
    }

    public Coordinate getPosition()
    {
        return cover.getPosition().copy();
    }

    public int getCoverEntry()
    {
        return coverTimer.getIndex();
    }

    public void setLinkToNextTarget(int nextIndex)
    {
        forceCompleteTimer.setTarget(nextIndex);        
    }

    // These getters are for test purposes
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

    public McuTimer getForceCompleteTimer()
    {
        return forceCompleteTimer;
    }

    public McuForceComplete getForceComplete()
    {
        return forceComplete;
    }

    public McuTimer getEscortCompleteTimer()
    {
        return escortCompleteTimer;
    }

    public IFlight getEscortFlight()
    {
        return escortFlight;
    }

    public IFlight getEscortedFlight()
    {
        return escortedFlight;
    }
}
