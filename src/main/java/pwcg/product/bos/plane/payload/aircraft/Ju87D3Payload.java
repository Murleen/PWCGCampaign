package pwcg.product.bos.plane.payload.aircraft;

import pwcg.campaign.plane.PlaneType;
import pwcg.campaign.plane.payload.IPlanePayload;
import pwcg.campaign.plane.payload.PayloadElement;
import pwcg.campaign.plane.payload.PlanePayload;
import pwcg.campaign.target.TargetCategory;
import pwcg.core.utils.RandomNumberGenerator;
import pwcg.mission.flight.Flight;
import pwcg.mission.flight.FlightTypes;

public class Ju87D3Payload extends PlanePayload
{
    public Ju87D3Payload(PlaneType planeType)
    {
        super(planeType);
    }

    protected void initialize()
	{
        setAvailablePayload(-2, "1000", PayloadElement.EXTRA_ARMOR);
        setAvailablePayload(-1, "10", PayloadElement.SIREN);
        setAvailablePayload(0, "1", PayloadElement.STANDARD);
		setAvailablePayload(1, "1", PayloadElement.SC250_X1, PayloadElement.SD70_X4);
        setAvailablePayload(2, "1", PayloadElement.SC500_X1);
        setAvailablePayload(3, "1", PayloadElement.SC500_X1, PayloadElement.SD70_X4);
        setAvailablePayload(4, "1", PayloadElement.SC500_X1, PayloadElement.SC250_X2);
		setAvailablePayload(5, "1", PayloadElement.SC250_X3);
		setAvailablePayload(6, "1", PayloadElement.SC1000_X1);
		setAvailablePayload(7, "101", PayloadElement.SC1800_X1);
		setAvailablePayload(9, "100001", PayloadElement.BK37_AP_GUNPOD);
		setAvailablePayload(10, "100001", PayloadElement.BK37_HE_GUNPOD);
	}

    @Override
    public IPlanePayload copy()
    {
        Ju87D3Payload clone = new Ju87D3Payload(planeType);
        
        return super.copy(clone);
    }

    @Override
    public int createWeaponsPayload(Flight flight)
    {
        selectedPrimaryPayloadId = 0;
        if (flight.getFlightType() == FlightTypes.DIVE_BOMB)
        {
            selectDiveBombPayload(flight);
        }
        else if (flight.getFlightType() == FlightTypes.GROUND_ATTACK)
        {
            selectGroundAttackPayload();
        }
        return selectedPrimaryPayloadId;
    }    

    private void selectDiveBombPayload(Flight flight)
    {
        selectedPrimaryPayloadId = 1;
        if (flight.getTargetCategory() == TargetCategory.TARGET_CATEGORY_SOFT)
        {
            selectSoftTargetPayload();
        }
        else if (flight.getTargetCategory() == TargetCategory.TARGET_CATEGORY_ARMORED)
        {
            selectArmoredTargetPayload();
        }
        else if (flight.getTargetCategory() == TargetCategory.TARGET_CATEGORY_MEDIUM)
        {
            selectMediumTargetPayload();
        }
        else if (flight.getTargetCategory() == TargetCategory.TARGET_CATEGORY_HEAVY)
        {
            selectHeavyTargetPayload();
        }
    }

    private void selectSoftTargetPayload()
    {
        selectedPrimaryPayloadId = 1;
    }    

    private void selectArmoredTargetPayload()
    {
        selectedPrimaryPayloadId = 2;
    }

    private void selectMediumTargetPayload()
    {
        int diceRoll = RandomNumberGenerator.getRandom(100);
        if (diceRoll < 80)
        {
            selectedPrimaryPayloadId = 1;
        }
        else
        {
            selectedPrimaryPayloadId = 2;
        }
    }

    private void selectHeavyTargetPayload()
    {
        selectedPrimaryPayloadId = 6;
    }

    private void selectGroundAttackPayload()
    {
        selectedPrimaryPayloadId = 9;
    }
}
