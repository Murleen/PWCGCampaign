package pwcg.mission.mcu.group;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pwcg.campaign.utils.IndexGenerator;
import pwcg.core.exception.PWCGException;
import pwcg.core.exception.PWCGIOException;
import pwcg.core.location.Coordinate;
import pwcg.core.utils.Logger;
import pwcg.mission.MissionStringHandler;
import pwcg.mission.flight.Flight;
import pwcg.mission.flight.plane.PlaneMCU;
import pwcg.mission.flight.waypoint.FormationGenerator;
import pwcg.mission.flight.waypoint.VirtualWayPointCoordinate;
import pwcg.mission.mcu.BaseFlightMcu;
import pwcg.mission.mcu.Coalition;
import pwcg.mission.mcu.McuCounter;
import pwcg.mission.mcu.McuDeactivate;
import pwcg.mission.mcu.McuSpawn;
import pwcg.mission.mcu.McuSubtitle;
import pwcg.mission.mcu.McuTimer;

public class VirtualWayPoint 
{    
    protected McuTimer vwpTimer = new McuTimer();

    protected McuTimer masterSpawnTimer = new McuTimer();
    protected McuTimer masterWpTriggerTimer = new McuTimer();

    protected Map<Integer, VmpSpawnContainer> vmpSpawnContainers = new HashMap<Integer, VmpSpawnContainer>();
    
    private int index = IndexGenerator.getInstance().getNextIndex();;
    
    protected VirtualWayPointCoordinate vwpCoordinate = null;

    protected List<McuSubtitle> subTitleList = new ArrayList<McuSubtitle>();
    
    protected boolean useSubtitles = false;


    private SelfDeactivatingCheckZone checkZone;
    
    private McuTimer nextVwpTimer = new McuTimer();

    private McuTimer nextVirtualWaypointTimer = new McuTimer();

    private McuTimer killVwpTimer = new McuTimer();
    private McuTimer vwpTriggeredTimer = new McuTimer();
    private McuDeactivate stopNextVwp = new McuDeactivate();
    
    public static int VWP_TRIGGGER_DISTANCE = 20000;
    
    public static int VWP_WAIT_TIME = 30;

    public VirtualWayPoint()
    {
        index = IndexGenerator.getInstance().getNextIndex();
    }

    public void initialize(
                    Flight flight,
                    VirtualWayPointCoordinate vwpCoordinate,
                    Coalition coalition) throws PWCGException 
    {
        this.vwpCoordinate = vwpCoordinate;                 

        buildMcus(vwpCoordinate);
        makeSubtitles(flight);
        setTargetAssociations(vwpCoordinate);
        generateSpawners(flight);
    }

    private void setTargetAssociations(VirtualWayPointCoordinate vwpCoordinate)
    {
        // Path of CZ triggered
        vwpTimer.setTarget(nextVwpTimer.getIndex());
    
        // 1. Deactivate our deactivate timer
        vwpTriggeredTimer.setTarget(stopNextVwp.getIndex());
        stopNextVwp.setTarget(nextVwpTimer.getIndex());
        
        // 2. Trigger the Spawn
        vwpTriggeredTimer.setTarget(masterSpawnTimer.getIndex());
        
        // Path of CZ Not triggered
        nextVwpTimer.setTarget(nextVirtualWaypointTimer.getIndex());

        // Set up activate and deactivate
        // virtualWPTimer activates the self deleting CZ
        // deactivateVWPTimer deactivates the self deleting CZ
        checkZone = new SelfDeactivatingCheckZone(vwpCoordinate.getCoordinate().copy(), VWP_TRIGGGER_DISTANCE);
        checkZone.setCheckZoneTarget(vwpTriggeredTimer.getIndex());
        checkZone.setAdditionalDeactivate(nextVwpTimer);
        
        // Trigger check zone from the vwp timer
        vwpTimer.setTarget(checkZone.getActivateEntryPoint());
        
        // If the plane counter goes off we want to kill the VWP without spawning
        // Link the kill to the stopNextVwp deactivate.
        // We bypass the vwpTriggeredTimer timer to avoid the spawn timer
        killVwpTimer.setTarget(stopNextVwp.getIndex());
        
        // We also want to terminate CZ porcessing
        killVwpTimer.setTarget(checkZone.getDeactivateEntryPoint());
    }

