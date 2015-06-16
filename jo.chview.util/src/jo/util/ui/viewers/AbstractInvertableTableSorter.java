/*
 * Created on Jun 27, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.ui.viewers;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;

public abstract class AbstractInvertableTableSorter extends InvertableSorter {
    private final InvertableSorter inverse = new InvertableSorter() {
 
        public int compare(Viewer viewer, Object e1, Object e2) {
            return (-1)*AbstractInvertableTableSorter.this
                            .compare(viewer, e1, e2);
        }
 
        InvertableSorter getInverseSorter() {
            return AbstractInvertableTableSorter.this;
        }
 
        public int getSortDirection() {
            return SWT.DOWN;
        }
    };
 
    InvertableSorter getInverseSorter() {
        return inverse;
    }
 
    public int getSortDirection() {
        return SWT.UP;
    }
}
