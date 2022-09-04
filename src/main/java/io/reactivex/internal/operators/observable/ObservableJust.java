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
import io.reactivex.internal.fuseable.ScalarCallable;
import io.reactivex.internal.operators.observable.ObservableScalarXMap.ScalarDisposable;

/**
 * Represents a constant scalar value.
 * @param <T> the value type
 */
//等于是对被观察者的一个再包装
public final class ObservableJust<T> extends Observable<T> implements ScalarCallable<T> {

    private final T value;
    public ObservableJust(final T value) {
        this.value = value;
    }

    @Override
    protected void subscribeActual(Observer<? super T> s) {
        //则会调用它了，s：SubscribeOnObserver
        //这里注意，把value也传进去了
        ScalarDisposable<T> sd = new ScalarDisposable<T>(s, value);
        s.onSubscribe(sd);
        //这里就调用run方法了
        sd.run();
    }

    @Override
    public T call() {
        return value;
    }
}
