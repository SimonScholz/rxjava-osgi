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
package rxjavasample.model;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

public interface TodoService {

	public Single<List<Todo>> getTodos();

	public Maybe<Todo> getTodo(int id);
}
