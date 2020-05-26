package de.symeda.sormas.ui.reports.campaign;

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.campaign.CampaignDataForm;
import de.symeda.sormas.ui.campaign.CampaignsView;
import de.symeda.sormas.ui.caze.CaseDataView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class CampaignController {

	public void createOrEdit(String uuid) {
		CommitDiscardWrapperComponent<CampaignDataForm> campaignComponent;
		if (uuid != null) {
			CampaignDto campaign = getCampaign(uuid);
			campaignComponent = getCampaignComponent(getCampaign(uuid), () -> SormasUI.refreshView());

			if (UserProvider.getCurrent().hasUserRight(UserRight.CAMPAIGN_DELETE)) {
				campaignComponent.addDeleteListener(() -> {
					FacadeProvider.getCampaignFacade().deleteCampaign(campaign.getUuid());
					UI.getCurrent().getNavigator().navigateTo(CampaignsView.VIEW_NAME);
				}, I18nProperties.getString(Strings.entityCampaign));
			}

			// Initialize 'Archive' button
			if (UserProvider.getCurrent().hasUserRight(UserRight.CAMPAIGN_ARCHIVE)) {
				boolean archived = FacadeProvider.getCampaignFacade().isArchived(campaign.getUuid());
				Button archiveCampaignButton = ButtonHelper.createButton(
						archived ? Captions.actionDearchive : Captions.actionArchive,
						e -> {
							campaignComponent.commit();
							archiveOrDearchiveCampaign(campaign.getUuid(), !archived);
						},
						ValoTheme.BUTTON_LINK);

				campaignComponent.getButtonsPanel().addComponentAsFirst(archiveCampaignButton);
				campaignComponent.getButtonsPanel().setComponentAlignment(archiveCampaignButton, Alignment.BOTTOM_LEFT);
			}
		} else {
			campaignComponent = getCampaignComponent(null, () -> SormasUI.refreshView());
		}
		VaadinUiUtil.showModalPopupWindow(campaignComponent,
				I18nProperties.getString(Strings.headingCreateNewCampaign));
	}

	private void archiveOrDearchiveCampaign(String campaignUuid, boolean archive) {
		if (archive) {
			Label contentLabel = new Label(String.format(I18nProperties.getString(Strings.confirmationArchiveCampaign),
					I18nProperties.getString(Strings.entityCampaign).toLowerCase(),
					I18nProperties.getString(Strings.entityCampaign).toLowerCase()));
			VaadinUiUtil.showConfirmationPopup(I18nProperties.getString(Strings.headingArchiveCampaign), contentLabel,
					I18nProperties.getString(Strings.yes), I18nProperties.getString(Strings.no), 640, e -> {
						if (e.booleanValue() == true) {
							FacadeProvider.getCampaignFacade().archiveOrDearchiveCampaign(campaignUuid, true);
							Notification.show(String.format(I18nProperties.getString(Strings.messageCampaignArchived),
									I18nProperties.getString(Strings.entityCampaign)), Type.ASSISTIVE_NOTIFICATION);
							navigateToView(CampaignsView.VIEW_NAME, campaignUuid, null);
						}
					});
		} else {
			Label contentLabel = new Label(String.format(I18nProperties.getString(Strings.confirmationDearchiveCampaign),
					I18nProperties.getString(Strings.entityCampaign).toLowerCase(),
					I18nProperties.getString(Strings.entityCampaign).toLowerCase()));
			VaadinUiUtil.showConfirmationPopup(I18nProperties.getString(Strings.headingDearchiveCampaign), contentLabel,
					I18nProperties.getString(Strings.yes), I18nProperties.getString(Strings.no), 640, e -> {
						if (e.booleanValue()) {
							FacadeProvider.getCampaignFacade().archiveOrDearchiveCampaign(campaignUuid, false);
							Notification.show(String.format(I18nProperties.getString(Strings.messageCampaignDearchived),
									I18nProperties.getString(Strings.entityCampaign)), Type.ASSISTIVE_NOTIFICATION);
							navigateToView(CampaignsView.VIEW_NAME, campaignUuid, null);
						}
					});
		}
	}

	public CommitDiscardWrapperComponent<CampaignDataForm> getCampaignComponent(CampaignDto campaignDto,
			Runnable callback) {
		CampaignDataForm campaignCreateForm = new CampaignDataForm(campaignDto == null);

		final CommitDiscardWrapperComponent<CampaignDataForm> editView = new CommitDiscardWrapperComponent<CampaignDataForm>(
				campaignCreateForm, campaignCreateForm.getFieldGroup());

		if (campaignDto == null) {
			campaignDto = CampaignDto.build();
			campaignDto.setCreatingUser(UserProvider.getCurrent().getUserReference());
		}
		campaignCreateForm.setValue(campaignDto);

		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!campaignCreateForm.getFieldGroup().isModified()) {
					CampaignDto dto = campaignCreateForm.getValue();
					FacadeProvider.getCampaignFacade().saveCampaign(dto);
					Notification.show(I18nProperties.getString(Strings.messageCampaignCreated), Type.WARNING_MESSAGE);
					callback.run();
				}
			}
		});

		return editView;
	}

	public void registerViews(Navigator navigator) {
		navigator.addView(CampaignsView.VIEW_NAME, CampaignsView.class);
	}

	private CampaignDto getCampaign(String uuid) {
		return FacadeProvider.getCampaignFacade().getByUuid(uuid);
	}
}
