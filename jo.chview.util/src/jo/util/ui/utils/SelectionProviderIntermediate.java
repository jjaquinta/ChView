package jo.util.ui.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

/**
 * IPostSelectionProvider implementation that delegates to another
 * ISelectionProvider or IPostSelectionProvider. The selection provider used
 * for delegation can be exchanged dynamically. Registered listeners are
 * adjusted accordingly. This utility class may be used in workbench parts with
 * multiple viewers.
 * 
 * @author Marc R. Hoffmann
 * Taken from http://www.eclipse.org/articles/Article-WorkbenchSelections/SelectionProviderIntermediate.java
 */
public class SelectionProviderIntermediate implements IPostSelectionProvider {

	private final List<ISelectionChangedListener> selectionListeners = new ArrayList<ISelectionChangedListener>();
	private final List<ISelectionChangedListener> postSelectionListeners = new ArrayList<ISelectionChangedListener>();

	private ISelectionProvider delegate;

	private ISelectionChangedListener selectionListener = new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent event) {
			if (event.getSelectionProvider() == delegate) {
				fireSelectionChanged(event.getSelection());
			}
		}
	};

	private ISelectionChangedListener postSelectionListener = new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent event) {
			if (event.getSelectionProvider() == delegate) {
				firePostSelectionChanged(event.getSelection());
			}
		}
	};

	/**
	 * Sets a new selection provider to delegate to. Selection listeners
	 * registered with the previous delegate are removed before. 
	 * 
	 * @param newDelegate new selection provider
	 */
	public void setSelectionProviderDelegate(ISelectionProvider newDelegate) {
		if (delegate != null) {
			delegate.removeSelectionChangedListener(selectionListener);
			if (delegate instanceof IPostSelectionProvider) {
				((IPostSelectionProvider)delegate).removePostSelectionChangedListener(postSelectionListener);
			}
		}
		delegate = newDelegate;
		if (newDelegate != null) {
			newDelegate.addSelectionChangedListener(selectionListener);
			if (newDelegate instanceof IPostSelectionProvider) {
				((IPostSelectionProvider)newDelegate).addPostSelectionChangedListener(postSelectionListener);
			}
			fireSelectionChanged(newDelegate.getSelection());
		}
	}

	protected void fireSelectionChanged(ISelection selection) {
		fireSelectionChanged(selectionListeners, selection);
	}

	protected void firePostSelectionChanged(ISelection selection) {
		fireSelectionChanged(postSelectionListeners, selection);
	}

	private void fireSelectionChanged(List<ISelectionChangedListener> list, ISelection selection) {
		SelectionChangedEvent event = new SelectionChangedEvent(delegate, selection);
		ISelectionChangedListener[] listeners = list.toArray(new ISelectionChangedListener[0]);
		for (ISelectionChangedListener listener : listeners)
			listener.selectionChanged(event);
	}

	// IPostSelectionProvider Implementation

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionListeners.add(listener);
	}

	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		selectionListeners.remove(listener);
	}

	public void addPostSelectionChangedListener(
			ISelectionChangedListener listener) {
		postSelectionListeners.add(listener);
	}

	public void removePostSelectionChangedListener(
			ISelectionChangedListener listener) {
		postSelectionListeners.remove(listener);
	}

	public ISelection getSelection() {
		return delegate == null ? null : delegate.getSelection();
	}

	public void setSelection(ISelection selection) {
		if (delegate != null) {
			delegate.setSelection(selection);
		}
	}

}
