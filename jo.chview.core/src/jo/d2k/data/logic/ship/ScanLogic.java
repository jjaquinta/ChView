package jo.d2k.data.logic.ship;

import java.util.ArrayList;
import java.util.List;

import jo.d2k.data.io.IOScanHandler;
import jo.d2k.data.logic.ApplicationLogic;
import jo.d2k.data.logic.stargen.data.BodyBean;
import jo.d2k.data.logic.stargen.data.GasBean;
import jo.d2k.data.logic.stargen.data.SolidBodyBean;
import jo.d2k.data.ship.ScanBean;
import jo.util.utils.obj.DoubleUtils;

public class ScanLogic
{
    private static IOScanHandler getHandler()
    {
        return (IOScanHandler)ApplicationLogic.getHandler(ApplicationLogic.SCAN_HANDLER);
    }
    
    public static ScanBean getByID(long oid)
    {
        return getHandler().findByOID(oid);
    }
    
    public static List<ScanBean> getBySystem(long system)
    {
        return getHandler().findMultiple("SystemOID", system, "ScanType");
    }
    
    public static List<ScanBean> getBySystemAndBody(long system, long body)
    {
        return getHandler().findMultiple("SystemOID", String.valueOf(system), "BodyOID", String.valueOf(body), "ScanType");
    }
    
    public static List<ScanBean> getBySystemAndBodyAndType(long system, long body, int type)
    {
        String[] cols = new String[] { "SystemOID", "BodyOID", "ScanType" };
        String[] vals = new String[] { String.valueOf(system), String.valueOf(body), String.valueOf(type) };
        try
        {
            return getHandler().find(cols, vals, false, "Tier");
        }
        catch (Exception e)
        {
            return new ArrayList<ScanBean>();
        }
    }
    
    public static List<ScanBean> getByUser(long user)
    {
        return getHandler().findMultiple("user", user, "ScanType");
    }
    
    public static List<ScanBean> getAll()
    {
        return getHandler().findAll();
    }
    
    public static void deleteAll()
    {
        getHandler().deleteAll();
    }

    public static void delete(ScanBean bean)
    {
        getHandler().delete(bean);
    }

    public static void delete(List<ScanBean> beans)
    {
        getHandler().delete(beans);
    }
    
    public static ScanBean create(long system, long body, long user, int type, int tier)
    {
        List<ScanBean> scans = getBySystemAndBodyAndType(system, body, type);
        for (ScanBean prev : scans)
            if (prev.getScanTier() >= tier)
                return null; // already scanned
        delete(scans);
        ScanBean bean = getHandler().newInstance();
        bean.setSystemOID(system);
        bean.setBodyOID(body);
        bean.setUser(user);
        bean.setTime(System.currentTimeMillis());
        bean.setScanType(type);
        bean.setScanTier(tier);
        getHandler().update(bean);
        return bean;
    }
    
    public static void applyScans(List<ScanBean> scans, BodyBean body)
    {
        for (ScanBean scan : scans)
            applyScan(scan.getScanType(), scan.getScanTier(), body);
    }
    
    public static void applyScan(ScanBean scan, BodyBean body)
    {
        applyScan(scan.getScanType(), scan.getScanTier(), body);
    }
    
    public static void applyScan(int scanType, int scanTier, BodyBean body)
    {
        if (scanType == ScanBean.RADAR)
            applyRadarScan(scanTier, body);
        else if (scanType == ScanBean.SPECTRAL)
            applySpectralScan(scanTier, body);
    }

    private static void applyRadarScan(int scanTier, BodyBean body)
    {
        body.setRadius(DoubleUtils.roundToSignificantFigures(body.getRadius(), scanTier));
        body.setMass(DoubleUtils.roundToSignificantFigures(body.getMass(), scanTier));
        if (body instanceof SolidBodyBean)
        {
            SolidBodyBean solid = (SolidBodyBean)body;
            solid.setAxialTilt(DoubleUtils.roundToSignificantFigures(solid.getAxialTilt(), scanTier));
            solid.setDensity(DoubleUtils.roundToSignificantFigures(solid.getDensity(), scanTier));
            solid.setDay(DoubleUtils.roundToSignificantFigures(solid.getDay(), scanTier));
            solid.setEscVelocity(DoubleUtils.roundToSignificantFigures(solid.getEscVelocity(), scanTier));
            solid.setSurfAccel(DoubleUtils.roundToSignificantFigures(solid.getSurfAccel(), scanTier));
            solid.setSurfGrav(DoubleUtils.roundToSignificantFigures(solid.getSurfGrav(), scanTier));
            solid.setCoreRadius(DoubleUtils.roundToSignificantFigures(solid.getCoreRadius(), scanTier));
            if (scanTier == 0)
            {
                solid.setResonantPeriod(false);
                solid.setMinorMoons(0);
            }
        }
    }

