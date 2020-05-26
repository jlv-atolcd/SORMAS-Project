package de.symeda.sormas.ui.campaign;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.campaign.CampaignCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.events.EventsView;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class CampaignsView extends AbstractView {

	private static final long serialVersionUID = 4551760940640983434L;

	public static final String VIEW_NAME = "campaigns";

	private CampaignCriteria criteria;
	private VerticalLayout gridLayout;
	private CampaignGrid grid;
	private Button createButton;

	public CampaignsView() {
		super(VIEW_NAME);

		ViewModelProviders.of(getClass()).get(ViewConfiguration.class);

		criteria = ViewModelProviders.of(EventsView.class).get(CampaignCriteria.class);

		grid = new CampaignGrid(criteria);
		gridLayout = new VerticalLayout();
//		gridLayout.addComponent(createFilterBar());
//		gridLayout.addComponent(createStatusFilterBar());
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");

		addComponent(gridLayout);

		if (UserProvider.getCurrent().hasUserRight(UserRight.CAMPAIGN_EDIT)) {
			createButton = ButtonHelper.createIconButton(Captions.campaignNewCampaign, VaadinIcons.PLUS_CIRCLE,
					e -> ControllerProvider.getCampaignController().createOrEdit(null),
					ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(createButton);
		}
	}

	private Component createStatusFilterBar() {
		// TODO Auto-generated method stub
		return null;
	}

	private Component createFilterBar() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}

}
