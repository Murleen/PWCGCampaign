package pwcg.campaign.target;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import pwcg.campaign.api.Side;
import pwcg.campaign.context.PWCGContextManager;
import pwcg.campaign.context.PWCGMap.FrontMapIdentifier;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.core.utils.DateUtils;
import pwcg.core.utils.PositionFinder;

@RunWith(MockitoJUnitRunner.class)
public class TargetTypeAvailabilityTest
{
    private TargetTypeAvailability targetTypeAvailability;
    
    @Before
    public void setup() throws PWCGException
    {
        PWCGContextManager.setRoF(false);
        
        targetTypeAvailability = new TargetTypeAvailability(Side.AXIS, DateUtils.getDateYYYYMMDD("19430401"));
    }
    
    @Test
    public void kubanDrifterTargetAvailabilityTest() throws PWCGException
    {
        PWCGContextManager.setRoF(false);
        PWCGContextManager.getInstance().changeContext(FrontMapIdentifier.KUBAN_MAP);
        double distanceOfClosestInstanceToReference = targetTypeAvailability.getTargetTypeAvailability(TacticalTarget.TARGET_DRIFTER, new Coordinate(216336, 0, 184721), 80000);
        assert(distanceOfClosestInstanceToReference < PositionFinder.ABSURDLY_LARGE_DISTANCE);
    }
    
    @Test
    public void moscpwDrifterTargetAvailabilityTest() throws PWCGException
    {
        PWCGContextManager.setRoF(false);
        PWCGContextManager.getInstance().changeContext(FrontMapIdentifier.MOSCOW_MAP);
        double distanceOfClosestInstanceToReference = targetTypeAvailability.getTargetTypeAvailability(TacticalTarget.TARGET_DRIFTER, new Coordinate(216336, 0, 184721), 80000);
        assert(distanceOfClosestInstanceToReference == PositionFinder.ABSURDLY_LARGE_DISTANCE);
    }
    

}
