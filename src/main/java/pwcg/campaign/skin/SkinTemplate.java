package pwcg.campaign.skin;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;

import javax.imageio.ImageIO;

import pwcg.campaign.Campaign;
import pwcg.campaign.context.PWCGContext;
import pwcg.core.utils.DDSWriter;
import pwcg.gui.dialogs.FontCache;
import pwcg.gui.dialogs.ImageCache;
import pwcg.mission.flight.plane.PlaneMcu;
import pwcg.mission.options.MapSeasonalParameters.Season;

public class SkinTemplate {
    enum HorizAlign {
        LEFT,
        CENTER,
        RIGHT
    }

    enum VertAlign {
        TOP,
        CENTER,
        BOTTOM
    }

    public static class ElementDef
    {
        public String text;
        public String image;
        public HorizAlign horizAlign = HorizAlign.LEFT;
        public VertAlign vertAlign = VertAlign.BOTTOM;
        public String font;
        public int size;
        public int x;
        public int y;
        public int width;
        public int height;
        public float orientation = 0;
        public boolean horizFlip = false;
        public String fillColor;
        public int strokeWidth = 0;
        public String strokeColor;
    }

    private String templateName;
    private String planeType;
    @SuppressWarnings("unused") // credit isn't used in PWCG, but is stored in the templates
    private String credit;
    private String[] parameters;
    private String baseImagePath;
    private String weatherImagePath;
    private ElementDef[] defs;

    public class SkinTemplateInstance
    {
        private Object[] values;

        public SkinTemplateInstance(Campaign campaign, PlaneMcu plane, Map<String, Object> overrides)
        {
            values = new Object[parameters.length];

            for (int i = 0; i < parameters.length; i++)
            {
                String param = parameters[i];
                Object value = null;

                if (overrides != null && overrides.containsKey(param))
                    value = overrides.get(param);
                else if (param.equals("NUM_IN_FLIGHT"))
                    value = plane.getCallnum();
                else if (param.equals("WINTER")) {
                    Season season = PWCGContext.getInstance().getCurrentMap().getMapWeather().getSeason(campaign.getDate());
                    value = (season == Season.WINTER) ? 1 : 0;
                }

                values[i] = value;
            }

        }

        public void generate() throws Exception {
            ImageCache imageCache = ImageCache.getInstance();
            FontCache fontCache = FontCache.getInstance();
            String skinTemplatesDir = PWCGContext.getInstance().getDirectoryManager().getPwcgSkinTemplatesDir();
            String imagesDir = PWCGContext.getInstance().getDirectoryManager().getPwcgImagesDir();
            String fontsDir = PWCGContext.getInstance().getDirectoryManager().getPwcgFontsDir();

            BufferedImage baseImage = ImageIO.read(new File(skinTemplatesDir + MessageFormat.format(baseImagePath, values)));

            // Alpha channel of the base image contains the shininess map, which should
            // be unchanged. Create a new image backed by the same data arrays, but only
            // using the color components.
            int[] bandList = {0, 1, 2};
            WritableRaster colorOnlyRaster = baseImage.getRaster().createWritableChild(
                                                 0, 0,
                                                 baseImage.getWidth(), baseImage.getHeight(),
                                                 0, 0,
                                                 bandList);

            ColorModel colorModel = new ComponentColorModel(baseImage.getColorModel().getColorSpace(), false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
            BufferedImage skin = new BufferedImage(colorModel, colorOnlyRaster, false, null);

            Graphics2D graphics = skin.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            AffineTransform origTransform = graphics.getTransform();

            for (ElementDef def : defs) {
                Rectangle2D bounds;

                if (def.text != null)
                {
                    String text = MessageFormat.format(def.text, values);
                    if (text.equals(""))
                        continue;

                    Font font = fontCache.getFont(fontsDir + def.font, def.size);

                    GlyphVector glyphVector = font.layoutGlyphVector(graphics.getFontRenderContext(), text.toCharArray(), 0, text.length(), Font.LAYOUT_LEFT_TO_RIGHT);
                    bounds = glyphVector.getVisualBounds();
                    Shape outline = glyphVector.getOutline();

                    setTransform(graphics, def, bounds, 1.0);

                    Color fillColor = Color.decode("0x" + MessageFormat.format(def.fillColor, values));
                    graphics.setColor(fillColor);
                    graphics.fill(outline);

                    if (def.strokeWidth > 0) {
                        Color strokeColor = Color.decode("0x" + MessageFormat.format(def.strokeColor, values));
                        graphics.setColor(strokeColor);
                        graphics.setStroke(new BasicStroke(def.strokeWidth));
                        graphics.draw(outline);
                    }
                } else {
                    String imagePath = MessageFormat.format(def.image, values);
                    if (imagePath == null || imagePath.equals("") || imagePath.equals("null"))
                        continue;
                    BufferedImage image = imageCache.getBufferedImage(imagesDir + imagePath);
                    if (image == null)
                        continue;
                    bounds = new Rectangle(0, -image.getHeight(), image.getWidth(), image.getHeight());
                    double scalingFactor = Math.min((double) def.width / image.getWidth(), (double) def.height / image.getHeight());

                    setTransform(graphics, def, bounds, scalingFactor);

                    graphics.drawImage(image, 0, -image.getHeight(), null);
                }

                graphics.setTransform(origTransform);
            }

            BufferedImage weatherImage = ImageIO.read(new File(skinTemplatesDir + MessageFormat.format(weatherImagePath, values)));
            graphics.drawImage(weatherImage, 0, 0, null);
            weatherImage = null;

            graphics.dispose();

            String fileName = getFilename();
            DDSWriter.writeImage(baseImage, new File(Skin.PRODUCT_SKIN_DIR + "\\" + planeType + "\\" + fileName));
        }

        private void setTransform(Graphics2D graphics, ElementDef def, Rectangle2D bounds, double scalingFactor) {
            int xOffset = 0;
            int yOffset = 0;

            if (def.horizAlign == HorizAlign.RIGHT) {
                xOffset = (int) -(bounds.getWidth() + bounds.getX());
            } else if (def.horizAlign == HorizAlign.CENTER) {
                xOffset = (int) -(bounds.getWidth() / 2 + bounds.getX());
            } else if (def.horizAlign == HorizAlign.LEFT) {
                xOffset = (int) -bounds.getX();
            }

            if (def.vertAlign == VertAlign.TOP) {
                yOffset = (int) -bounds.getY();
            } else if (def.vertAlign == VertAlign.CENTER) {
                yOffset = (int) -(bounds.getHeight() / 2 + bounds.getY());
            } else if (def.vertAlign == VertAlign.BOTTOM) {
                yOffset = (int) -(bounds.getHeight() + bounds.getY());
            }

            graphics.translate(def.x, def.y);
            graphics.rotate(Math.toRadians(def.orientation));
            graphics.scale(scalingFactor, scalingFactor);
            if (def.horizFlip)
                graphics.scale(-1, 1);
            graphics.translate(xOffset, yOffset);
        }

        public String getFilename()
        {
            return "PWCG_" + templateName + "_" + Integer.toHexString(Arrays.deepHashCode(values)) + ".dds";
        }

        public boolean skinExists()
        {
            File file = new File(Skin.PRODUCT_SKIN_DIR + "\\" + planeType + "\\" + getFilename());
            return file.exists();
        }
    }

    public SkinTemplateInstance instantiate(Campaign campaign, PlaneMcu plane, Map<String, Object> overrides)
    {
        return new SkinTemplateInstance(campaign, plane, overrides);
    }

}
