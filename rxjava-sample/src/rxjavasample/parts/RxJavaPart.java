/*******************************************************************************
 * Copyright (c) 2017 Simon Scholz and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Simon Scholz <simon.scholz@vogella.com> - initial API and implementation
 *******************************************************************************/
package rxjavasample.parts;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.swt.schedulers.SwtSchedulers;
import rxjavasample.model.Todo;
import rxjavasample.model.TodoService;

public class RxJavaPart {

	private CompositeDisposable compositeDisposable;

	private TableViewer viewer;

	private Text text;

	public RxJavaPart() {
		compositeDisposable = new CompositeDisposable();
	}

	@PostConstruct
	public void postConstruct(Composite parent, TodoService todoService) {

		// Do the todo retrieval on another thread, then synchronize with the
		// SWT main thread and cache the value so that the result can be reused.
		Single<List<Todo>> todos = todoService.getTodos().subscribeOn(Schedulers.io())
				.observeOn(SwtSchedulers.defaultDisplayThread()).cache();

		Button loadDataButton = new Button(parent, SWT.PUSH);
		loadDataButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		loadDataButton.setText("Load data -->");
		loadDataButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {	
				compositeDisposable.add(todos.subscribe(viewer::setInput, Throwable::printStackTrace));

				// subscribe to the same Single, but without doing the expensive
				// todo retrieval again, because the Single is cached.
				compositeDisposable.add(todos.map(list -> list.get(0).getSummary()).subscribe(text::setText,
						Throwable::printStackTrace));
				
				loadDataButton.setText("Data is cached now!");
			}
		});

		Button UiNotFrozen = new Button(parent, SWT.CHECK);
		UiNotFrozen.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		UiNotFrozen.setText("I should react on click because the hard work is done on another thread.");
		UiNotFrozen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("The UI is not frozen!");
			}
		});

		createTableViewer(parent);

		createText(parent);

		GridLayoutFactory.fillDefaults().numColumns(2).generateLayout(parent);
	}

	private void createText(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		label.setText("First Todo's summary: ");

		text = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		text.setMessage("Shows the first Todo's summary");
	}

	private void createTableViewer(Composite parent) {
		viewer = new TableViewer(parent);
		GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(viewer.getControl());

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
	}

	@PreDestroy
	public void dispose() {
		// Make sure that viewer input is not set when the part is disposed
		compositeDisposable.dispose();
	}

}
