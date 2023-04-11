/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.object.internal.field.business.type;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.dynamic.data.mapping.form.field.type.constants.ObjectDDMFormFieldTypeConstants;
import com.liferay.object.exception.ObjectFieldSettingNameException;
import com.liferay.object.exception.ObjectFieldSettingValueException;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.field.render.ObjectFieldRenderingContext;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.vulcan.extension.PropertyDefinition;

import java.math.BigDecimal;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(
	property = "object.field.business.type.key=" + ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
	service = ObjectFieldBusinessType.class
)
public class AttachmentObjectFieldBusinessType
	extends BaseObjectFieldBusinessType {

	@Override
	public Set<String> getAllowedObjectFieldSettingsNames() {
		return SetUtil.fromArray(
			"showFilesInDocumentsAndMedia", "storageDLFolderPath");
	}

	@Override
	public String getDBType() {
		return ObjectFieldConstants.DB_TYPE_LONG;
	}

	@Override
	public String getDDMFormFieldTypeName() {
		return ObjectDDMFormFieldTypeConstants.ATTACHMENT;
	}

	@Override
	public String getDescription(Locale locale) {
		return _language.get(
			locale, "upload-files-or-select-from-documents-and-media");
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "attachment");
	}

	@Override
	public String getName() {
		return ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT;
	}

	@Override
	public Map<String, Object> getProperties(
		ObjectField objectField,
		ObjectFieldRenderingContext objectFieldRenderingContext) {

		Map<String, Object> properties = super.getProperties(
			objectField, objectFieldRenderingContext);

		properties.remove(
			ObjectFieldSettingConstants.NAME_SHOW_FILES_IN_DOCS_AND_MEDIA);
		properties.remove(
			ObjectFieldSettingConstants.NAME_STORAGE_DL_FOLDER_PATH);

		return HashMapBuilder.<String, Object>put(
			"objectFieldId", objectField.getObjectFieldId()
		).put(
			"portletId", objectFieldRenderingContext.getPortletId()
		).putAll(
			properties
		).build();
	}

	@Override
	public PropertyDefinition.PropertyType getPropertyType() {
		return PropertyDefinition.PropertyType.LONG;
	}

	@Override
	public Set<String> getRequiredObjectFieldSettingsNames(
		ObjectField objectField) {

		return SetUtil.fromArray(
			"acceptedFileExtensions", "fileSource", "maximumFileSize");
	}

	@Override
	public void validateObjectFieldSettings(
			ObjectField objectField,
			List<ObjectFieldSetting> objectFieldSettings)
		throws PortalException {

		super.validateObjectFieldSettings(objectField, objectFieldSettings);

		Map<String, String> objectFieldSettingsValues =
			getObjectFieldSettingsValues(objectFieldSettings);

		String fileSource = objectFieldSettingsValues.get(
			ObjectFieldSettingConstants.NAME_FILE_SOURCE);

		if (Objects.equals(
				fileSource, ObjectFieldSettingConstants.VALUE_DOCS_AND_MEDIA)) {

			Set<String> notAllowedObjectFieldSettingsNames = new HashSet<>();

			if (objectFieldSettingsValues.containsKey(
					ObjectFieldSettingConstants.
						NAME_SHOW_FILES_IN_DOCS_AND_MEDIA)) {

				notAllowedObjectFieldSettingsNames.add(
					ObjectFieldSettingConstants.
						NAME_SHOW_FILES_IN_DOCS_AND_MEDIA);
			}

			if (objectFieldSettingsValues.containsKey(
					ObjectFieldSettingConstants.NAME_STORAGE_DL_FOLDER_PATH)) {

				notAllowedObjectFieldSettingsNames.add(
					ObjectFieldSettingConstants.NAME_STORAGE_DL_FOLDER_PATH);
			}

			if (!notAllowedObjectFieldSettingsNames.isEmpty()) {
				throw new ObjectFieldSettingNameException.NotAllowedNames(
					objectField.getName(), notAllowedObjectFieldSettingsNames);
			}
		}
		else if (Objects.equals(
					fileSource,
					ObjectFieldSettingConstants.VALUE_USER_COMPUTER)) {

			validateRelatedObjectFieldSettings(
				objectField,
				ObjectFieldSettingConstants.NAME_SHOW_FILES_IN_DOCS_AND_MEDIA,
				ObjectFieldSettingConstants.NAME_STORAGE_DL_FOLDER_PATH,
				objectFieldSettingsValues);
		}
		else {
			throw new ObjectFieldSettingValueException.InvalidValue(
				objectField.getName(),
				ObjectFieldSettingConstants.NAME_FILE_SOURCE, fileSource);
		}

		String maximumFileSizeString = objectFieldSettingsValues.get(
			ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE);

		try {
			BigDecimal maximumFileSize = new BigDecimal(maximumFileSizeString);

			if (maximumFileSize.signum() == -1) {
				throw new ObjectFieldSettingValueException.InvalidValue(
					objectField.getName(),
					ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE,
					maximumFileSizeString);
			}
		}
		catch (NumberFormatException numberFormatException) {
			throw new ObjectFieldSettingValueException.InvalidValue(
				objectField.getName(),
				ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE,
				maximumFileSizeString, numberFormatException);
		}
	}

	@Reference
	private Language _language;

}