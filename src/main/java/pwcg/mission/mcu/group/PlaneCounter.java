package pwcg.mission.mcu.group;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pwcg.campaign.utils.IndexGenerator;
import pwcg.core.exception.PWCGIOException;
import pwcg.core.location.Coordinate;
import pwcg.core.utils.Logger;
import pwcg.mission.MissionStringHandler;
import pwcg.mission.flight.Flight;
import pwcg.mission.flight.waypoint.VirtualWaypointPackage;
import pwcg.mission.flight.waypoint.WaypointPackage;
import pwcg.mission.mcu.McuCounter;
import pwcg.mission.mcu.McuSubtitle;
import pwcg.mission.mcu.McuTimer;

/**
 * @author Patrick Wilson
 * 
 * PlaneCounter keeps track of the number of planes that have spawned
 * and kills the spawners for any other planes when the limit has
 * been reached.
 *
 */
public class PlaneCounter
{

    private McuCounter planeCounter = new McuCounter();
    private McuTimer disableMorePlanesTimer = new McuTimer();

    private List<McuSubtitle> subTitleList = new ArrayList<McuSubtitle>();

    private boolean useSubtitles = false;

    private int index = IndexGenerator.getInstance().getNextIndex();;

    /**
     * 
     */
     public PlaneCounter()
    {
        index = IndexGenerator.getInstance().getNextIndex();
        
        planeCounter.setCounter(12);
    }

    /**
     * @param plane
     */
     public void initialize(Coordinate mcuCoordinate) 
     {
         // set position
         planeCounter.setPosition(mcuCoordinate);
         disableMorePlanesTimer.setPosition(mcuCoordinate);

         // set name
         planeCounter.setName("planeCounter");
         disableMorePlanesTimer.setName("disableMorePlanesTimer ");

         // set name
         planeCounter.setDesc("planeCounter");
         disableMorePlanesTimer.setDesc("disableMorePlanesTimer");

         // Timer values
         disableMorePlanesTimer.setTimer(1);


         if (useSubtitles)
         {
             makeSubtitles(mcuCoordinate);
         }


         // Link up targets
         planeCounter.setTarget(disableMorePlanesTimer.getIndex());
     }


     /**
     * @param mcuCoordinate
     */
    protected void makeSubtitles(Coordinate mcuCoordinate)
     {
         McuSubtitle planeCounterSubtitle = new McuSubtitle();
         planeCounterSubtitle.setName("planeCounterSubtitle Subtitle");
         planeCounterSubtitle.setText("Plane Counter Triggered");
         planeCounterSubtitle.setPosition(mcuCoordinate.copy());
         disableMorePlanesTimer.setTarget(planeCounterSubtitle.getIndex());
         subTitleList.add(planeCounterSubtitle);
         
         MissionStringHandler subtitleHandler = MissionStringHandler.getInstance();
         subtitleHandler.registerMissionText(planeCounterSubtitle.getLcText(), planeCounterSubtitle.getText());
     }

     /**
     * @param flight
     * @
     */
    public void setPlaneCounterForFlight(Flight flight) 
     {
         WaypointPackage wpPackage = flight.getWaypointPackage();
         VirtualWaypointPackage virtualWPackage = null;
         if (wpPackage instanceof VirtualWaypointPackage)
         {
             virtualWPackage = (VirtualWaypointPackage)wpPackage;

             for (VirtualWayPoint vwp : virtualWPackage.getVirtualWaypoints())
             {
                 // Link this VWP spawners to the plane counter
                 vwp.registerPlaneCounter(this.planeCounter);
                 
                 // If it is a CZ VWP, set disable when plane count reaches maximum
                 if (vwp instanceof VirtualWayPoint)
                 {
                     VirtualWayPoint vwpCZ = (VirtualWayPoint)vwp;
                     disableMorePlanesTimer.setTarget(vwpCZ.getKillVwpTimer().getIndex());
                  }
             }
         }
     }

     /**
      * Write the mission to a file
      * 
      * @param writer
     * @throws PWCGIOException 
      * @
      */
     public void write(BufferedWriter writer) throws PWCGIOException 
     {
         try
        {
            writer.write("Group");
             writer.newLine();
             writer.write("{");
             writer.newLine();

             writer.write("  Name = \"Plane Counter\";");
             writer.newLine();
             writer.write("  Index = " + index + ";");
             writer.newLine();
             writer.write("  Desc = \"Plane Counter\";");
             writer.newLine();

             planeCounter.write(writer);
             disableMorePlanesTimer.write(writer);

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
     
     /**
     * @param numPlanes
     */
    public void setPlaneCounter(int numPlanes)
     {
         planeCounter.setCounter(numPlanes);
     }
}

