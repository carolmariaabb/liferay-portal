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

package com.liferay.dynamic.data.mapping.form.field.type.internal.radio;

import com.liferay.dynamic.data.mapping.form.field.type.BaseDDMFormFieldTypeSettingsTestCase;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutColumn;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutPage;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutRow;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.dynamic.data.mapping.util.DDMFormLayoutFactory;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Matchers;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Leonardo Barros
 */
@PrepareForTest({PortalClassLoaderUtil.class, ResourceBundleUtil.class})
@RunWith(PowerMockRunner.class)
public class RadioDDMFormFieldTypeSettingsTest
	extends BaseDDMFormFieldTypeSettingsTestCase {

	@Before
	@Override
	public void setUp() {
		setUpLanguageUtil();
		setUpPortalUtil();
		setUpResourceBundleUtil();
	}

	@Test
	public void testCreateRadioDDMFormFieldTypeSettingsDDMForm() {
		DDMForm ddmForm = DDMFormFactory.create(
			RadioDDMFormFieldTypeSettings.class);

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(false);

		DDMFormField inlineDDMFormField = ddmFormFieldsMap.get("inline");

		Assert.assertNotNull(inlineDDMFormField);

		Assert.assertNotNull(inlineDDMFormField.getLabel());
		Assert.assertEquals(
			"true", inlineDDMFormField.getProperty("showAsSwitcher"));

		DDMFormField optionsDDMFormField = ddmFormFieldsMap.get("options");

		Assert.assertNotNull(optionsDDMFormField);
		Assert.assertEquals("ddm-options", optionsDDMFormField.getDataType());
		Assert.assertNotNull(optionsDDMFormField.getLabel());
		Assert.assertEquals("options", optionsDDMFormField.getType());

		DDMFormField indexTypeDDMFormField = ddmFormFieldsMap.get("indexType");

		Assert.assertNotNull(indexTypeDDMFormField);
		Assert.assertNotNull(indexTypeDDMFormField.getLabel());
		Assert.assertEquals("radio", indexTypeDDMFormField.getType());
	}

	@Test
	public void testRadioDDMFormFieldTypeSettingsDDMFormLayout() {
		DDMFormLayout ddmFormLayout = DDMFormLayoutFactory.create(
			RadioDDMFormFieldTypeSettings.class);

		List<DDMFormLayoutPage> ddmFormLayoutPages =
			ddmFormLayout.getDDMFormLayoutPages();

		DDMFormLayoutPage ddmFormLayoutPageBasic = ddmFormLayoutPages.get(0);

		for (DDMFormLayoutRow ddmFormLayoutRow :
				ddmFormLayoutPageBasic.getDDMFormLayoutRows()) {

			for (DDMFormLayoutColumn ddmFormLayoutColumn :
					ddmFormLayoutRow.getDDMFormLayoutColumns()) {

				List<String> ddmFormFieldNamesColumnBasic =
					ddmFormLayoutColumn.getDDMFormFieldNames();

				Assert.assertEquals(
					"label", ddmFormFieldNamesColumnBasic.get(0));
				Assert.assertEquals("tip", ddmFormFieldNamesColumnBasic.get(1));
				Assert.assertEquals(
					"required", ddmFormFieldNamesColumnBasic.get(2));
				Assert.assertEquals(
					"options", ddmFormFieldNamesColumnBasic.get(3));
			}
		}

		DDMFormLayoutPage ddmFormLayoutPageAdvanced = ddmFormLayoutPages.get(1);

		for (DDMFormLayoutRow ddmFormLayoutRow :
				ddmFormLayoutPageAdvanced.getDDMFormLayoutRows()) {

			for (DDMFormLayoutColumn ddmFormLayoutColumn :
					ddmFormLayoutRow.getDDMFormLayoutColumns()) {

				List<String> ddmFormFieldNamesColumnAdvanced =
					ddmFormLayoutColumn.getDDMFormFieldNames();

				Assert.assertEquals(
					"name", ddmFormFieldNamesColumnAdvanced.get(0));
				Assert.assertEquals(
					"predefinedValue", ddmFormFieldNamesColumnAdvanced.get(1));
				Assert.assertEquals(
					"visibilityExpression",
					ddmFormFieldNamesColumnAdvanced.get(2));
				Assert.assertEquals(
					"validation", ddmFormFieldNamesColumnAdvanced.get(3));
				Assert.assertEquals(
					"fieldNamespace", ddmFormFieldNamesColumnAdvanced.get(4));
				Assert.assertEquals(
					"indexType", ddmFormFieldNamesColumnAdvanced.get(5));
				Assert.assertEquals(
					"localizable", ddmFormFieldNamesColumnAdvanced.get(6));
				Assert.assertEquals(
					"readOnly", ddmFormFieldNamesColumnAdvanced.get(7));
				Assert.assertEquals(
					"dataType", ddmFormFieldNamesColumnAdvanced.get(8));
				Assert.assertEquals(
					"type", ddmFormFieldNamesColumnAdvanced.get(9));
				Assert.assertEquals(
					"showLabel", ddmFormFieldNamesColumnAdvanced.get(10));
				Assert.assertEquals(
					"repeatable", ddmFormFieldNamesColumnAdvanced.get(11));
				Assert.assertEquals(
					"inline", ddmFormFieldNamesColumnAdvanced.get(12));
			}
		}
	}

	@Override
	protected void setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = PowerMockito.mock(Language.class);

		languageUtil.setLanguage(language);
	}

	protected void setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		Portal portal = mock(Portal.class);

		ResourceBundle resourceBundle = mock(ResourceBundle.class);

		when(
			portal.getResourceBundle(Matchers.any(Locale.class))
		).thenReturn(
			resourceBundle
		);

		portalUtil.setPortal(portal);
	}

	@Override
	protected void setUpResourceBundleUtil() {
		PowerMockito.mockStatic(ResourceBundleUtil.class);

		PowerMockito.when(
			ResourceBundleUtil.getBundle(
				Matchers.anyString(), Matchers.any(Locale.class),
				Matchers.any(ClassLoader.class))
		).thenReturn(
			ResourceBundleUtil.EMPTY_RESOURCE_BUNDLE
		);
	}

}