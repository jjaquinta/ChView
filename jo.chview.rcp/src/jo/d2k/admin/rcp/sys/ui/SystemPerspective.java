package jo.d2k.admin.rcp.sys.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class SystemPerspective implements IPerspectiveFactory {
    public static final String ID = SystemPerspective.class.getName();
    
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		/*
		layout.setFixed(true);
		
        String editorArea = layout.getEditorArea();
		IFolderLayout client = layout.createFolder("client", IPageLayout.RIGHT, 0.5f, editorArea);
        //IFolderLayout summary = layout.createFolder("summary", IPageLayout.RIGHT, 0.5f, StarsView.ID);
        //IFolderLayout info = layout.createFolder("info", IPageLayout.BOTTOM, 0.5f, "summary");
		client.addView(StarsView.ID);
		client.addPlaceholder(TwoDView.ID+":*");
        //summary.addView(SystemView.ID);
        //info.addView(InfoView.ID);         
         */
	}

}
