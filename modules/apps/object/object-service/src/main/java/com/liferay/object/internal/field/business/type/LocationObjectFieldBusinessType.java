/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.field.business.type;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.dynamic.data.mapping.form.field.type.constants.ObjectDDMFormFieldTypeConstants;
import com.liferay.object.exception.ObjectEntryValuesException;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.dto.v1_0.Coordinates;
import com.liferay.object.rest.dto.v1_0.Location;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.extension.PropertyDefinition;

import java.io.Serializable;

import java.util.Locale;
import java.util.Map;

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
	public Serializable getDTOValue(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			ObjectField objectField, Serializable serializable)
		throws Exception {

		if (serializable instanceof Location) {
			return serializable;
		}

		if (!(serializable instanceof Map)) {
			return null;
		}

		Map<String, Serializable> locationMap =
			(Map<String, Serializable>)serializable;

		Map<String, Serializable> coordinatesMap =
			(Map<String, Serializable>)locationMap.get("coordinates");

		if (MapUtil.isNotEmpty(coordinatesMap)) {
			return Location.toDTO(_jsonFactory.looseSerializeDeep(locationMap));
		}

		return new Location() {
			{
				setAddress(() -> MapUtil.getString(locationMap, "address"));
				setCoordinates(
					() -> new Coordinates() {
						{
							setLat(() -> MapUtil.getDouble(locationMap, "lat"));
							setLng(() -> MapUtil.getDouble(locationMap, "lng"));
						}
					});
			}
		};
	}

	@Override
	public String getDescription(Locale locale) {
		return _language.get(
			locale, "addresses-and-precise-geographic-coordinates");
	}

	@Override
	public Object getDisplayContextValue(
			ObjectField objectField, long userId, Map<String, Object> values)
		throws PortalException {

		return values.get(objectField.getName());
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
	public Object getValue(
			Long groupId, ObjectField objectField, long userId,
			Map<String, Object> values)
		throws PortalException {

		Object value = values.get(objectField.getName());

		if (value == null) {
			return null;
		}

		try {
			if (value instanceof Location location) {
				Coordinates coordinates = location.getCoordinates();

				return _getValue(
					location.getAddress(), coordinates.getLat(),
					coordinates.getLng());
			}
			else if (value instanceof Map) {
				Map<String, Serializable> valueMap =
					(Map<String, Serializable>)value;

				Map<String, Serializable> coordinatesMap =
					(Map<String, Serializable>)valueMap.get("coordinates");

				return _getValue(
					MapUtil.getString(valueMap, "address"),
					MapUtil.getDouble(coordinatesMap, "lat"),
					MapUtil.getDouble(coordinatesMap, "lng"));
			}
			else if (value instanceof String) {
				JSONObject jsonObject = _jsonFactory.createJSONObject(
					(String)value);

				JSONObject coordinatesJSONObject = jsonObject.getJSONObject(
					"coordinates");

				return _getValue(
					jsonObject.getString("address"),
					coordinatesJSONObject.getDouble("lat"),
					coordinatesJSONObject.getDouble("lng"));
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new ObjectEntryValuesException.InvalidValue(
				objectField.getName());
		}

		return null;
	}

	@Override
	public boolean isVisible(ObjectDefinition objectDefinition) {
		return FeatureFlagManagerUtil.isEnabled(
			objectDefinition.getCompanyId(), "LPD-11388");
	}

	private Object _getValue(String address, double lat, double lng)
		throws Exception {

		// Add validation here, convert address to lat/lng

		return HashMapBuilder.<String, Serializable>put(
			"address", address
		).put(
			"lat", lat
		).put(
			"lng", lng
		).build();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LocationObjectFieldBusinessType.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

}