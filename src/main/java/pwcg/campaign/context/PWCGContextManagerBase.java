package pwcg.campaign.context;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pwcg.campaign.BattleManager;
import pwcg.campaign.Campaign;
import pwcg.campaign.api.IAirfield;
import pwcg.campaign.context.PWCGMap.FrontMapIdentifier;
import pwcg.campaign.group.AirfieldManager;
import pwcg.campaign.plane.PlaneTypeFactory;
import pwcg.campaign.plane.payload.IPayloadFactory;
import pwcg.campaign.skin.SkinManager;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.Logger;
import pwcg.core.utils.Logger.LogLevel;

public abstract class PWCGContextManagerBase implements IPWCGContextManager
{
    RoFContextManager rofContextManager;
    BoSContextManager bosContextManager;

    protected Map<FrontMapIdentifier, PWCGMap> pwcgMaps = new HashMap<FrontMapIdentifier, PWCGMap>();
    protected FrontMapIdentifier currentMap = null;
    protected Campaign campaign = null;
    protected AceManager aceManager = new AceManager();
    protected SquadronManager squadronManager = new SquadronManager();
    protected BattleManager battleManager = new BattleManager();
    protected SkinManager skinManager = new SkinManager();
    protected PlaneTypeFactory planeTypeFactory = new PlaneTypeFactory();
    protected boolean testMode = false;
    
    protected boolean testUseMovingFront = true;
        
    protected List<String> campaignStartDates = new ArrayList<String>();
    
    public abstract boolean determineUseMovingFront() throws PWCGException;

    @Override
    public void resetForMovingFront() throws PWCGException
    {        
        for (PWCGMap map : pwcgMaps.values())
        {
            map.configure();
        }

        squadronManager.initialize();
    }

    protected void initialize() throws PWCGException 
    {
        initializeMap();
        
        planeTypeFactory.initialize();
        aceManager.configure();
        squadronManager.initialize();
        battleManager.initialize();
        skinManager.initialize();
    }

    @Override
    public void changeContext(FrontMapIdentifier frontMapIdentifier) throws PWCGException  
    {
        currentMap = frontMapIdentifier;
    }

    @Override
    public void setCampaign(Campaign campaign) throws PWCGException  
    {
        this.campaign = campaign;
        
        if (campaign != null)
        {
            List<FrontMapIdentifier> mapIdentifiers = getMapForAirfield(campaign.getAirfieldName());
            if (mapIdentifiers.size() > 0)
            {
                changeContext(mapIdentifiers.get(0));
                resetForMovingFront();
            }
        }
    }

    @Override
    public Date getEarliestPwcgDate() throws PWCGException 
    {
        Date earliesDateForPWCG = null;        
        for (PWCGMap map : pwcgMaps.values())
        {
            Date earliestMapDate = map.getFrontDatesForMap().getEarliestMapDate();
            if (earliesDateForPWCG == null)
            {
                earliesDateForPWCG = earliestMapDate;
            }
            else
            {
                if (earliestMapDate.before(earliesDateForPWCG))
                {
                    earliesDateForPWCG = earliestMapDate;
                }
            }
        }
        
        return earliesDateForPWCG;
    }

    @Override
    public Campaign getCampaign()
    {
        return campaign;
    }

    @Override
    public PWCGMap getCurrentMap()
    {
        return pwcgMaps.get(currentMap);
    }

    @Override
    public PWCGMap getMapByMapName(String mapName)
    {
        FrontMapIdentifier mapId = PWCGMap.getFrontMapIdentifierForName(mapName);
        return pwcgMaps.get(mapId);
    }

    @Override
    public PWCGMap getMapByMapId(FrontMapIdentifier mapId)
    {
        return pwcgMaps.get(mapId);
    }

    @Override
    public AceManager getAceManager()
    {
        return aceManager;
    }

    @Override
    public boolean isTestMode()
    {
        return testMode;
    }

    @Override
    public void setTestMode(boolean testMode)
    {
        this.testMode = testMode;
    }

    @Override
    public List<String> getCampaignStartDates()
    {
        return campaignStartDates;
    }

    @Override
    public boolean isTestUseMovingFront()
    {
        return testUseMovingFront;
    }

    @Override
    public void setTestUseMovingFront(boolean newTestUseMovingFront) throws PWCGException
    {
        testUseMovingFront = newTestUseMovingFront;
        initialize();
    }

    @Override
    public List<PWCGMap> getAllMaps()
    {
        List<PWCGMap> allMaps = new ArrayList<PWCGMap>();
        allMaps.addAll(pwcgMaps.values());
        
        return allMaps;
    }

    @Override
    public IAirfield getAirfieldAllMaps(String airfieldName)
    {
        IAirfield airfield = null;
        
        for (PWCGMap map : pwcgMaps.values())
        {
            AirfieldManager airfieldManager = map.getAirfieldManager();
            if (airfieldManager != null)
            {
                airfield = airfieldManager.getAirfield(airfieldName);
                
                if (airfield != null)
                {
                    Logger.log(LogLevel.DEBUG, airfieldName + ": found on map " + map.getMapName());
                    return airfield;
                }
            }

            Logger.log(LogLevel.DEBUG, airfieldName + ": found NOT on map " + map.getMapName());
        }

        return airfield;
    }

    @Override
    public List<FrontMapIdentifier> getMapForAirfield(String airfieldName)
    {
        List<FrontMapIdentifier> mapsForAirfield = new ArrayList<>();
        for (PWCGMap map : pwcgMaps.values())
        {
            AirfieldManager airfieldManager = map.getAirfieldManager();
            if (airfieldManager != null)
            {
                IAirfield airfield = airfieldManager.getAirfield(airfieldName);
                
                if (airfield != null)
                {
                    mapsForAirfield.add(map.getMapIdentifier());
                }
            }
        }

        return mapsForAirfield;
    }
    
    @Override
    public SquadronManager getSquadronManager()
    {
        return squadronManager;
    }

    @Override
    public BattleManager getBattleManager()
    {
        return battleManager;
    }

    @Override
    public SkinManager getSkinManager()
    {
        return skinManager;
    }

    @Override
    public PlaneTypeFactory getPlaneTypeFactory()
    {
        return planeTypeFactory;
    }

    @Override
    public abstract IPayloadFactory getPayloadFactory() throws PWCGException; 

    @Override
    public abstract void initializeMap() throws PWCGException;    

}
