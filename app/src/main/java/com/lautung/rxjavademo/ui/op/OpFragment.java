package com.lautung.rxjavademo.ui.op;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.lautung.rxjavademo.bean.ProjectBean;
import com.lautung.rxjavademo.rx.WanAndroidApi;
import com.lautung.rxjavademo.util.HttpUtil;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;


public class OpFragment extends ListFragment {

    private static final String TAG = "OpFragment";

    public static Fragment newIntance() {
        OpFragment fragment = new OpFragment();
        return fragment;
    }

    ArrayAdapter<String> arrayAdapter;


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] array = new String[]{
                "map",
                "flatmap",
                "concatmap",
                "buffer",
                "retry",
                "retryWhen",
        };
        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, array);
        setListAdapter(arrayAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
        String item = arrayAdapter.getItem(position);
        Toast.makeText(getActivity(), item, Toast.LENGTH_LONG).show();
        switch (position) {
            case 0:
                map();
                break;
            case 1:
                flatmap();
                break;
            case 2:
                concatmap();
                break;
            case 3:
                buffer();
                break;
            case 4:
                retry();
                break;
            case 5:
                retryWhen();
                break;

            default:
                break;
        }
    }

    public void map() { // ??????RxJava??????????????????????????????
        Observable.create(new ObservableOnSubscribe<Integer>() {

            // 1. ???????????????????????? = ??????????????? = 1???2???3
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);

            }
            // 2. ??????Map?????????????????????Function???????????????????????????????????????????????????????????????????????????????????????
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return integer + "_map";
            }
        }).subscribe(new Consumer<String>() {

            // 3. ????????????????????????????????????????????????????????? = ???????????????
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        });
    }

    public void flatmap() {// ??????RxJava??????????????????????????????
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }

            // ??????flatMap?????????????????????
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                final List<String> list = new ArrayList<>();
                for (int i = 10; i < 14; i++) {
                    list.add(integer + "_flatMap_" + i);
                    // ??????flatMap????????????????????????????????????????????????????????????????????????????????????????????????????????????String??????
                    // ???????????????????????????????????????
                }
                return Observable.fromIterable(list);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        });
    }

    public void concatmap() {
        // ??????RxJava??????????????????????????????
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }

            // ??????concatMap?????????????????????
        }).concatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                final List<String> list = new ArrayList<>();
                for (int i = 10; i < 14; i++) {
                    list.add(integer + "_concatMap_" + i);
                    // ??????concatMap????????????????????????????????????????????????????????????????????????????????????????????????????????????String??????
                    // ???????????????????????????????????????
                }
                return Observable.fromIterable(list);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        });
    }

    public void buffer() {
        // ???????????? ????????????5?????????
        Observable.just(1, 2, 3, 4, 5)
                .buffer(3, 1) // ????????????????????? & ??????
                // ??????????????? = ?????????????????????????????????????????????
                // ?????? = ??????????????????????????????
                .subscribe(new Observer<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Integer> stringList) {
                        //
                        Log.d(TAG, " ??????????????????????????? = " + stringList.size());
                        for (Integer value : stringList) {
                            Log.d(TAG, " ?????? = " + value);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "???Error??????????????????");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "???Complete??????????????????");
                    }
                });
    }

    public void flowabledemo() {
        /**
         * ?????????Subscription?????????
         * ??????????????????RxJava2.0?????????Flowable??????????????????128
         * ???????????????
         * 1?????????Flowable?????????subscription.request();??????????????????????????????????????????
         * 2?????????Flowable?????????subscription.request();???????????????????????????????????????128???
         * ??????MissingBackpressureException???
         *
         * @param v
         */

        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i < 129; i++) {
                    Log.e(TAG, "emitter=" + i);
                    emitter.onNext(i);
                }
            }
        }, BackpressureStrategy.MISSING).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
//                        s.request(Long.MAX_VALUE);
                        Log.e(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "onNext=" + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, "onError=" + t);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete");
                    }
                });

    }



    public  void retry(){
        //TODO:
        int count = 0;
        WanAndroidApi wanAndroidApi = HttpUtil.getOnlineCookieRetrofit().create(WanAndroidApi.class);


        wanAndroidApi.getProject().retry(new Predicate<Throwable>() {
            @Override
            public boolean test(Throwable throwable) throws Exception {
                if( count < 4){
                    Log.i(TAG,"????????? " + count);
                    return true;
                }
                return false;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(projectBean -> Log.i(TAG, "retry: "+ projectBean));

    }

    // ???????????????
    int maxConnectCount = 10;
    // ?????????????????????
    int currentRetryCount = 0;
    // ??????????????????
    int waitRetryTime = 0;

    public  void retryWhen(){

        WanAndroidApi wanAndroidApi = HttpUtil.getOnlineCookieRetrofit().create(WanAndroidApi.class);

        Observable<ProjectBean> observable = wanAndroidApi.getProject();
        // ??????4????????????????????? & ??????retryWhen??????????????????
        // ??????????????????????????????retryWhen??????????????????
        observable.retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(@NonNull Observable<Throwable> throwableObservable) throws Exception {
                // ??????Observable<Throwable>???????????? = ???????????????????????????????????????????????????????????????????????????
                return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(@NonNull Throwable throwable) throws Exception {

                        // ??????????????????
                        Log.d(TAG,  "???????????? = "+ throwable.toString());

                        /**
                         * ??????1???????????????????????????????????????
                         * ???????????????????????? = ???????????? = IO?????? ???????????????
                         */
                        if (throwable instanceof IOException){

                            Log.d(TAG,  "??????IO??????????????????" );

                            /**
                             * ??????2?????????????????????
                             * ???????????????????????? < ???????????????????????????????????????
                             */
                            if (currentRetryCount < maxConnectCount){

                                // ??????????????????
                                currentRetryCount++;
                                Log.d(TAG,  "???????????? = " + currentRetryCount);

                                /**
                                 * ??????2???????????????
                                 * ???????????????Observable??????????????? = Next?????????????????????retryWhen??????????????????????????????????????????
                                 *
                                 * ??????3?????????1??????????????????
                                 * ??????delay????????? = ??????????????????????????????????????????????????????
                                 *
                                 * ??????4???????????????????????????????????????
                                 * ???delay????????????????????????????????? = ?????????1??????????????????????????????1s
                                 */
                                // ??????????????????
                                waitRetryTime = 1000 + currentRetryCount* 1000;
                                Log.d(TAG,  "???????????? =" + waitRetryTime);
                                return Observable.just(1).delay(waitRetryTime, TimeUnit.MILLISECONDS);


                            }else{
                                // ?????????????????? > ?????????????????????????????????
                                // ????????????error????????????????????????????????????onError????????????????????????
                                return Observable.error(new Throwable("????????????????????????????????? = " +currentRetryCount  + "?????? ????????????"));

                            }
                        }

                        // ???????????????????????????I/O?????????????????????
                        // ???????????????Observable??????????????? = Error?????? ???????????????????????????onError????????????????????????
                        else{
                            return Observable.error(new Throwable("??????????????????????????????I/O?????????"));
                        }
                    }
                });
            }
        }).subscribeOn(Schedulers.io())               // ?????????IO????????????????????????
                .observeOn(AndroidSchedulers.mainThread())  // ????????????????????? ??????????????????
                .subscribe(new Observer<ProjectBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ProjectBean result) {
                        // ??????????????????????????????
                        Log.d(TAG,  "????????????");
                    }

                    @Override
                    public void onError(Throwable e) {
                        // ???????????????????????????
                        Log.d(TAG,  e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }
}