    private void buildMcus(VirtualWayPointCoordinate vwpCoordinate)
    {
        vwpTimer.setPosition(vwpCoordinate.getCoordinate().copy());
        vwpTimer.setName("VWP Timer");
        vwpTimer.setDesc("VWP Timer");
        vwpTimer.setTimer(1);

        masterSpawnTimer.setPosition(vwpCoordinate.getCoordinate().copy());
        masterSpawnTimer.setName("VWP Spawn Timer");
        masterSpawnTimer.setDesc("VWP Master Spawn Timer");
        masterSpawnTimer.setTimer(1);

        nextVwpTimer.setPosition(vwpCoordinate.getCoordinate().copy());
        nextVwpTimer.setName("VWP CZ Deactivate Timer");
        nextVwpTimer.setDesc("VWP CZ Deactivate Timer");
        nextVwpTimer.setTimer(VWP_WAIT_TIME);

        vwpTriggeredTimer.setPosition(vwpCoordinate.getCoordinate().copy());
        vwpTriggeredTimer.setName("VWP Trigger Timer");
        vwpTriggeredTimer.setDesc("VWP Stop Deactivate Timer");
        vwpTriggeredTimer.setTimer(0);

        stopNextVwp.setPosition(vwpCoordinate.getCoordinate().copy());
        stopNextVwp.setName("VWP Stop Next");
        stopNextVwp.setDesc("VWP Stop Deactivate");

        nextVirtualWaypointTimer.setPosition(vwpCoordinate.getCoordinate().copy());
        nextVirtualWaypointTimer.setName("Next VWP Timer");
        nextVirtualWaypointTimer.setDesc("Next VWP Timer");

        killVwpTimer.setPosition(vwpCoordinate.getCoordinate().copy());
        killVwpTimer.setName("Kill VWP Timer");
        killVwpTimer.setDesc("Kill VWP Timer");

        masterWpTriggerTimer.setPosition(vwpCoordinate.getCoordinate().copy());
        masterWpTriggerTimer.setName("Master WP Trigger Timer");
        masterWpTriggerTimer.setDesc("Master WP Trigger Timer");
    }

    protected void makeSubtitles(Flight flight) throws PWCGException
    {     
        if (!useSubtitles)
        {
            return;
        }

        MissionStringHandler subtitleHandler = MissionStringHandler.getInstance();

        String squadronName = flight.getSquadron().determineDisplayName(flight.getCampaign().getDate());
        
        String wpName = "Unknown";
        List<BaseFlightMcu>  planeMissionPoints = flight.getAllMissionPointsForPlane(flight.getPlanes().get(0));
        if (planeMissionPoints != null && !planeMissionPoints.isEmpty())
        {
            BaseFlightMcu planeMissionPoint = planeMissionPoints.get(vwpCoordinate.getWaypointindex());
            wpName = planeMissionPoint.getName();
        }
        
        McuSubtitle checkZoneSubtitle = new McuSubtitle();
        checkZoneSubtitle.setName("checkZone Subtitle");
        checkZoneSubtitle.setText("VWP " +  squadronName +  " CZ triggered " + wpName);
        checkZoneSubtitle.setPosition(vwpCoordinate.getCoordinate().copy());
        checkZone.setCheckZoneTarget(checkZoneSubtitle.getIndex());
        subTitleList.add(checkZoneSubtitle);
        
        subtitleHandler.registerMissionText(checkZoneSubtitle.getLcText(), checkZoneSubtitle.getText());
    }

    protected void generateSpawners(Flight flight) throws PWCGException
    {
        FormationGenerator formationGenerator = new FormationGenerator();
        List<Coordinate> flightCoordinates = formationGenerator.createPlaneInitialPosition(flight.getPlanes(), vwpCoordinate.getCoordinate(), vwpCoordinate.getOrientation());
        
        McuTimer lastSpawnTimer = null;
        
        for (int i = 0; i < flight.getPlanes().size(); ++i)
        {
            PlaneMCU plane = flight.getPlanes().get(i);
            Coordinate planeCoordinate = flightCoordinates.get(i);
            double planeAltitude = vwpCoordinate.getCoordinate().getYPos() + (30 * i);
            if (planeAltitude < 800.0)
            {
                planeAltitude = 800.0;
            }
            planeCoordinate.setYPos(planeAltitude);
            
            // Get the mission points (WPs plus attackarea) and select the one associated with
            // this VWP coordinate.
            List<BaseFlightMcu>  planeMissionPoints = flight.getAllMissionPointsForPlane(plane);

            // Since every plane has a matching set of missionpoints, the index will be
            // identical for each plane
            BaseFlightMcu planeMissionPoint = planeMissionPoints.get(vwpCoordinate.getWaypointindex());
            VmpSpawnContainer vmpSpawnContainer = new VmpSpawnContainer(planeMissionPoint);

            // Chain the spawns such that they happen one after the other
            if (lastSpawnTimer == null)
            {
                masterSpawnTimer.setTarget(vmpSpawnContainer.spawnTimer.getIndex());
            }
            else
            {
                lastSpawnTimer.setTarget(vmpSpawnContainer.spawnTimer.getIndex());
            }
            
            lastSpawnTimer = vmpSpawnContainer.spawnTimer;
            
            vmpSpawnContainer.create(plane, i, planeCoordinate);
            
            vmpSpawnContainers.put(plane.getIndex(), vmpSpawnContainer);
        }        
    }

