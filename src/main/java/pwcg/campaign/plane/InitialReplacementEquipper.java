package pwcg.campaign.plane;

import java.util.List;

import pwcg.campaign.ArmedService;
import pwcg.campaign.Campaign;
import pwcg.campaign.context.PWCGContextManager;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;

public class InitialReplacementEquipper
{
    
    private Campaign campaign;
    private ArmedService service;
    private Equipment equipment = new Equipment();
    private PlaneEquipmentFactory equipmentFactory;

    public InitialReplacementEquipper(Campaign campaign, ArmedService service) 
    {
        this.campaign = campaign;
        this.service = service;
        this.equipmentFactory = new PlaneEquipmentFactory(campaign);
    }

    public Equipment createReplacementPoolForService() throws PWCGException
    {
        List<Squadron> activeSquadronsForService = PWCGContextManager.getInstance().getSquadronManager().getFlyableSquadronsByService(service, campaign.getDate());
        for (Squadron squadron : activeSquadronsForService)
        {
            EquipmentWeightCalculator equipmentWeightCalculator = createPlaneCalculator(squadron);            
            makeReplacementPlanesForSquadron(equipmentWeightCalculator);
        }
        return equipment;
    }

    private EquipmentWeightCalculator createPlaneCalculator(Squadron squadron) throws PWCGException
    {
        List<PlaneType> planeTypesForSquadron = squadron.determineCurrentAircraftList(campaign.getDate());
        EquipmentWeightCalculator equipmentWeightCalculator = new EquipmentWeightCalculator(campaign);
        equipmentWeightCalculator.determinePlaneWeightsForPlanes(planeTypesForSquadron);
        return equipmentWeightCalculator;
    }

    private void makeReplacementPlanesForSquadron(EquipmentWeightCalculator equipmentWeightCalculator) throws PWCGException
    {
        for (int i = 0; i < Squadron.REPLACEMENTS_AIRCRAFT_PER_SQUADRON; ++i)
        {
            String planeTypeName = equipmentWeightCalculator.getPlaneTypeFromWeight();
            EquippedPlane equippedPlane = equipmentFactory.getPlaneByPlaneType(planeTypeName);
            equipment.addEquippedPlane(equippedPlane);
        }
    }
}
