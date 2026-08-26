/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.internal.util;

import com.liferay.batch.engine.thread.local.BatchEngineThreadLocal;
import com.liferay.exportimport.kernel.empty.model.EmptyModelManagerUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Michael C. Han
 */
public class UserUtil {

	public static User getUser(
			String externalReferenceCode, long userId, String screenName,
			String emailAddress, ServiceContext serviceContext)
		throws PortalException {

		long companyId = serviceContext.getCompanyId();

		User user = _fetchUser(
			externalReferenceCode, userId, screenName, emailAddress, companyId);

		if (user != null) {
			return user;
		}

		if (Validator.isNull(externalReferenceCode) ||
			!BatchEngineThreadLocal.isBatchImportInProcess()) {

			EmptyModelManagerUtil.reportMissingReference(
				User.class.getName(),
				_getReferenceKey(userId, screenName, emailAddress),
				serviceContext.getScopeGroupId());

			return null;
		}

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			return UserLocalServiceUtil.getOrAddEmptyUser(
				externalReferenceCode, companyId, serviceContext.getUserId(),
				screenName, emailAddress);
		}
	}

	private static User _fetchUser(
		String externalReferenceCode, long userId, String screenName,
		String emailAddress, long companyId) {

		if (Validator.isNotNull(externalReferenceCode)) {
			User user = UserLocalServiceUtil.fetchUserByExternalReferenceCode(
				externalReferenceCode, companyId);

			if (user != null) {
				return user;
			}
		}

		if (userId > 0) {
			return UserLocalServiceUtil.fetchUser(userId);
		}

		if (Validator.isNotNull(emailAddress)) {
			return UserLocalServiceUtil.fetchUserByEmailAddress(
				companyId, emailAddress);
		}

		if (Validator.isNotNull(screenName)) {
			return UserLocalServiceUtil.fetchUserByScreenName(
				companyId, screenName);
		}

		return null;
	}

	private static String _getReferenceKey(
		long userId, String screenName, String emailAddress) {

		if (Validator.isNotNull(emailAddress)) {
			return emailAddress;
		}

		if (Validator.isNotNull(screenName)) {
			return screenName;
		}

		return String.valueOf(userId);
	}

}