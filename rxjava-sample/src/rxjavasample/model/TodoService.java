package rxjavasample.model;

import java.util.List;

import io.reactivex.Single;

public interface TodoService {

	public Single<List<Todo>> getTodos();

	public Single<Todo> getTodo(int id);
}
