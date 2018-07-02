package de.symeda.sormas.app.caze.edit;

import android.content.res.Resources;
import android.databinding.ObservableArrayList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.component.controls.ControlSwitchField;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentCaseEditHospitalizationLayoutBinding;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;

/**
 * Created by Orson on 16/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class CaseEditHospitalizationFragment extends BaseEditActivityFragment<FragmentCaseEditHospitalizationLayoutBinding, Hospitalization, Case> {

    private AsyncTask onResumeTask;
    private Hospitalization record;
    private Case caze;
    private int mAdmittedToFacilityLastCheckedId = -1;
    private int mIsolationLastCheckedId = -1;
    private int mPreviousHospitalizationLastCheckedId = -1;
    private IEntryItemOnClickListener onAddEntryClickListener;
    private IEntryItemOnClickListener onPrevHosItemClickListener;

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_hospitalization_information);
    }

    @Override
    public Hospitalization getPrimaryData() {
        return record;
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Case caze = getActivityRootData();

            if (caze != null) {
                if (caze.isUnreadOrChildUnread())
                    DatabaseHelper.getCaseDao().markAsRead(caze);

                if (caze.getPerson() == null) {
                    caze.setPerson(DatabaseHelper.getPersonDao().build());
                }

                //TODO: Do we really need to do this
                if (caze.getHospitalization() != null)
                    caze.setHospitalization(DatabaseHelper.getHospitalizationDao().queryUuid(caze.getHospitalization().getUuid()));
            }

            resultHolder.forItem().add(caze.getHospitalization());
            resultHolder.forItem().add(caze);
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

            if (itemIterator.hasNext())
                record = itemIterator.next();

            if (itemIterator.hasNext())
                caze = itemIterator.next();

            setupCallback();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentCaseEditHospitalizationLayoutBinding contentBinding) {
        contentBinding.txtHealthFacility.setVisibility((caze.getHealthFacility() != null)? View.VISIBLE : View.GONE);

        if (caze.getHealthFacility() != null) {
            boolean otherHealthFacility = caze.getHealthFacility().getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
            boolean noneHealthFacility = caze.getHealthFacility().getUuid().equals(FacilityDto.NONE_FACILITY_UUID);

            if (otherHealthFacility) {
                contentBinding.txtHealthFacilityDesc.setVisibility(View.VISIBLE);
            } else if (noneHealthFacility) {
                contentBinding.txtHealthFacilityDesc.setVisibility(View.VISIBLE);
            } else {
                contentBinding.txtHealthFacilityDesc.setVisibility(View.GONE);
            }
        } else {
            contentBinding.txtHealthFacilityDesc.setVisibility(View.GONE);
        }





        contentBinding.setData(record);
        contentBinding.setCaze(caze);
        contentBinding.setYesNoUnknownClass(YesNoUnknown.class);
        contentBinding.setPreviousHospitalizationList(getPreviousHospitalizations());
        contentBinding.setPrevHosItemClickCallback(onPrevHosItemClickListener);
        contentBinding.setAddEntryClickCallback(onAddEntryClickListener);
    }

    @Override
    public void onAfterLayoutBinding(FragmentCaseEditHospitalizationLayoutBinding contentBinding) {
        contentBinding.dtpDateOfAdmission.setFragmentManager(getFragmentManager());
        contentBinding.dtpDateOfDischarge.setFragmentManager(getFragmentManager());
        contentBinding.dtpIsolationDate.setFragmentManager(getFragmentManager());
    }

    @Override
    protected void updateUI(FragmentCaseEditHospitalizationLayoutBinding contentBinding, Hospitalization hospitalization) {

    }

    @Override
    public void onPageResume(FragmentCaseEditHospitalizationLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
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
                    Case caze = getActivityRootData();

                    if (caze != null) {
                        if (caze.isUnreadOrChildUnread())
                            DatabaseHelper.getCaseDao().markAsRead(caze);

                        if (caze.getPerson() == null) {
                            caze.setPerson(DatabaseHelper.getPersonDao().build());
                        }

                        //TODO: Do we really need to do this
                        if (caze.getHospitalization() != null)
                            caze.setHospitalization(DatabaseHelper.getHospitalizationDao().queryUuid(caze.getHospitalization().getUuid()));
                    }

                    resultHolder.forItem().add(caze.getHospitalization());
                    resultHolder.forItem().add(caze);
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

                    ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

                    if (itemIterator.hasNext())
                        record = itemIterator.next();

                    if (itemIterator.hasNext())
                        caze = itemIterator.next();

                    if (record != null && caze != null)
                        requestLayoutRebind();
                    else {
                        getActivity().finish();
                    }
                }
            });
        } catch (Exception ex) {
            //getBaseActivity().hidePreloader();
            //getBaseActivity().showFragmentView();
        }
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_case_edit_hospitalization_layout;
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    @Override
    public boolean showSaveAction() {
        return true;
    }

    @Override
    public boolean showAddAction() {
        return false;
    }

    private void setupCallback() {
//        onAdmittedToFacilityCheckedCallback = new OnTeboSwitchCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(ControlSwitchField teboSwitch, Object checkedItem, int checkedId) {
//                if (checkedId < 0)
//                    return;
//
//                if (mAdmittedToFacilityLastCheckedId == checkedId) {
//                    return;
//                }
//
//                mAdmittedToFacilityLastCheckedId = checkedId;
//
//                YesNoUnknown answer = (YesNoUnknown)checkedItem;
//
//                if (answer == YesNoUnknown.YES) {
//                    getContentBinding().ctrlAdmission.setVisibility(View.VISIBLE);
//                    getContentBinding().ctrlIsolation.setVisibility(View.VISIBLE);
//                } else {
//                    getContentBinding().ctrlAdmission.setVisibility(View.GONE);
//                    getContentBinding().ctrlIsolation.setVisibility(View.GONE);
//                }
//            }
//        };
//
//
//        onIsolationCheckedCallback = new OnTeboSwitchCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(ControlSwitchField teboSwitch, Object checkedItem, int checkedId) {
//                if (checkedId < 0)
//                    return;
//
//                if (mIsolationLastCheckedId == checkedId) {
//                    return;
//                }
//
//                mIsolationLastCheckedId = checkedId;
//
//                YesNoUnknown answer = (YesNoUnknown)checkedItem;
//
//                if (answer == YesNoUnknown.YES) {
//                    getContentBinding().dtpIsolationDate.setVisibility(View.VISIBLE);
//                } else {
//                    getContentBinding().dtpIsolationDate.setVisibility(View.GONE);
//                }
//
//            }
//        };
//
//
//        onPreviousHospitalizationCheckedCallback = new OnTeboSwitchCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(ControlSwitchField teboSwitch, Object checkedItem, int checkedId) {
//                if (checkedId < 0)
//                    return;
//
//                if (mPreviousHospitalizationLastCheckedId == checkedId) {
//                    return;
//                }
//
//                mPreviousHospitalizationLastCheckedId = checkedId;
//
//                YesNoUnknown answer = (YesNoUnknown)checkedItem;
//
//                if (answer == YesNoUnknown.YES) {
//                    getContentBinding().ctrlPrevHospitalization.setVisibility(View.VISIBLE);
//                } else {
//                    getContentBinding().ctrlPrevHospitalization.setVisibility(View.GONE);
//                }
//
//                verifyPrevHospitalizationStatus();
//
//            }
//        };


        onPrevHosItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final PreviousHospitalization hospitalization = (PreviousHospitalization)item;
                final PreviousHospitalizationDialog dialog = new PreviousHospitalizationDialog(CaseEditActivity.getActiveActivity(), hospitalization);

                dialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        updatePreviousHospitalizations((PreviousHospitalization)item);
                        dialog.dismiss();
                    }
                });

                dialog.setOnDeleteClickListener(new TeboAlertDialogInterface.DeleteOnClickListener() {
                    @Override
                    public void onDeleteClick(View v, Object item, View viewRoot) {
                        removePreviousHospitalizations((PreviousHospitalization)item);
                        dialog.dismiss();
                    }
                });

                dialog.show(null);
            }
        };

        onAddEntryClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final PreviousHospitalization hospitalization = DatabaseHelper.getPreviousHospitalizationDao().build();
                final PreviousHospitalizationDialog dialog = new PreviousHospitalizationDialog(CaseEditActivity.getActiveActivity(), hospitalization);

                dialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        addPreviousHospitalizations((PreviousHospitalization)item);
                        dialog.dismiss();
                    }
                });

                dialog.setOnDeleteClickListener(new TeboAlertDialogInterface.DeleteOnClickListener() {
                    @Override
                    public void onDeleteClick(View v, Object item, View viewRoot) {
                        removePreviousHospitalizations((PreviousHospitalization)item);
                        dialog.dismiss();
                    }
                });

                dialog.show(null);

                //results.add(0, MemoryDatabaseHelper.PREVIOUS_HOSPITALIZATION.getPreviousHospitalizations(20).get(new Random().nextInt(10)));
            }
        };
    }

    private ObservableArrayList getPreviousHospitalizations() {
        ObservableArrayList newPreHospitalizations = new ObservableArrayList();
        if (record != null)
            newPreHospitalizations.addAll(record.getPreviousHospitalizations());

        return newPreHospitalizations;
    }

    private void removePreviousHospitalizations(PreviousHospitalization item) {
        if (record == null)
            return;

        if (record.getPreviousHospitalizations() == null)
            return;

        record.getPreviousHospitalizations().remove(item);

        getContentBinding().setPreviousHospitalizationList(getPreviousHospitalizations());
        verifyPrevHospitalizationStatus();
    }

    private void updatePreviousHospitalizations(PreviousHospitalization item) {
        if (record == null)
            return;

        if (record.getPreviousHospitalizations() == null)
            return;

        //record.getPreviousHospitalizations().remove(item);
        //record.getPreviousHospitalizations().add(0, (PreviousHospitalization)item);

        getContentBinding().setPreviousHospitalizationList(getPreviousHospitalizations());
        verifyPrevHospitalizationStatus();
    }

    private void addPreviousHospitalizations(PreviousHospitalization item) {
        if (record == null)
            return;

        if (record.getPreviousHospitalizations() == null)
            return;

        record.getPreviousHospitalizations().add(0, (PreviousHospitalization)item);

        getContentBinding().setPreviousHospitalizationList(getPreviousHospitalizations());
        verifyPrevHospitalizationStatus();
    }

    private void verifyPrevHospitalizationStatus() {
        YesNoUnknown hospitalizedPreviously = record.getAdmittedToHealthFacility();
        if (hospitalizedPreviously == YesNoUnknown.YES && getPreviousHospitalizations().size() <= 0) {
            getContentBinding().swhPreviousHospitalization.enableErrorState((NotificationContext)getActivity(), R.string.validation_soft_add_list_entry);
        } else {
            getContentBinding().swhPreviousHospitalization.disableErrorState();
        }
    }

    public static CaseEditHospitalizationFragment newInstance(CaseFormNavigationCapsule capsule, Case activityRootData) {
        return newInstance(CaseEditHospitalizationFragment.class, capsule, activityRootData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);
    }

}
