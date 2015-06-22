package jo.d2k.stars.logic;

import java.util.Random;

import jo.d2k.data.logic.StarLogic;
import jo.d4w.logic.D4WStarGenLogic;
import jo.d4w.logic.D4WStarLogic;

public class DensitySurvey
{
    public static void main(String[] argv)
    {
        /*
        BufferedImage imgTop = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < imgTop.getWidth(); x++)
        {
            double gx = MathUtils.interpolate(x, 0, imgTop.getWidth() - 1, -D4WStarGenLogic.GALAXY_DISK_RADIUS, D4WStarGenLogic.GALAXY_DISK_RADIUS);
            for (int y = 0; y < imgTop.getHeight(); y++)
            {
                double gy = MathUtils.interpolate(y, 0, imgTop.getWidth() - 1, -D4WStarGenLogic.GALAXY_DISK_RADIUS, D4WStarGenLogic.GALAXY_DISK_RADIUS);
                double d = D4WStarGenLogic.galaxyDensity(gx, gy, 0);
                int rgb = (int)(255*d);
                rgb |= (rgb<<8)|(rgb<<16);
                imgTop.setRGB(x, y, rgb);
            }
        }
        int earthX = (int)MathUtils.interpolateCos(D4WStarGenLogic.GALAXY_EARTH_RADIUS, -D4WStarGenLogic.GALAXY_DISK_RADIUS, D4WStarGenLogic.GALAXY_DISK_RADIUS, 0, imgTop.getWidth());
        for (int x = 0; x < imgTop.getWidth(); x++)
            imgTop.setRGB(x, imgTop.getHeight()/2, 0x0000FF);
        for (int y = 0; y < imgTop.getHeight(); y++)
            imgTop.setRGB(earthX, y, 0x0000FF);
        BufferedImage imgSide = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < imgTop.getWidth(); x++)
        {
            double gx = MathUtils.interpolate(x, 0, imgTop.getWidth() - 1, -D4WStarGenLogic.GALAXY_DISK_RADIUS, D4WStarGenLogic.GALAXY_DISK_RADIUS);
            for (int z = 0; z < imgTop.getHeight(); z++)
            {
                double gz = MathUtils.interpolate(z, 0, imgTop.getWidth() - 1, -D4WStarGenLogic.GALAXY_DISK_RADIUS, D4WStarGenLogic.GALAXY_DISK_RADIUS);
                double d = D4WStarGenLogic.galaxyDensity(gx, 0, gz);
                int rgb = (int)(255*d);
                rgb |= (rgb<<8)|(rgb<<16);
                imgSide.setRGB(x, z, rgb);
            }
        }
        for (int x = 0; x < imgSide.getWidth(); x++)
            imgSide.setRGB(x, imgSide.getHeight()/2, 0x0000FF);
        for (int y = 0; y < imgSide.getHeight(); y++)
            imgSide.setRGB(earthX, y, 0x0000FF);
        try
        {
            ImageIO.write(imgTop, "PNG", new File("c:\\temp\\gal_top.png"));
            ImageIO.write(imgSide, "PNG", new File("c:\\temp\\gal_side.png"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        */
        double numStars = 0;
        for (double x = -D4WStarGenLogic.GALAXY_DISK_RADIUS; x < D4WStarGenLogic.GALAXY_DISK_RADIUS; x += 1000)
            for (double y = -D4WStarGenLogic.GALAXY_DISK_RADIUS; y < D4WStarGenLogic.GALAXY_DISK_RADIUS; y += 1000)
                for (double z = -D4WStarGenLogic.GALAXY_BULGE_RADIUS; z < D4WStarGenLogic.GALAXY_BULGE_RADIUS; z += 1000)
                {
                    double d = D4WStarGenLogic.galaxyDensity(x, y, z);
                    numStars += 1000*1000*1000*D4WStarGenLogic.GALAXY_BASE_DENSITY*d;
                }
        System.out.println("Number of stars: "+numStars/1000000000+" billion");
        int qx = (int)(D4WStarGenLogic.GALAXY_EARTH_RADIUS/StarLogic.QUAD_SIZE);
        long seed = qx;
        Random rnd = new Random(seed);
        System.out.println("Density @ earth quad: "+D4WStarGenLogic.quadDensity(qx, 0, 0));
        System.out.println("Density @ earth quad: "+D4WStarGenLogic.genQuadPopulation(rnd, qx, 0, 0));
        int d2kCount = StarLogic.getAllWithin(0, 0, 0, 75).size();
        int d4wCount = D4WStarLogic.getAllWithin(D4WStarGenLogic.GALAXY_EARTH_RADIUS, 0, 0, 75).getStars().size();
        double vol = 4.0/3.0*Math.PI*50*50*50;
        System.out.println("d2k density = "+d2kCount/vol+" ("+d2kCount+")");
        System.out.println("d4w density = "+d4wCount/vol+" ("+d4wCount+")");
    }
}
