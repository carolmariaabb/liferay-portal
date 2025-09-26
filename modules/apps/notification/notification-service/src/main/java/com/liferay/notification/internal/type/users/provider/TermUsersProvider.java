/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.internal.type.users.provider;

import com.liferay.notification.constants.NotificationRecipientConstants;
import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.internal.type.util.NotificationTypeUtil;
import com.liferay.notification.term.evaluator.NotificationTermEvaluator;
import com.liferay.notification.term.evaluator.NotificationTermEvaluatorTracker;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Feliphe Marinho
 */
public class TermUsersProvider implements UsersProvider {

	public TermUsersProvider(
		PermissionCheckerFactory permissionCheckerFactory,
		NotificationTermEvaluatorTracker notificationTermEvaluatorTracker,
		UserLocalService userLocalService) {

		_permissionCheckerFactory = permissionCheckerFactory;
		_notificationTermEvaluatorTracker = notificationTermEvaluatorTracker;
		_userLocalService = userLocalService;
	}

	@Override
	public String getRecipientType() {
		return NotificationRecipientConstants.TYPE_TERM;
	}

	@Override
	public List<User> provide(
			NotificationContext notificationContext, List<String> values)
		throws PortalException {

		Set<User> users = new HashSet<>();

		for (String value : values) {
			Matcher matcher = _pattern.matcher(value);

			if (!matcher.find()) {
				NotificationTypeUtil.addUser(
					notificationContext, _permissionCheckerFactory,
					_userLocalService.getUserByScreenName(
						notificationContext.getCompanyId(), value),
					users);

				continue;
			}

			for (NotificationTermEvaluator notificationTermEvaluator :
					_notificationTermEvaluatorTracker.
						getNotificationTermEvaluators(
							notificationContext.getClassName())) {

				String termValue = notificationTermEvaluator.evaluate(
					NotificationTermEvaluator.Context.RECIPIENT,
					notificationContext.getTermValues(), value);

				if (Objects.equals(value, termValue)) {
					continue;
				}

				NotificationTypeUtil.addUser(
					notificationContext, _permissionCheckerFactory,
					_userLocalService.getUser(GetterUtil.getLong(termValue)),
					users);
			}
		}

		return new ArrayList<>(users);
	}

	private static final Pattern _pattern = Pattern.compile(
		"\\[%[^\\[%]+%\\]", Pattern.CASE_INSENSITIVE);

	private final NotificationTermEvaluatorTracker
		_notificationTermEvaluatorTracker;
	private final PermissionCheckerFactory _permissionCheckerFactory;
	private final UserLocalService _userLocalService;

}