package pwcg.aar.inmission.phase1.parse;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import pwcg.aar.prelim.AARHeaderParser;
import pwcg.aar.prelim.PwcgMissionData;
import pwcg.campaign.Campaign;
import pwcg.campaign.CampaignData;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.context.PWCGProduct;
import pwcg.core.exception.PWCGException;
import pwcg.mission.data.MissionHeader;

@RunWith(MockitoJUnitRunner.class)
public class AARMissionFileLogResultMatcherTest
{
    @Mock private Campaign campaign;
    @Mock private CampaignData campaignData;
    @Mock private PwcgMissionData pwcgMissionData;       
    @Mock private MissionHeader missionHeader;
    @Mock private AARHeaderParser aarHeaderParser;        
    
    @InjectMocks
    AARMissionFileLogResultMatcher missionFileLogResultMatcher;
    
    @Before
    public void setup() throws PWCGException
    {
        PWCGContext.setProduct(PWCGProduct.BOS);
        Mockito.when(campaign.getCampaignData()).thenReturn(campaignData);
        Mockito.when(campaignData.getName()).thenReturn("Patrik Schorner");
    }
    
    @Test
    public void testMissionResultFileMatch() throws PWCGException
    {        
        Mockito.when(aarHeaderParser.parseHeaderOnly(ArgumentMatchers.<String>any(), ArgumentMatchers.<String>any())).thenReturn("Patrik Schorner 1941-10-02.mission");
        Mockito.when(pwcgMissionData.getMissionHeader()).thenReturn(missionHeader);
        Mockito.when(missionHeader.getMissionFileName()).thenReturn("Patrik Schorner 1941-10-02");
        
        List<String>logFileSets = new ArrayList<>();
        logFileSets.add("missionReport(2018-05-05_18-20-11)[0].txt");
        
        AARMissionLogFileSet aarLogFileMissionFile = missionFileLogResultMatcher.matchMissionFileAndLogFile(pwcgMissionData, logFileSets);
        assert (aarLogFileMissionFile.getLogFileName().equals("missionReport(2018-05-05_18-20-11)[0].txt"));
    }
    
    @Test 
    public void testMissionResultFileMatchFail() throws PWCGException
    {
        Mockito.when(aarHeaderParser.parseHeaderOnly(ArgumentMatchers.<String>any(), ArgumentMatchers.<String>any())).thenReturn("Patrik Schorner 1941-10-02.mission");
        Mockito.when(pwcgMissionData.getMissionHeader()).thenReturn(missionHeader);
        Mockito.when(missionHeader.getMissionFileName()).thenReturn("Wrong File");
        
        List<String>logFileSets = new ArrayList<>();
        logFileSets.add("missionReport(2018-05-05_18-20-11)[0].txt");
        
        AARMissionLogFileSet aarLogFileMissionFile = missionFileLogResultMatcher.matchMissionFileAndLogFile(pwcgMissionData, logFileSets);
        assert (aarLogFileMissionFile == null);
    }


	@Test 
	public void testMissionResultFileMatchFailEmptyList() throws PWCGException
	{
        
        List<String>logFileSets = new ArrayList<>();
        
        AARMissionLogFileSet aarLogFileMissionFile = missionFileLogResultMatcher.matchMissionFileAndLogFile(pwcgMissionData, logFileSets);
        assert (aarLogFileMissionFile == null);
	}
}
