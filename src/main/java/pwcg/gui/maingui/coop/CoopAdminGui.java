package pwcg.gui.maingui.coop;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import pwcg.core.exception.PWCGException;
import pwcg.core.utils.PWCGLogger;
import pwcg.gui.CampaignGuiContextManager;
import pwcg.gui.PwcgThreePanelUI;
import pwcg.gui.UiImageResolver;
import pwcg.gui.colors.ColorMap;
import pwcg.gui.dialogs.ErrorDialog;
import pwcg.gui.dialogs.PWCGMonitorFonts;
import pwcg.gui.utils.ContextSpecificImages;
import pwcg.gui.utils.ImageResizingPanel;
import pwcg.gui.utils.ImageResizingPanelBuilder;
import pwcg.gui.utils.PWCGButtonFactory;

public class CoopAdminGui extends JPanel implements ActionListener
{
    private static final long serialVersionUID = 1L;

    private PwcgThreePanelUI pwcgThreePanel;
    private ButtonGroup buttonGroup = new ButtonGroup();

    public CoopAdminGui()
    {
        super();
        this.setLayout(new BorderLayout());
        this.pwcgThreePanel = new PwcgThreePanelUI(this);
    }
    
    public void makePanels() 
    {
        try
        {        	
            pwcgThreePanel.setLeftPanel(makeNavigatePanel());
            pwcgThreePanel.setRightPanel(makeCoopAdminActionSelectPanel());
            pwcgThreePanel.setCenterPanel(makeBlankCenterPanel());
            CampaignGuiContextManager.getInstance().pushToContextStack(this);
        }
        catch (Throwable e)
        {
            PWCGLogger.logException(e);
            ErrorDialog.internalError(e.getMessage());
        }
    }

	public JPanel makeBlankCenterPanel()  
    {       
        String imagePath = ContextSpecificImages.imagesMisc() + "Paper.jpg";
        ImageResizingPanel blankPanel = ImageResizingPanelBuilder.makeImageResizingPanel(imagePath);
        blankPanel.setLayout(new BorderLayout());
                
        return blankPanel;
    }

    public JPanel makeNavigatePanel() throws PWCGException  
    {
        String imagePath = UiImageResolver.getImageMain("Barracks.jpg");

        JPanel navPanel = ImageResizingPanelBuilder.makeImageResizingPanel(imagePath);
        navPanel.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new GridLayout(0,1));
        buttonPanel.setOpaque(false);

        JButton acceptButton = PWCGButtonFactory.makeMenuButton("Finished", "Finished", this);
        buttonPanel.add(acceptButton);

        navPanel.add(buttonPanel, BorderLayout.NORTH);
        
        return navPanel;
    }

    public JPanel makeCoopAdminActionSelectPanel() throws PWCGException  
    {
        String imagePath = UiImageResolver.getImageMain("Barracks2.jpg");

        JPanel configPanel = ImageResizingPanelBuilder.makeImageResizingPanel(imagePath);
        configPanel.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new GridLayout(0,1));
        buttonPanel.setOpaque(false);
        

        JLabel label = PWCGButtonFactory.makeMenuLabelLarge("Select Coop Admin Action:");
        buttonPanel.add(label);

        JLabel spacer = PWCGButtonFactory.makeMenuLabelLarge("   ");
        buttonPanel.add(spacer);

        buttonPanel.add(makeActionSelectRadioButton("Show Coop Participant Information"));
        buttonPanel.add(makeActionSelectRadioButton("Add Coop User"));
        buttonPanel.add(makeActionSelectRadioButton("Remove Coop User"));
        
        add (buttonPanel);

        configPanel.add(buttonPanel, BorderLayout.NORTH);
        
        return configPanel;
    }

    private JRadioButton makeActionSelectRadioButton(String buttonText) throws PWCGException 
    {
        Color fgColor = ColorMap.CHALK_FOREGROUND;

        Font font = PWCGMonitorFonts.getPrimaryFont();

        JRadioButton button = new JRadioButton(buttonText);
        button.setActionCommand(buttonText);
        button.setHorizontalAlignment(SwingConstants.LEFT );
        button.setBorderPainted(false);
        button.addActionListener(this);
        button.setOpaque(false);
        button.setForeground(fgColor);
        button.setFont(font);

        buttonGroup.add(button);

        return button;
    }

    public void actionPerformed(ActionEvent ae)
    {
        try
        {
            String action = ae.getActionCommand();
            if (action.equalsIgnoreCase("Finished"))
            {
                CampaignGuiContextManager.getInstance().popFromContextStack();
                return;
            }

            if (action.equalsIgnoreCase("Show Coop Participant Information"))
            {
                CoopPersonaInfoPanel coopPersonaInfoPanel = new CoopPersonaInfoPanel();
                coopPersonaInfoPanel.makePanels();                
                pwcgThreePanel.setCenterPanel(coopPersonaInfoPanel);
            }
            else if (action.equalsIgnoreCase("Add Coop User"))
            {
                CoopCreateUserPanel coopCreateUser = new CoopCreateUserPanel();
                coopCreateUser.makePanels();
                pwcgThreePanel.setCenterPanel(coopCreateUser);
            }
            else if (action.contains("Remove Coop User"))
            {
                CoopUserRemovePanel coopUserRemove = new CoopUserRemovePanel();
                coopUserRemove.makePanels();
                coopUserRemove.loadPanels();
                pwcgThreePanel.setCenterPanel(coopUserRemove);
            }
        }
        catch (Throwable e)
        {
            PWCGLogger.logException(e);
            ErrorDialog.internalError(e.getMessage());
        }
    }
}


