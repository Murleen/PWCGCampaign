package pwcg.aar.data;

import java.util.Date;

import pwcg.aar.inmission.phase1.parse.AARMissionLogRawData;
import pwcg.aar.inmission.phase2.logeval.AARMissionEvaluationData;
import pwcg.aar.inmission.phase3.reconcile.ReconciledInMissionData;
import pwcg.aar.outofmission.phase1.elapsedtime.ReconciledOutOfMissionData;
import pwcg.aar.prelim.AARPreliminaryData;
import pwcg.aar.prelim.CampaignMembersOutOfMissionFinder;
import pwcg.campaign.Campaign;
import pwcg.campaign.squadmember.SquadronMembers;
import pwcg.core.exception.PWCGException;


public class AARContext
{
    private Campaign campaign;
    
    private AARPreliminaryData preliminaryData = new AARPreliminaryData();
    private Date newDate;
    private ExtendedTimeReason reasonForExtendedTime = ExtendedTimeReason.NO_EXTENDED_TIME;

    // In mission
    private AARMissionLogRawData missionLogRawData = new AARMissionLogRawData();
    private AARMissionEvaluationData missionEvaluationData = new AARMissionEvaluationData();
    private ReconciledInMissionData reconciledInMissionData = new ReconciledInMissionData();

    // Out of mission
    private ReconciledOutOfMissionData reconciledOutOfMissionData = new ReconciledOutOfMissionData();

    // Cumulative
    private ReconciledOutOfMissionData cumulativeMissionData = new ReconciledOutOfMissionData();

    // Tabulated
    private AARTabulatedData aarTabulatedData;
    
    public AARContext(Campaign campaign)
    {
        this.campaign = campaign;
        this.aarTabulatedData = new AARTabulatedData(campaign);
    }

    public void resetContextForNextTimeIncrement() throws PWCGException
    {
        cumulativeMissionData.merge(reconciledOutOfMissionData);

        preliminaryData = new AARPreliminaryData();
        CampaignMembersOutOfMissionFinder campaignMembersOutOfMissionHandler = new CampaignMembersOutOfMissionFinder();        
        SquadronMembers campaignMembersOutOfMission = campaignMembersOutOfMissionHandler.getCampaignMembersNotInMission(campaign, preliminaryData.getCampaignMembersInMission());
        preliminaryData.setCampaignMembersOutOfMission(campaignMembersOutOfMission);

        missionLogRawData = new AARMissionLogRawData();
        missionEvaluationData = new AARMissionEvaluationData();
        reconciledInMissionData = new ReconciledInMissionData();
        reconciledOutOfMissionData = new ReconciledOutOfMissionData();
        aarTabulatedData = new AARTabulatedData(campaign);
    }
    
    public AARPreliminaryData getPreliminaryData()
    {
        return preliminaryData;
    }

    public AARMissionLogRawData getMissionLogRawData()
    {
        return missionLogRawData;
    }

    public ReconciledOutOfMissionData getReconciledOutOfMissionData()
    {
        return reconciledOutOfMissionData;
    }

    public ReconciledInMissionData getReconciledInMissionData()
    {
        return reconciledInMissionData;
    }

    public CampaignUpdateData getCampaignUpdateData()
    {
        return aarTabulatedData.getCampaignUpdateData();
    }

    public AARMissionEvaluationData getMissionEvaluationData()
    {
        return missionEvaluationData;
    }

    public void setPreliminaryData(AARPreliminaryData preliminaryData)
    {
        this.preliminaryData = preliminaryData;
    }

    public void setMissionLogRawData(AARMissionLogRawData missionLogRawData)
    {
        this.missionLogRawData = missionLogRawData;
    }

    public void setMissionEvaluationData(AARMissionEvaluationData missionEvaluationData)
    {
        this.missionEvaluationData = missionEvaluationData;
    }

    public void setReconciledInMissionData(ReconciledInMissionData reconciledInMissionData)
    {
        this.reconciledInMissionData = reconciledInMissionData;
    }

    public void setReconciledOutMissionData(ReconciledOutOfMissionData reconciledOutOfMissionData)
    {
        this.reconciledOutOfMissionData = reconciledOutOfMissionData;
    }

    public void setCampaignUpdateData(CampaignUpdateData campaignUpdateData)
    {
        this.aarTabulatedData.setCampaignUpdateData(campaignUpdateData);
    }
    
    public ReconciledOutOfMissionData getCumulativeMissionData()
    {
        return cumulativeMissionData;
    }

    public Date getNewDate()
    {
        return newDate;
    }

    public void setNewDate(Date newDate)
    {
        this.newDate = newDate;
    }

    public ExtendedTimeReason getReasonForExtendedTime()
    {
        return reasonForExtendedTime;
    }

    public void setReasonForExtendedTime(ExtendedTimeReason reasonForExtendedTime)
    {
        this.reasonForExtendedTime = reasonForExtendedTime;
    }

    public AARTabulatedData getAarTabulatedData()
    {
        return aarTabulatedData;
    }

    public void setAarTabulatedData(AARTabulatedData aarTabulatedData)
    {
        this.aarTabulatedData = aarTabulatedData;
    }
}
