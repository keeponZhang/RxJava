/**
 * Copyright (c) 2016-present, RxJava Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package io.reactivex.internal.operators.observable;

import io.reactivex.*;
import io.reactivex.internal.fuseable.HasUpstreamObservableSource;
//具体一个源可消费操作符的基类
/**
 * Base class for operators with a source consumable.
 *
 * @param <T> the input source type
 * @param <U> the output type
 */
//看这命名，由于此操作符属于上游
abstract class AbstractObservableWithUpstream<T, U> extends Observable<U> implements HasUpstreamObservableSource<T> {
                                                      // Observable 妥妥的是被观察者的包装类
    /** The source consumable Observable. */
    protected final ObservableSource<T> source;

    /**
     * Constructs the ObservableSource with the given consumable.
     * @param source the consumable Observable
     */
    AbstractObservableWithUpstream(ObservableSource<T> source) {
        //也就是永远会持有上一个操作符返回的被观察者对象
        this.source = source;
    }

    @Override
    public final ObservableSource<T> source() {
        return source;
    }

}
