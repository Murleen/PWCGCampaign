package pwcg.aar.prelim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.context.PWCGDirectorySimulatorManager;
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
        List<String> sortedLogSetsFromData = getLogFilesFromData();
        List<String> sortedLogSetsFromUserDefined = getLogFilesFromUserDefined();

        List<String> sortedLogSets = new ArrayList<>();
        sortedLogSets.addAll(sortedLogSetsFromData);
        sortedLogSets.addAll(sortedLogSetsFromUserDefined);
        Collections.sort(sortedLogSets);
        Collections.reverse(sortedLogSets);
        return sortedLogSets;
    }

    private List<String> getLogFilesFromData() throws PWCGException
    {
        String simulatorDataDir = PWCGDirectorySimulatorManager.getInstance().getSimulatorDataDir();
        directoryReader.sortilesInDir(simulatorDataDir);
        List<String> sortedLogSetsFromData = directoryReader.getSortedFilesWithFilter("[0].txt");
        return sortedLogSetsFromData;
    }

    private List<String> getLogFilesFromUserDefined() throws PWCGException
    {
        List<String> sortedLogSetsFromUserDefined = new ArrayList<>();

        String userLogDir = PWCGContext.getInstance().getMissionLogDirectory();
        if (!userLogDir.isEmpty())
        {
            directoryReader.sortilesInDir(userLogDir);
            sortedLogSetsFromUserDefined = directoryReader.getSortedFilesWithFilter("[0].txt");
        }
        return sortedLogSetsFromUserDefined;
    }
}
