package pwcg.mission.mcu.group;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pwcg.campaign.squadron.Squadron;
import pwcg.campaign.utils.IndexGenerator;
import pwcg.core.config.ConfigItemKeys;
import pwcg.core.config.ConfigManagerCampaign;
import pwcg.core.exception.PWCGException;
import pwcg.core.exception.PWCGIOException;
import pwcg.core.location.Coordinate;
import pwcg.core.utils.Logger;
import pwcg.mission.MissionStringHandler;
import pwcg.mission.flight.Flight;
import pwcg.mission.flight.plane.PlaneMCU;
import pwcg.mission.mcu.Coalition;
import pwcg.mission.mcu.McuDelete;
import pwcg.mission.mcu.McuProximity;
import pwcg.mission.mcu.McuSubtitle;
import pwcg.mission.mcu.McuTimer;

public class PlaneRemover
{
    private McuProximity outOfEnemyRangeProximity = null;
    private McuTimer outOfEnemyRangeProximityTimer = new McuTimer();
    
    private McuTimer deletePlaneTimer = new McuTimer();
    private McuDelete deletePlane = new McuDelete();
    
    private List<McuSubtitle> subTitleList = new ArrayList<McuSubtitle>();
    
    private int index = IndexGenerator.getInstance().getNextIndex();;
    
    private boolean useSubtitles = false;

    public PlaneRemover()
    {
        index = IndexGenerator.getInstance().getNextIndex();
    }

    public void initialize(Flight flight, PlaneMCU plane) throws PWCGException 
    {
        outOfEnemyRangeProximity = new McuProximity();
        outOfEnemyRangeProximity.addCoalition(Coalition.getFriendlyCoalition(plane.getCountry()));
        outOfEnemyRangeProximity.addCoalition(Coalition.getEnemyCoalition(plane.getCountry()));
        
        // set position
        outOfEnemyRangeProximityTimer.setPosition(plane.getPosition().copy());
        outOfEnemyRangeProximity.setPosition(plane.getPosition().copy());
        deletePlaneTimer.setPosition(plane.getPosition().copy());
        deletePlane.setPosition(plane.getPosition().copy());

        // set name
        outOfEnemyRangeProximityTimer.setName("outOfEnemyRangeProximityTimer");
        outOfEnemyRangeProximity.setName("outOfEnemyRangeProximity");
        deletePlaneTimer.setName("deletePlaneTimer");
        deletePlane.setName("deletePlane");

        // set name
        outOfEnemyRangeProximityTimer.setDesc("outOfEnemyRangeProximityTimer");
        outOfEnemyRangeProximity.setDesc("outOfEnemyRangeProximity");
        deletePlaneTimer.setDesc("deletePlaneTimer");
        deletePlane.setDesc("deletePlane");
        
        // Timer values
        outOfEnemyRangeProximityTimer.setTimer(60);
        deletePlaneTimer.setTimer(3);
        
        // Coalition is enemy
        ConfigManagerCampaign configManager = flight.getCampaign().getCampaignConfigManager();

        // Enemy proximity is based on coalition
        int enemyDistance = configManager.getIntConfigParam(ConfigItemKeys.PlaneDeleteEnemyDistanceKey);
        outOfEnemyRangeProximity.setCloser(0);
        outOfEnemyRangeProximity.setDistance(enemyDistance);

        if (useSubtitles)
        {
            makeSubtitles(flight, plane);
        }
        
        // Link up targets
        outOfEnemyRangeProximityTimer.setTarget(outOfEnemyRangeProximity.getIndex());
        outOfEnemyRangeProximity.setTarget(deletePlaneTimer.getIndex());
        deletePlaneTimer.setTarget(deletePlane.getIndex());
        
        deletePlane.setObject(plane.getEntity().getIndex());
    }

    protected void makeSubtitles(Flight flight, PlaneMCU plane) throws PWCGException
    {
        Coordinate coordinate = plane.getPosition().copy();
        
        Squadron squadron = flight.getSquadron();
        
        Date campaignDate = flight.getCampaign().getDate();
        
        McuSubtitle planeRemoverStartedSubtitle = new McuSubtitle();
        planeRemoverStartedSubtitle.setName("planeRemoverStartedSubtitle Subtitle");
        planeRemoverStartedSubtitle.setText("Plane Remover Started " +  squadron.determineDisplayName(campaignDate) +  " for " + plane.getName());
        planeRemoverStartedSubtitle.setPosition(coordinate.copy());
        outOfEnemyRangeProximityTimer.setTarget(planeRemoverStartedSubtitle.getIndex());
        
        McuSubtitle noNearbyPlaneSubtitle = new McuSubtitle();
        noNearbyPlaneSubtitle.setName("noPlanesSubtitle Subtitle");
        noNearbyPlaneSubtitle.setText("No planes " +  squadron.determineDisplayName(campaignDate) +  " Triggered for " + plane.getName());
        noNearbyPlaneSubtitle.setPosition(coordinate.copy());
        outOfEnemyRangeProximity.setTarget(noNearbyPlaneSubtitle.getIndex());
        subTitleList.add(noNearbyPlaneSubtitle);

        McuSubtitle deletePlaneSubtitle = new McuSubtitle();
        deletePlaneSubtitle.setName("deletePlane Subtitle");
        deletePlaneSubtitle.setText("DEL " +  squadron.determineDisplayName(campaignDate) +  " Delete Triggered for " + plane.getName());
        deletePlaneSubtitle.setPosition(coordinate.copy());
        deletePlaneTimer.setTarget(deletePlaneSubtitle.getIndex());
        subTitleList.add(deletePlaneSubtitle);
        
        MissionStringHandler subtitleHandler = MissionStringHandler.getInstance();
        subtitleHandler.registerMissionText(planeRemoverStartedSubtitle.getLcText(), planeRemoverStartedSubtitle.getText());
        subtitleHandler.registerMissionText(noNearbyPlaneSubtitle.getLcText(), noNearbyPlaneSubtitle.getText());
        subtitleHandler.registerMissionText(deletePlaneSubtitle.getLcText(), deletePlaneSubtitle.getText());
    }

    public void write(BufferedWriter writer) throws PWCGIOException 
    {
        try
        {

            if (deletePlane.getObjects().size() == 0)
            {
                return;
            }

            writer.write("Group");
            writer.newLine();
            writer.write("{");
            writer.newLine();
            
            writer.write("  Name = \"Plane Remover\";");
            writer.newLine();
            writer.write("  Index = " + index + ";");
            writer.newLine();
            writer.write("  Desc = \"Plane Remover\";");
            writer.newLine();
            
            outOfEnemyRangeProximityTimer.write(writer);
            outOfEnemyRangeProximity.write(writer);
            
            deletePlaneTimer.write(writer);
            deletePlane.write(writer);
            
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

    public McuTimer getDeletePlaneTimer()
    {
        return deletePlaneTimer;
    }

    public McuTimer getEntryPoint()
    {
        return this.outOfEnemyRangeProximityTimer;
    }

}
