/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.internal.type.users.provider;

import com.liferay.notification.constants.NotificationRecipientConstants;
import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.internal.type.util.NotificationTypeUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Feliphe Marinho
 */
public class DefaultUsersProvider implements UsersProvider {

	public DefaultUsersProvider(
		PermissionCheckerFactory permissionCheckerFactory,
		UserLocalService userLocalService) {

		_permissionCheckerFactory = permissionCheckerFactory;
		_userLocalService = userLocalService;
	}

	@Override
	public String getRecipientType() {
		return NotificationRecipientConstants.TYPE_USER;
	}

	@Override
	public List<User> provide(
			NotificationContext notificationContext, List<String> values)
		throws PortalException {

		Set<User> users = new HashSet<>();

		for (String value : values) {
			NotificationTypeUtil.addUser(
				notificationContext, _permissionCheckerFactory,
				_userLocalService.getUserByScreenName(
					notificationContext.getCompanyId(), value),
				users);
		}

		return new ArrayList<>(users);
	}

	private final PermissionCheckerFactory _permissionCheckerFactory;
	private final UserLocalService _userLocalService;

}