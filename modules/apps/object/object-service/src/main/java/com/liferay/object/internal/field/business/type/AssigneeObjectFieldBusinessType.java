/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.field.business.type;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.dynamic.data.mapping.form.field.type.constants.ObjectDDMFormFieldTypeConstants;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.dto.v1_0.Assignee;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.extension.PropertyDefinition;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Igor Franca
 */
@Component(
	property = "object.field.business.type.key=" + ObjectFieldConstants.BUSINESS_TYPE_ASSIGNEE,
	service = ObjectFieldBusinessType.class
)
public class AssigneeObjectFieldBusinessType
	implements ObjectFieldBusinessType {

	@Override
	public String getDBType() {
		return ObjectFieldConstants.DB_TYPE_LONG;
	}

	@Override
	public String getDDMFormFieldTypeName() {
		return ObjectDDMFormFieldTypeConstants.ASSIGNEE;
	}

	@Override
	public String getDescription(Locale locale) {
		return LanguageUtil.get(
			ResourceBundleUtil.getModuleAndPortalResourceBundle(
				locale, getClass()),
			"assigns-the-entry-to-an-user-or-role");
	}

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(
			ResourceBundleUtil.getModuleAndPortalResourceBundle(
				locale, getClass()),
			"assignee");
	}

	@Override
	public String getName() {
		return ObjectFieldConstants.BUSINESS_TYPE_ASSIGNEE;
	}

	@Override
	public PropertyDefinition.PropertyType getPropertyType() {
		return PropertyDefinition.PropertyType.LONG;
	}

	@Override
	public Object getValue(
			Long groupId, ObjectField objectField, long userId,
			Map<String, Object> values)
		throws PortalException {

		Object value = values.get(objectField.getName());

		if (value == null) {
			return null;
		}

		if (value instanceof Assignee) {
			Assignee assignee = (Assignee)value;

			if (Assignee.Type.ROLE.equals(assignee.getType())) {
				return _getRoleAssigneeMap(
					assignee.getExternalReferenceCode(), assignee.getName());
			}
			else if (Assignee.Type.USER.equals(assignee.getType())) {
				return _getUserAssigneeMap(
					objectField.getCompanyId(),
					assignee.getExternalReferenceCode());
			}
		}
		else if (value instanceof Map) {
			if (StringUtil.equals(
					MapUtil.getString((Map<String, String>)value, "type"),
					Assignee.Type.ROLE.toString())) {

				return _getRoleAssigneeMap(
					MapUtil.getString(
						(Map<String, String>)value, "externalReferenceCode"),
					MapUtil.getString((Map<String, String>)value, "name"));
			}
			else if (StringUtil.equals(
						MapUtil.getString((Map<String, String>)value, "type"),
						Assignee.Type.USER.toString())) {

				return _getUserAssigneeMap(
					objectField.getCompanyId(),
					MapUtil.getString(
						(Map<String, String>)value, "externalReferenceCode"));
			}
		}

		return null;
	}

	@Override
	public boolean isLocalizationSupported(ObjectField objectField) {
		return false;
	}

	private Map<String, Long> _getRoleAssigneeMap(
		String externalReferenceCode, String name) {

		try {
			Role role = _roleService.getOrAddEmptyRole(
				externalReferenceCode, StringPool.BLANK, 0, name,
				RoleConstants.TYPE_REGULAR);

			return HashMapBuilder.put(
				"classNameId", _portal.getClassNameId(Role.class.getName())
			).put(
				"classPK", role.getRoleId()
			).build();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return Collections.emptyMap();
		}
	}

	private Map<String, Long> _getUserAssigneeMap(
		long companyId, String externalReferenceCode) {

		User user = _userLocalService.fetchUserByExternalReferenceCode(
			externalReferenceCode, companyId);

		if (user == null) {
			return Collections.emptyMap();
		}

		return HashMapBuilder.put(
			"classNameId", _portal.getClassNameId(User.class.getName())
		).put(
			"classPK", user.getUserId()
		).build();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssigneeObjectFieldBusinessType.class);

	@Reference
	private Portal _portal;

	@Reference
	private RoleService _roleService;

	@Reference
	private UserLocalService _userLocalService;

}