    private static void applySpectralScan(int scanTier, BodyBean body)
    {
        body.setRadius(DoubleUtils.roundToSignificantFigures(body.getRadius(), scanTier));
        body.setMass(DoubleUtils.roundToSignificantFigures(body.getMass(), scanTier));
        if (body instanceof SolidBodyBean)
        {
            SolidBodyBean solid = (SolidBodyBean)body;
            solid.setDustMass(DoubleUtils.roundToSignificantFigures(solid.getDustMass(), scanTier));
            solid.setGasMass(DoubleUtils.roundToSignificantFigures(solid.getGasMass(), scanTier));
            solid.setRMSVelocity(DoubleUtils.roundToSignificantFigures(solid.getRMSVelocity(), scanTier));
            solid.setMolecWeight(DoubleUtils.roundToSignificantFigures(solid.getMolecWeight(), scanTier));
            solid.setVolatileGasInventory(DoubleUtils.roundToSignificantFigures(solid.getVolatileGasInventory(), scanTier));
            solid.setSurfPressure(DoubleUtils.roundToSignificantFigures(solid.getSurfPressure(), scanTier));
            solid.setBoilPoint(DoubleUtils.roundToSignificantFigures(solid.getBoilPoint(), scanTier));
            solid.setExosphericTemp(DoubleUtils.roundToSignificantFigures(solid.getExosphericTemp(), scanTier));
            solid.setEstimatedTemp(DoubleUtils.roundToSignificantFigures(solid.getEstimatedTemp(), scanTier));
            solid.setEstimatedTerrTemp(DoubleUtils.roundToSignificantFigures(solid.getEstimatedTerrTemp(), scanTier));
            solid.setSurfTemp(DoubleUtils.roundToSignificantFigures(solid.getSurfTemp(), scanTier));
            solid.setGreenhsRise(DoubleUtils.roundToSignificantFigures(solid.getGreenhsRise(), scanTier));
            solid.setHighTemp(DoubleUtils.roundToSignificantFigures(solid.getHighTemp(), scanTier));
            solid.setLowTemp(DoubleUtils.roundToSignificantFigures(solid.getLowTemp(), scanTier));
            solid.setMaxTemp(DoubleUtils.roundToSignificantFigures(solid.getMaxTemp(), scanTier));
            solid.setMinTemp(DoubleUtils.roundToSignificantFigures(solid.getMinTemp(), scanTier));
            solid.setHydrosphere(DoubleUtils.roundToSignificantFigures(solid.getHydrosphere(), scanTier));
            solid.setCloudCover(DoubleUtils.roundToSignificantFigures(solid.getCloudCover(), scanTier));
            solid.setIceCover(DoubleUtils.roundToSignificantFigures(solid.getIceCover(), scanTier));
            solid.setRockCover(DoubleUtils.roundToSignificantFigures(solid.getRockCover(), scanTier));
            solid.setHydroAlbedo(DoubleUtils.roundToSignificantFigures(solid.getHydroAlbedo(), scanTier));
            solid.setCloudAlbedo(DoubleUtils.roundToSignificantFigures(solid.getCloudAlbedo(), scanTier));
            solid.setIceAlbedo(DoubleUtils.roundToSignificantFigures(solid.getIceAlbedo(), scanTier));
            solid.setRockAlbedo(DoubleUtils.roundToSignificantFigures(solid.getRockAlbedo(), scanTier));
            for (GasBean gas : solid.getAtmosphere())
                gas.setSurfacePressure(DoubleUtils.roundToSignificantFigures(gas.getSurfacePressure(), scanTier));
            if (scanTier == 0)
            {
                solid.setGreenhouseEffect(false);
            }
        }
    }
}
