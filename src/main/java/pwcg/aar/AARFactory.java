package pwcg.aar;

import pwcg.aar.inmission.phase1.parse.AARLogEvaluationCoordinator;
import pwcg.aar.inmission.phase1.parse.AARMissionFileLogResultMatcher;
import pwcg.aar.prelim.AARHeaderParser;
import pwcg.aar.prelim.AARLogSetFinder;
import pwcg.aar.prelim.AARMostRecentLogSetFinder;
import pwcg.aar.prelim.AARPwcgMissionFinder;
import pwcg.campaign.Campaign;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.DirectoryReader;

public class AARFactory
{
    public static AARLogEvaluationCoordinator makePhase1Coordinator()
    {
        AARLogEvaluationCoordinator phase1Coordinator = new AARLogEvaluationCoordinator();
        return phase1Coordinator;
    }
    
    public static AARMostRecentLogSetFinder makeMostRecentLogSetFinder(Campaign campaign) throws PWCGException
    {
        AARLogSetFinder logSetFinder = makeLogSorter();
        AARHeaderParser aarHeaderParser = new AARHeaderParser();        
        AARPwcgMissionFinder pwcgMissionFinder = new AARPwcgMissionFinder(campaign);
        AARMissionFileLogResultMatcher matcher = new AARMissionFileLogResultMatcher(campaign, aarHeaderParser);
        return new AARMostRecentLogSetFinder(campaign, matcher, logSetFinder, pwcgMissionFinder);
    }
    
    public static AARLogSetFinder makeLogSorter() throws PWCGException
    {
        DirectoryReader directoryReader = new DirectoryReader();
        AARLogSetFinder logSetFinder = new AARLogSetFinder(directoryReader);
        return logSetFinder;
    }

    
    
}
