package pwcg.aar.prelim;

import java.util.List;

import pwcg.campaign.context.PWCGDirectoryManager;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.DirectoryReader;

public class AARLogSetFinder
{
    private DirectoryReader directoryReader;

    public AARLogSetFinder(DirectoryReader directoryReader)
    {
        this.directoryReader = directoryReader;
    }
    
    public List<String> getSortedLogFileSets() throws PWCGException
    {
        String simulatorDataDir = PWCGDirectoryManager.getInstance().getSimulatorDataDir();
        directoryReader.sortilesInDir(simulatorDataDir);
        List<String> sortedLogSets = directoryReader.getSortedFilesWithFilter("[0].txt");

        return sortedLogSets;
    }
}
