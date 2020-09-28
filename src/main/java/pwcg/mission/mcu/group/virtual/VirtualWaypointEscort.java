package pwcg.mission.mcu.group.virtual;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import pwcg.core.exception.PWCGException;
import pwcg.mission.MissionStringHandler;
import pwcg.mission.flight.IFlightInformation;
import pwcg.mission.flight.plane.PlaneMcu;
import pwcg.mission.flight.waypoint.virtual.VirtualWayPointCoordinate;
import pwcg.mission.mcu.McuActivate;
import pwcg.mission.mcu.McuCover;
import pwcg.mission.mcu.McuSubtitle;
import pwcg.mission.mcu.McuTimer;

public class VirtualWaypointEscort
{
    private VirtualWaypointPlanes vwpPlanes;
    private VirtualWayPointCoordinate vwpCoordinate;
    private VirtualWaypointActivate vwpActivate;

    private McuTimer activateEscortTimer = new McuTimer();
    private McuActivate activateEscort = new McuActivate();
    private McuTimer coverTimer = null;
    private McuCover cover = null;
    private List<PlaneMcu> escortAtActivate = new ArrayList<>();

    private List<McuSubtitle> subTitleList = new ArrayList<McuSubtitle>();

    public VirtualWaypointEscort(VirtualWayPointCoordinate vwpCoordinate, VirtualWaypointPlanes vwpPlanes, VirtualWaypointActivate vwpActivate)
    {
        this.vwpCoordinate = vwpCoordinate;
        this.vwpPlanes = vwpPlanes;
        this.vwpActivate = vwpActivate;
    }

    public void buildEscort(IFlightInformation vwpEscortFlightInformation) throws PWCGException
    {
        buildEscortPlanes(vwpEscortFlightInformation);
        buildPayloadForEscorts();
        buildMcus();
        makeSubtitles();
        createTargetAssociations();
        createObjectAssociations();
        linkEscortToActivateFlight();
    }

    public void write(BufferedWriter writer) throws PWCGException
    {
        for (PlaneMcu plane : escortAtActivate)
        {
            plane.write(writer);
        }

        activateEscortTimer.write(writer);
        activateEscort.write(writer);
        coverTimer.write(writer);
        cover.write(writer);

        McuSubtitle.writeSubtitles(subTitleList, writer);
    }

    private void buildEscortPlanes(IFlightInformation vwpEscortFlightInformation) throws PWCGException
    {
        int altitudeOffset = 500;
        VirtualWaypointPlaneBuilder vwpPlaneBuilder = new VirtualWaypointPlaneBuilder(vwpCoordinate, altitudeOffset);
        escortAtActivate = vwpPlaneBuilder.buildVwpPlanes(vwpEscortFlightInformation.getPlanes());
    }

    private void buildPayloadForEscorts() throws PWCGException
    {
        for (PlaneMcu escortPlane : escortAtActivate)
        {
            escortPlane.buildStandardPlanePayload();
        }
    }

    private void buildMcus()
    {
        activateEscort.setPosition(vwpCoordinate.getPosition().copy());
        activateEscort.setOrientation(vwpCoordinate.getOrientation().copy());
        activateEscort.setName("Activate Escort");
        activateEscort.setDesc("Activate Escort");

        activateEscortTimer.setTimer(1);
        activateEscortTimer.setPosition(vwpCoordinate.getPosition().copy());
        activateEscortTimer.setOrientation(vwpCoordinate.getOrientation().copy());
        activateEscortTimer.setName("Activate Escort Timer");
        activateEscortTimer.setDesc("Activate Escort Timer");

        cover = new McuCover();
        cover.setName("Virtual Escort Cover");
        cover.setDesc("Virtual Escort Cover");
        cover.setPosition(vwpCoordinate.getPosition());

        coverTimer = new McuTimer();
        coverTimer.setName("Virtual Escort Cover Timer");
        coverTimer.setDesc("Virtual Escort Cover Timer");
        coverTimer.setPosition(vwpCoordinate.getPosition());
    }

    private void makeSubtitles() throws PWCGException
    {
        McuSubtitle escortActivatedSubtitle = new McuSubtitle();
        escortActivatedSubtitle.setName("CheckZone Subtitle");
        escortActivatedSubtitle.setText("VWP Activate Escort: " +   vwpPlanes.getLeadActivatePlane().getLinkTrId() + " " + vwpPlanes.getLeadActivatePlane().getName());
        escortActivatedSubtitle.setPosition(vwpCoordinate.getPosition().copy());
        escortActivatedSubtitle.setDuration(5);
        subTitleList.add(escortActivatedSubtitle);
        
        MissionStringHandler subtitleHandler = MissionStringHandler.getInstance();
        subtitleHandler.registerMissionText(escortActivatedSubtitle.getLcText(), escortActivatedSubtitle.getText());
        
        activateEscortTimer.setTarget(escortActivatedSubtitle.getIndex());
    }

    private void createTargetAssociations()
    {
        activateEscortTimer.setTarget(activateEscort.getIndex());
        activateEscortTimer.setTarget(coverTimer.getIndex());
        coverTimer.setTarget(cover.getIndex());
        cover.setTarget(vwpPlanes.getLeadActivatePlane().getLinkTrId());
    }

    private void createObjectAssociations()
    {
        for (PlaneMcu escortPlane : escortAtActivate)
        {
            activateEscort.setObject(escortPlane.getEntity().getIndex());
        }
        
        cover.setObject(escortAtActivate.get(0).getLinkTrId());
    }

    private void linkEscortToActivateFlight()
    {
        vwpActivate.linkToEscort(activateEscortTimer.getIndex());
    }
}
