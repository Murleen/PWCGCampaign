package pwcg.aar.inmission.phase1.parse.event;

import java.io.BufferedWriter;

import pwcg.core.exception.PWCGException;
import pwcg.core.exception.PWCGIOException;
import pwcg.core.location.Coordinate;
import pwcg.core.utils.PWCGLogger;

public abstract class ATypeBase 
{	
    private static int sequenceNumCounter = 0;
	private int sequenceNum = 0;
	private AType atype;
	
	public ATypeBase (AType atype)
	{
	    this.atype = atype;
	    this.sequenceNum = sequenceNumCounter;
	    ++sequenceNumCounter;
	}

	protected Coordinate findCoordinate(String line, String startTag) throws PWCGException 
	{
		try
        {
            // Get the location
            int startLocXPos = line.indexOf(startTag) + startTag.length();
            String endLocXTag = ",";
            int endLocXPos = line.indexOf(endLocXTag);
            String locXStr = line.substring(startLocXPos, endLocXPos);
            
            int startLocYPos = endLocXPos + 1;
            int endLocYPos = line.indexOf(",", startLocYPos);
            String locYStr = line.substring(startLocYPos, endLocYPos);
            
            int startLocZPos = endLocYPos + 1;
            int endLocZPos = line.indexOf(")", startLocZPos);
            String locZStr = line.substring(startLocZPos, endLocZPos);
            
            Coordinate coords = new Coordinate();
            coords.setXPos(Double.valueOf(locXStr));
            coords.setYPos(Double.valueOf(locYStr));
            coords.setZPos(Double.valueOf(locZStr));
            
            return coords;
        }
        catch (NumberFormatException e)
        {
            PWCGLogger.logException(e);
            throw new PWCGException(e.getMessage());
        }

	}

	protected String getString(String line, String startTag, String endTag) 
	{
	    String lineLower = line.toLowerCase();
        startTag = startTag.toLowerCase();
		int startPos = lineLower.indexOf(startTag) + startTag.length();
		String value = "";
		if(endTag != null)
		{
	        endTag = endTag.toLowerCase();
			int endPos = lineLower.indexOf(endTag);
			value = line.substring(startPos, endPos);
		}
		else
		{
			value = line.substring(startPos);
		}
		return value;
	}

	protected int getInteger(String line, String startTag, String endTag) 
	{
		int startPos = line.indexOf(startTag) + startTag.length();
		int endPos = line.indexOf(endTag);
		int value = Integer.valueOf(line.substring(startPos, endPos));
		
		return value;
	}

	protected double getDouble(String line, String startTag, String endTag) 
	{
		int startDamagePos = line.indexOf(startTag) + startTag.length();
		int endDamagePos = line.indexOf(endTag);
		double value = Double.valueOf(line.substring(startDamagePos, endDamagePos));
		
		return value;
	}

	public int getSequenceNum() 
	{
		return sequenceNum;
	}
	
    public AType getAtype()
    {
        return atype;
    }

    public abstract void write(BufferedWriter writer) throws PWCGIOException;
}
