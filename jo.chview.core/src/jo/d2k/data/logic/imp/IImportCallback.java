package jo.d2k.data.logic.imp;

import jo.d2k.data.data.DeletionBean;
import jo.d2k.data.data.MetadataBean;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarRouteBean;

public interface IImportCallback
{
    public void importStart(int size);
    public StarBean importStar(StarBean star);
    public void importDone();
    public MetadataBean importMetadata(MetadataBean md);
    public StarRouteBean importRoute(StarRouteBean route);
    public DeletionBean importDeletion(DeletionBean del);
    public boolean isCanceled();
}
