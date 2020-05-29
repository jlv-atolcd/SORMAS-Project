package de.symeda.sormas.backend.campaign;

import de.symeda.sormas.api.campaign.CampaignCriteria;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.*;

@Stateless
@LocalBean
public class CampaignService extends AbstractCoreAdoService<Campaign> {

	public CampaignService() {
		super(Campaign.class);
	}

	/**
	 * a user who has access to @CamnpaignView can read all campaigns
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Campaign, Campaign> from) {
		return null;
	}

	public Predicate buildCriteriaFilter(CampaignCriteria campaignCriteria, CriteriaBuilder cb, Root<Campaign> from) {
		Predicate filter = null;
		if (campaignCriteria.getDeleted() != null) {
			filter = and(cb, filter, cb.equal(from.get(Campaign.DELETED), campaignCriteria.getDeleted()));
		}
		if (campaignCriteria.getStartDateAfter() != null || campaignCriteria.getStartDateBefore() != null) {
			filter = and(cb, filter, cb.between(from.get(Campaign.START_DATE), campaignCriteria.getStartDateAfter(),
					campaignCriteria.getStartDateBefore()));
		}
		if (campaignCriteria.getEndDateAfter() != null || campaignCriteria.getEndDateBefore() != null) {
			filter = and(cb, filter, cb.between(from.get(Campaign.END_DATE), campaignCriteria.getEndDateAfter(),
					campaignCriteria.getEndDateAfter()));
		}
		return filter;
	}
}
