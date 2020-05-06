package pwcg.product.bos.map.east1944;

import java.util.Map;
import java.util.TreeMap;

import pwcg.campaign.api.ICountry;
import pwcg.campaign.api.Side;
import pwcg.campaign.context.Country;
import pwcg.campaign.context.PWCGMap;
import pwcg.campaign.factory.CountryFactory;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.DateUtils;
import pwcg.product.bos.country.BoSServiceManager;

public class East1944Map extends PWCGMap
{
    private static final Map<String, Integer> missionSpacingMyDate;
    static
    { 
        missionSpacingMyDate = new TreeMap<>(); 
        missionSpacingMyDate.put("19440101", 3); 
        missionSpacingMyDate.put("19440316", 2); 
        missionSpacingMyDate.put("19440612", 1); 
        missionSpacingMyDate.put("19440908", 2); 
        missionSpacingMyDate.put("19441011", 3); 
    } 

    public East1944Map()
    {
        super();
    }

    public void configure() throws PWCGException
    {
        this.mapName = EAST1944_MAP_NAME;        
        this.mapIdentifier = FrontMapIdentifier.EAST1944_MAP;
        
        if (missionOptions == null)
        {
            missionOptions = new East1944MissionOptions();
        }

        if (mapWeather == null)
        {
            this.mapWeather = new East1944MapWeather();
        }
        
        frontParameters = new East1944FrontParameters();
        buildArmedServicesActiveForMap();
        
        super.configure();
    }
    
    private void buildArmedServicesActiveForMap()
    {
        armedServicesActiveForMap.add(BoSServiceManager.VVS);
        armedServicesActiveForMap.add(BoSServiceManager.NORMANDIE);
        armedServicesActiveForMap.add(BoSServiceManager.LUFTWAFFE);
    }

    @Override
    protected void configureTransitionDates() throws PWCGException
    {
        this.frontDatesForMap.addMapDateRange(DateUtils.getDateYYYYMMDD("19440101"), DateUtils.getDateYYYYMMDD("19441231"));
        this.frontDatesForMap.addFrontDate("19440101");
        this.frontDatesForMap.addFrontDate("19440201");
        this.frontDatesForMap.addFrontDate("19440301");
        this.frontDatesForMap.addFrontDate("19440401");
        this.frontDatesForMap.addFrontDate("19440501");
        this.frontDatesForMap.addFrontDate("19440601");
        this.frontDatesForMap.addFrontDate("19440701");
        this.frontDatesForMap.addFrontDate("19440801");
        this.frontDatesForMap.addFrontDate("19440901");
        this.frontDatesForMap.addFrontDate("19441001");
        this.frontDatesForMap.addFrontDate("19441101");
        this.frontDatesForMap.addFrontDate("19441201");
    }

    @Override
    public ICountry getGroundCountryForMapBySide(Side side)
    {
        if (side == Side.ALLIED)
        {
            return CountryFactory.makeCountryByCountry(Country.RUSSIA);
        }
        else
        {
            return CountryFactory.makeCountryByCountry(Country.GERMANY);
        }
    }

    @Override
    protected Map<String, Integer> getMissionSpacingMyDate()
    {
        return missionSpacingMyDate;
    }
}
