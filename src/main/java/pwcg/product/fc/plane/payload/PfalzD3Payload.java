package pwcg.product.fc.plane.payload;

import pwcg.campaign.plane.PlaneType;
import pwcg.campaign.plane.payload.IPlanePayload;
import pwcg.campaign.plane.payload.PayloadElement;
import pwcg.campaign.plane.payload.PlanePayload;
import pwcg.mission.flight.Flight;

public class PfalzD3Payload extends PlanePayload implements IPlanePayload
{
    public PfalzD3Payload(PlaneType planeType)
    {
        super(planeType);
    }
    
    protected void initialize()
    {
        setAvailablePayload(-3, "10000000", PayloadElement.AMMO_COUNTER);
        setAvailablePayload(-3, "1000000", PayloadElement.ATTITUDE_GUAGE);
        setAvailablePayload(-2, "100000", PayloadElement.ALTITUDE_GUAGE);
        setAvailablePayload(-1, "1000", PayloadElement.IRON_SIGHT);
        setAvailablePayload(0, "1", PayloadElement.STANDARD);
    }

    @Override
    public IPlanePayload copy()
    {
        PfalzD3Payload clone = new PfalzD3Payload(planeType);
        return super.copy(clone);
    }
    
    public int createWeaponsPayload(Flight flight)
    {
        selectedPrimaryPayloadId = 0;
        return selectedPrimaryPayloadId;
    }
}
