/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.dynamic.data.mapping.form.field.type.internal.location;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.map.util.MapProviderHelperUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.dynamic.data.mapping.form.field.type.constants.ObjectDDMFormFieldTypeConstants;
import com.liferay.object.dynamic.data.mapping.form.field.type.internal.BaseDDMFormFieldTemplateContextContributor;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PrefsPropsUtil;

import jakarta.portlet.PortletPreferences;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Kevin Tan
 */
@Component(
	property = "ddm.form.field.type.name=" + ObjectDDMFormFieldTypeConstants.LOCATION,
	service = DDMFormFieldTemplateContextContributor.class
)
public class LocationDDMFormFieldTemplateContextContributor
	extends BaseDDMFormFieldTemplateContextContributor {

	@Override
	public Map<String, Object> getParameters(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				GetterUtil.getLong(
					ddmFormField.getProperty("objectDefinitionId")));

		long groupId = _getGroupId(ddmFormField, objectDefinition);

		return HashMapBuilder.<String, Object>put(
			"googleMapsAPIKey",
			() -> {
				PortletPreferences companyPortletPreferences =
					PrefsPropsUtil.getPreferences(
						objectDefinition.getCompanyId());

				String companyGoogleMapsAPIKey =
					companyPortletPreferences.getValue(
						"googleMapsAPIKey", null);

				Group group = _groupLocalService.fetchGroup(groupId);

				if ((group == null) || group.isControlPanel()) {
					return companyGoogleMapsAPIKey;
				}

				return GetterUtil.getString(
					group.getTypeSettingsProperty("googleMapsAPIKey"),
					companyGoogleMapsAPIKey);
			}
		).put(
			"mapProviderKey",
			GetterUtil.getString(
				MapProviderHelperUtil.getMapProviderKey(
					_groupLocalService, objectDefinition.getCompanyId(),
					groupId),
				"OpenStreetMap")
		).putAll(
			super.getParameters(ddmFormField, ddmFormFieldRenderingContext)
		).build();
	}

	private long _getGroupId(
		DDMFormField ddmFormField, ObjectDefinition objectDefinition) {

		if (Objects.equals(
				objectDefinition.getScope(),
				ObjectDefinitionConstants.SCOPE_DEPOT)) {

			return 0;
		}

		return GetterUtil.getLong(ddmFormField.getProperty("groupId"));
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}