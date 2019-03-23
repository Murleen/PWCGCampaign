package pwcg.mission.io;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import pwcg.campaign.Campaign;
import pwcg.campaign.context.PWCGContextManager;
import pwcg.core.exception.PWCGException;
import pwcg.core.exception.PWCGIOException;
import pwcg.core.utils.Logger;
import pwcg.mission.IMissionDescription;
import pwcg.mission.MissionStringHandler;

public class MissionDescriptionFile 
{
	public void writeMissionDescription(IMissionDescription missionDescription, Campaign campaign) throws PWCGException 
	{
		try
        {
            String filename = MissionFileWriter.getMissionFileName(campaign);
            String filePath = getMissionFilePath(campaign, filename) + ".eng";
            OutputStream ostream = new FileOutputStream(filePath);
            OutputStreamWriter writer = new OutputStreamWriter(ostream, "UTF-16LE");
            
            writer.write("\uFEFF");
            writer.write("0:" + missionDescription.getTitle() + "\r\n");

            writer.write("1:" + missionDescription.getHtml() + "\r\n");

            writer.write("2:" + missionDescription.getAuthor() + "\r\n");
            
            Map <Integer, String> subtitles = MissionStringHandler.getInstance().getMissionText();
            for (int key : subtitles.keySet())
            {
            	String text = subtitles.get(key);
            	writer.write(key + ":" + text + "\r\n");
            }
            
            
            writer.flush();
            writer.close();
        }
        catch (PWCGException e)
        {
            Logger.logException(e);
            throw new PWCGIOException(e.getMessage());
        }
        catch (FileNotFoundException e)
        {
            Logger.logException(e);
            throw new PWCGIOException(e.getMessage());
        }
        catch (UnsupportedEncodingException e)
        {
            Logger.logException(e);
            throw new PWCGIOException(e.getMessage());
        }
        catch (IOException e)
        {
            Logger.logException(e);
            throw new PWCGIOException(e.getMessage());
        }
	}
	

    protected String getMissionFilePath(Campaign campaign, String fileName) throws PWCGException 
    {
        String filepath = "..\\Data\\Missions\\" + fileName;
        if (campaign.getCampaignData().isCoop())
        {
            filepath = PWCGContextManager.getInstance().getDirectoryManager().getSimulatorDataDir() + "Multiplayer\\Cooperative\\" + fileName;

        }
        
        return filepath;
    }
}
