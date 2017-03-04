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
package rxjavasample.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.jobs.Job;
import org.osgi.service.component.annotations.Component;

import io.reactivex.Maybe;
import io.reactivex.Single;
import rxjavasample.model.Todo;
import rxjavasample.model.TodoService;

@Component(service=TodoService.class)
public class TodoServiceImpl implements TodoService {

	private static int current = 1;

	private List<Todo> todos;

	public TodoServiceImpl() {
		todos = createTodos();
	}

	@Override
	public Single<List<Todo>> getTodos() {
		return Single.create(e -> {
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException ex) {
				e.onError(ex);
			}
			e.onSuccess(todos);
		});
		
	}

	@Override
	public Maybe<Todo> getTodo(int id) {
		Optional<Todo> optionalTodo = todos.stream().filter(t -> t.id == id).findAny();

		return Maybe.create(e -> {
			Job.create("Getting Todo with id: " + id, monitor -> {
				try {
					// mimic some delay for the todo retrieval
					TimeUnit.SECONDS.sleep(3);
				} catch (InterruptedException ex) {
					e.onError(ex);
				}
				if (optionalTodo.isPresent()) {
					e.onSuccess(optionalTodo.get());
				} else {
					e.onComplete();
				}
			}).schedule();
		});
	}

	private List<Todo> createTodos() {
		List<Todo> todos = new ArrayList<>();

		todos.add(createTodo("Application model", "Flexible and extensible"));
		todos.add(createTodo("DI", "@Inject as programming mode"));
		todos.add(createTodo("OSGi", "Services"));
		todos.add(createTodo("SWT", "Widgets"));
		todos.add(createTodo("JFace", "Especially Viewers!"));
		todos.add(createTodo("CSS Styling", "Style your application"));
		todos.add(createTodo("Eclipse services", "Selection, model, Part"));
		todos.add(createTodo("Renderer", "Different UI toolkit"));
		todos.add(createTodo("Compatibility Layer", "Run Eclipse 3.x"));

		return todos;
	}

	private Todo createTodo(String summary, String description) {
		return new Todo(current++, summary, description, false, new Date());
	}
}
