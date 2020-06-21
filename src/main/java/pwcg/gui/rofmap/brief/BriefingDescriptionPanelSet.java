package pwcg.gui.rofmap.brief;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import pwcg.campaign.Campaign;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.squadmember.SquadronMember;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.PWCGLogger;
import pwcg.gui.CampaignGuiContextManager;
import pwcg.gui.campaign.home.CampaignHomeGUI;
import pwcg.gui.colors.ColorMap;
import pwcg.gui.dialogs.ErrorDialog;
import pwcg.gui.dialogs.PWCGMonitorBorders;
import pwcg.gui.dialogs.PWCGMonitorFonts;
import pwcg.gui.helper.BriefingMissionFlight;
import pwcg.gui.sound.SoundManager;
import pwcg.gui.utils.ContextSpecificImages;
import pwcg.gui.utils.ImageResizingPanel;
import pwcg.gui.utils.ImageResizingPanelBuilder;
import pwcg.gui.utils.PWCGButtonFactory;
import pwcg.gui.utils.ScrollBarWrapper;
import pwcg.mission.IMissionDescription;
import pwcg.mission.Mission;
import pwcg.mission.MissionDescriptionFactory;

public class BriefingDescriptionPanelSet extends JPanel implements ActionListener, IFlightChanged
{
    private CampaignHomeGUI campaignHomeGui = null;

	private static final long serialVersionUID = 1L;
    private JTextArea missionTextArea = new JTextArea();
    private Mission mission;
    private BriefingContext briefingContext;
    private BriefingFlightChooser briefingFlightChooser;

	public BriefingDescriptionPanelSet(CampaignHomeGUI campaignHomeGui, Mission mission) throws PWCGException 
	{
        super();
	    
        this.campaignHomeGui =  campaignHomeGui;
        this.mission =  mission;

        briefingContext = new BriefingContext(mission);
        briefingContext.buildBriefingMissions();

		SoundManager.getInstance().playSound("BriefingStart.WAV");
	}

	public void makePanels() 
	{
		try
		{
            briefingFlightChooser = new BriefingFlightChooser(mission, this);
            briefingFlightChooser.createBriefingSquadronSelectPanel();

			this.removeAll();
			this.add(BorderLayout.WEST, makeLeftPanel());
			this.add(BorderLayout.CENTER, makeBriefingPanel());
	        setMissionText();
		}
		catch (Exception e)
		{
			PWCGLogger.logException(e);
			ErrorDialog.internalError(e.getMessage());
		}
	}

    private JPanel makeLeftPanel() throws PWCGException 
    {
        String imagePath = ContextSpecificImages.imagesMisc() + "BrickLeft.jpg";
        ImageResizingPanel leftPanel = ImageResizingPanelBuilder.makeImageResizingPanel(imagePath);
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setOpaque(false);

        JPanel buttonPanel = makeButtonPanel();
        leftPanel.add(buttonPanel, BorderLayout.NORTH);
        leftPanel.add(briefingFlightChooser.getFlightChooserPanel(), BorderLayout.CENTER);
        return leftPanel;
    }
    
    private JPanel makeButtonPanel() throws PWCGException 
    {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);

        JPanel buttonGrid = new JPanel();
        buttonGrid.setLayout(new GridLayout(0,1));
        buttonGrid.setOpaque(false);
            
        if (mission.isFinalized())
        {
            buttonGrid.add(PWCGButtonFactory.makeDummy());
            makeButton(buttonGrid, "Back to Campaign");
        }
        
        buttonGrid.add(PWCGButtonFactory.makeDummy());
        makeButton(buttonGrid, "Scrub Mission");

        buttonGrid.add(PWCGButtonFactory.makeDummy());
        makeButton(buttonGrid, "Briefing Map");

        buttonPanel.add(buttonGrid, BorderLayout.NORTH);
        
