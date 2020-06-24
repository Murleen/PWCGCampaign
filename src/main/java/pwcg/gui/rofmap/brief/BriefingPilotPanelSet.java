package pwcg.gui.rofmap.brief;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import pwcg.campaign.Campaign;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.squadmember.SquadronMember;
import pwcg.campaign.squadron.Squadron;
import pwcg.campaign.utils.AutoStart;
import pwcg.campaign.utils.PlanesOwnedManager;
import pwcg.core.exception.PWCGException;
import pwcg.core.exception.PWCGIOException;
import pwcg.core.utils.MissionLogFileValidator;
import pwcg.core.utils.PWCGLogger;
import pwcg.gui.CampaignGuiContextManager;
import pwcg.gui.UiImageResolver;
import pwcg.gui.campaign.home.CampaignHome;
import pwcg.gui.dialogs.ErrorDialog;
import pwcg.gui.helper.BriefingMissionFlight;
import pwcg.gui.helper.PlayerFlightEditor;
import pwcg.gui.sound.SoundManager;
import pwcg.gui.utils.ImageResizingPanel;
import pwcg.gui.utils.PWCGButtonFactory;
import pwcg.mission.Mission;
import pwcg.mission.flight.IFlight;
import pwcg.mission.flight.crew.CrewPlanePayloadPairing;
import pwcg.mission.flight.plane.PlaneMcu;
import pwcg.mission.io.MissionFileWriter;

public class BriefingPilotPanelSet extends ImageResizingPanel implements ActionListener, IFlightChanged
{
    private static final long serialVersionUID = 1L;
    private CampaignHome campaignHomeGui;
    private Campaign campaign;
    private Mission mission;
    private JPanel briefingMapCenterPanel;
    private BriefingPilotChalkboard pilotPanel;
    private BriefingContext briefingContext;
    private Map<Integer, BriefingPlaneModificationsPicker> planeModifications = new HashMap<>();
    private BriefingFlightChooser briefingFlightChooser;

    public BriefingPilotPanelSet(Campaign campaign, CampaignHome campaignHomeGui, BriefingContext briefingContext, Mission mission)
    {
        super("");
        this.setLayout(new BorderLayout());
        

        this.campaign = campaign;
        this.campaignHomeGui = campaignHomeGui;
        this.briefingContext = briefingContext;
        this.mission = mission;
    }

    public void makePanels()
    {
        try
        {
            String imagePath = UiImageResolver.getImageMain("CampaignHome.jpg");
            this.setImage(imagePath);

            this.removeAll();
            
            briefingFlightChooser = new BriefingFlightChooser(mission, this);
            briefingFlightChooser.createBriefingSquadronSelectPanel();

            this.add(BorderLayout.WEST, makeLeftPanel());
            this.add(BorderLayout.CENTER, createCenterPanel());
        }
        catch (Exception e)
        {
            PWCGLogger.logException(e);
            ErrorDialog.internalError(e.getMessage());
        }
    }

    private JPanel makeLeftPanel() throws PWCGException 
    {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setOpaque(false);

        JPanel buttonPanel = makeButtonPanel();
        leftPanel.add(buttonPanel, BorderLayout.NORTH);
        leftPanel.add(briefingFlightChooser.getFlightChooserPanel(), BorderLayout.CENTER);
        return leftPanel;
    }

    private JPanel makeButtonPanel() throws PWCGException
    {
        JPanel pilotAssignmentNavPanel = new JPanel(new BorderLayout());
        pilotAssignmentNavPanel.setOpaque(false);

        JPanel buttonGrid = new JPanel(new GridLayout(0, 1));
        buttonGrid.setOpaque(false);

        JButton payloadAsLeaderButton = PWCGButtonFactory.makeMenuButton("Synchronize Payload", "Synchronize Payload", this);
        buttonGrid.add(payloadAsLeaderButton);
        buttonGrid.add(PWCGButtonFactory.makeDummy());

        JButton scrubButton = PWCGButtonFactory.makeMenuButton("Scrub Mission", "Scrub Mission", this);
        buttonGrid.add(scrubButton);
        buttonGrid.add(PWCGButtonFactory.makeDummy());

        JButton backToMapButton = PWCGButtonFactory.makeMenuButton("Back To Map", "Back To Map", this);
        buttonGrid.add(backToMapButton);
        buttonGrid.add(PWCGButtonFactory.makeDummy());

        if (!mission.isFinalized())
        {
            JButton acceptMissionButton = PWCGButtonFactory.makeMenuButton("Accept Mission", "Accept Mission", this);
            buttonGrid.add(acceptMissionButton);
            buttonGrid.add(PWCGButtonFactory.makeDummy());
        }
        else
        {
            JButton backToCampaignButton = PWCGButtonFactory.makeMenuButton("Back To Campaign", "Back To Campaign", this);
            buttonGrid.add(backToCampaignButton);
            buttonGrid.add(PWCGButtonFactory.makeDummy());
        }
        
        if (PwcgGuiModSupport.isRunningIntegrated() || PwcgGuiModSupport.isRunningDebrief())
        {
            JButton flyMissionButton = PWCGButtonFactory.makeMenuButton("Fly Mission", "Fly Mission", this);
            buttonGrid.add(flyMissionButton);
        }
        pilotAssignmentNavPanel.add(buttonGrid, BorderLayout.NORTH);

        return pilotAssignmentNavPanel;
    }

