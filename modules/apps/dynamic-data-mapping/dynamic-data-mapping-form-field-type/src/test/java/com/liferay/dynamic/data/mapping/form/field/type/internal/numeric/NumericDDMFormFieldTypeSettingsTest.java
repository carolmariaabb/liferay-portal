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

package com.liferay.dynamic.data.mapping.form.field.type.internal.numeric;

import com.liferay.dynamic.data.mapping.form.field.type.BaseDDMFormFieldTypeSettingsTestCase;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutColumn;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutPage;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutRow;
import com.liferay.dynamic.data.mapping.model.DDMFormRule;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
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
public class NumericDDMFormFieldTypeSettingsTest
	extends BaseDDMFormFieldTypeSettingsTestCase {

	@Before
	@Override
	public void setUp() {
		setUpLanguageUtil();
		setUpPortalUtil();
		setUpResourceBundleUtil();
	}

	@Test
	public void testCreateNumericDDMFormFieldTypeSettingsDDMForm() {
		DDMForm ddmForm = DDMFormFactory.create(
			NumericDDMFormFieldTypeSettings.class);

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(false);

		DDMFormField dataTypeDDMFormField = ddmFormFieldsMap.get("dataType");

		Assert.assertNotNull(dataTypeDDMFormField);
		Assert.assertNotNull(dataTypeDDMFormField.getLabel());
		Assert.assertEquals("radio", dataTypeDDMFormField.getType());

		LocalizedValue predefinedValue =
			dataTypeDDMFormField.getPredefinedValue();

		Assert.assertEquals(
			"integer",
			predefinedValue.getString(predefinedValue.getDefaultLocale()));

		DDMFormField placeholderDDMFormField = ddmFormFieldsMap.get(
			"placeholder");

		Assert.assertNotNull(placeholderDDMFormField);
		Assert.assertEquals("string", placeholderDDMFormField.getDataType());
		Assert.assertEquals("text", placeholderDDMFormField.getType());

		DDMFormField tooltipDDMFormField = ddmFormFieldsMap.get("tooltip");

		Assert.assertNotNull(tooltipDDMFormField);

		DDMFormField validationDDMFormField = ddmFormFieldsMap.get(
			"validation");

		Assert.assertNotNull(validationDDMFormField);
		Assert.assertEquals("numeric", validationDDMFormField.getDataType());

		DDMFormField indexTypeDDMFormField = ddmFormFieldsMap.get("indexType");

		Assert.assertNotNull(indexTypeDDMFormField);
		Assert.assertNotNull(indexTypeDDMFormField.getLabel());
		Assert.assertEquals("radio", indexTypeDDMFormField.getType());

		List<DDMFormRule> ddmFormRules = ddmForm.getDDMFormRules();

		Assert.assertEquals(ddmFormRules.toString(), 1, ddmFormRules.size());

		DDMFormRule ddmFormRule0 = ddmFormRules.get(0);

		Assert.assertEquals("TRUE", ddmFormRule0.getCondition());

		List<String> actions = ddmFormRule0.getActions();

		Assert.assertEquals(actions.toString(), 4, actions.size());

		Assert.assertTrue(
			actions.toString(),
			actions.contains(
				"setDataType('predefinedValue', getValue('dataType'))"));
		Assert.assertTrue(
			actions.toString(),
			actions.contains("setVisible('tooltip', false)"));
		Assert.assertTrue(
			actions.toString(),
			actions.contains(
				"setValidationDataType('validation', getValue('dataType'))"));
		Assert.assertTrue(
			actions.toString(),
			actions.contains(
				"setValidationFieldName('validation', getValue('name'))"));
	}

	@Test
	public void testNumericDDMFormFieldTypeSettingsDDMFormLayout() {
		DDMFormLayout ddmFormLayout = DDMFormLayoutFactory.create(
			NumericDDMFormFieldTypeSettings.class);

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
				Assert.assertEquals(
					"placeholder", ddmFormFieldNamesColumnBasic.get(1));
				Assert.assertEquals("tip", ddmFormFieldNamesColumnBasic.get(2));
				Assert.assertEquals(
					"dataType", ddmFormFieldNamesColumnBasic.get(3));
				Assert.assertEquals(
					"required", ddmFormFieldNamesColumnBasic.get(4));
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
					"fieldNamespace", ddmFormFieldNamesColumnAdvanced.get(3));
				Assert.assertEquals(
					"indexType", ddmFormFieldNamesColumnAdvanced.get(4));
				Assert.assertEquals(
					"localizable", ddmFormFieldNamesColumnAdvanced.get(5));
				Assert.assertEquals(
					"readOnly", ddmFormFieldNamesColumnAdvanced.get(6));
				Assert.assertEquals(
					"type", ddmFormFieldNamesColumnAdvanced.get(7));
				Assert.assertEquals(
					"showLabel", ddmFormFieldNamesColumnAdvanced.get(8));
				Assert.assertEquals(
					"repeatable", ddmFormFieldNamesColumnAdvanced.get(9));
				Assert.assertEquals(
					"validation", ddmFormFieldNamesColumnAdvanced.get(10));
				Assert.assertEquals(
					"tooltip", ddmFormFieldNamesColumnAdvanced.get(11));
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