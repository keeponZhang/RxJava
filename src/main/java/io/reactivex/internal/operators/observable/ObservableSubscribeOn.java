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

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;

public final class ObservableSubscribeOn<T> extends AbstractObservableWithUpstream<T, T> {
    final Scheduler scheduler;

    public ObservableSubscribeOn(ObservableSource<T> source, Scheduler scheduler) {
        //这个source则为上一个操作符返回的被观察的自雷
        super(source);
        this.scheduler = scheduler;
    }
    //s：ObserveOnObserver
    @Override
    public void subscribeActual(final Observer<? super T> s) {
        //首先还是对观察者就行包装（包装出来的好像叫parent）
        final SubscribeOnObserver<T> parent = new SubscribeOnObserver<T>(s);
        //调用下面一个观察者的onSubscribe方法，表示触发订阅了（ObservableObserveOn.ObserveOnObserver.onSubscribe）
        s.onSubscribe(parent);
        // 设置被观察者的运行线程：
        //scheduler:IoScheduler；SubscribeTask实现了Runnable
        parent.setDisposable(scheduler.scheduleDirect(new SubscribeTask(parent)));
    }
    //实现了观察者接口和Disposable接口
    static final class SubscribeOnObserver<T> extends AtomicReference<Disposable> implements Observer<T>, Disposable {

        private static final long serialVersionUID = 8094547886072529208L;
        final Observer<? super T> actual;

        final AtomicReference<Disposable> s;
        //actual表示下面一层的观察者
        SubscribeOnObserver(Observer<? super T> actual) {
            this.actual = actual;
            this.s = new AtomicReference<Disposable>();
        }

        @Override
        public void onSubscribe(Disposable s) {
            //哦，也就是最终木有回调咱们的观察者的onSubscribe方法
            DisposableHelper.setOnce(this.s, s);
        }

        @Override
        public void onNext(T t) {
            //此时actual：ObserveOnObserver
            actual.onNext(t);
        }

        @Override
        public void onError(Throwable t) {
            actual.onError(t);
        }

        @Override
        public void onComplete() {
            actual.onComplete();
        }

        @Override
        public void dispose() {
            DisposableHelper.dispose(s);
            DisposableHelper.dispose(this);
        }

        @Override
        public boolean isDisposed() {
            return DisposableHelper.isDisposed(get());
        }

        void setDisposable(Disposable d) {
            DisposableHelper.setOnce(this, d);
        }
    }

    final class SubscribeTask implements Runnable {
        private final SubscribeOnObserver<T> parent;

        SubscribeTask(SubscribeOnObserver<T> parent) {
            this.parent = parent;
        }

        @Override
        public void run() {
            // 其中它的run方法中是调用的source的subscribe()方法了，那这个source是啥呢？很明显是它上一个操作符所创建的被观察者
            //source:ObservableJust
            source.subscribe(parent);
        }
    }
}
