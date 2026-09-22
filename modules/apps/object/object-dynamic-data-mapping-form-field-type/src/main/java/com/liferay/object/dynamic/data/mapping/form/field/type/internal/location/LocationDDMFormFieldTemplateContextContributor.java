/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.dynamic.data.mapping.form.field.type.internal.location;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.map.util.MapProviderHelperUtil;
import com.liferay.object.dynamic.data.mapping.form.field.type.constants.ObjectDDMFormFieldTypeConstants;
import com.liferay.object.dynamic.data.mapping.form.field.type.internal.BaseDDMFormFieldTemplateContextContributor;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletPreferences;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

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

		ThemeDisplay themeDisplay = _getThemeDisplay(
			ddmFormFieldRenderingContext);

		return HashMapBuilder.<String, Object>put(
			"googleMapsAPIKey", _getGoogleMapsAPIKey(themeDisplay)
		).put(
			"mapProviderKey", _getMapProviderKey(themeDisplay)
		).putAll(
			super.getParameters(ddmFormField, ddmFormFieldRenderingContext)
		).build();
	}

	private String _getGoogleMapsAPIKey(ThemeDisplay themeDisplay) {
		if (themeDisplay == null) {
			return null;
		}

		PortletPreferences companyPortletPreferences =
			PrefsPropsUtil.getPreferences(themeDisplay.getCompanyId());

		String companyGoogleMapsAPIKey = companyPortletPreferences.getValue(
			"googleMapsAPIKey", null);

		Group group = themeDisplay.getScopeGroup();

		if ((group == null) || group.isControlPanel()) {
			return companyGoogleMapsAPIKey;
		}

		return GetterUtil.getString(
			group.getTypeSettingsProperty("googleMapsAPIKey"),
			companyGoogleMapsAPIKey);
	}

	private String _getMapProviderKey(ThemeDisplay themeDisplay) {
		if (themeDisplay == null) {
			return _DEFAULT_MAP_PROVIDER_KEY;
		}

		return GetterUtil.getString(
			MapProviderHelperUtil.getMapProviderKey(
				_groupLocalService, themeDisplay.getCompanyId(),
				themeDisplay.getScopeGroupId()),
			_DEFAULT_MAP_PROVIDER_KEY);
	}

	private ThemeDisplay _getThemeDisplay(
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		HttpServletRequest httpServletRequest =
			ddmFormFieldRenderingContext.getHttpServletRequest();

		if (httpServletRequest == null) {
			return null;
		}

		return (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	private static final String _DEFAULT_MAP_PROVIDER_KEY = "OpenStreetMap";

	@Reference
	private GroupLocalService _groupLocalService;

}