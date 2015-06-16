package jo.util.ui.viewers;

import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

public class BeanTransfer extends Transfer
{
    private static String TYPE_NAME = BeanTransfer.class.getName();
    private static int TYPE_ID = registerType(TYPE_NAME);
    
    private static String[] TYPE_NAMES = { TYPE_NAME };
    private static int[] TYPE_IDS = { TYPE_ID };

    private static BeanTransfer mInstance;
    
    public static BeanTransfer getInstance()
    {
        if (mInstance == null)
            mInstance = new BeanTransfer();
        return mInstance;
    }
    
    public TransferData[] getSupportedTypes()
    {
        BeanTransferData[] types = new BeanTransferData[1];
        types[0] = new BeanTransferData();
        types[0].type = TYPE_ID;
        return types;
    }

    protected int[] getTypeIds()
    {
        return TYPE_IDS;
    }

    protected String[] getTypeNames()
    {
        return TYPE_NAMES;
    }

    public boolean isSupportedType(TransferData transferData)
    {
        return transferData.type == TYPE_ID;
    }

    protected void javaToNative(Object object, TransferData transferData)
    {
        BeanTransferData data = (BeanTransferData)transferData;
        data.mBean = (Object[])object;
    }

    protected Object nativeToJava(TransferData transferData)
    {
        BeanTransferData data = (BeanTransferData)transferData;
        return data.mBean;
    }

}
