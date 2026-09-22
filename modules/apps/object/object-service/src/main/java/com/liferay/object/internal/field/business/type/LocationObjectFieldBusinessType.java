/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.field.business.type;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.dynamic.data.mapping.form.field.type.constants.ObjectDDMFormFieldTypeConstants;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.vulcan.extension.PropertyDefinition;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Kevin Tan
 */
@Component(
	property = "object.field.business.type.key=" + ObjectFieldConstants.BUSINESS_TYPE_LOCATION,
	service = ObjectFieldBusinessType.class
)
public class LocationObjectFieldBusinessType
	extends BaseObjectFieldBusinessType {

	@Override
	public String getDBType() {
		return ObjectFieldConstants.DB_TYPE_CLOB;
	}

	@Override
	public String getDDMFormFieldTypeName() {
		return ObjectDDMFormFieldTypeConstants.LOCATION;
	}

	@Override
	public String getDescription(Locale locale) {
		return _language.get(
			locale, "addresses-and-precise-geographic-coordinates");
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "location");
	}

	@Override
	public String getName() {
		return ObjectFieldConstants.BUSINESS_TYPE_LOCATION;
	}

	@Override
	public PropertyDefinition.PropertyType getPropertyType() {
		return PropertyDefinition.PropertyType.TEXT;
	}

	@Override
	public boolean isVisible(ObjectDefinition objectDefinition) {
		return FeatureFlagManagerUtil.isEnabled(
			objectDefinition.getCompanyId(), "LPD-11388");
	}

	@Reference
	private Language _language;

}