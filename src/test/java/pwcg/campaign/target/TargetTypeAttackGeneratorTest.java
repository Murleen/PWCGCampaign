package pwcg.campaign.target;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import pwcg.campaign.Campaign;
import pwcg.campaign.api.Side;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.context.PWCGMap.FrontMapIdentifier;
import pwcg.campaign.context.PWCGProduct;
import pwcg.campaign.target.locator.targettype.TargetTypeAttackGenerator;
import pwcg.campaign.target.locator.targettype.TargetTypeAvailabilityInputs;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.core.utils.DateUtils;
import pwcg.mission.target.TargetType;

@RunWith(MockitoJUnitRunner.class)
public class TargetTypeAttackGeneratorTest
{    
    @Mock private TargetTypeAvailabilityInputs targetTypeAvailabilityInputs;
    @Mock private Campaign campaign;
    
    @Before
    public void setup() throws PWCGException
    {
        PWCGContext.setProduct(PWCGProduct.BOS);

        Mockito.when(targetTypeAvailabilityInputs.getSide()).thenReturn(Side.AXIS);
        Mockito.when(targetTypeAvailabilityInputs.getTargetGeneralLocation()).thenReturn(new Coordinate(216336, 0, 184721));
        Mockito.when(targetTypeAvailabilityInputs.getPreferredDistance()).thenReturn(60000.0);
        Mockito.when(targetTypeAvailabilityInputs.getMaxDistance()).thenReturn(100000.0);        
    }
    
    @Test
    public void kubanTargetDrifterAvailabilityTest() throws PWCGException
    {
        Mockito.when(targetTypeAvailabilityInputs.getDate()).thenReturn(DateUtils.getDateYYYYMMDD("19430401"));
        Mockito.when(campaign.useMovingFrontInCampaign()).thenReturn(true);
        PWCGContext.getInstance().changeContext(FrontMapIdentifier.KUBAN_MAP);
        testPlaceAndTarget(TargetType.TARGET_DRIFTER, true);
    }
    
    @Test
    public void kubanTargetShippingAvailabilityTest() throws PWCGException
    {
        Mockito.when(targetTypeAvailabilityInputs.getDate()).thenReturn(DateUtils.getDateYYYYMMDD("19430401"));
        Mockito.when(campaign.useMovingFrontInCampaign()).thenReturn(true);
        PWCGContext.getInstance().changeContext(FrontMapIdentifier.KUBAN_MAP);
        testPlaceAndTarget(TargetType.TARGET_SHIPPING, true);
    }
    
    @Test
    public void moscowNoTargetDrifterAvailabilityTest() throws PWCGException
    {
        Mockito.when(targetTypeAvailabilityInputs.getDate()).thenReturn(DateUtils.getDateYYYYMMDD("19411001"));
        Mockito.when(campaign.useMovingFrontInCampaign()).thenReturn(true);
        PWCGContext.getInstance().changeContext(FrontMapIdentifier.MOSCOW_MAP);
        testPlaceAndTarget(TargetType.TARGET_DRIFTER, false);
    }
    
    @Test
    public void moscowNoTargetShippingAvailabilityTest() throws PWCGException
    {
        Mockito.when(targetTypeAvailabilityInputs.getDate()).thenReturn(DateUtils.getDateYYYYMMDD("19411001"));
        Mockito.when(campaign.useMovingFrontInCampaign()).thenReturn(true);
        PWCGContext.getInstance().changeContext(FrontMapIdentifier.MOSCOW_MAP);
        testPlaceAndTarget(TargetType.TARGET_SHIPPING, false);
    }

    private void testPlaceAndTarget(TargetType targetType, boolean assertion) throws PWCGException
    {
        TargetTypeAttackGenerator targetTypeAttackGenerator = new TargetTypeAttackGenerator(targetTypeAvailabilityInputs);        
        targetTypeAttackGenerator.formTargetPriorities();
        List <TargetType> preferredTargetTypes = targetTypeAttackGenerator.getPreferredTargetTypes();
        assert(preferredTargetTypes.contains(targetType) == assertion);
    }

}
