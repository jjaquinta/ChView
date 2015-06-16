package jo.d2k.admin.rcp.sys.ui;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewRenderLogic;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.stargen.data.BodyBean;
import jo.d2k.data.logic.stargen.data.SolidBodyBean;
import jo.d2k.data.logic.stargen.data.SunBean;
import jo.util.ui.utils.ImageUtils;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class SystemLabelProvider extends LabelProvider
{
    @Override
    public String getText(Object element)
    {
        if (element instanceof StarBean)
            return ChViewRenderLogic.getStarName(((StarBean)element));
        else if (element instanceof BodyBean)
            return ((BodyBean)element).getName();
        return super.getText(element);
    }
    
    @Override
    public Image getImage(Object element)
    {
        return getImageForObject(element);
    }
    public static Image getImageForObject(Object element)
    {
        if (element instanceof StarBean)
        {
            switch (((StarBean)element).getSpectra().charAt(0))
            {
                case 'O':
                    return ImageUtils.getMappedImage("sys_star_o");
                case 'B':
                    return ImageUtils.getMappedImage("sys_star_b");
                case 'A':
                    return ImageUtils.getMappedImage("sys_star_a");
                case 'F':
                    return ImageUtils.getMappedImage("sys_star_f");
                case 'G':
                    return ImageUtils.getMappedImage("sys_star_g");
                case 'K':
                    return ImageUtils.getMappedImage("sys_star_k");
                case 'M':
                    return ImageUtils.getMappedImage("sys_star_m");
                case 'L':
                    return ImageUtils.getMappedImage("sys_star_l");
                case 'T':
                    return ImageUtils.getMappedImage("sys_star_t");
                case 'Y':
                    return ImageUtils.getMappedImage("sys_star_y");
            }
        }
        else if (element instanceof SunBean)
        {
            return getImageForObject(((SunBean)element).getStar());
        }
        else if (element instanceof SolidBodyBean)
        {
            switch (((SolidBodyBean)element).getType())
            {
                case tUnknown:
                    return ImageUtils.getMappedImage("sys_body_unknown");
                case tRock:
                    return ImageUtils.getMappedImage("sys_body_rock");
                case tVenusian:
                    return ImageUtils.getMappedImage("sys_body_venusian");
                case tTerrestrial:
                    return ImageUtils.getMappedImage("sys_body_terrestrial");
                case tGasGiant:
                    return ImageUtils.getMappedImage("sys_body_gas_giant");
                case tMartian:
                    return ImageUtils.getMappedImage("sys_body_martian");
                case tWater:
                    return ImageUtils.getMappedImage("sys_body_water");
                case tIce:
                    return ImageUtils.getMappedImage("sys_body_ice");
                case tSubGasGiant:
                    return ImageUtils.getMappedImage("sys_body_sub_gas_giant");
                case tSubSubGasGiant:
                    return ImageUtils.getMappedImage("sys_body_sub_sub_gas_giant");
                case tAsteroids:
                    return ImageUtils.getMappedImage("sys_body_asteroids");
                case t1Face:
                    return ImageUtils.getMappedImage("sys_body_one_face");
            }
        }
        return null;
    }
}
