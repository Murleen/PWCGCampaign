package pwcg.product.bos.map.velikieluki;

import java.awt.Point;

import pwcg.campaign.context.PWCGMap;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.DateUtils;

public class VelikieLukiMap extends PWCGMap
{

    public VelikieLukiMap()
    {
        super();
    }
    
    
    /**
     * Configure all of the manager objects for this map
     * @throws PWCGException 
     */
    public void configure() throws PWCGException
    {
        this.mapName = VELIKIE_LUKI_MAP_NAME;        
        this.mapIdentifier = FrontMapIdentifier.VELIKIE_LUKI_MAP;
        this.mapCenter = new Point(700, 700);
        
        this.missionOptions = new VelikieLukiMissionOptions();
        this.mapWeather = new VelikieLukiMapWeather();
        
        frontParameters = new VelikieLukiFrontParameters();

        super.configure();
    }
    
    @Override
    protected void configureTransitionDates() throws PWCGException
    {
        this.frontDatesForMap.addMapDateRange(DateUtils.getDateYYYYMMDD("19430701"), DateUtils.getDateYYYYMMDD("19430831"));

        this.frontDatesForMap.addFrontDate("19430715");
}

}
