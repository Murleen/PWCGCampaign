package pwcg.gui.rofmap.event;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JTabbedPane;

import pwcg.aar.AARCoordinator;
import pwcg.aar.ui.events.model.VictoryEvent;
import pwcg.campaign.Campaign;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.PWCGLogger;
import pwcg.gui.colors.ColorMap;
import pwcg.gui.dialogs.ErrorDialog;
import pwcg.gui.rofmap.debrief.AAREventPanel;
import pwcg.gui.utils.ContextSpecificImages;
import pwcg.gui.utils.ImageResizingPanel;

public class AAROutOfMissionVictoryPanel extends AAREventPanel
{
    private static final long serialVersionUID = 1L;
    private AARCoordinator aarCoordinator;
    private Campaign campaign;

    public AAROutOfMissionVictoryPanel(Campaign campaign)
	{
        super();
        this.aarCoordinator = AARCoordinator.getInstance();
        this.campaign = campaign;
	}

	public void makePanel() throws PWCGException  
	{
        try
        {
            JTabbedPane eventTabPane = createPilotVictoriesTab();
            createPostCombatReportTabs(eventTabPane);
        }
        catch (Exception e)
        {
            PWCGLogger.logException(e);
            ErrorDialog.internalError(e.getMessage());
        }
    }
    
    private void createPostCombatReportTabs(JTabbedPane eventTabPane)
    {
        ImageResizingPanel postCombatPanel = new ImageResizingPanel(ContextSpecificImages.imagesMisc() + "PaperPart.jpg");
        postCombatPanel.setLayout(new BorderLayout());
        postCombatPanel.add(eventTabPane, BorderLayout.CENTER);
        this.add(postCombatPanel, BorderLayout.CENTER);
    }

    private JTabbedPane createPilotVictoriesTab() throws PWCGException
    {
        Color bgColor = ColorMap.PAPER_BACKGROUND;

        JTabbedPane eventTabPane = new JTabbedPane();
        eventTabPane.setBackground(bgColor);
        eventTabPane.setOpaque(false);
       
        HashMap<String, CampaignReportVictoryGUI> equipmentGuiList = createPilotLostSubTabs() ;
        for (String tabName : equipmentGuiList.keySet())
        {
            eventTabPane.addTab(tabName, equipmentGuiList.get(tabName));
            this.shouldDisplay = true;
        }


        return eventTabPane;
    }

	private HashMap<String, CampaignReportVictoryGUI> createPilotLostSubTabs() throws PWCGException 
	{
	    HashMap<String, List<VictoryEvent>> victoriesByPilotsInMySquadron = collateVictoriesByPilotInMySquadron();
        HashMap<String, CampaignReportVictoryGUI> pilotVictoryPanelData = createVictoryTabs(victoriesByPilotsInMySquadron);
        return pilotVictoryPanelData;
	}

    private HashMap<String, List<VictoryEvent>> collateVictoriesByPilotInMySquadron() throws PWCGException
    {
        HashMap<String, List<VictoryEvent>> victoriesByPilotsInMySquadron = new HashMap<>();
        List<VictoryEvent>  outOfMissionVictories = aarCoordinator.getAarContext().getUiDebriefData().getOutOfMissionVictoryPanelData().getOutOfMissionVictoryEvents();
        for (VictoryEvent victoryEvent : outOfMissionVictories)
		{
            if (victoryEvent.getSquadronId() == campaign.findReferencePlayer().getSquadronId())
            {
                if (!victoriesByPilotsInMySquadron.containsKey(victoryEvent.getPilotName()))
                {
                    List<VictoryEvent> victoriesForPilot = new ArrayList<>();
                    victoriesByPilotsInMySquadron.put(victoryEvent.getPilotName(), victoriesForPilot);
                }
                List<VictoryEvent> victoriesForPilot = victoriesByPilotsInMySquadron.get(victoryEvent.getPilotName());
                victoriesForPilot.add(victoryEvent);
            }
		}
        return victoriesByPilotsInMySquadron;
    }

    private HashMap<String, CampaignReportVictoryGUI> createVictoryTabs(HashMap<String, List<VictoryEvent>> victoriesByPilotsInMySquadron)
    {
        HashMap<String, CampaignReportVictoryGUI> pilotVictoryPanelData = new HashMap<>();
        for (String pilotName : victoriesByPilotsInMySquadron.keySet())
        {
            List<VictoryEvent> victoriesForPilot = victoriesByPilotsInMySquadron.get(pilotName);
            CampaignReportVictoryGUI victoryGUI = new CampaignReportVictoryGUI(campaign, victoriesForPilot);
            String tabName = "Plane Lost: " + pilotName;
            pilotVictoryPanelData.put(tabName, victoryGUI);
        }
        return pilotVictoryPanelData;
    }

    @Override
    public void finished()
    {
    }
}
