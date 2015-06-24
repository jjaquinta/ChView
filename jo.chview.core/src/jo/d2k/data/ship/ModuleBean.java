package jo.d2k.data.ship;

import jo.util.beans.Bean;

public class ModuleBean extends Bean
{
    // dparams
    public static final int ENERGY_STORED = 1; // group = ENERGY_STORE

    private long    mShipOID;
    private int     mType;
    private boolean mEnabled;
    private long    mLParam1;
    private long    mLParam2;
    private long    mLParam3;
    private long    mLParam4;
    private long    mLParam5;
    private long    mLParam6;
    private long    mLParam7;
    private long    mLParam8;
    private double  mDParam1;
    private double  mDParam2;
    private double  mDParam3;
    private double  mDParam4;
    private double  mDParam5;
    private double  mDParam6;
    private double  mDParam7;
    private double  mDParam8;
    
    public long getShipOID()
    {
        return mShipOID;
    }
    public void setShipOID(long shipOID)
    {
        mShipOID = shipOID;
    }
    public int getType()
    {
        return mType;
    }
    public void setType(int type)
    {
        mType = type;
    }
    public long getLParam1()
    {
        return mLParam1;
    }
    public void setLParam1(long lParam1)
    {
        mLParam1 = lParam1;
    }
    public long getLParam2()
    {
        return mLParam2;
    }
    public void setLParam2(long lParam2)
    {
        mLParam2 = lParam2;
    }
    public long getLParam3()
    {
        return mLParam3;
    }
    public void setLParam3(long lParam3)
    {
        mLParam3 = lParam3;
    }
    public long getLParam4()
    {
        return mLParam4;
    }
    public void setLParam4(long lParam4)
    {
        mLParam4 = lParam4;
    }
    public long getLParam5()
    {
        return mLParam5;
    }
    public void setLParam5(long lParam5)
    {
        mLParam5 = lParam5;
    }
    public long getLParam6()
    {
        return mLParam6;
    }
    public void setLParam6(long lParam6)
    {
        mLParam6 = lParam6;
    }
    public long getLParam7()
    {
        return mLParam7;
    }
    public void setLParam7(long lParam7)
    {
        mLParam7 = lParam7;
    }
    public long getLParam8()
    {
        return mLParam8;
    }
    public void setLParam8(long lParam8)
    {
        mLParam8 = lParam8;
    }
    public double getDParam1()
    {
        return mDParam1;
    }
    public void setDParam1(double dParam1)
    {
        mDParam1 = dParam1;
    }
    public double getDParam2()
    {
        return mDParam2;
    }
    public void setDParam2(double dParam2)
    {
        mDParam2 = dParam2;
    }
    public double getDParam3()
    {
        return mDParam3;
    }
    public void setDParam3(double dParam3)
    {
        mDParam3 = dParam3;
    }
    public double getDParam4()
    {
        return mDParam4;
    }
    public void setDParam4(double dParam4)
    {
        mDParam4 = dParam4;
    }
    public double getDParam5()
    {
        return mDParam5;
    }
    public void setDParam5(double dParam5)
    {
        mDParam5 = dParam5;
    }
    public double getDParam6()
    {
        return mDParam6;
    }
    public void setDParam6(double dParam6)
    {
        mDParam6 = dParam6;
    }
    public double getDParam7()
    {
        return mDParam7;
    }
    public void setDParam7(double dParam7)
    {
        mDParam7 = dParam7;
    }
    public double getDParam8()
    {
        return mDParam8;
    }
    public void setDParam8(double dParam8)
    {
        mDParam8 = dParam8;
    }
    public boolean isEnabled()
    {
        return mEnabled;
    }
    public void setEnabled(boolean enabled)
    {
        mEnabled = enabled;
    }

    public void setDParam(int param, double value)
    {
        switch (param)
        {
            case 1:
                mDParam1 = value;
                break;
            case 2:
                mDParam2 = value;
                break;
            case 3:
                mDParam3 = value;
                break;
            case 4:
                mDParam4 = value;
                break;
            case 5:
                mDParam5 = value;
                break;
            case 6:
                mDParam6 = value;
                break;
            case 7:
                mDParam7 = value;
                break;
            case 8:
                mDParam8 = value;
                break;
            default:
                throw new IllegalArgumentException("Cannot set DParam="+param);
        }
    }

    public double getDParam(int param)
    {
        switch (param)
        {
            case 1:
                return mDParam1;
            case 2:
                return mDParam2;
            case 3:
                return mDParam3;
            case 4:
                return mDParam4;
            case 5:
                return mDParam5;
            case 6:
                return mDParam6;
            case 7:
                return mDParam7;
            case 8:
                return mDParam8;
        }
        throw new IllegalArgumentException("Cannot get DParam="+param);
    }

    public void setLParam(int param, long value)
    {
        switch (param)
        {
            case 1:
                mLParam1 = value;
                break;
            case 2:
                mLParam2 = value;
                break;
            case 3:
                mLParam3 = value;
                break;
            case 4:
                mLParam4 = value;
                break;
            case 5:
                mLParam5 = value;
                break;
            case 6:
                mLParam6 = value;
                break;
            case 7:
                mLParam7 = value;
                break;
            case 8:
                mLParam8 = value;
                break;
            default:
                throw new IllegalArgumentException("Cannot set LParam="+param);
        }
    }

    public long getLParam(int param)
    {
        switch (param)
        {
            case 1:
                return mLParam1;
            case 2:
                return mLParam2;
            case 3:
                return mLParam3;
            case 4:
                return mLParam4;
            case 5:
                return mLParam5;
            case 6:
                return mLParam6;
            case 7:
                return mLParam7;
            case 8:
                return mLParam8;
        }
        throw new IllegalArgumentException("Cannot get LParam="+param);
    }
}
