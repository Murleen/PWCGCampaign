package pwcg.gui.rofmap.event;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.HashMap;

import javax.swing.JTabbedPane;

import pwcg.aar.AARCoordinator;
import pwcg.aar.ui.display.model.AAREquipmentLossPanelData;
import pwcg.aar.ui.events.model.PlaneStatusEvent;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.squadmember.SquadronMember;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.Logger;
import pwcg.gui.colors.ColorMap;
import pwcg.gui.dialogs.ErrorDialog;
import pwcg.gui.rofmap.debrief.AAREventPanel;
import pwcg.gui.utils.ContextSpecificImages;
import pwcg.gui.utils.ImageResizingPanel;

public class AAREquipmentChangePanel extends AAREventPanel
{
    private static final long serialVersionUID = 1L;
    private AARCoordinator aarCoordinator;
    private SquadronMember referencePlayer;

    public AAREquipmentChangePanel()
	{
        super();
        this.aarCoordinator = AARCoordinator.getInstance();
        this.referencePlayer = PWCGContext.getInstance().getReferencePlayer();
	}

	public void makePanel() throws PWCGException  
	{
        try
        {
            JTabbedPane eventTabPane = createEquipmentLostTab();
            createPostCombatReportTabs(eventTabPane);
        }
        catch (Exception e)
        {
            Logger.logException(e);
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

    private JTabbedPane createEquipmentLostTab() throws PWCGException
    {
        Color bgColor = ColorMap.PAPER_BACKGROUND;

        JTabbedPane eventTabPane = new JTabbedPane();
        eventTabPane.setBackground(bgColor);
        eventTabPane.setOpaque(false);
       
        HashMap<String, CampaignReportEquipmentStatusGUI> equipmentGuiList = createPilotLostSubTabs() ;
        for (String tabName : equipmentGuiList.keySet())
        {
            eventTabPane.addTab(tabName, equipmentGuiList.get(tabName));
            shouldDisplay = true;
        }


        return eventTabPane;
    }

	private HashMap<String, CampaignReportEquipmentStatusGUI> createPilotLostSubTabs() throws PWCGException 
	{
	    AAREquipmentLossPanelData equipmentLossPanelData = aarCoordinator.getAarContext().getUiDebriefData().getEquipmentLossPanelData();
        HashMap<String, CampaignReportEquipmentStatusGUI> planesLostGuiList = new HashMap<>();
        for (PlaneStatusEvent planeStatusEvent : equipmentLossPanelData.getEquipmentLost().values())
		{
            if (planeStatusEvent.getSquadronId() == referencePlayer.getSquadronId())
            {
                CampaignReportEquipmentStatusGUI equipmentChangeGui = new CampaignReportEquipmentStatusGUI(planeStatusEvent);
                String tabName = "Plane Lost: " + planeStatusEvent.getPlaneSerialNumber();
                planesLostGuiList.put(tabName, equipmentChangeGui);
            }
		}
        
        return planesLostGuiList;
	}

	
    @Override
    public void finished()
    {
    }
}
