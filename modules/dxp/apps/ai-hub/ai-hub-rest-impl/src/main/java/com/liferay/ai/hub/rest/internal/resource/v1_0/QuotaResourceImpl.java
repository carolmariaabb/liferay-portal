/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.internal.resource.v1_0;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryService;
import com.liferay.ai.hub.quota.util.QuotaUtil;
import com.liferay.ai.hub.rest.resource.v1_0.QuotaResource;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Carolina Barbosa
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/quota.properties",
	scope = ServiceScope.PROTOTYPE, service = QuotaResource.class
)
public class QuotaResourceImpl extends BaseQuotaResourceImpl {

	@Override
	public void putAccountAccountEntryQuotaRefreshMonthly(Long accountEntryId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-62272")) {

			throw new UnsupportedOperationException();
		}

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (!permissionChecker.isCompanyAdmin() &&
			!permissionChecker.isOmniadmin()) {

			throw new NotAuthorizedException(Response.Status.UNAUTHORIZED);
		}

		AccountEntry accountEntry = _accountEntryService.getAccountEntry(
			accountEntryId);

		QuotaUtil.refreshUsage(
			contextCompany.getCompanyId(),
			"guest-quota-" + accountEntry.getAccountEntryId(),
			contextUser.getUserId());
		QuotaUtil.refreshUsage(
			contextCompany.getCompanyId(),
			"quota-" + accountEntry.getAccountEntryId(),
			contextUser.getUserId());
	}

	@Reference
	private AccountEntryService _accountEntryService;

}