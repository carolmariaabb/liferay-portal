/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.internal.type.users.provider;

import com.liferay.notification.constants.NotificationRecipientConstants;
import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.internal.type.util.NotificationTypeUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Caio Farias
 */
public class UserGroupUsersProvider implements UsersProvider {

	public UserGroupUsersProvider(
		PermissionCheckerFactory permissionCheckerFactory,
		UserGroupLocalService userGroupLocalService,
		UserLocalService userLocalService) {

		_permissionCheckerFactory = permissionCheckerFactory;
		_userGroupLocalService = userGroupLocalService;
		_userLocalService = userLocalService;
	}

	@Override
	public String getRecipientType() {
		return NotificationRecipientConstants.TYPE_USER_GROUP;
	}

	@Override
	public List<User> provide(
			NotificationContext notificationContext, List<String> values)
		throws PortalException {

		Set<User> users = new HashSet<>();

		for (String value : values) {
			UserGroup userGroup = _userGroupLocalService.getUserGroup(
				notificationContext.getCompanyId(), value);

			for (User user :
					_userLocalService.getUserGroupUsers(
						userGroup.getUserGroupId())) {

				NotificationTypeUtil.addUser(
					notificationContext, _permissionCheckerFactory, user,
					users);
			}
		}

		return new ArrayList<>(users);
	}

	private final PermissionCheckerFactory _permissionCheckerFactory;
	private final UserGroupLocalService _userGroupLocalService;
	private final UserLocalService _userLocalService;

}