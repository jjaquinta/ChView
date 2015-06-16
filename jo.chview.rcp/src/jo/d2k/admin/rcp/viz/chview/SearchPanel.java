package jo.d2k.admin.rcp.viz.chview;

import java.util.ArrayList;
import java.util.List;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewRenderLogic;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.d2k.data.data.SearchParams;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.StarSearchLogic;
import jo.util.geom3d.Point3D;
import jo.util.logic.ThreadLogic;
import jo.util.ui.utils.ClipboardLogic;
import jo.util.ui.utils.GridUtils;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;

import chuck.terran.admin.ui.StarsViewer;

public class SearchPanel extends Composite
{
    private SearchParams    mSearchParams;
    private int             mLastResults;
    private String          mMenuSelectText;
    
    private ScrolledComposite mInputScroller;
    private SearchParamsPanel mInputParams;
    private Button            mStartSearch;
    private Button            mGoto;
    private ProgressBar       mSearchProgress;

    private StarsViewer mResults;

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    public SearchPanel(Composite parent, int style)
    {
        super(parent, style);
        mSearchParams = new SearchParams();
        mSearchParams.setCenter(ChViewVisualizationLogic.mPreferences.getCenter());
        mSearchParams.setSearchRadius(ChViewVisualizationLogic.mPreferences.getRadius()*3);
        mSearchParams.setCache(ChViewVisualizationLogic.mPreferences.getStars());
        
        setLayout(new FillLayout(SWT.VERTICAL));
        Group inputs = new Group(this, SWT.NULL);
        inputs.setText("Search Parameters");
        Group outputs = new Group(this, SWT.NULL);
        outputs.setText("Search Results");
                
        inputs.setLayout(new FillLayout());
        Composite inputsClient = new Composite(inputs, SWT.NULL);
        inputsClient.setLayout(new GridLayout(2, false));
        mInputScroller = new ScrolledComposite(inputsClient, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        GridUtils.setLayoutData(mInputScroller, "2x1 fill=hv");
        mInputParams = new SearchParamsPanel(mInputScroller, SWT.NULL);
        mInputScroller.setExpandHorizontal(true);
        mInputScroller.setExpandVertical(true);
        mInputScroller.setContent(mInputParams);
        mInputParams.setSearch(mSearchParams);
        mStartSearch = GridUtils.makeButton(inputsClient, "Start", "");
        mSearchProgress = new ProgressBar(inputsClient, SWT.HORIZONTAL);
        GridUtils.setLayoutData(mSearchProgress, "fill=h");
        mSearchProgress.setVisible(false);

        outputs.setLayout(new GridLayout(2, false));
        if ((style&SWT.FULL_SELECTION) != 0)
            mResults = new StarsViewer(outputs, SWT.FULL_SELECTION|SWT.MULTI);
        else
            mResults = new StarsViewer(outputs, SWT.FULL_SELECTION);
        mResults.setInput(mSearchParams.getResults());
        GridUtils.setLayoutData(mResults.getControl(), "2x1 fill=hv");
        if ((style&SWT.FULL_SELECTION) != 0)
        {
            mGoto = GridUtils.makeButton(outputs, "Goto", "");
            GridUtils.makeLabel(outputs, "", "fill=h");
            mGoto.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e)
                {
                    doGoto();
                }
            });
        }

        mStartSearch.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doStartSearch();
            }
        });
        mResults.addSelectionChangedListener(new ISelectionChangedListener() {            
            @Override
            public void selectionChanged(SelectionChangedEvent ev)
            {
                doResultsSelectionChanged();
            }
        });
        mResults.addDoubleClickListener(new IDoubleClickListener() {            
            @Override
            public void doubleClick(DoubleClickEvent arg0)
            {
                doGoto();
            }
        });
        mResults.getViewer().getTree().addMenuDetectListener(new MenuDetectListener() {            
            @Override
            public void menuDetected(MenuDetectEvent ev)
            {
                Point p = new Point(ev.x, ev.y);
                doMenu(p);
            }
        });
        Menu menu = new Menu(mResults.getControl());
        MenuItem item1 = new MenuItem(menu, SWT.PUSH);
        item1.setText("Copy");
        item1.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doCopy();
            }
        });
        MenuItem item2 = new MenuItem(menu, SWT.PUSH);
        item2.setText("Copy Selected");
        item2.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doCopySelected();
            }
        });
        MenuItem item3 = new MenuItem(menu, SWT.PUSH);
        item3.setText("Copy All");
        item3.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doCopyAll();
            }
        });
        mResults.getControl().setMenu(menu);
        
        doResultsSelectionChanged();
        updateScroller();
    }

    private void updateScroller()
    {
        mInputParams.pack();
        //mInputs.setSize(mInputs.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        mInputScroller.setMinSize(mInputParams.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        mInputScroller.layout();
    }

    private void beginSearch()
    {
        //System.out.println("SearchPanel.beginSearch()");
        mStartSearch.setText("Cancel");
        mSearchProgress.setSelection(0);
        mSearchProgress.setVisible(true);
        mStartSearch.getParent().layout();
        
        mInputParams.getSearch(); // read from UI
        mSearchParams.setCache(ChViewVisualizationLogic.mPreferences.getStars());
        mSearchParams.getResults().clear();
        mSearchParams.setDone(false);
        mResults.refresh();
        mLastResults = 0;
        ThreadLogic.runOnBackgroundThread(new Thread("Star Search") { public void run() { 
            //System.out.println("StarSearchLogic.findStars - starting");
            StarSearchLogic.findStars(mSearchParams, ChViewVisualizationLogic.mPreferences); 
            //System.out.println("StarSearchLogic.findStars - done");
            } });
        ThreadLogic.runMethodOnBackgroundThread(this, "monitorSearch")
            .setName("Star Search Monitor");
    }
    
    // run on non-UI thread
    public void monitorSearch()
    {
        //System.out.println("SearchPanel.monitorSearch() - starting");
        for (;;)
        {
            if (mInputParams.isDisposed())
            {
                //System.out.println("SearchPanel.monitorSearch() - disposed");
                break;
            }
            ThreadLogic.runMethodOnUIThread(this, "updateSearch");
            if (mSearchParams.isDone())
            {
                //System.out.println("SearchPanel.monitorSearch() - search done");
                break;
            }
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
            }
        } 
        //System.out.println("SearchPanel.monitorSearch() - done");
    }
    
    // run on UI thread
    public void updateSearch()
    {
        //System.out.println("StarSearchLogic.updateSearch");
        if (mInputParams.isDisposed())
            return;
        if (mLastResults != mSearchParams.getResults().size())
        {
            mLastResults = mSearchParams.getResults().size();
            mResults.refresh();
        }
        if (mSearchParams.isDone())
        {
            mStartSearch.setText("Start");
            mSearchProgress.setVisible(false);
            updateScroller();
        }
        else
        {
            mSearchProgress.setMinimum(0);
            mSearchProgress.setMaximum(mSearchParams.getTotalSteps());
            mSearchProgress.setSelection(mSearchParams.getTakenSteps());
        }

    }
    
    private void cancelSearch()
    {
        //System.out.println("StarSearchLogic.cancelSearch");
        mSearchParams.setCancel(true);
    }
    
    private void doStartSearch()
    {
        //System.out.println("mSearchParams.isDone()="+mSearchParams.isDone());
        if (mSearchParams.isDone())
            beginSearch();
        else
            cancelSearch();
    }
    
    private void doResultsSelectionChanged()
    {
        ISelection sel = mResults.getSelection();
        if (mGoto != null)
            mGoto.setEnabled(!sel.isEmpty());
    }
    
    private void doGoto()
    {
        StarBean star = (StarBean)mResults.getSelectedItem();
        Point3D center = new Point3D(star.getX(), star.getY(), star.getZ());
        ChViewVisualizationLogic.setCenter(center);
        mSearchParams.setCenter(center);
        mSearchParams.setCache(ChViewVisualizationLogic.mPreferences.getStars());
        mInputParams.setSearch(mSearchParams);
    }
    
    private void doMenu(Point p)
    {
        int[] rc = mResults.getSelectedCell(p);
        StarBean star = mResults.getInput().get(rc[0]);
        mMenuSelectText = mResults.getLabels().getColumnText(star, rc[1]);
    }

    private void doCopy()
    {
        ClipboardLogic.setAsText(mMenuSelectText);
    }

    private void doCopySelected()
    {
        List<StarBean> stars = new ArrayList<StarBean>();
        for (Object star : mResults.getSelectedItems())
            stars.add((StarBean)star);
        copyList(stars);
    }

    private void doCopyAll()
    {
        copyList(mResults.getInput());
    }

    private void copyList(List<StarBean> stars)
    {
        StringBuffer sb = new StringBuffer();
        for (StarBean star : stars)
        {
            if (sb.length() > 0)
                sb.append(System.getProperty("line.separator"));
            sb.append(ChViewRenderLogic.getStarName(star)+","+star.getX()+","+star.getY()+","+star.getZ()+","+star.getSpectra());
        }
        ClipboardLogic.setAsText(sb.toString());
    }
    
    public ISelectionProvider getSelectionProvider()
    {
        return mResults;
    }

    public SearchParams getSearchParams()
    {
        return mSearchParams;
    }
    
    public void setPattern(String pattern)
    {
        mSearchParams.setPattern(pattern);
        mInputParams.setSearch(mSearchParams);
    }
}
