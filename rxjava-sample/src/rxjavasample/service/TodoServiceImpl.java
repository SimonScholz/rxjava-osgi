package rxjavasample.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.ui.di.UISynchronize;

import io.reactivex.Single;
import rxjavasample.model.Todo;
import rxjavasample.model.TodoService;

public class TodoServiceImpl implements TodoService {

	private static int current = 1;

	private List<Todo> todos;

	@Inject
	private UISynchronize sync;

	public TodoServiceImpl() {
		todos = createTodos();
	}

	@Override
	public Single<List<Todo>> getTodos() {
		return Single.create(e -> {
			Job.create("Getting Todos", monitor -> {
				try {
					TimeUnit.SECONDS.sleep(2);
				} catch (InterruptedException ex) {
					sync.asyncExec(() -> {
						e.onError(ex);
					});
				}
				sync.asyncExec(() -> {
					e.onSuccess(todos);
				});
			}).schedule();
		});
	}

	@Override
	public Single<Todo> getTodo(int id) {
		Optional<Todo> optionalTodo = todos.stream().filter(t -> t.id == id).findAny();

		return Single.create(e -> {
			Job.create("Getting Todo with id: " + id, monitor -> {
				try {
					TimeUnit.SECONDS.sleep(2);
				} catch (InterruptedException ex) {
					e.onError(ex);
				}
				if (optionalTodo.isPresent()) {
					e.onSuccess(optionalTodo.get());
				} else {
					e.onError(new Exception("Todo not found"));
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
