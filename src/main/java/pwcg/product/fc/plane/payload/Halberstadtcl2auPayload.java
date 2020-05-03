package pwcg.product.fc.plane.payload;

import pwcg.campaign.plane.PlaneType;
import pwcg.campaign.plane.payload.IPlanePayload;
import pwcg.campaign.plane.payload.PayloadElement;
import pwcg.campaign.plane.payload.PlanePayload;
import pwcg.mission.flight.FlightTypes;
import pwcg.mission.flight.IFlight;

public class Halberstadtcl2auPayload extends PlanePayload implements IPlanePayload
{
    public Halberstadtcl2auPayload(PlaneType planeType)
    {
        super(planeType);
    }

    protected void initialize()
    {
        setAvailablePayload(-3, "100000", PayloadElement.ATTITUDE_GUAGE);
        setAvailablePayload(-2, "100", PayloadElement.TWIN_GUN_TURRET);
        setAvailablePayload(-1, "10", PayloadElement.TWIN_SPANDAU);
        setAvailablePayload(0, "1", PayloadElement.STANDARD);
        setAvailablePayload(1, "10000001", PayloadElement.BOMBS);
        setAvailablePayload(2, "100000001", PayloadElement.CAMERA);
        setAvailablePayload(3, "1000000001", PayloadElement.RADIO);
    }

    @Override
    public IPlanePayload copy()
    {
        Halberstadtcl2auPayload clone = new Halberstadtcl2auPayload(planeType);
        return super.copy(clone);
    }

    public int createWeaponsPayload(IFlight flight)
    {
        selectedPrimaryPayloadId = 0;
        if (FlightTypes.isBombingFlight(flight.getFlightType()))
        {
            selectBombingPayload(flight);
        }
        else if (flight.getFlightType() == FlightTypes.RECON)
        {
            selectReconPayload(flight);
        }
        else if (flight.getFlightType() == FlightTypes.ARTILLERY_SPOT)
        {
            selectArtillerySpotPayload(flight);
        }
        return selectedPrimaryPayloadId;
    }

    protected void selectBombingPayload(IFlight flight)
    {
        selectedPrimaryPayloadId = 1;
    }

    protected void selectReconPayload(IFlight flight)
    {
        selectedPrimaryPayloadId = 2;
    }

    protected void selectArtillerySpotPayload(IFlight flight)
    {
        selectedPrimaryPayloadId = 3;
    }    
}
