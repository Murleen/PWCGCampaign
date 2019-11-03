package pwcg.aar.inmission.phase1.parse;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pwcg.core.exception.PWCGException;
import pwcg.core.utils.Logger;

public class AARLogReader 
{
    private AARMissionLogFileSet aarLogFileMissionFile;
    private AARLogFileSetFactory aarLogFileSetFactory = new AARLogFileSetFactory();
    private List<String> logLinesFromMission = new ArrayList<>();

    public AARLogReader(AARMissionLogFileSet aarLogFileMissionFile)
    {
        this.aarLogFileMissionFile = aarLogFileMissionFile;
    }

    public List<String> readLogFilesForMission() throws PWCGException 
    {
        try
        {
            String selectedFileSet = aarLogFileMissionFile.getLogFileName();
                        
            aarLogFileSetFactory.determineMissionResultsFileForRequestedFileSet(selectedFileSet);
            List<String> aarLogFilesForThisSet = aarLogFileSetFactory.getLogFileSets();
            if (aarLogFilesForThisSet.size() == 0)
            {
                throw new PWCGException("No files found for log set " + selectedFileSet);
            }
            
            for (String filename : aarLogFilesForThisSet) 
            {
                readLogFile(filename);
            }
        }
        catch (IOException e)
        {
            Logger.logException(e);
            throw new PWCGException(e.getMessage());
        }
        
        return AARLogKeeper.selectLogLinesToKeep(logLinesFromMission);        
    }

    private void readLogFile(String filename) throws FileNotFoundException, IOException, PWCGException
    {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;

        while ((line = reader.readLine()) != null) 
        {
            logLinesFromMission.add(line);
        }

        reader.close();
    }
}

