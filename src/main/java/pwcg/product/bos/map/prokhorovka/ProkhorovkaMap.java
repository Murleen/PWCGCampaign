package pwcg.product.bos.map.prokhorovka;

import java.awt.Point;

import pwcg.campaign.context.PWCGMap;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.DateUtils;

public class ProkhorovkaMap extends PWCGMap
{

    public ProkhorovkaMap()
    {
        super();
    }
    
    
    /**
     * Configure all of the manager objects for this map
     * @throws PWCGException 
     */
    public void configure() throws PWCGException
    {
        this.mapName = PROKHOROVKA_MAP_NAME;        
        this.mapIdentifier = FrontMapIdentifier.PROKHOROVKA_MAP;
        this.mapCenter = new Point(700, 700);
        
        this.missionOptions = new ProkhorovkaMissionOptions();
        this.mapWeather = new ProkhorovkaMapWeather();
        
        frontParameters = new ProkhorovkaFrontParameters();

        super.configure();
    }
    
    @Override
    protected void configureTransitionDates() throws PWCGException
    {
        this.frontDatesForMap.addMapDateRange(DateUtils.getDateYYYYMMDD("19430701"), DateUtils.getDateYYYYMMDD("19430831"));

        this.frontDatesForMap.addFrontDate("19430715");
}

}
