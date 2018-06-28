package pwcg.campaign;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pwcg.aar.ui.events.model.SquadronMoveEvent;
import pwcg.campaign.api.IAirfield;
import pwcg.campaign.api.ICountry;
import pwcg.campaign.context.PWCGContextManager;
import pwcg.campaign.context.PWCGDirectoryManager;
import pwcg.campaign.context.PWCGMap.FrontMapIdentifier;
import pwcg.campaign.group.AirfieldManager;
import pwcg.campaign.io.json.CampaignIOJson;
import pwcg.campaign.personnel.SquadronPersonnel;
import pwcg.campaign.plane.Role;
import pwcg.campaign.squadmember.SerialNumber;
import pwcg.campaign.squadmember.SquadronMember;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.config.ConfigItemKeys;
import pwcg.core.config.ConfigManagerCampaign;
import pwcg.core.exception.PWCGException;
import pwcg.core.exception.PWCGUserException;
import pwcg.core.location.Coordinate;
import pwcg.core.utils.DateUtils;
import pwcg.core.utils.Logger;
import pwcg.mission.Mission;

public class Campaign
{
    private CampaignData campaignData = new CampaignData();
    

    private ConfigManagerCampaign campaignConfigManager = null;
    private CampaignLogs campaignLogs = null;

    private Mission currentMission = null;    
    private CampaignPersonnelManager personnelManager = null;
    private SquadronMoveEvent squadronMoveEvent = new SquadronMoveEvent();
    private SerialNumber serialNumber = new SerialNumber();

    public Campaign() 
    {
        personnelManager = new CampaignPersonnelManager(this);
        campaignLogs = new CampaignLogs();
    }

    public boolean open(String campaignName) throws PWCGException 
    {
        campaignData.setName(campaignName);
        
        initializeCampaignConfigs();
        if (!readValidCampaign())
        {
            return false;
        }

        setNextSerialNumberForCampaign();
        return true;
    }
        
    private void setNextSerialNumberForCampaign() throws PWCGException
    {
        int lastSerialNumber = SerialNumber.AI_STARTING_SERIAL_NUMBER;
        for (SquadronPersonnel squadronPersonnel : personnelManager.getAllSquadronPersonnel())
        {
            for (SquadronMember squadronMember : squadronPersonnel.getActiveSquadronMembers().getSquadronMembers().values())
            {
                if (squadronMember.getSerialNumber() > lastSerialNumber)
                {
                    lastSerialNumber = squadronMember.getSerialNumber();
                }
            }
        }
        
        ++lastSerialNumber;
        serialNumber.setLastSerialNumber(lastSerialNumber);
    }

