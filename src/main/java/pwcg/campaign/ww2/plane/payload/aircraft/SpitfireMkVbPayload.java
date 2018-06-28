package pwcg.campaign.ww2.plane.payload.aircraft;

import pwcg.campaign.plane.PlaneType;
import pwcg.campaign.plane.payload.IPlanePayload;
import pwcg.campaign.plane.payload.PayloadElement;
import pwcg.campaign.plane.payload.PlanePayload;
import pwcg.mission.flight.Flight;

public class SpitfireMkVbPayload extends PlanePayload implements IPlanePayload
{
    public SpitfireMkVbPayload(PlaneType planeType)
    {
        super(planeType);
    }

    protected void initialize()
	{
        setAvailablePayload(-2, "10", PayloadElement.MERLIN_ENGINE);
        setAvailablePayload(-1, "100", PayloadElement.MIRROR);
        setAvailablePayload(0, "1", PayloadElement.STANDARD);
	}

    @Override
    public int createWeaponsPayload(Flight flight)
    {
        selectedPrimaryPayloadId = 0;
        return selectedPrimaryPayloadId;
    }

    @Override
    public IPlanePayload copy()
    {
    	SpitfireMkVbPayload clone = new SpitfireMkVbPayload(planeType);
        
        return super.copy(clone);
    }
}