    private JPanel createCenterPanel() throws PWCGException
    {
        if (briefingMapCenterPanel != null)
        {
            this.remove(briefingMapCenterPanel);
        }
        
        briefingMapCenterPanel = new JPanel(new BorderLayout());
        briefingMapCenterPanel.setOpaque(false);

        pilotPanel = new BriefingPilotChalkboard(briefingContext, this);
        pilotPanel.makePanel();
        briefingMapCenterPanel.add(pilotPanel, BorderLayout.CENTER);
        
        return briefingMapCenterPanel;
    }

    public void actionPerformed(ActionEvent ae)
    {
        try
        {
            String action = ae.getActionCommand();
            if (action.equalsIgnoreCase("Back To Map"))
            {
                CampaignGuiContextManager.getInstance().popFromContextStack();
                return;
            }
            else if (action.equals("Synchronize Payload"))
            {
                synchronizePayload();
                synchronizeModifications();
                refreshPilotDisplay();
            }
            else if (action.equals("Scrub Mission"))
            {
                scrubMission();
            }
            else if (action.equals("Accept Mission"))
            {
                acceptMission();
            }
            else if (action.equals("Back To Campaign"))
            {
                backToCampaign();
            }
            else if (action.equals("Fly Mission"))
            {
                flyMission();
            }
            else if (action.contains("Change Plane:"))
            {
                changePlaneForPilot(action);
            }
            else if (action.contains("Change Payload:"))
            {
                changePayloadForPlane(action);
            }
            else if (action.contains("SelectPlaneModification:"))
            {
                changeModificationsForPlane(action);
            }
            else if (action.contains("Assign Pilot:"))
            {
                assignPilot(action);
            }
            else if (action.contains("Unassign Pilot:"))
            {
                unassignPilot(action);
            }
        }
        catch (Exception e)
        {
            PWCGLogger.logException(e);
            ErrorDialog.internalError(e.getMessage());
        }
    }

    private void changePlaneForPilot(String action) throws PWCGException
    {
        if (!mission.isFinalized())
        {
            BriefingMissionFlight briefingMissionHandler = briefingContext.getActiveBriefingHandler();
            Integer pilotSerialNumber = getPilotSerialNumberFromAction(action);

            BriefingPlanePicker briefingPlanePicker = new BriefingPlanePicker(briefingMissionHandler, this);
            String newPlaneChoice = briefingPlanePicker.pickPlane(pilotSerialNumber);
            if (newPlaneChoice != null)
            {
                int index = newPlaneChoice.indexOf(":");
                index += 2;
                String planeSerialNumberString = newPlaneChoice.substring(index);
                Integer planeSerialNumber = Integer.valueOf(planeSerialNumberString);

                briefingMissionHandler.changePlane(pilotSerialNumber, planeSerialNumber);
            }

            refreshPilotDisplay();
        }
    }

    private void assignPilot(String action) throws PWCGException
    {
        if (!mission.isFinalized())
        {
            BriefingMissionFlight briefingMissionHandler = briefingContext.getActiveBriefingHandler();
            if (briefingMissionHandler.getBriefingAssignmentData().getUnassignedPlanes().size() > 0)
            {
                Integer pilotSerialNumber = getPilotSerialNumberFromAction(action);

                briefingMissionHandler.assignPilotFromBriefing(pilotSerialNumber);
                refreshPilotDisplay();
            }
        }
    }

    private void unassignPilot(String action) throws PWCGException
    {
        if (!mission.isFinalized())
        {
            BriefingMissionFlight briefingMissionHandler = briefingContext.getActiveBriefingHandler();
            Integer pilotSerialNumber = getPilotSerialNumberFromAction(action);

            CrewPlanePayloadPairing planeCrew = briefingMissionHandler.getPairingByPilot(pilotSerialNumber);
            SquadronMember squadronMember = planeCrew.getPilot();
            briefingMissionHandler.unassignPilotFromBriefing(squadronMember.getSerialNumber());
            refreshPilotDisplay();
        }
    }

