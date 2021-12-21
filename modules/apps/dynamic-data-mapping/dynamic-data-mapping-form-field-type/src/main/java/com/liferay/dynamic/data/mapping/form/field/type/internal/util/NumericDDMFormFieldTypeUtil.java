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

package com.liferay.dynamic.data.mapping.form.field.type.internal.util;

import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.dynamic.data.mapping.util.NumericDDMFormFieldUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Carolina Barbosa
 */
public class NumericDDMFormFieldTypeUtil {

	public static Map<String, Object> getParameters(
		String dataType, DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		boolean inputMask = GetterUtil.getBoolean(
			DDMFormFieldTypeUtil.getPropertyValue(
				ddmFormField, ddmFormFieldRenderingContext, "inputMask"));
		Locale locale = ddmFormFieldRenderingContext.getLocale();

		if (!inputMask) {
			if (!StringUtil.equals(dataType, "double")) {
				return new HashMap<>();
			}

			return HashMapBuilder.<String, Object>put(
				"localizedSymbols",
				() -> {
					if (ddmFormFieldRenderingContext.isViewMode()) {
						return null;
					}

					return _getLocalizedSymbols();
				}
			).put(
				"symbols", _getSymbols(locale)
			).build();
		}

		if (!StringUtil.equals(dataType, "double")) {
			return HashMapBuilder.<String, Object>put(
				"inputMask", inputMask
			).put(
				"inputMaskFormat",
				DDMFormFieldTypeUtil.getPropertyValue(
					ddmFormField, ddmFormFieldRenderingContext, locale,
					"inputMaskFormat")
			).build();
		}

		String numericInputMask = DDMFormFieldTypeUtil.getPropertyValue(
			ddmFormField, ddmFormFieldRenderingContext, locale,
			"numericInputMask");

		return HashMapBuilder.<String, Object>put(
			"inputMask", inputMask
		).put(
			"numericInputMask", numericInputMask
		).putAll(
			_getNumericInputMaskParameters(numericInputMask)
		).build();
	}

	private static Map<String, Map<String, Object>> _getLocalizedSymbols() {
		Map<String, Map<String, Object>> localizedSymbols = new HashMap<>();

		for (Locale availableLocale : LanguageUtil.getAvailableLocales()) {
			localizedSymbols.put(
				LanguageUtil.getLanguageId(availableLocale),
				_getSymbols(availableLocale));
		}

		return localizedSymbols;
	}

	private static Map<String, Object> _getNumericInputMaskParameters(
		String numericInputMask) {

		try {
			JSONObject numericInputMaskJSONObject =
				JSONFactoryUtil.createJSONObject(numericInputMask);

			return HashMapBuilder.<String, Object>put(
				"append", numericInputMaskJSONObject.getString("append")
			).put(
				"appendType", numericInputMaskJSONObject.getString("appendType")
			).put(
				"decimalPlaces",
				numericInputMaskJSONObject.getInt("decimalPlaces")
			).put(
				"symbols", numericInputMaskJSONObject.getJSONObject("symbols")
			).build();
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException, jsonException);
			}

			return new HashMap<>();
		}
	}

	private static Map<String, Object> _getSymbols(Locale locale) {
		DecimalFormat decimalFormat = NumericDDMFormFieldUtil.getDecimalFormat(
			locale);

		DecimalFormatSymbols decimalFormatSymbols =
			decimalFormat.getDecimalFormatSymbols();

		return HashMapBuilder.<String, Object>put(
			"decimalSymbol",
			String.valueOf(decimalFormatSymbols.getDecimalSeparator())
		).put(
			"thousandsSeparator",
			String.valueOf(decimalFormatSymbols.getGroupingSeparator())
		).build();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		NumericDDMFormFieldTypeUtil.class);

}