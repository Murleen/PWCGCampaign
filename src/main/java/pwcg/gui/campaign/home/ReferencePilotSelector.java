package pwcg.gui.campaign.home;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import pwcg.campaign.Campaign;
import pwcg.campaign.squadmember.SquadronMember;
import pwcg.campaign.squadmember.SquadronMemberStatus;
import pwcg.campaign.squadmember.SquadronMembers;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.PWCGLogger;
import pwcg.gui.CampaignGuiContextManager;
import pwcg.gui.UiImageResolver;
import pwcg.gui.colors.ColorMap;
import pwcg.gui.dialogs.ErrorDialog;
import pwcg.gui.dialogs.PWCGMonitorFonts;
import pwcg.gui.utils.ImageResizingPanel;
import pwcg.gui.utils.ImageResizingPanelBuilder;
import pwcg.gui.utils.PWCGButtonFactory;
import pwcg.gui.utils.SpacerPanelFactory;

public class ReferencePilotSelector extends ImageResizingPanel implements ActionListener
{
    private static final long serialVersionUID = 1L;
    private JComboBox<String> squadronMemberSelector;
    private CampaignHome campaignHomeGui;
    private Campaign campaign;
    private Map<String, SquadronMember> coopSquadronMembersInCampaign = new HashMap<>();

    public ReferencePilotSelector(Campaign campaign,CampaignHome campaignHomeGui)
    {
        super("");
        this.setLayout(new BorderLayout());
        this.setOpaque(false);

        this.campaign = campaign;
        this.campaignHomeGui = campaignHomeGui;
    }
    
    public void makePanels() 
    {
        try
        {
            String imagePath = UiImageResolver.getImageMain("TableTop.jpg");
            this.setImage(imagePath);

            this.add(BorderLayout.WEST, makeNavigatePanel());
            this.add(BorderLayout.CENTER, makeCoopPersonaSelectorPanel());
            this.add(BorderLayout.EAST, SpacerPanelFactory.makeDocumentSpacerPanel(1400));
        }
        catch (Throwable e)
        {
            PWCGLogger.logException(e);
            ErrorDialog.internalError(e.getMessage());
        }
    }

    private JPanel makeCoopPersonaSelectorPanel() throws PWCGException
    {
        Font font = PWCGMonitorFonts.getPrimaryFontLarge();
        squadronMemberSelector = new JComboBox<String>();
        squadronMemberSelector.setOpaque(false);
        squadronMemberSelector.setBackground(ColorMap.PAPER_BACKGROUND);
        squadronMemberSelector.setFont(font);
        
        SquadronMembers players = campaign.getPersonnelManager().getAllActivePlayers();
        for (SquadronMember player : players.getSquadronMemberList())
        {
            if (player.getPilotActiveStatus() >= SquadronMemberStatus.STATUS_CAPTURED)
            {
                String selectiontext = player.getNameAndRank();
                squadronMemberSelector.addItem(selectiontext);
                coopSquadronMembersInCampaign.put(selectiontext, player);
            }
        }

        if (squadronMemberSelector.getItemCount() > 0)
        {
            squadronMemberSelector.setSelectedIndex(0);
        }
        
        JPanel gridPanel = new JPanel(new GridLayout(0,3));
        gridPanel.setOpaque(false);
        
        gridPanel.add(PWCGButtonFactory.makeDummy());
        gridPanel.add(squadronMemberSelector);
        gridPanel.add(PWCGButtonFactory.makeDummy());

        String imagePath = UiImageResolver.getImageMisc("document.png");
        ImageResizingPanel centerPanel = ImageResizingPanelBuilder.makeImageResizingPanel(imagePath);
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(gridPanel, BorderLayout.NORTH);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(150,50,50,100));

        return centerPanel;
    }

	public JPanel makeNavigatePanel() throws PWCGException  
    {
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setOpaque(false);

        JPanel buttonPanel = new JPanel(new GridLayout(0,1));
        buttonPanel.setOpaque(false);

        JButton acceptButton = PWCGButtonFactory.makeMenuButton("Accept Reference Pilot", "AcceptReferencePilot", this);
        buttonPanel.add(acceptButton);

        JButton cancelButton = PWCGButtonFactory.makeMenuButton("Cancel", "CancelReferencePilot", this);
        buttonPanel.add(cancelButton);

        navPanel.add(buttonPanel, BorderLayout.NORTH);
        
        return navPanel;
    }

    public void actionPerformed(ActionEvent ae)
    {
        try
        {
            String action = ae.getActionCommand();
            if (action.equalsIgnoreCase("AcceptReferencePilot"))
            {
                SquadronMember referencePlayer = coopSquadronMembersInCampaign.get(squadronMemberSelector.getSelectedItem());
                if (referencePlayer != null)
                {
                    campaign.getCampaignData().setReferencePlayerSerialNumber(referencePlayer.getSerialNumber());
                }
                
                campaignHomeGui.createCampaignHomeContext();
                CampaignGuiContextManager.getInstance().popFromContextStack();
            }
            else if (action.equalsIgnoreCase("CancelReferencePilot"))
            {
                CampaignGuiContextManager.getInstance().popFromContextStack();
            }
        }
        catch (Throwable e)
        {
            PWCGLogger.logException(e);
            ErrorDialog.internalError(e.getMessage());
        }
    }
}