    private void changePayloadForPlane(String action) throws PWCGException
    {
        if (!mission.isFinalized())
        {
            BriefingMissionFlight briefingMissionHandler = briefingContext.getActiveBriefingHandler();
            Integer pilotSerialNumber = getPilotSerialNumberFromAction(action);
            CrewPlanePayloadPairing crewPlane = briefingMissionHandler.getPairingByPilot(pilotSerialNumber);

            BriefingPayloadPicker briefingPayloadPicker = new BriefingPayloadPicker(this);
            int newPayload = briefingPayloadPicker.pickPayload(crewPlane.getPlane().getType());
            if (newPayload != -1)
            {
                briefingMissionHandler.modifyPayload(pilotSerialNumber, newPayload);
            }

            refreshPilotDisplay();
        }
    }

    private void changeModificationsForPlane(String action) throws PWCGException
    {
        if (!mission.isFinalized())
        {
            Integer pilotSerialNumber = getPilotSerialNumberFromAction(action);
            setModificationInCrewPlane(pilotSerialNumber);
            refreshPilotDisplay();
        }
    }
    
    public void addPlaneModification(int pilotSerialNumber, BriefingPlaneModificationsPicker planeModification)
    {
        planeModifications.put(pilotSerialNumber, planeModification);
    }

    private void setModificationInCrewPlane(Integer pilotSerialNumber) throws PWCGException
    {
        BriefingMissionFlight briefingMissionHandler = briefingContext.getActiveBriefingHandler();
        CrewPlanePayloadPairing crewPlane = briefingMissionHandler.getPairingByPilot(pilotSerialNumber);
        crewPlane.clearModification();
        BriefingPlaneModificationsPicker modificationPicker = planeModifications.get(pilotSerialNumber);
        for (String modificationDescription : modificationPicker.getPlaneModifications().keySet())
        {
            JCheckBox planeModificationCheckBox = modificationPicker.getPlaneModifications().get(modificationDescription);
            boolean ismodificationSelected = planeModificationCheckBox.isSelected();
            if (ismodificationSelected)
            {
                crewPlane.addModification(modificationDescription);
            }
            else
            {
                crewPlane.removeModification(modificationDescription);
            }
        }
    }

    private Integer getPilotSerialNumberFromAction(String action)
    {
        int index = action.indexOf(":");
        String pilotSerialNumberString = action.substring(index + 1);
        Integer pilotSerialNumber = Integer.valueOf(pilotSerialNumberString);
        return pilotSerialNumber;
    }

    private void synchronizePayload() throws PWCGException
    {
        BriefingMissionFlight briefingMissionHandler = briefingContext.getActiveBriefingHandler();
        List<CrewPlanePayloadPairing> assignedPairings = briefingMissionHandler.getCrewsSorted();
        CrewPlanePayloadPairing leadPlane = assignedPairings.get(0);
        for (int i = 1; i < assignedPairings.size(); ++i)
        {
            CrewPlanePayloadPairing subordinatePlane = assignedPairings.get(i);
            if (leadPlane.getPlane().getType().equals(subordinatePlane.getPlane().getType()))
            {
                subordinatePlane.setPayloadId(leadPlane.getPayloadId());
                subordinatePlane.setModifications(leadPlane.getModifications());
            }
        }
    }

    private void synchronizeModifications() throws PWCGException
    {
        BriefingMissionFlight briefingMissionHandler = briefingContext.getActiveBriefingHandler();
        List<CrewPlanePayloadPairing> assignedPairings = briefingMissionHandler.getCrewsSorted();
        CrewPlanePayloadPairing leadPlane = assignedPairings.get(0);
        for (int i = 1; i < assignedPairings.size(); ++i)
        {
            CrewPlanePayloadPairing subordinatePlane = assignedPairings.get(i);
            if (leadPlane.getPlane().equals(subordinatePlane.getPlane()))
            {
                subordinatePlane.clearModification();
                for (String modificationDescription : leadPlane.getModifications())
                {
                    subordinatePlane.addModification(modificationDescription);
                }
            }
        }
    }

    private void refreshPilotDisplay() throws PWCGException
    {
        this.add(BorderLayout.CENTER, createCenterPanel());
        this.revalidate();
        this.repaint();
    }

