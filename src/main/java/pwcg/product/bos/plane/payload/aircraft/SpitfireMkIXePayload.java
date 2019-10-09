package pwcg.product.bos.plane.payload.aircraft;

import pwcg.campaign.plane.PlaneType;
import pwcg.campaign.plane.payload.IPlanePayload;
import pwcg.campaign.plane.payload.PayloadElement;
import pwcg.campaign.plane.payload.PlanePayload;
import pwcg.campaign.target.TargetCategory;
import pwcg.mission.flight.Flight;
import pwcg.mission.flight.FlightTypes;

public class SpitfireMkIXePayload extends PlanePayload implements IPlanePayload
{
    public SpitfireMkIXePayload(PlaneType planeType)
    {
        super(planeType);
    }

    protected void initialize()
	{
        setAvailablePayload(-4, "10000000", PayloadElement.MERLIN_70_ENGINE);
        setAvailablePayload(-3, "1000000", PayloadElement.SPITFIRE_IX_WINGTIPS);
        setAvailablePayload(-2, "100000", PayloadElement.MIRROR);
        setAvailablePayload(-1, "10000", PayloadElement.GYRO_GUNSIGHT);
        setAvailablePayload(0, "1", PayloadElement.STANDARD);
        setAvailablePayload(1, "11", PayloadElement.SC500_X1);
        setAvailablePayload(2, "101", PayloadElement.SC250_X2);
        setAvailablePayload(4, "1001", PayloadElement.RP3_X2);        
	}

    @Override
    public int createWeaponsPayload(Flight flight)
    {
        selectedPrimaryPayloadId = 0;
        if (flight.getFlightType() == FlightTypes.GROUND_ATTACK)
        {
            selectGroundAttackPayload(flight);
        }
        
        return selectedPrimaryPayloadId;
    }

    protected void selectGroundAttackPayload(Flight flight)
    {
        selectedPrimaryPayloadId = 2;
        if (flight.getTargetCategory() == TargetCategory.TARGET_CATEGORY_SOFT)
        {
            selectedPrimaryPayloadId = 2;
        }
        else if (flight.getTargetCategory() == TargetCategory.TARGET_CATEGORY_ARMORED)
        {
            selectedPrimaryPayloadId = 4;
        }
        else if (flight.getTargetCategory() == TargetCategory.TARGET_CATEGORY_MEDIUM)
        {
            selectedPrimaryPayloadId = 1;
        }
        else if (flight.getTargetCategory() == TargetCategory.TARGET_CATEGORY_HEAVY)
        {
            selectedPrimaryPayloadId = 1;
        }
    }
    
    @Override
    public IPlanePayload copy()
    {
    	SpitfireMkIXePayload clone = new SpitfireMkIXePayload(planeType);
        
        return super.copy(clone);
    }
}
