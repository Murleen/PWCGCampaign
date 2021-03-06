package pwcg.product.bos.map.bodenplatte;

import java.awt.Point;

import pwcg.campaign.context.PWCGMap;
import pwcg.product.bos.country.BoSServiceManager;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.DateUtils;

public class BodenplatteMap extends PWCGMap
{

    public BodenplatteMap()
    {
        super();
    }

    public void configure() throws PWCGException
    {
        this.mapName = BODENPLATTE_MAP_NAME;        
        this.mapIdentifier = FrontMapIdentifier.BODENPLATTE_MAP;
        this.mapCenter = new Point(1000, 1000);
        
        this.missionOptions = new BodenplatteMissionOptions();
        this.mapWeather = new BodenplatteMapWeather();
        
        frontParameters = new BodenplatteFrontParameters();
        buildArmedServicesActiveForMap();
        
        super.configure();
    }
    
    private void buildArmedServicesActiveForMap()
    {
        armedServicesActiveForMap.add(BoSServiceManager.USAAF);
        armedServicesActiveForMap.add(BoSServiceManager.RAF);
        armedServicesActiveForMap.add(BoSServiceManager.FREE_FRENCH);
        armedServicesActiveForMap.add(BoSServiceManager.LUFTWAFFE);
    }

    @Override
    protected void configureTransitionDates() throws PWCGException
    {
        this.frontDatesForMap.addMapDateRange(DateUtils.getDateYYYYMMDD("19440901"), DateUtils.getDateYYYYMMDD("19450503"));

        this.frontDatesForMap.addFrontDate("19440901");
        this.frontDatesForMap.addFrontDate("19441001");
        this.frontDatesForMap.addFrontDate("19441101");
        this.frontDatesForMap.addFrontDate("19441220");
        this.frontDatesForMap.addFrontDate("19441225");
        this.frontDatesForMap.addFrontDate("19441229");
        this.frontDatesForMap.addFrontDate("19450207");
        this.frontDatesForMap.addFrontDate("19450310");
        this.frontDatesForMap.addFrontDate("19450404");
    }

}
