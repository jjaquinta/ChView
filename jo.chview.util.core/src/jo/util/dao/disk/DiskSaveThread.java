/*
 * Created on Jun 27, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.dao.disk;

public class DiskSaveThread extends Thread
{
    private static final long PAUSE = 1*60*1000; // 1 minute
    
    public DiskSaveThread()
    {
        setDaemon(true);
        setName("Disk Save Thread");
    }
    
    public void run()
    {
        for (;;)
        {
            try
            {
                Thread.sleep(PAUSE);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            DiskUtils.saveList();
        }
    }
}
