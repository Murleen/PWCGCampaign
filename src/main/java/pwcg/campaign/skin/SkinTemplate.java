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
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;

import pwcg.campaign.Campaign;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.factory.RankFactory;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;
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
        public String fillShine;
        public String strokeShine;
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

        public SkinTemplateInstance(Campaign campaign, PlaneMcu plane, Map<String, Object> overrides) throws PWCGException
        {
            Squadron squadron = PWCGContext.getInstance().getSquadronManager().getSquadron(plane.getSquadronId());
            values = new Object[parameters.length];

            for (int i = 0; i < parameters.length; i++)
            {
                String param = parameters[i];
                Object value = null;

                if (overrides != null && overrides.containsKey(param))
                    value = overrides.get(param);
                else if (param.equals("UNIT_ID_CODE"))
                    value = squadron.determineUnitIdCode(campaign.getDate());
                else if (param.equals("SUB_UNIT_ID_CODE"))
                    value = squadron.determineSubUnitIdCode(campaign.getDate());
                else if (param.equals("AIRCRAFT_ID_CODE"))
                    value = plane.getAircraftIdCode();
                else if (param.equals("WINTER")) {
                    Season season = PWCGContext.getInstance().getCurrentMap().getMapWeather().getSeason(campaign.getDate());
                    value = (season == Season.WINTER) ? 1 : 0;
                }
                else if (param.equals("PILOT_RANK"))
                    value = RankFactory.createRankHelper().getRankPosByService(plane.getPilot().getRank(), plane.getPilot().determineService(campaign.getDate()));
                else if (param.equals("PILOT_NAME_RANK"))
                    value = plane.getPilot().getNameAndRank();
                else if (param.equals("PILOT_NAME_RANK_UC"))
                    value = plane.getPilot().getNameAndRank().toUpperCase();

                values[i] = value;
            }

        }

        public void generate() throws Exception {
            ImageCache imageCache = ImageCache.getInstance();
            FontCache fontCache = FontCache.getInstance();
            String skinTemplatesDir = PWCGContext.getInstance().getDirectoryManager().getPwcgSkinTemplatesDir();
            String imagesDir = PWCGContext.getInstance().getDirectoryManager().getPwcgImagesDir();
            String fontsDir = PWCGContext.getInstance().getDirectoryManager().getPwcgFontsDir();

            BufferedImage baseImage = imageCache.getBufferedImage(skinTemplatesDir + MessageFormat.format(baseImagePath, values));
            baseImage = new BufferedImage(baseImage.getColorModel(), baseImage.copyData(baseImage.getRaster().createCompatibleWritableRaster()), false, null);

            // Alpha channel of the base image contains the shininess map, which should
            // be unchanged. Create a new image backed by the same data arrays, but only
            // using the color components.
            int[] bandList = {0, 1, 2};
            WritableRaster colorOnlyRaster = baseImage.getRaster().createWritableChild(
                                                 0, 0,
                                                 baseImage.getWidth(), baseImage.getHeight(),
                                                 0, 0,
                                                 bandList);
            WritableRaster alphaRaster = baseImage.getAlphaRaster();

            ColorModel colorModel = new ComponentColorModel(baseImage.getColorModel().getColorSpace(), false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
            BufferedImage skin = new BufferedImage(colorModel, colorOnlyRaster, false, null);
            // Create our own 1:1 lookup table for the shininess map, as otherwise Java does weird gamma correction
            byte[] lut = new byte[256];
            for (int i = 0; i < 256; i++)
                lut[i] = (byte) i;
            ColorModel shineColorModel = new IndexColorModel(8, 256, lut, lut, lut);
            BufferedImage shineMap = new BufferedImage(shineColorModel, alphaRaster, false, null);

            Graphics2D graphics = skin.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            Graphics2D shineGraphics = shineMap.createGraphics();
            shineGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            shineGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            AffineTransform origTransform = graphics.getTransform();

            for (ElementDef def : defs) {
                Rectangle2D bounds;

                if (def.text != null)
                {
                    String text = MessageFormat.format(def.text, values);
                    if (text.equals(""))
                        continue;

                    Font font = fontCache.getFont(fontsDir + def.font, def.size);

                    // Java seems to get weird values for a font's ascent/descent metrics, so measure the letter "M" instead
                    Rectangle2D mBounds = font.createGlyphVector(graphics.getFontRenderContext(), "M").getVisualBounds();

                    GlyphVector glyphVector = font.layoutGlyphVector(graphics.getFontRenderContext(), text.toCharArray(), 0, text.length(), Font.LAYOUT_LEFT_TO_RIGHT);
                    bounds = glyphVector.getVisualBounds();
                    Shape outline = glyphVector.getOutline();
                    bounds = new Rectangle2D.Double(bounds.getX(), mBounds.getY(), bounds.getWidth(), mBounds.getHeight());

                    setTransform(graphics, def, bounds, 1.0);
                    shineGraphics.setTransform(graphics.getTransform());

                    Color fillColor = Color.decode("0x" + MessageFormat.format(def.fillColor, values));
                    graphics.setColor(fillColor);
                    graphics.fill(outline);

                    if (def.fillShine != null) {
                        Color fillShine = Color.decode("0x" + MessageFormat.format(def.fillShine + def.fillShine + def.fillShine, values));
                        shineGraphics.setColor(fillShine);
                        shineGraphics.fill(outline);
                    }

                    if (def.strokeWidth > 0) {
                        Color strokeColor = Color.decode("0x" + MessageFormat.format(def.strokeColor, values));
                        graphics.setColor(strokeColor);
                        graphics.setStroke(new BasicStroke(def.strokeWidth));
                        graphics.draw(outline);

                        if (def.strokeShine != null) {
                            Color strokeShine = Color.decode("0x" + MessageFormat.format(def.strokeShine + def.strokeShine + def.strokeShine, values));
                            shineGraphics.setColor(strokeShine);
                            shineGraphics.setStroke(new BasicStroke(def.strokeWidth));
                            shineGraphics.draw(outline);
                        }
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

            BufferedImage weatherImage = imageCache.getBufferedImage(skinTemplatesDir + MessageFormat.format(weatherImagePath, values));
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

    public SkinTemplateInstance instantiate(Campaign campaign, PlaneMcu plane, Map<String, Object> overrides) throws PWCGException
    {
        return new SkinTemplateInstance(campaign, plane, overrides);
    }

}
