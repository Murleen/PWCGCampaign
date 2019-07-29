package pwcg.campaign;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pwcg.aar.ui.events.model.SquadronMoveEvent;
import pwcg.campaign.api.ICountry;
import pwcg.campaign.context.PWCGContextManager;
import pwcg.campaign.context.SquadronManager;
import pwcg.campaign.factory.CampaignModeFactory;
import pwcg.campaign.io.json.CampaignIOJson;
import pwcg.campaign.mode.ICampaignActive;
import pwcg.campaign.mode.ICampaignCountryBuilder;
import pwcg.campaign.mode.ICampaignDescriptionBuilder;
import pwcg.campaign.personnel.InitialSquadronBuilder;
import pwcg.campaign.personnel.SquadronPersonnel;
import pwcg.campaign.plane.Role;
import pwcg.campaign.squadmember.SerialNumber;
import pwcg.campaign.squadmember.SquadronMember;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.config.ConfigItemKeys;
import pwcg.core.config.ConfigManagerCampaign;
import pwcg.core.exception.PWCGException;
import pwcg.core.exception.PWCGUserException;
import pwcg.core.utils.DateUtils;
import pwcg.core.utils.FileUtils;
import pwcg.core.utils.Logger;
import pwcg.core.utils.RandomNumberGenerator;
import pwcg.mission.Mission;

public class Campaign
{
    private CampaignData campaignData = new CampaignData();
    private ConfigManagerCampaign campaignConfigManager = null;
    private CampaignLogs campaignLogs = null;

    private Mission currentMission = null;    
    private CampaignPersonnelManager personnelManager = null;
    private CampaignEquipmentManager equipmentManager = new CampaignEquipmentManager();
    private SquadronMoveEvent squadronMoveEvent = new SquadronMoveEvent();

    public Campaign() 
    {
        personnelManager = new CampaignPersonnelManager(this);
        campaignLogs = new CampaignLogs();
    }

    public boolean open(String campaignName) throws PWCGException 
    {
        campaignData.setName(campaignName);

        initializeCampaignDirectories();
        initializeCampaignConfigs();
        if (!readValidCampaign())
        {
            return false;
        }
        
        CampaignV5V6Converter converter = new CampaignV5V6Converter(this);
        converter.convert();
        
        CampaignModeChooser campaignModeChooser = new CampaignModeChooser(this);
        CampaignMode campaignMode = campaignModeChooser.chooseCampaignMode();
        this.getCampaignData().setCampaignMode(campaignMode);

        InitialSquadronBuilder initialSquadronBuilder = new InitialSquadronBuilder();
        initialSquadronBuilder.buildNewSquadrons(this);

        return true;
    }

    public void write() throws PWCGException
    {
        CampaignIOJson.writeJson(this);
        initializeCampaignDirectories();
    }
    
	private boolean readValidCampaign()
	{
		try
		{
			CampaignIOJson.readJson(this);
	        if (!isValidCampaignForProduct())
	        {
	            return false;
	        }
		}
		catch (PWCGException e)
		{
            Logger.logException(e);
            return false;
		}

		return true;
	}

	public void initializeCampaignDirectories() 
	{
        FileUtils fileUtils = new FileUtils();
		
        String campaignCombatReportsDir = getCampaignPath() + "CombatReports\\";
        fileUtils.createConfigDirIfNeeded(campaignCombatReportsDir);

        String campaignConfigDir = getCampaignPath() + "config\\";
        fileUtils.createConfigDirIfNeeded(campaignConfigDir);
		
        String campaignEquipmentDir = getCampaignPath() + "Equipment\\";
        fileUtils.createConfigDirIfNeeded(campaignEquipmentDir);
		
        String campaignMissionDataDir = getCampaignPath() + "MissionData\\";
        fileUtils.createConfigDirIfNeeded(campaignMissionDataDir);
		
        String campaignPersonnelDir = getCampaignPath() + "Personnel\\";
        fileUtils.createConfigDirIfNeeded(campaignPersonnelDir);
	}

    public void  initializeCampaignConfigs() throws PWCGException 
    {
        String campaignConfigDir = getCampaignPath() + "config\\";
        campaignConfigManager = new ConfigManagerCampaign(campaignConfigDir);
        campaignConfigManager.initialize();
    }

    public boolean isHumanSquadron(int squadronId)
    {
    	SquadronPersonnel squadronPersonnel = personnelManager.getSquadronPersonnel(squadronId);
    	return squadronPersonnel.isPlayerSquadron();
    }

    public Date getDate()
    {
        return campaignData.getDate();
    }

    public void setDate(Date date) throws PWCGException 
    {
    	campaignData.setDate(date);
    }

    public String getCampaignDescription() throws PWCGException
    {
        ICampaignDescriptionBuilder campaignDescriptionBuilder = CampaignModeFactory.makeCampaignDescriptionBuilder(this);
        return campaignDescriptionBuilder.getCampaignDescription();
    }

    public boolean useMovingFrontInCampaign() throws PWCGException
    {
        boolean useMovingFront = true;
        if (getCampaignConfigManager().getIntConfigParam(ConfigItemKeys.MovingFrontKey) == 0)
        {
            useMovingFront = false;
        }
        
        return useMovingFront;
    }

