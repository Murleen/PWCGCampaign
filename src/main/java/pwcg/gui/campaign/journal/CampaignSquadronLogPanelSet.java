package pwcg.gui.campaign.journal;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import pwcg.campaign.Campaign;
import pwcg.campaign.CampaignLog;
import pwcg.campaign.CampaignLogEntry;
import pwcg.campaign.context.PWCGContext;
import pwcg.core.config.ConfigItemKeys;
import pwcg.core.config.ConfigManagerGlobal;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.DateUtils;
import pwcg.core.utils.PWCGLogger;
import pwcg.gui.PwcgGuiContext;
import pwcg.gui.dialogs.ErrorDialog;
import pwcg.gui.dialogs.MonitorSupport;
import pwcg.gui.sound.SoundManager;
import pwcg.gui.utils.ContextSpecificImages;
import pwcg.gui.utils.ImageResizingPanel;
import pwcg.gui.utils.PWCGButtonFactory;
import pwcg.gui.utils.PageTurner;

public class CampaignSquadronLogPanelSet extends PwcgGuiContext implements ActionListener
{
    private static final long serialVersionUID = 1L;

    private JPanel logPagesGridPanel = new JPanel();
    private JPanel pageTurnerPanel = null;

	private Campaign campaign = null;
    private int linesPerPage = 20;
    private int charsPerLine = 50;
    private int logsForSquadronId = 0;
	
	private JPanel leftpage = null;
	private JPanel rightpage = null;

	private int pageNum = 0;
	private Map<Integer, StringBuffer> pages = null;

	public CampaignSquadronLogPanelSet (int logsForSquadronId)
	{
        super();
        
        this.logsForSquadronId = logsForSquadronId;

		Dimension screenSize = MonitorSupport.getPWCGFrameSize();

        calculateLinesPerPage(screenSize);
        calculateCharsPerLine(screenSize);
        adjustTextForFontSize();

		this.campaign = PWCGContext.getInstance().getCampaign();
	}

    private void adjustTextForFontSize()
    {
        try
        {
            int fontSize = ConfigManagerGlobal.getInstance().getIntConfigParam(ConfigItemKeys.CursiveFontSizeKey);
            
            if (fontSize > 20)
            {
                charsPerLine -= 10;
                linesPerPage -= 10;
            }
        }
        catch (PWCGException e)
        {
            e.printStackTrace();
        }
    }

    private void calculateLinesPerPage(Dimension screenSize)
    {
        linesPerPage = 45;
        if (screenSize.getHeight() < 1200)
        {
            linesPerPage = 40;
        }
        if (screenSize.getHeight() < 1000)
        {
            linesPerPage = 35;
        }
		if (screenSize.getHeight() < 800)
		{
			linesPerPage = 25;
		}		
    }

    private void calculateCharsPerLine(Dimension screenSize)
    {
        charsPerLine = 70;
        if (screenSize.getWidth() < 1200)
        {
            charsPerLine = 60;
        }
        if (screenSize.getWidth() < 1000)
        {
            charsPerLine = 45;
        }
        if (screenSize.getWidth() < 800)
        {
            charsPerLine = 35;
        }
    }

	public void makeVisible(boolean visible) 
	{
	}
	
	public void makePanels() throws PWCGException  
	{
		pages = orderPageEntries();
		setLeftPanel(makeLogLeftPanel());
		setCenterPanel(makeLogCenterPanel());
		
        makePages();        
	}

	private Map<Integer, StringBuffer> orderPageEntries()
	{
		Map<Integer, StringBuffer> pages = new TreeMap<Integer, StringBuffer>();
		
		StringBuffer page = new StringBuffer("");
		int pageCount = 0;
		// For each date in the log entries
        for (CampaignLog campaignLog : campaign.getCampaignLogs().retrieveCampaignLogsInDateOrder())
		{            
			// Don't end the page with a date entry. leave room for at least one logs
			if (countLines(page.toString()) >= (linesPerPage-5))
			{
				pages.put(pageCount, page);
				++pageCount;
				page = new StringBuffer("");
			}
			
			String dateAsString = DateUtils.getDateStringPretty(campaignLog.getDate());
            page.append("\n");
			page.append(dateAsString);
            page.append("\n");
			
			// Enter logs.  New page as necessary
        	for (CampaignLogEntry logEntry : campaignLog.getLogs())
			{
        	    if (logEntry.getSquadronId() != logsForSquadronId)
        	    {
        	        continue;
        	    }
        	    
                int linesCountedEntry = countLines(logEntry.getLog());
                int linesCountedPage = countLines(page.toString());
				if ((linesCountedPage+linesCountedEntry) >= linesPerPage)
				{
					pages.put(pageCount, page);
					++pageCount;
	                page = new StringBuffer("");
				}
				
                page.append(logEntry.getLog() + "\n");
			}
		}
		
		// The last page
		pages.put(pageCount, page);
		
		return pages;
	}

	private int countLines(String logLine)
	{
	    String[] lines = logLine.split("\r\n|\r|\n");
	    // Default is one line per CR
	    int calculatedLines = lines.length;
	    
	    // Account for wrap by taking line length into account
	    for (String line : lines)
	    {
	        // We are going to wrap so add a line
	        if (line.length() > charsPerLine)
	        {
	            ++calculatedLines;
	        }
	    }
	    
	    
	    return  calculatedLines;
	}