        return buttonPanel;
    }
    
    public JPanel makeBriefingPanel() throws PWCGException  
    {
        String imagePath = ContextSpecificImages.imagesMisc() + "BrickCenter.jpg";
        JPanel briefingPanel = ImageResizingPanelBuilder.makeImageResizingPanel(imagePath);
        briefingPanel.setLayout(new BorderLayout());
        briefingPanel.setOpaque(false);

        JPanel missionTextPanel = makeMissionText();
        
        JScrollPane missionScrollPane = ScrollBarWrapper.makeScrollPane(missionTextPanel);

        briefingPanel.add(missionScrollPane, BorderLayout.CENTER);

        return briefingPanel;
    }

    private JPanel makeMissionText() throws PWCGException 
    {
        JPanel missionTextPanel = new JPanel(new BorderLayout());
        missionTextPanel.setOpaque(false);
        
        Font font = PWCGMonitorFonts.getBriefingChalkboardFont();

        missionTextArea.setFont(font);
        missionTextArea.setOpaque(false);
        missionTextArea.setLineWrap(true);
        missionTextArea.setWrapStyleWord(true);
        missionTextArea.setForeground(ColorMap.CHALK_FOREGROUND);
        
        // Calculate the writable area of the text and generate margins scaled to screen size
        Insets margins = PWCGMonitorBorders.calculateBorderMargins(50, 35, 65, 35);
        missionTextArea.setMargin(margins);
        
        missionTextPanel.add(missionTextArea, BorderLayout.CENTER);

        return missionTextPanel;
    }

    private void setMissionText() throws PWCGException 
    {
        Campaign campaign = PWCGContext.getInstance().getCampaign();

        String missionPrefix = getMissionPrefix();

        IMissionDescription missionDescription = MissionDescriptionFactory.buildMissionDescription(campaign, mission, briefingContext.getSelectedFlight());
        String missionDescriptionText = missionDescription.createDescription();
        
        StringBuffer missionDescriptionBuffer = new StringBuffer("");
        missionDescriptionBuffer.append(missionPrefix);
        missionDescriptionBuffer.append(missionDescriptionText);

        String pilotList = makePilotList();
        missionDescriptionBuffer.append(pilotList.toString());
        
        missionTextArea.setText(missionDescriptionBuffer.toString());
    }

    private String getMissionPrefix()
    {
        String missionPrefix = "Mission: \n";
        if (mission.isNightMission())
        {
            missionPrefix = "Mission: Night Mission!\n";
        }
        return missionPrefix;
    }

    private String makePilotList() throws PWCGException 
    {
        BriefingMissionFlight activeMissionHandler = briefingContext.getActiveBriefingHandler();
        StringBuffer assignedPilotsBuffer = new StringBuffer ("Assigned Pilots:\n");
        for (SquadronMember squadronMember : activeMissionHandler.getSortedAssigned())
        {
            assignedPilotsBuffer.append("    " + squadronMember.getNameAndRank() + "\n");
        }
        
        return assignedPilotsBuffer.toString();
    }

    private JButton makeButton(JPanel buttonPanel, String buttonText) throws PWCGException
    {
        JButton button = PWCGButtonFactory.makeMenuButton(buttonText, buttonText, this);
        buttonPanel.add(button);
        
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) 
    {       
        try
        {
            String action = arg0.getActionCommand();
            
            if (action.equals("Back to Campaign"))
            {
                backToCampaign();
            }
            else if (action.equals("Briefing Map"))
            {
                forwardToBriefingMap();
            }
            else if (action.equals("Scrub Mission"))
            {
                scrubMission();
            }
        }
        catch (Exception e)
        {
            PWCGLogger.logException(e);
            ErrorDialog.internalError(e.getMessage());
        }
    }

    @Override
    public void flightChanged(Squadron squadron) throws PWCGException
    {
        briefingContext.changeSelectedFlight(squadron);
        setMissionText();
    }

    private void forwardToBriefingMap() throws PWCGException 
    {
        SoundManager.getInstance().playSound("Typewriter.WAV");

        BriefingMapGUI briefingMap = new BriefingMapGUI(campaignHomeGui, mission, briefingContext, campaignHomeGui.getCampaign().getDate());
        briefingMap.makePanels();
        CampaignGuiContextManager.getInstance().pushToContextStack(briefingMap);
    }

    private void scrubMission() throws PWCGException
    {
        Campaign campaign  = PWCGContext.getInstance().getCampaign();
        campaign.setCurrentMission(null);
        
        campaignHomeGui.createCampaignHomeContext();

        CampaignGuiContextManager.getInstance().popFromContextStack();
    }

    private void backToCampaign() throws PWCGException
    {
        Campaign campaign  = PWCGContext.getInstance().getCampaign();

        briefingContext.updateMissionBriefingParameters();
        
        campaign.setCurrentMission(mission);
        
        campaignHomeGui.createCampaignHomeContext();
        CampaignGuiContextManager.getInstance().popFromContextStack();
    }
    
    public void refreshScreen() throws PWCGException
    {
        setMissionText();
        briefingFlightChooser.setSelectedButton(briefingContext.getSelectedFlight().getSquadron().getSquadronId());
    }
}
