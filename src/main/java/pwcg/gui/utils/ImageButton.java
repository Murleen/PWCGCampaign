package pwcg.gui.utils;

import java.awt.Font;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

import pwcg.core.exception.PWCGException;
import pwcg.gui.dialogs.PWCGMonitorFonts;

public class ImageButton 
{

	public static PWCGJButton makeButton(String text, String imageName) throws PWCGException 
	{
        String imagePath = ContextSpecificImages.imagesNational() + imageName;
		ImageIcon icon = new ImageIcon(imagePath);

		PWCGJButton button= new PWCGJButton(text);
		button.setBorderPainted(false);
        button.setFocusPainted(false);
		button.setIcon(icon);
		
		Font font = PWCGMonitorFonts.getPrimaryFontSmall();

		button.setFont(font);
		button.setOpaque(false);

		return button;
	}

	public static PWCGJButton makeButton(String text, Image image) throws PWCGException 
	{
		ImageIcon icon = new ImageIcon(image);

		PWCGJButton button= new PWCGJButton(text);
		button.setIcon(icon);
		
		Font font = PWCGMonitorFonts.getPrimaryFontSmall();

		button.setFont(font);
		button.setOpaque(false);

		return button;
	}

    public static JLabel makePilotPicButton(Image image) throws PWCGException 
    {
        ImageIcon icon = new ImageIcon(image);

        JLabel button= new JLabel("");
        button.setIcon(icon);

        return button;
    }

	public static JCheckBox makeCheckBox(String text, String imageName) throws PWCGException 
	{
		Icon notSelectedIcon = getOwnedIcon(imageName, false);
		Icon selectedIcon = getOwnedIcon(imageName, true);

		JCheckBox checkBox= new JCheckBox(text);
		
		Font font = PWCGMonitorFonts.getPrimaryFontSmall();

		checkBox.setFont(font);
		checkBox.setHorizontalAlignment(JLabel.LEFT);
		checkBox.setOpaque(false);
        checkBox.setSize(300, 50);
        checkBox.setIcon(notSelectedIcon);
        checkBox.setSelectedIcon(selectedIcon);

		return checkBox;
	}

	private static Icon getOwnedIcon(String imageName, boolean owned) 
	{
        String imagePath = ContextSpecificImages.imagesProfiles() + imageName;
		if (!owned)
		{
			imagePath += "No";
		}

		imagePath += ".jpg";
		
		ImageIcon icon = new ImageIcon(imagePath);
		
		return icon;
	}

}