    private void scrubMission() throws PWCGException
    {
        campaign.setCurrentMission(null);
        campaignHomeGui.createCampaignHomeContext();
        CampaignGuiContextManager.getInstance().backToCampaignHome();
    }

    private void acceptMission() throws PWCGException, PWCGException
    {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        pushEditsToMission();
        if (!ensurePlayerIsInMission())
        {
            return;
        }

        if (!ensurePlayerOwnsPlane())
        {
            return;
        }

        SoundManager.getInstance().playSound("BriefingEnd.WAV");

        briefingContext.finalizeMission();
        verifyLoggingEnabled();

        campaign.setCurrentMission(mission);
        
        campaignHomeGui.createCampaignHomeContext();
        
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        CampaignGuiContextManager.getInstance().popFromContextStack();
    }

    private void backToCampaign() throws PWCGException, PWCGException
    {
        campaignHomeGui.createCampaignHomeContext();
        CampaignGuiContextManager.getInstance().popFromContextStack();
    }
    
    private boolean ensurePlayerIsInMission() throws PWCGException
    {
    	if (campaignHomeGui.getCampaign().isCoop())
    	{
    		return true;
    	}
    	
    	IFlight playerFlight = briefingContext.getSelectedFlight();
        List<PlaneMcu> playerPlanes = playerFlight.getFlightPlanes().getPlayerPlanes();
        for (PlaneMcu playerPlane : playerPlanes)
        {
            SquadronMember squadronMember = playerPlane.getPilot();
            if (squadronMember.isPlayer())
            {
                return true;
            }
        }

        ErrorDialog.userError("Player is not assigned to this mission");
        return false;
    }

    private boolean ensurePlayerOwnsPlane() throws PWCGException
    {
        IFlight playerFlight = briefingContext.getSelectedFlight();
        List<PlaneMcu> playerPlanes = playerFlight.getFlightPlanes().getPlayerPlanes();
        for (PlaneMcu playerPlane : playerPlanes)
        {
            if (!PlanesOwnedManager.getInstance().isPlaneOwned(playerPlane.getType()))
            {
                ErrorDialog.userError("Player does not own his assigned plane: " + playerPlane.getDisplayName() + ".  Mission will not be written.");
                return false;
            }
        }
        
        return true;
    }

    private void verifyLoggingEnabled()
    {
        MissionLogFileValidator missionLogFileValidator = new MissionLogFileValidator();
        boolean missionLogsEnabled = missionLogFileValidator.validateMissionLogsEnabled();
        if (!missionLogsEnabled)
        {
            ErrorDialog.userError(
                    "Mission logging is not enabled.  Before flying the mission open <game install dir>\\Data\\Startup.cfg and set mission_text_log = 1");
        }
    }

    private void flyMission() throws PWCGException, PWCGIOException
    {
        pushEditsToMission();

        MissionLogFileValidator missionLogFileValidator = new MissionLogFileValidator();
        boolean missionLogsEnabled = missionLogFileValidator.validateMissionLogsEnabled();
        if (missionLogsEnabled)
        {
            SoundManager.getInstance().playSound("BriefingEnd.WAV");

            briefingContext.finalizeMission();

            makeDataFileForMission();

            CampaignGuiContextManager.getInstance().popFromContextStack();

            System.exit(0);
        }
        else
        {
            ErrorDialog
                    .userError("Mission not started because logging is not enabled.  Open .<game install dir>\\Data\\Startup.cfg and set mission_text_log = 1");
        }

    }

    private void makeDataFileForMission() throws PWCGException
    {
        Campaign campaign = PWCGContext.getInstance().getCampaign();
        String campaignName = campaign.getCampaignData().getName();

        String missionFileName = MissionFileWriter.getMissionFileName(campaign) + ".mission";

        AutoStart autoStartFile = new AutoStart();
        autoStartFile.setCampaignName(campaignName);
        autoStartFile.setMissionFileName(missionFileName);
        autoStartFile.write();
    }

    private void pushEditsToMission() throws PWCGException
    {
        BriefingMissionFlight briefingMissionHandler = briefingContext.getActiveBriefingHandler();
        PlayerFlightEditor planeGeneratorPlayer = new PlayerFlightEditor(mission.getCampaign(), briefingMissionHandler.getFlight());
        planeGeneratorPlayer.updatePlayerPlanes(briefingMissionHandler.getCrewsSorted());
    }

    @Override
    public void flightChanged(Squadron squadron) throws PWCGException
    {
        pushEditsToMission();
        briefingContext.changeSelectedFlight(squadron);
        this.add(BorderLayout.CENTER, createCenterPanel());
    }
}
