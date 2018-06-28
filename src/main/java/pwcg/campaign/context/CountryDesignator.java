package pwcg.campaign.context;

import java.util.Date;

import pwcg.campaign.api.ICountry;
import pwcg.campaign.api.IProductSpecificConfiguration;
import pwcg.campaign.api.Side;
import pwcg.campaign.factory.CountryFactory;
import pwcg.campaign.factory.ProductSpecificConfigurationFactory;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.core.utils.MathUtils;

public class CountryDesignator
{
    
    public ICountry determineCountry(Coordinate objectCoordinate, Date date) throws PWCGException
    {
        ICountry country = CountryFactory.makeNeutralCountry();
        
        FrontLinesForMap frontLines = PWCGContextManager.getInstance().getCurrentMap().getFrontLinesForMap(date);
        
        Coordinate closestAllied = frontLines.findClosestFrontCoordinateForSide(objectCoordinate, Side.ALLIED);
        Coordinate closestAxis = frontLines.findClosestFrontCoordinateForSide(objectCoordinate, Side.AXIS);

        double distanceToAllied = MathUtils.calcDist(objectCoordinate, closestAllied);
        double distanceToAxis = MathUtils.calcDist(objectCoordinate, closestAxis);
        
        IProductSpecificConfiguration productSpecific = ProductSpecificConfigurationFactory.createProductSpecificConfiguration();
        int neutralZone = productSpecific.geNeutralZone();

        if (distanceToAllied > neutralZone && distanceToAxis > neutralZone)
        {
            country = CountryFactory.makeMapReferenceCountry(Side.ALLIED);
            if (distanceToAxis < distanceToAllied)
            {
                country = CountryFactory.makeMapReferenceCountry(Side.AXIS);
            }
        }

        return country;
    }
}
