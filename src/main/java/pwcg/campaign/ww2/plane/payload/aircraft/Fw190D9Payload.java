package pwcg.campaign.ww2.plane.payload.aircraft;

import pwcg.campaign.plane.PlaneType;
import pwcg.campaign.plane.payload.IPlanePayload;
import pwcg.campaign.plane.payload.PayloadElement;
import pwcg.campaign.plane.payload.PlanePayload;
import pwcg.campaign.target.TargetCategory;
import pwcg.core.utils.RandomNumberGenerator;
import pwcg.mission.flight.Flight;
import pwcg.mission.flight.FlightTypes;

public class Fw190D9Payload extends PlanePayload implements IPlanePayload
{
    public Fw190D9Payload(PlaneType planeType)
    {
        super(planeType);
    }

    protected void initialize()
	{        
        setAvailablePayload(-2, "10000000", PayloadElement.BUBBLE_CANOPY);
        setAvailablePayload(-1, "1000000", PayloadElement.GYRO_GUNSIGHT);
        setAvailablePayload(0, "1", PayloadElement.STANDARD);
        setAvailablePayload(1, "11", PayloadElement.SD70_X4);
        setAvailablePayload(2, "101", PayloadElement.SC250_X1);
        setAvailablePayload(3, "1001", PayloadElement.SC500_X1);
        setAvailablePayload(4, "10001", PayloadElement.BR21_X2);
        setAvailablePayload(8, "100001", PayloadElement.R4M_X26);
	}
    
    @Override
    public IPlanePayload copy()
    {
        Fw190D9Payload clone = new Fw190D9Payload(planeType);
        
        return super.copy(clone);
    }

    @Override
    public int createWeaponsPayload(Flight flight)
    {
        selectedPrimaryPayloadId = 0;
        if (flight.getFlightType() == FlightTypes.GROUND_ATTACK)
        {
            selectGroundAttackPayload(flight);
        }
        else if (flight.getFlightType() == FlightTypes.INTERCEPT)
        {
            selectInterceptPayload();
        }
        else 
        {
            createStandardPayload();
        }
        
        return selectedPrimaryPayloadId;
    }    

    protected void createStandardPayload()
    {
        selectedPrimaryPayloadId = 0;
   }

	protected void selectGroundAttackPayload(Flight flight)
    {
        selectedPrimaryPayloadId = 1;
        if (flight.getTargetCategory() == TargetCategory.TARGET_CATEGORY_SOFT)
        {
            selectedPrimaryPayloadId = 1;
        }
        else if (flight.getTargetCategory() == TargetCategory.TARGET_CATEGORY_ARMORED)
        {
            selectedPrimaryPayloadId = 3;
        }
        else if (flight.getTargetCategory() == TargetCategory.TARGET_CATEGORY_MEDIUM)
        {
            selectedPrimaryPayloadId = 2;
        }
        else if (flight.getTargetCategory() == TargetCategory.TARGET_CATEGORY_HEAVY)
        {
            selectedPrimaryPayloadId = 3;
        }
    }

    protected void selectInterceptPayload()
    {
        int diceRoll = RandomNumberGenerator.getRandom(100);
        if (diceRoll < 30)
        {
            selectedPrimaryPayloadId = 4;
        }
        else if (diceRoll < 60)
        {
            selectedPrimaryPayloadId = 8;
        }
        else
        {
            selectedPrimaryPayloadId = 0;
        }
    }    

}
