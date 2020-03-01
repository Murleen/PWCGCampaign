package pwcg.campaign;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pwcg.campaign.plane.Equipment;
import pwcg.campaign.plane.EquippedPlane;
import pwcg.campaign.plane.PlaneStatus;
import pwcg.campaign.resupply.depot.EquipmentDepot;
import pwcg.core.exception.PWCGException;

public class CampaignEquipmentManager
{
    private Map<Integer, Equipment> equipmentAllSquadrons = new HashMap<>();
    private Map<Integer, EquipmentDepot> equipmentDepotsForServices = new HashMap<>();

    public Equipment getEquipmentForSquadron(Integer squadronId)
    {
        return equipmentAllSquadrons.get(squadronId);
    }

    public EquipmentDepot getEquipmentDepotForService(Integer serviceId)
    {
        return equipmentDepotsForServices.get(serviceId);
    }

    public void addEquipmentForSquadron(Integer squadronId, Equipment equipmentForSquadron)
    {
        equipmentAllSquadrons.put(squadronId, equipmentForSquadron);
    }

    public void addEquipmentDepotForService(Integer serviceId, EquipmentDepot replacementEquipmentForService)
    {
        equipmentDepotsForServices.put(serviceId, replacementEquipmentForService);
    }

    public Map<Integer, Equipment> getEquipmentAllSquadrons()
    {
        return equipmentAllSquadrons;
    }

    public EquipmentDepot getEquipmentDepot(Integer serviceId)
    {
        return equipmentDepotsForServices.get(serviceId);
    }
    
    public List<Integer> getServiceIdsForDepots()
    {
        return new ArrayList<Integer>(equipmentDepotsForServices.keySet());
    }

    public EquippedPlane getPlaneFromAnySquadron(Integer serialNumber) throws PWCGException
    {
        for (Equipment equipment : equipmentAllSquadrons.values())
        {
            EquippedPlane equippedPlane = equipment.getEquippedPlane(serialNumber);
            if (equippedPlane != null)
            {
                return equippedPlane;
            }        
        }
         throw new PWCGException ("Unable to locate equipped plane for serial number " + serialNumber);
    }

    public EquippedPlane getAnyActivePlaneFromSquadron(Integer squadronId) throws PWCGException
    {
        Equipment equipment = equipmentAllSquadrons.get(squadronId);
        for (EquippedPlane equippedPlane : equipment.getActiveEquippedPlanes().values())
        {
            return equippedPlane;
        }

        throw new PWCGException ("Unable to locate active equipped plane for squadron " + squadronId);
    }

    public EquippedPlane destroyPlaneFromSquadron(int squadronId, Date date) throws PWCGException
    {
        EquippedPlane destroyedPlane = getAnyActivePlaneFromSquadron(squadronId);
        destroyedPlane.setPlaneStatus(PlaneStatus.STATUS_DESTROYED);
        destroyedPlane.setDateRemovedFromService(date);
        return destroyedPlane;
    }

    public EquippedPlane destroyPlane(int serialNumber, Date date) throws PWCGException
    {
        EquippedPlane destroyedPlane = getPlaneFromAnySquadron(serialNumber);
        destroyedPlane.setPlaneStatus(PlaneStatus.STATUS_DESTROYED);
        destroyedPlane.setDateRemovedFromService(date);
        return destroyedPlane;
    }
}
