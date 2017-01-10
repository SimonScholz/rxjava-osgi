
package rxjavasample.parts;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import rxjavasample.model.Todo;
import rxjavasample.model.TodoService;

public class RxJavaPart {

	private CompositeDisposable compositeDisposable;

	public RxJavaPart() {
		compositeDisposable = new CompositeDisposable();
	}

	@PostConstruct
	public void postConstruct(Composite parent, TodoService todoService) {
		Single<List<Todo>> todos = todoService.getTodos();

		TableViewer viewer = new TableViewer(parent);
		viewer.setContentProvider(ArrayContentProvider.getInstance());

		TableViewerColumn summaryColumn = new TableViewerColumn(viewer, SWT.NONE);
		summaryColumn.getColumn().setWidth(300);
		summaryColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Todo) {
					return ((Todo) element).getSummary();
				}
				return super.getText(element);
			}
		});

		TableViewerColumn descriptionColumn = new TableViewerColumn(viewer, SWT.NONE);
		descriptionColumn.getColumn().setWidth(300);
		descriptionColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Todo) {
					return ((Todo) element).getDescription();
				}
				return super.getText(element);
			}
		});

		compositeDisposable.add(todos.subscribeWith(new DisposableSingleObserver<List<Todo>>() {

			@Override
			public void onSuccess(List<Todo> t) {
				viewer.setInput(t);
			}

			@Override
			public void onError(Throwable e) {
				e.printStackTrace();
			}
		}));
	}

	@PreDestroy
	public void dispose() {
		compositeDisposable.dispose();
	}

}
