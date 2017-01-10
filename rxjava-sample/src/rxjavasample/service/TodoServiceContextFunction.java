package rxjavasample.service;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.osgi.service.component.annotations.Component;

import rxjavasample.model.TodoService;

@Component(service = IContextFunction.class, property = "service.context.key=rxjavasample.model.TodoService")
public class TodoServiceContextFunction implements IContextFunction {

	@Override
	public Object compute(IEclipseContext context, String contextKey) {

		TodoServiceImpl todoServiceImpl = ContextInjectionFactory.make(TodoServiceImpl.class, context);

		MApplication app = context.get(MApplication.class);
		app.getContext().set(TodoService.class, todoServiceImpl);

		return todoServiceImpl;
	}

}