	private JPanel makeLogLeftPanel() throws PWCGException  
	{
        String imagePath = getSideImage(campaign, "CampaignLogNav.jpg");

		ImageResizingPanel squadronLogPanel = new ImageResizingPanel(imagePath);
		squadronLogPanel.setLayout(new BorderLayout());
		squadronLogPanel.setOpaque(false);

		JPanel buttonPanel = new JPanel(new GridLayout(0,1));
		buttonPanel.setOpaque(false);
        
        JButton finishedButton = PWCGButtonFactory.makeMenuButton("Finished Reading", "SquadronLogFinished", this);
        buttonPanel.add(finishedButton);

		squadronLogPanel.add(buttonPanel, BorderLayout.NORTH);
		
		return squadronLogPanel;
	}

	private JPanel  makeLogCenterPanel() throws PWCGException  
	{
        String imagePath = ContextSpecificImages.imagesMisc() + "CampaignLog.jpg";
        ImageResizingPanel logCenterPanel = new ImageResizingPanel(imagePath);
        logCenterPanel.setLayout(new BorderLayout());
        logCenterPanel.setOpaque(false);
        
        logPagesGridPanel = new JPanel();
        logPagesGridPanel.setLayout(new GridLayout(0,2));
        logPagesGridPanel.setOpaque(false);
        
        logCenterPanel.add(logPagesGridPanel, BorderLayout.CENTER);
        
        return logCenterPanel;

	}

	private void makePages() throws PWCGException  
	{
	    logPagesGridPanel.removeAll();

		if (leftpage != null)
		{
			leftpage.removeAll();
			leftpage = null;
		}
		
		if (rightpage != null)
		{
			rightpage.removeAll();
			rightpage = null;
		}
		
		StringBuffer leftPageEntries = new StringBuffer("");
		int leftPageNum = pageNum;
		if (pages.size() > leftPageNum)
		{
			leftPageEntries = pages.get(leftPageNum);
		}

        StringBuffer rightPageEntries = new StringBuffer("");
		int rightPageNum = pageNum + 1;
		if (pages.size() > (rightPageNum))
		{
			rightPageEntries = pages.get(rightPageNum);
		}

		leftpage = makePage (leftPageEntries, leftPageNum);
		rightpage = makePage (rightPageEntries, rightPageNum);

        if (leftpage != null)
        {
            logPagesGridPanel.add(leftpage);
        }
        
        if (rightpage != null)
        {
            logPagesGridPanel.add(rightpage);
        }
        
		
		addPageTurner();
	}

    private void addPageTurner() throws PWCGException
    {
        if (pageTurnerPanel != null)
        {
            getCenterPanel().remove(pageTurnerPanel);
        }
        
        int numPages = pages.size();
        pageTurnerPanel = PageTurner.makeButtonPanel(pageNum+1, numPages, this);
        getCenterPanel().add(pageTurnerPanel, BorderLayout.SOUTH);
    }

	private JPanel makePage(StringBuffer pageEntries, int pageNum) throws PWCGException 
	{
		JPanel campaignLogPanelBorder = new JPanel();
		campaignLogPanelBorder.setLayout(new BorderLayout());
		campaignLogPanelBorder.setOpaque(false);

		JPanel campaignLogPanel = new JPanel();
		campaignLogPanel.setLayout(new GridLayout(0,1));
		campaignLogPanel.setOpaque(false);

		Font font = MonitorSupport.getCursiveFont();

        final JTextArea logTextArea= new JTextArea();
        
        logTextArea.setLineWrap(true);
        logTextArea.setWrapStyleWord(true);
        logTextArea.setEditable(false);
        logTextArea.setOpaque(false);
        logTextArea.setFont(font);
        logTextArea.setSize(logTextArea.getPreferredSize().width, 1);
        
        // Calculate the writable area of the text and generate margins scaled to screen size
        Insets margins = MonitorSupport.calculateInset(15, 50, 15, 20);
        if (pageNum%2 != 0)
        {
            margins = MonitorSupport.calculateInset(15, 20, 15, 35);           
        }
        logTextArea.setMargin(margins);

		logTextArea.setText(pageEntries.toString());
		campaignLogPanel.add(logTextArea);
		
        final JTextArea bufferTextArea= new JTextArea();
        bufferTextArea.setOpaque(false);
        
		campaignLogPanelBorder.add(bufferTextArea, BorderLayout.WEST);
		campaignLogPanelBorder.add(campaignLogPanel, BorderLayout.CENTER);
		
		return campaignLogPanelBorder;
	}

	public void actionPerformed(ActionEvent ae)
	{
		try
		{
            String action = ae.getActionCommand();
            
            if (action.equalsIgnoreCase("SquadronLogFinished"))
            {
                finishedWithCampaignScreen();
            }
            else if (action.equalsIgnoreCase("Next Page"))
			{
                nextPage();
			}
			else if (action.equalsIgnoreCase("Previous Page"))
			{
                previousPage();
			}
		}
		catch (Exception e)
		{
			PWCGLogger.logException(e);
			ErrorDialog.internalError(e.getMessage());
		}
	}

    private void previousPage() throws PWCGException
    {
        SoundManager.getInstance().playSound("PageTurn.WAV");
        this.pageNum -= 2;
        makePages();
        getCenterPanel().setVisible(false);
        getCenterPanel().setVisible(true);
    }

    private void nextPage() throws PWCGException
    {
        SoundManager.getInstance().playSound("PageTurn.WAV");
        this.pageNum += 2;
        makePages();
        getCenterPanel().setVisible(false);
        getCenterPanel().setVisible(true);
    }

}