    public void write() throws PWCGException
    {
        CampaignIOJson.writeJson(this);
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

    public void  initializeCampaignConfigs() throws PWCGException 
    {
        String campaignConfigDir = getCampaignPath() + "config\\";
        createConfigDirIfNeeded(campaignConfigDir);
        
        campaignConfigManager = new ConfigManagerCampaign(campaignConfigDir);
        campaignConfigManager.initialize();
    }

    private String createConfigDirIfNeeded(String campaignConfigDir)
    {
        File dir = new File(campaignConfigDir);
        if (!dir.exists())
        {
            dir.mkdir();
        }

        return campaignConfigDir;
    }

    public SquadronMember getPlayer() throws PWCGException 
    {
        for (SquadronPersonnel squadronPersonnel : personnelManager.getAllSquadronPersonnel())
        {
            for (SquadronMember squadMember : squadronPersonnel.getActiveSquadronMembers().getSquadronMembers().values())
            {
                if (squadMember.isPlayer())
                {
                    return squadMember;
                }
            }
        }

        return null;
    }

    public String getAirfieldName() throws PWCGException 
    {
        Squadron squadron = determineSquadron();
        if (squadron != null)
        {
            return squadron.determineCurrentAirfieldName(getDate());
        }
        
        throw new PWCGException ("Unable to find campaign squadron for id " + campaignData.getSquadId());
    }

    public ICountry determineCountry() throws PWCGException
    {
        Squadron squadron = PWCGContextManager.getInstance().getSquadronManager().getSquadron(campaignData.getSquadId());
        return squadron.determineSquadronCountry(campaignData.getDate());
    }

    public Date getDate()
    {
        return campaignData.getDate();
    }

    public void setDate(Date date) throws PWCGException 
    {
    	campaignData.setDate(date);
    }

    public int getSquadronId()
    {
        return campaignData.getSquadId();
    }

    public void setSquadId(int squadId)
    {
    	campaignData.setSquadId(squadId);
    }

    public Squadron determineSquadron() throws PWCGException 
    {
        Squadron squadron = PWCGContextManager.getInstance().getSquadronManager().getSquadron(campaignData.getSquadId());
        
        if (squadron == null)
        {
        	throw new PWCGException("Unable to determine squadron for campaign.  Squadron id is " + campaignData.getSquadId());
        }
        
        return squadron;
    }

    public String getName()
    {
        return campaignData.getName();
    }

    public void setName(String name)
    {
    	campaignData.setName(name);
    }

    public FrontMapIdentifier getMapForCampaign() throws PWCGException
    {
        List<FrontMapIdentifier> mapsForAirfield = AirfieldManager.getMapIdForAirfield(getAirfieldName());

        for (FrontMapIdentifier map : mapsForAirfield)
        {
        	return map;
        }
        
        return null;
    }

    public IAirfield getPlayerAirfield() throws PWCGException
    {
        String airfieldName = getAirfieldName();
        IAirfield playerAirfield =  PWCGContextManager.getInstance().getAirfieldAllMaps(airfieldName);

        return playerAirfield;
    }

    public Coordinate getPosition() throws PWCGException
    {
        IAirfield playerAirfield =  getPlayerAirfield();
        Coordinate playerFieldPosition = playerAirfield.getPosition().copy();

        return playerFieldPosition;
    }

    public int getCampaignStatus()
    {
        return campaignData.getCampaignStatus();
    }

    public void setCampaignStatus(int campaignStatus)
    {
    	campaignData.setCampaignStatus(campaignStatus);;
    }

    public String getCampaignDescription() throws PWCGException
    {
        String campaignDescription = "";
        SquadronMember player = getPlayer();
        
        String nameString = player.getRank() + " " + player.getSerialNumber();
        campaignDescription += nameString;

        campaignDescription += "     " + DateUtils.getDateString(getDate());
        
        Squadron squadron =  determineSquadron();
        campaignDescription += "     " + squadron.determineDisplayName(getDate());
        
        campaignDescription += "     " + getAirfieldName();
        
        return campaignDescription;
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
        String campaignRootDirName = PWCGDirectoryManager.getInstance().getPwcgCampaignsDir();
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
		String dir = PWCGDirectoryManager.getInstance().getPwcgCampaignsDir();
		String campaignPath = dir + getName() + "\\"; 
		
		File campaignDir = new File(campaignPath); 
		if (!campaignDir.exists())
		{
			campaignDir.mkdir();
		}
		
		return campaignPath; 
	}

	public boolean isBattle() throws PWCGException
	{
	    if (PWCGContextManager.getInstance().getBattleManager().getBattleForCampaign(this.getMapForCampaign(), this.getPlayerAirfield().getPosition(), this.getDate()) != null)
	    {
	        return true;
	    }
	    
	    return false;
	}
	
    public boolean isFighterCampaign() throws PWCGException 
    {
        Squadron squad = determineSquadron();
        if (squad.isSquadronThisRole(campaignData.getDate(), Role.ROLE_FIGHTER))
        {
        	return true;
        }
        
        return false;
    }

	public boolean isPlayerCommander() throws PWCGException
	{
	    return getPlayer().determineIsSquadronMemberCommander();
	}

	private boolean isValidCampaignForProduct() throws PWCGException 
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

    public SerialNumber getSerialNumber()
    {
        return serialNumber;
    }

    public boolean isGreatAce()
	{
	    return campaignData.isGreatAce();
	}
    
    public void setGreatAce(boolean isGreatAce)
    {
    	campaignData.setGreatAce(isGreatAce);
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
}
