package pwcg.mission.mcu;

import java.io.BufferedWriter;
import java.io.IOException;

import pwcg.campaign.utils.LCIndexGenerator;
import pwcg.core.exception.PWCGIOException;
import pwcg.core.utils.Logger;

public class McuSubtitle extends BaseFlightMcu
{
	private int lcText = LCIndexGenerator.getInstance().getNextIndex();
    private String text;
    private int duration = 3;

	public McuSubtitle ()
	{
 		super();
	}

	public int getLcText() 
	{
		return lcText;
	}

	public String getText() 
	{
		return text;
	}

	public void setText(String text) 
	{
		this.text = text;
	}

	public int getDuration()
    {
        return this.duration;
    }

    public void setDuration(int duration)
    {
        this.duration = duration;
    }

    public void write(BufferedWriter writer) throws PWCGIOException
	{
		try
        {
            writer.write("MCU_TR_Subtitle");
            writer.newLine();
            writer.write("{");
            writer.newLine();
            
            super.write(writer);
            
            
            writer.write("  Enabled = 1;");
            writer.newLine();
            
            writer.write("  SubtitleInfo");
            writer.newLine();
            
            writer.write("  {");
            writer.newLine();
            
            writer.write("    Duration = " + duration + ";");
            writer.newLine();
            
            writer.write("    FontSize = 20;");
            writer.newLine();
            
            writer.write("    HAlign = 1;");
            writer.newLine();
            
            writer.write("    VAlign = 2;");
            writer.newLine();
            
            writer.write("    RColor = 255;");
            writer.newLine();
            
            writer.write("    GColor = 255;");
            writer.newLine();
            
            writer.write("    BColor = 255;");
            writer.newLine();
            
            writer.write("    LCText = " + lcText + ";");
            writer.newLine();
            
            writer.write("  }");
            writer.newLine();
            
            
            writer.write("  Coalitions = [0, 1, 2, 3, 4, 5, 6, 7];");
            writer.newLine();
            		  
            writer.write("}");
            writer.newLine();
            writer.newLine();
            writer.newLine();
        }
        catch (IOException e)
        {
            Logger.logException(e);
            throw new PWCGIOException(e.getMessage());
        }
	}	
}
