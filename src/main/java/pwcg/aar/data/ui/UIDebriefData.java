package pwcg.aar.data.ui;

import pwcg.aar.ui.display.model.AARAceLeavePanelData;
import pwcg.aar.ui.display.model.AAREquipmentLossPanelData;
import pwcg.aar.ui.display.model.AARMedalPanelData;
import pwcg.aar.ui.display.model.AARNewsPanelData;
import pwcg.aar.ui.display.model.AAROutOfMissionVictoryPanelData;
import pwcg.aar.ui.display.model.AARPilotLossPanelData;
import pwcg.aar.ui.display.model.AARPromotionPanelData;
import pwcg.aar.ui.display.model.TransferPanelData;
import pwcg.campaign.Campaign;
import pwcg.core.exception.PWCGException;

public class UIDebriefData
{
    private AARNewsPanelData newsPanelData = new AARNewsPanelData();
    private AARAceLeavePanelData aceLeavePanelData = new AARAceLeavePanelData();
    private AARMedalPanelData medalPanelData = new AARMedalPanelData();
    private AARPilotLossPanelData pilotLossPanelData = new AARPilotLossPanelData();
    private AAREquipmentLossPanelData equipmentLossPanelData = new AAREquipmentLossPanelData();
    private AARPromotionPanelData promotionPanelData = new AARPromotionPanelData();
    private TransferPanelData transferPanelData = new TransferPanelData();
    private AAROutOfMissionVictoryPanelData outOfMissionVictoryPanelData = new AAROutOfMissionVictoryPanelData();

    public AARNewsPanelData getNewsPanelData()
    {
        return newsPanelData;
    }

    public void setNewsPanelData(AARNewsPanelData newsPanelData)
    {
        this.newsPanelData = newsPanelData;
    }
    public AARAceLeavePanelData getAceLeavePanelData()
    {
        return aceLeavePanelData;
    }

    public void addAceLeavePanelData(AARAceLeavePanelData aceLeavePanelData)
    {
        this.aceLeavePanelData.getAcesOnLeaveDuringElapsedTime().addAll(aceLeavePanelData.getAcesOnLeaveDuringElapsedTime());
    }

    public AARMedalPanelData getMedalPanelData()
    {
        return medalPanelData;
    }

    public void setMedalPanelData(AARMedalPanelData medalPanelData)
    {
        this.medalPanelData = medalPanelData;
    }

    public AARPilotLossPanelData getPilotLossPanelData()
    {
        return pilotLossPanelData;
    }
    
    public AAREquipmentLossPanelData getEquipmentLossPanelData()
    {
        return equipmentLossPanelData;
    }

    public void setEquipmentLossPanelData(AAREquipmentLossPanelData equipmentLossPanelData)
    {
        this.equipmentLossPanelData = equipmentLossPanelData;
    }

    public void setPilotLossPanelData(AARPilotLossPanelData pilotLossPanelData)
    {
        this.pilotLossPanelData = pilotLossPanelData;
    }
    
    public AARPromotionPanelData getPromotionPanelData()
    {
        return promotionPanelData;
    }

    public void setPromotionPanelData(AARPromotionPanelData promotionPanelData)
    {
        this.promotionPanelData = promotionPanelData;
    }

    public TransferPanelData getTransferPanelData()
    {
        return transferPanelData;
    }

    public void setTransferPanelData(TransferPanelData transferPanelData)
    {
        this.transferPanelData = transferPanelData;
    }

    public void setOutOfMissionVictoryPanelData(AAROutOfMissionVictoryPanelData outOfMissionVictoryPanelData)
    {
        this.outOfMissionVictoryPanelData = outOfMissionVictoryPanelData;
    }

    public AAROutOfMissionVictoryPanelData getOutOfMissionVictoryPanelData()
    {
        return outOfMissionVictoryPanelData;
    }

    public void merge(Campaign campaign, UIDebriefData uiDebriefDataForIteration) throws PWCGException
    {
        newsPanelData.merge(uiDebriefDataForIteration.getNewsPanelData());
        medalPanelData.merge(uiDebriefDataForIteration.getMedalPanelData());
        pilotLossPanelData.merge(uiDebriefDataForIteration.getPilotLossPanelData());
        equipmentLossPanelData.merge(uiDebriefDataForIteration.getEquipmentLossPanelData());
        promotionPanelData.merge(uiDebriefDataForIteration.getPromotionPanelData());
        transferPanelData.merge(uiDebriefDataForIteration.getTransferPanelData());
        outOfMissionVictoryPanelData.merge(uiDebriefDataForIteration.getOutOfMissionVictoryPanelData());
    }
}