    public static List<String> getCampaignNames() throws PWCGUserException 
    {       
        List<String> campaignList = new ArrayList<String>();
        String campaignRootDirName = PWCGContextManager.getInstance().getDirectoryManager().getPwcgCampaignsDir();
        File campaignRootDir = new File(campaignRootDirName);

        if (!campaignRootDir.exists() || !campaignRootDir.isDirectory())
        {
            throw new PWCGUserException ("Campaign directory does not exist: " + campaignRootDirName);
        } 
    
        String[] campaignNames = campaignRootDir.list();
        if (campaignNames != null) 
        {
            for (String campaignName : campaignNames) 
            {
                String campaignDirPath = campaignRootDirName + "\\" + campaignName;
                
                File campaignDir = new File(campaignDirPath);
                if (campaignDir.isDirectory())
                {
                    String[] fileNames = campaignDir.list();
                    if (fileNames != null) 
                    {
                        for (String fileName : fileNames) 
                        {
                            if(fileName.equals("Campaign.json")) 
                            {
                                campaignList.add(campaignName);
                            }
                        }
                    }
                }
            }
        } 
                
        return campaignList;
    }
	
	public String getCampaignPath()
	{
		String dir = PWCGContextManager.getInstance().getDirectoryManager().getPwcgCampaignsDir();
		String campaignPath = dir + campaignData.getName() + "\\"; 
		
		File campaignDir = new File(campaignPath); 
		if (!campaignDir.exists())
		{
			campaignDir.mkdir();
		}
		
		return campaignPath; 
	}
	
    public boolean isFighterCampaign() throws PWCGException 
    {
        for (SquadronMember player : this.personnelManager.getAllActivePlayers().getSquadronMemberList())
        {
            Squadron squadron =  PWCGContextManager.getInstance().getSquadronManager().getSquadron(player.getSquadronId());
            if (squadron.isSquadronThisPrimaryRole(getDate(), Role.ROLE_FIGHTER))
            {
                return true;
            }
        }
        
        return false;
    }

	public boolean isValidCampaignForProduct() throws PWCGException 
	{
		Date campaignDate = campaignData.getDate();
        if (campaignDate.before(DateUtils.getDateYYYYMMDD("19300101")) && !PWCGContextManager.isRoF())
        {
            return false;
        }
        
        if (campaignDate.after(DateUtils.getDateYYYYMMDD("19300101")) && PWCGContextManager.isRoF())
        {
            return false;
        }
		
		return true;
	}

    public boolean isCampaignActive() throws PWCGException
    {
        ICampaignActive campaignActive = CampaignModeFactory.makeCampaignActive(this);
        return campaignActive.isCampaignActive();
    }

    public boolean isCampaignCanFly() throws PWCGException
    {
        if (personnelManager.getFlyingPlayers().getSquadronMemberList().size() > 0)
        {
            return true;
        }
        return false;
    }

    public SquadronMember getReferenceCampaignMember() throws PWCGException
    {
        List<SquadronMember> players = this.getPersonnelManager().getAllActivePlayers().getSquadronMemberList();
        int index = RandomNumberGenerator.getRandom(players.size());
        SquadronMember referencePlayer = players.get(index);
        return referencePlayer;
    }

    public ICountry determineCampaignCountry() throws PWCGException
    {
        ICampaignCountryBuilder countryBuilder = CampaignModeFactory.makeCampaignCountryBuilder(this);
        return countryBuilder.determineCampaignCountry();
    }

    public List<Squadron> determinePlayerSquadrons() throws PWCGException
    {
        List<Squadron> playerSquadrons = new ArrayList<>();
        SquadronManager squadronManager = PWCGContextManager.getInstance().getSquadronManager();
        for (SquadronMember player : personnelManager.getAllActivePlayers().getSquadronMemberList())
        {
            Squadron playerSquadron = squadronManager.getSquadron(player.getSquadronId());
            playerSquadrons.add(playerSquadron);
        }
        return playerSquadrons;
    }

    public boolean isCoop()
    {
        if (campaignData.getCampaignMode() == CampaignMode.CAMPAIGN_MODE_SINGLE)
        {
            return false;
        }
        
        return true;
    }
    
    public SerialNumber getSerialNumber()
    {
        return campaignData.getSerialNumber();
    }

    public void resetFerryMission()
    {
        this.squadronMoveEvent = new SquadronMoveEvent();
    }

	public CampaignData getCampaignData()
	{
		return campaignData;
	}

	public void setCampaignData(CampaignData campaignData)
	{
		this.campaignData = campaignData;
	}

	public ConfigManagerCampaign getCampaignConfigManager()
	{
		return campaignConfigManager;
	}

	public void setCampaignConfigManager(ConfigManagerCampaign campaignConfigManager)
	{
		this.campaignConfigManager = campaignConfigManager;
	}

	public CampaignLogs getCampaignLogs()
	{
		return campaignLogs;
	}

	public void setCampaignLogs(CampaignLogs campaignLog)
	{
		this.campaignLogs = campaignLog;
	}

	public Mission getCurrentMission()
	{
		return currentMission;
	}

	public void setCurrentMission(Mission currentMission)
	{
		this.currentMission = currentMission;
	}

	public CampaignPersonnelManager getPersonnelManager()
	{
		return personnelManager;
	}

	public SquadronMoveEvent getSquadronMoveEvent()
	{
		return squadronMoveEvent;
	}

	public void setSquadronMoveEvent(SquadronMoveEvent squadronMoveEvent)
	{
		this.squadronMoveEvent = squadronMoveEvent;
	}

    public CampaignEquipmentManager getEquipmentManager()
    {
        return equipmentManager;
    }

    public void setEquipmentManager(CampaignEquipmentManager equipmentManager)
    {
        this.equipmentManager = equipmentManager;
    }

    public void setPersonnelManager(CampaignPersonnelManager personnelManager)
    {
        this.personnelManager = personnelManager;
    }
}
