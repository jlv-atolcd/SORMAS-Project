package de.symeda.sormas.app.contact.read;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.shared.ContactFormNavigationCapsule;
import de.symeda.sormas.app.shared.TaskFormNavigationCapsule;
import de.symeda.sormas.app.task.read.TaskReadActivity;

/**
 * Created by Orson on 01/01/2018.
 */

public class ContactReadTaskListFragment extends BaseReadActivityFragment<FragmentFormListLayoutBinding, List<Task>, Contact> implements OnListItemClickListener {

    private AsyncTask onResumeTask;
    private String recordUuid;
    private FollowUpStatus followUpStatus;
    private ContactClassification contactClassification = null;
    private List<Task> record;

    private ContactReadTaskListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        saveFilterStatusState(outState, followUpStatus);
        savePageStatusState(outState, contactClassification);
        saveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        followUpStatus = (FollowUpStatus) getFilterStatusArg(arguments);
        contactClassification = (ContactClassification) getPageStatusArg(arguments);
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Contact contact = getActivityRootData();
            List<Task> taskList = new ArrayList<Task>();

            //Case caze = DatabaseHelper.getCaseDao().queryUuidReference(recordUuid);
            if (contact != null) {
                if (contact.isUnreadOrChildUnread())
                    DatabaseHelper.getContactDao().markAsRead(contact);

                taskList = DatabaseHelper.getTaskDao().queryByContact(contact);
            }

            resultHolder.forList().add(taskList);
        } else {
            ITaskResultHolderIterator listIterator = resultHolder.forList().iterator();

            if (listIterator.hasNext())
                record =  listIterator.next();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentFormListLayoutBinding contentBinding) {
        showEmptyListHint(record, R.string.entity_task);

        adapter = new ContactReadTaskListAdapter(ContactReadTaskListFragment.this.getActivity(),
                R.layout.row_read_contact_task_list_item_layout, ContactReadTaskListFragment.this, record);

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
        contentBinding.recyclerViewForList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAfterLayoutBinding(FragmentFormListLayoutBinding contentBinding) {

    }

    @Override
    protected void updateUI(FragmentFormListLayoutBinding contentBinding, List<Task> tasks) {

    }

    @Override
    public void onPageResume(FragmentFormListLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        final SwipeRefreshLayout swiperefresh = (SwipeRefreshLayout)this.getView().findViewById(R.id.swiperefresh);
        if (swiperefresh != null) {
            swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getBaseActivity().synchronizeData(SynchronizeDataAsync.SyncMode.Changes, true, false, true, swiperefresh, null);
                }
            });
        }

        if (!hasBeforeLayoutBindingAsyncReturn)
            return;

        try {
            DefaultAsyncTask executor = new DefaultAsyncTask(getContext()) {
                @Override
                public void onPreExecute() {
                    //getBaseActivity().showPreloader();
                    //
                }

                @Override
                public void doInBackground(TaskResultHolder resultHolder) {
                    Contact contact = getActivityRootData();
                    List<Task> taskList = new ArrayList<Task>();

                    //Case caze = DatabaseHelper.getCaseDao().queryUuidReference(recordUuid);
                    if (contact != null) {
                        if (contact.isUnreadOrChildUnread())
                            DatabaseHelper.getContactDao().markAsRead(contact);

                        taskList = DatabaseHelper.getTaskDao().queryByContact(contact);
                    }

                    resultHolder.forList().add(taskList);
                }
            };
            onResumeTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getBaseActivity().hidePreloader();
                    //getBaseActivity().showFragmentView();

                    if (resultHolder == null){
                        return;
                    }

                    ITaskResultHolderIterator listIterator = resultHolder.forList().iterator();
                    if (listIterator.hasNext())
                        record = listIterator.next();

                    requestLayoutRebind();
                }
            });
        } catch (Exception ex) {
            //getBaseActivity().hidePreloader();
            //getBaseActivity().showFragmentView();
        }

    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_contact_tasks);
    }

    @Override
    public List<Task> getPrimaryData() {
        return record;
    }

    @Override
    public int getRootReadLayout() {
        return R.layout.fragment_root_list_form_layout;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_form_list_layout;
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Task task = (Task)item;
        TaskFormNavigationCapsule dataCapsule = new TaskFormNavigationCapsule(getContext(),
                task.getUuid(), task.getTaskStatus());
        TaskReadActivity.goToActivity(getActivity(), dataCapsule);
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    public static ContactReadTaskListFragment newInstance(ContactFormNavigationCapsule capsule, Contact activityRootData) {
        return newInstance(ContactReadTaskListFragment.class, capsule, activityRootData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);
    }
}