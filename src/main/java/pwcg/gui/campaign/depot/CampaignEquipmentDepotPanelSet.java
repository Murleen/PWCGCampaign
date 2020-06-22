package pwcg.gui.campaign.depot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import pwcg.campaign.ArmedService;
import pwcg.campaign.Campaign;
import pwcg.campaign.api.IArmedServiceManager;
import pwcg.campaign.factory.ArmedServiceFactory;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.PWCGLogger;
import pwcg.gui.CampaignGuiContextManager;
import pwcg.gui.UiImageResolver;
import pwcg.gui.colors.ColorMap;
import pwcg.gui.dialogs.ErrorDialog;
import pwcg.gui.utils.ContextSpecificImages;
import pwcg.gui.utils.ImageJTabbedPane;
import pwcg.gui.utils.ImageResizingPanel;
import pwcg.gui.utils.ImageResizingPanelBuilder;
import pwcg.gui.utils.PWCGButtonFactory;

public class CampaignEquipmentDepotPanelSet extends JPanel implements ActionListener
{
    private static final long serialVersionUID = 1L;
	private ImageJTabbedPane tabs = new ImageJTabbedPane();
	private Campaign campaign;

	public CampaignEquipmentDepotPanelSet(Campaign campaign)
	{
        super();
	    this.campaign = campaign;
        this.setOpaque(false);
	}

	public void makePanels() throws PWCGException  
	{
        this.add(BorderLayout.EAST, null);
        this.add(BorderLayout.CENTER,  makeCenterPanel());
        this.add(BorderLayout.WEST, makeNavigatePanel());
	}

	private JPanel makeNavigatePanel() throws PWCGException  
	{		
        String imagePath = UiImageResolver.getImage(campaign, "IntelNav.jpg");

		ImageResizingPanel intelPanel = ImageResizingPanelBuilder.makeImageResizingPanel(imagePath);
		intelPanel.setLayout(new BorderLayout());
		intelPanel.setOpaque(false);

		JPanel buttonPanel = new JPanel(new GridLayout(0,1));
		buttonPanel.setOpaque(false);
		
        JButton acceptButton = PWCGButtonFactory.makeMenuButton("Finished", "EquipmentDepoFinished", this);
		buttonPanel.add(acceptButton);
		
		intelPanel.add(buttonPanel, BorderLayout.NORTH);
		
		return intelPanel;
	}

	private JPanel makeCenterPanel() 
	{
		ImageResizingPanel intelPanel = null;

		try
		{
	        String imagePath = ContextSpecificImages.imagesMisc() + "Paper.jpg";
	        intelPanel = ImageResizingPanelBuilder.makeImageResizingPanel(imagePath);
			intelPanel.setLayout(new BorderLayout());
			
			Color tabBG = ColorMap.PAPER_BACKGROUND;
			tabs.setBackground(tabBG);
			tabs.setOpaque(false);
			
			IArmedServiceManager serviceManager = ArmedServiceFactory.createServiceManager();
			for (ArmedService service : serviceManager.getAllActiveArmedServices(campaign.getDate()))
			{
			    CampaignEquipmentDepotPanel serviceEquipmentDepoTab = new CampaignEquipmentDepotPanel(campaign, service);
			    serviceEquipmentDepoTab.makePanel();
	            tabs.addTab(service.getName(), serviceEquipmentDepoTab);      
			}
						
			for (int i = 0; i < tabs.getTabCount(); ++i)
			{
				tabs.setBackgroundAt(i, tabBG);
			}

			intelPanel.add(tabs, BorderLayout.CENTER);
		}
		catch (Exception e)
		{
			PWCGLogger.logException(e);
			ErrorDialog.internalError(e.getMessage());
		}
		
		return intelPanel;
	}

    public void actionPerformed(ActionEvent ae)
    {
        try
        {
            String action = ae.getActionCommand();

            if (action.equalsIgnoreCase("EquipmentDepoFinished"))
            {
                campaign.write();                
                CampaignGuiContextManager.getInstance().popFromContextStack();
            }
        }
        catch (Exception e)
        {
            PWCGLogger.logException(e);
            ErrorDialog.internalError(e.getMessage());
        }
    }

}