    public void write(BufferedWriter writer) throws PWCGIOException 
    {
        try
        {
            writer.write("Group");
            writer.newLine();
            writer.write("{");
            writer.newLine();

            writer.write("  Name = \"Virtual WP\";");
            writer.newLine();
            writer.write("  Index = " + index + ";");
            writer.newLine();
            writer.write("  Desc = \"Virtual WP\";");
            writer.newLine();

            vwpTimer.write(writer);
            masterSpawnTimer.write(writer);
            
            checkZone.write(writer);
            nextVwpTimer.write(writer);
            nextVirtualWaypointTimer.write(writer);
            vwpTriggeredTimer.write(writer);
            stopNextVwp.write(writer);
            killVwpTimer.write(writer);
            
            for (VmpSpawnContainer vmpSpawnContainer : vmpSpawnContainers.values())
            {
                vmpSpawnContainer.write(writer);
            }
            
            for (int i = 0; i < subTitleList.size(); ++i)
            {
                McuSubtitle subtitle = subTitleList.get(i);
                subtitle.write(writer);
                writer.newLine();
            }

            writer.write("}");
            writer.newLine();
        }
        catch (IOException e)
        {
            Logger.logException(e);
            throw new PWCGIOException(e.getMessage());
        }
    }

    public McuTimer getEntryPoint()
    {
        return vwpTimer;
    }

    public void linkToNextVirtualWaypoint(VirtualWayPoint nextVWP)
    {
        // If the VWP did not trigger, this moves things to the next VWP
        nextVirtualWaypointTimer.setTarget(nextVWP.getEntryPoint().getIndex());
    }

    public void onTriggerAddTarget(PlaneMCU plane, int index)
    {
        vmpSpawnContainers.get(plane.getIndex()).wpActivateTimer.setTarget(index);
    }

    public VirtualWayPointCoordinate getCoordinate()
    {
        return vwpCoordinate;
    }

    public McuTimer getKillVwpTimer()
    {
        return killVwpTimer;
    }

    public McuTimer getNextVwpTimer()
    {
        return nextVwpTimer;
    }
    
    public void registerPlaneCounter(McuCounter counter)
    {
        for (VmpSpawnContainer vmpSpawnContainer : vmpSpawnContainers.values())
        {
            vmpSpawnContainer.spawnTimer.setTarget(counter.getIndex());
        }
    }
    
    /**
     * A sequence that used per plane after a VWP is activated.
     * Trigger the spawn and activate the associated WP
     */
    private class VmpSpawnContainer
    {
        private VmpSpawnContainer (BaseFlightMcu waypoint)
        {
            this.waypoint = waypoint;
        }
        
        private McuTimer spawnTimer = new McuTimer();
        private McuSpawn spawner = new McuSpawn();
        private McuTimer wpActivateTimer = new McuTimer();
        private BaseFlightMcu waypoint = null;
        
        private void create(PlaneMCU plane, int index, Coordinate planeCoordinate)
        {
            spawner.setPosition(planeCoordinate.copy());
            spawner.setOrientation(vwpCoordinate.getOrientation().copy());
            spawner.setObject(plane.getEntity().getIndex());
            spawner.setName("Spawn " + (index+1));
            spawner.setDesc("Spawn " + (index+1));
            
            spawnTimer.setTimer(1);
            spawnTimer.setPosition(planeCoordinate.copy());
            spawnTimer.setOrientation(vwpCoordinate.getOrientation().copy());
            spawnTimer.setName("Spawn Timer " + (index+1));
            spawnTimer.setDesc("Spawn Timer " + (index+1));
            
            
            wpActivateTimer.setTimer(1);
            wpActivateTimer.setPosition(planeCoordinate.copy());
            wpActivateTimer.setOrientation(vwpCoordinate.getOrientation().copy());
            wpActivateTimer.setName("Spawn WP Timer " + (index+1));
            wpActivateTimer.setDesc("Spawn WP Timer " + (index+1));

            spawnTimer.setTarget(spawner.getIndex());
            spawnTimer.setTarget(wpActivateTimer.getIndex());
            
            wpActivateTimer.setTarget(waypoint.getIndex());
        }
        
        private void write(BufferedWriter writer) throws PWCGIOException 
        {
            spawnTimer.write(writer);
            spawner.write(writer);
            wpActivateTimer.write(writer);
        }
    }

    public SelfDeactivatingCheckZone getTriggerCheckZone()
    {
        return checkZone;
    }
}
