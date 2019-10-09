package pwcg.campaign.group;

import java.util.List;

import org.junit.Test;

import pwcg.campaign.api.IAirfield;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.context.PWCGMap.FrontMapIdentifier;
import pwcg.campaign.context.PWCGProduct;
import pwcg.campaign.group.airfield.HotSpot;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.CoordinateBox;

public class EmptySpaceFinderTest
{
    @Test
    public void findEmptySpaceAroundAirfield() throws PWCGException
    {
        PWCGContext.setProduct(PWCGProduct.BOS);
        PWCGContext.getInstance().changeContext(FrontMapIdentifier.STALINGRAD_MAP);
        AirfieldManager airfieldManager  = PWCGContext.getInstance().getCurrentMap().getAirfieldManager();
        
        for (IAirfield airfield : airfieldManager.getAllAirfields().values())
        {
            EmptySpaceFinder emptySpaceFinder = new EmptySpaceFinder();
            List<HotSpot> hotSpots = emptySpaceFinder.findEmptySpaces(airfield.getPosition(), 1000);
            
            assert (hotSpots.size() > 0);

            CoordinateBox coordinateBox = CoordinateBox.coordinateBoxFromCenter(airfield.getPosition(), 1000);
            for (HotSpot hotSpot : hotSpots)
            {
                assert(coordinateBox.isInBox(hotSpot.getPosition()));
            }
            
        }
    }
}
