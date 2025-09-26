/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.internal.type.util;

import com.liferay.notification.context.NotificationContext;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;

import java.util.Set;

/**
 * @author Carolina Barbosa
 */
public class NotificationTypeUtil {

	public static void addUser(
		NotificationContext notificationContext,
		PermissionCheckerFactory permissionCheckerFactory, User user,
		Set<User> users) {

		if (ModelResourcePermissionUtil.contains(
				permissionCheckerFactory.create(user),
				notificationContext.getGroupId(),
				notificationContext.getClassName(),
				notificationContext.getClassPK(), ActionKeys.VIEW)) {

			users.add(user);
		}
	}

}