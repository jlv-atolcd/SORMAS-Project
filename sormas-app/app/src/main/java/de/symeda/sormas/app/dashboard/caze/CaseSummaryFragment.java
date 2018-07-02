package de.symeda.sormas.app.dashboard.caze;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.symeda.sormas.app.BaseSummaryFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.visualization.SummaryCircularProgressBinder;
import de.symeda.sormas.app.component.visualization.SummaryTotalBinder;
import de.symeda.sormas.app.component.visualization.ViewTypeHelper;
import de.symeda.sormas.app.component.visualization.data.SummaryCircularData;
import de.symeda.sormas.app.component.visualization.data.SummaryTotalData;
import de.symeda.sormas.app.core.DashboardNavigationCapsule;
import de.symeda.sormas.app.core.adapter.multiview.IAdapterRegistrationContext;
import de.symeda.sormas.app.core.adapter.multiview.IAdapterRegistrationService;
import de.symeda.sormas.app.dashboard.SummaryObservableDataResult;
import de.symeda.sormas.app.util.ResourceUtils;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Orson on 08/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class CaseSummaryFragment extends BaseSummaryFragment<ViewTypeHelper.ViewTypeEnum, CaseSummaryAdapter> {

    public static final String TAG = CaseSummaryFragment.class.getSimpleName();

    private CompositeSubscription mSubscription = new CompositeSubscription();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        showPreloader();
        Subscription mDataSubscription = Observable
                .zip(getTotalDataObservable(), getCircularDataObservable(), getMergeDataObservable())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SummaryObservableDataResult>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "Completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage(), e);
                        hidePreloader();
                        showEmptySummaryHint();
                    }

                    @Override
                    public void onNext(SummaryObservableDataResult summparyObservableDataResult) {
                        final List<SummaryTotalData> totalData = summparyObservableDataResult.getTotalData();
                        final List<SummaryCircularData> circularData = summparyObservableDataResult.getCircularData();

                        try {
                            getLandingAdapter().startConfig().forViewType(ViewTypeHelper.ViewTypeEnum.TOTAL, new IAdapterRegistrationService() {
                                @Override
                                public void register(IAdapterRegistrationContext context) throws java.lang.InstantiationException, IllegalAccessException {
                                    context.registerBinder(SummaryTotalBinder.class).registerData(totalData);
                                }
                            })
                                    .forViewType(ViewTypeHelper.ViewTypeEnum.SINGLE_CIRCULAR_PROGRESS, new IAdapterRegistrationService() {
                                        @Override
                                        public void register(IAdapterRegistrationContext context) throws java.lang.InstantiationException, IllegalAccessException {
                                            context.registerBinder(SummaryCircularProgressBinder.class).registerData(circularData);
                                        }
                                    });

                            hidePreloader();

                        } catch (IllegalAccessException e) {
                            Log.e(TAG, e.getMessage(), e);
                        } catch (java.lang.InstantiationException e) {
                            Log.e(TAG, e.getMessage(), e);
                        }
                    }
                });

        mSubscription.add(mDataSubscription);
        configure();
    }

    //<editor-fold desc="Observable Methods">
    private Observable<List<SummaryTotalData>> getTotalDataObservable() {
        return Observable.defer(new Func0<Observable<List<SummaryTotalData>>>() {
            @Override
            public Observable<List<SummaryTotalData>> call() {
                return Observable.create(new Observable.OnSubscribe<List<SummaryTotalData>>() {
                    @Override
                    public void call(Subscriber<? super List<SummaryTotalData>> subscriber) {
                        try {
                            subscriber.onNext(getTotalDataAsync());
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                });
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<List<SummaryCircularData>> getCircularDataObservable() {
        return Observable.defer(new Func0<Observable<List<SummaryCircularData>>>() {
            @Override
            public Observable<List<SummaryCircularData>> call() {
                return Observable.create(new Observable.OnSubscribe<List<SummaryCircularData>>() {
                    @Override
                    public void call(Subscriber<? super List<SummaryCircularData>> subscriber) {
                        try {
                            subscriber.onNext(getCircularDataAsync());
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                });
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Func2<List<SummaryTotalData>, List<SummaryCircularData>, SummaryObservableDataResult> getMergeDataObservable() {
        return new Func2<List<SummaryTotalData>, List<SummaryCircularData>, SummaryObservableDataResult>() {
            @Override
            public SummaryObservableDataResult call(List<SummaryTotalData> summaryTotalData, List<SummaryCircularData> summaryCircularData) {
                return new SummaryObservableDataResult(summaryTotalData, summaryCircularData);
            }
        };
    }
    //</editor-fold>

    //<editor-fold desc="More Overrides">
    @Override
    protected int getSectionTitleResId() {
        return R.string.dashboard_section_title_case;
    }

    @Override
    protected int getEntityResId() {
        return R.string.entity_case;
    }

    @Override
    protected CaseSummaryAdapter createSummaryAdapter() {
        return new CaseSummaryAdapter(getActivity());
    }

    @Override
    protected RecyclerView.LayoutManager createLayoutManager() {
        return new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
    }

    @Override
    protected int getContainerResId() {
        return R.id.fragment_frame_case;
    }

    @Override
    public String getIdentifier() {
        return TAG;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mSubscription != null && !mSubscription.isUnsubscribed())
            mSubscription.unsubscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mSubscription != null && !mSubscription.isUnsubscribed())
            mSubscription.unsubscribe();
    }
    //</editor-fold>

    //<editor-fold desc="Private Methods">
    private List<SummaryTotalData> getTotalDataAsync() {
        List<SummaryTotalData> dataSet = new ArrayList<>();

        SummaryTotalData data = new SummaryTotalData();
        data.dataTitle = ResourceUtils.getString(getActivity(), R.string.title_landing_cell_total_cases);
        ;
        data.dataValue = String.valueOf(new Random().nextInt(10000));
        dataSet.add(data);

        return dataSet;
    }

    private List<SummaryCircularData> getCircularDataAsync() {
        Random random = new Random();
        List<SummaryCircularData> dataSet = new ArrayList<>();

        String titleConfirmedCase = ResourceUtils.getString(getActivity(), R.string.title_landing_cell_cases_confirmed_case);
        String titleProbableCase = ResourceUtils.getString(getActivity(), R.string.title_landing_cell_cases_probable_case);
        String titleSuspectedCase = ResourceUtils.getString(getActivity(), R.string.title_landing_cell_cases_suspected_case);
        String titleFatalities = ResourceUtils.getString(getActivity(), R.string.title_landing_cell_cases_fatalities);
        String titleCaseFatalityRate = ResourceUtils.getString(getActivity(), R.string.title_landing_cell_cases_case_fatality_rate);

        //Confirmed Cases
        SummaryCircularData data1 = new SummaryCircularData(titleConfirmedCase, random.nextInt(10000), random.nextInt(100));

        //Probable Cases
        SummaryCircularData data2 = new SummaryCircularData(titleProbableCase, random.nextInt(10000), random.nextInt(100));

        //Suspected Case
        SummaryCircularData data3 = new SummaryCircularData(titleSuspectedCase, random.nextInt(10000), random.nextInt(100));

        //Fatalities
        SummaryCircularData data4 = new SummaryCircularData(titleFatalities, random.nextInt(10000), random.nextInt(100), R.color.circularProgressFinishedWatchOut,
                R.color.circularProgressUnfinishedWatchOut);

        //Case Fatality Rate
        SummaryCircularData data5 = new SummaryCircularData(titleCaseFatalityRate, random.nextInt(10000), random.nextInt(100), R.color.circularProgressFinishedWatchOut,
                R.color.circularProgressUnfinishedWatchOut);

        dataSet.add(data1);
        dataSet.add(data2);
        dataSet.add(data3);
        dataSet.add(data4);
        dataSet.add(data5);

        return dataSet;
    }
    //</editor-fold>

    public static CaseSummaryFragment newInstance(DashboardNavigationCapsule capsule) {
        try {
            return newInstance(CaseSummaryFragment.class, capsule);
        } catch (IllegalAccessException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (java.lang.InstantiationException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return null;
    }
}
