package chuck.terran.admin.tools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import jo.util.utils.DebugUtils;
import jo.util.utils.FormatUtils;
import jo.util.utils.io.FileUtils;

public class UpdateVersions
{
    public static void main(String[] argv)
    {
        try
        {
            File root = new File(".");
            File manifestMF = new File(root, "META-INF/MANIFEST.MF");
            File productXML = new File(root, "chuck.terran.admin.product");
            String version = FormatUtils.formatDateVersion(System.currentTimeMillis());
            DebugUtils.info(version);
            String txt = FileUtils.readFileAsString(manifestMF.toString());
            int start = txt.indexOf("Bundle-Version: ") + 16;
            int end = txt.indexOf('\n', start);
            txt = txt.substring(0, start) + "1.0.0."+version + txt.substring(end);
            FileUtils.writeFile(txt, manifestMF);
            
            txt = FileUtils.readFileAsString(productXML.toString());
            start = txt.indexOf("Version ") + 8;
            end = txt.indexOf('\n', start);
            txt = txt.substring(0, start) + "1.0.0."+version + txt.substring(end);
            FileUtils.writeFile(txt, productXML);
            
            File splashBMP = new File(root, "splash.bmp");
            File aboutPNG = new File(root, "images/about.png");
            BufferedImage img = ImageIO.read(splashBMP);
            Image i = img.getScaledInstance(250, 330, BufferedImage.SCALE_SMOOTH);
            img = new BufferedImage(250, 330, BufferedImage.TYPE_INT_ARGB);
            Graphics g = img.createGraphics();
            g.drawImage(i, 0, 0, null);
            g.setColor(Color.yellow);
            g.drawString("Version: "+version, 8, img.getHeight() - 12);
            g.dispose();
            ImageIO.write(img, "PNG", aboutPNG);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
