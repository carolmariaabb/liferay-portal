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

package com.liferay.dynamic.data.mapping.internal.upgrade.v3_7_1;

import com.liferay.dynamic.data.mapping.constants.DDMFormInstanceReportConstants;
import com.liferay.dynamic.data.mapping.internal.report.CheckboxMultipleDDMFormFieldTypeReportProcessor;
import com.liferay.dynamic.data.mapping.internal.report.GridDDMFormFieldTypeReportProcessor;
import com.liferay.dynamic.data.mapping.internal.report.NumericDDMFormFieldTypeReportProcessor;
import com.liferay.dynamic.data.mapping.internal.report.RadioDDMFormFieldTypeReportProcessor;
import com.liferay.dynamic.data.mapping.internal.report.TextDDMFormFieldTypeReportProcessor;
import com.liferay.dynamic.data.mapping.model.DDMContent;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceReport;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.report.DDMFormFieldTypeReportProcessor;
import com.liferay.dynamic.data.mapping.service.DDMContentLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordVersionLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceReportLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * @author Marcos Martins
 */
public class UpgradeDDMFormInstanceReport extends UpgradeProcess {

	public UpgradeDDMFormInstanceReport(
		DDMContentLocalService ddmContentLocalService,
		DDMFormInstanceLocalService ddmFormInstanceLocalService,
		DDMFormInstanceRecordLocalService ddmFormInstanceRecordLocalService,
		DDMFormInstanceRecordVersionLocalService
			ddmFormInstanceRecordVersionLocalService,
		DDMFormInstanceReportLocalService ddmFormInstanceReportLocalService,
		DDMStructureLocalService ddmStructureLocalService) {

		_ddmContentLocalService = ddmContentLocalService;
		_ddmFormInstanceLocalService = ddmFormInstanceLocalService;
		_ddmInstanceRecordLocalService = ddmFormInstanceRecordLocalService;
		_ddmInstanceRecordVersionLocalService =
			ddmFormInstanceRecordVersionLocalService;
		_ddmInstanceReportLocalService = ddmFormInstanceReportLocalService;
		_ddmStructureLocalService = ddmStructureLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_ddmInstanceReportLocalService.deleteFormInstanceReports();

		List<DDMFormInstance> ddmFormInstances =
			_ddmFormInstanceLocalService.getDDMFormInstances(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (DDMFormInstance ddmFormInstance : ddmFormInstances) {
			DDMFormInstanceReport ddmFormInstanceReport =
				_ddmInstanceReportLocalService.addFormInstanceReport(
					ddmFormInstance.getFormInstanceId());

			JSONObject dataJSONObject = JSONFactoryUtil.createJSONObject(
				ddmFormInstanceReport.getData());

			List<DDMFormInstanceRecord> ddmFormInstanceRecords =
				_ddmInstanceRecordLocalService.getFormInstanceRecords(
					ddmFormInstance.getFormInstanceId());

			for (DDMFormInstanceRecord ddmFormInstanceRecord :
					ddmFormInstanceRecords) {

				DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion =
					_ddmInstanceRecordVersionLocalService.
						getLatestFormInstanceRecordVersion(
							ddmFormInstanceRecord.getFormInstanceRecordId(),
							WorkflowConstants.STATUS_APPROVED);

				dataJSONObject = _processFormValues(
					_getDDMFormValues(ddmFormInstanceRecordVersion),
					ddmFormInstanceRecord.getFormInstanceRecordId(),
					dataJSONObject);

				dataJSONObject.put(
					"totalItems", dataJSONObject.getInt("totalItems") + 1);
			}

			ddmFormInstanceReport.setData(dataJSONObject.toJSONString());

			_ddmInstanceReportLocalService.updateDDMFormInstanceReport(
				ddmFormInstanceReport);
		}
	}

	private DDMFormValues _getDDMFormValues(
			DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion)
		throws Exception {

		DDMFormInstance ddmFormInstance =
			_ddmFormInstanceLocalService.getFormInstance(
				ddmFormInstanceRecordVersion.getFormInstanceId());

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			ddmFormInstance.getStructureId());

		DDMForm ddmForm = _ddmStructureLocalService.getStructureDDMForm(
			ddmStructure);

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();

		DDMContent ddmContent = _ddmContentLocalService.getDDMContent(
			ddmFormInstanceRecordVersion.getStorageId());

		JSONObject dataJSONObject = JSONFactoryUtil.createJSONObject(
			ddmContent.getData());

		JSONArray fieldValuesJSONArray = dataJSONObject.getJSONArray(
			"fieldValues");

		Iterator<JSONObject> iterator = fieldValuesJSONArray.iterator();

		while (iterator.hasNext()) {
			JSONObject jsonObject = iterator.next();

			String name = jsonObject.getString("name");

			DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

			ddmFormFieldValue.setDDMFormValues(ddmFormValues);
			ddmFormFieldValue.setInstanceId(jsonObject.getString("instanceId"));
			ddmFormFieldValue.setName(name);

			DDMFormField ddmFormField = ddmFormFieldValue.getDDMFormField();

			Value value = null;

			if (ddmFormField.isLocalizable()) {
				value = new LocalizedValue();

				JSONObject valueJSONObject = jsonObject.getJSONObject("value");

				for (String key : valueJSONObject.keySet()) {
					value.addString(
						LocaleUtil.fromLanguageId(key),
						valueJSONObject.getString(key));
				}
			}
			else {
				value = new UnlocalizedValue(
					Optional.of(
						jsonObject.get("value")
					).orElse(
						StringPool.BLANK
					).toString());
			}

			ddmFormFieldValue.setValue(value);

			ddmFormFieldValues.add(ddmFormFieldValue);
		}

		ddmFormValues.setDDMFormFieldValues(ddmFormFieldValues);

		return ddmFormValues;
	}

	private JSONObject _processFormValues(
			DDMFormValues ddmFormValues, long formInstanceRecordId,
			JSONObject dataJSONObject)
		throws Exception, JSONException {

		for (DDMFormFieldValue ddmFormFieldValue :
				ddmFormValues.getDDMFormFieldValues()) {

			DDMFormFieldTypeReportProcessor ddmFormFieldTypeReportProcessor =
				_ddmFormFieldTypeReportProcessorTracker.
					getDDMFormFieldTypeReportProcessor(
						ddmFormFieldValue.getType());

			if (ddmFormFieldTypeReportProcessor != null) {
				String fieldName = ddmFormFieldValue.getName();

				JSONObject fieldJSONObject = dataJSONObject.getJSONObject(
					fieldName);

				if (fieldJSONObject == null) {
					fieldJSONObject = JSONUtil.put(
						"type", ddmFormFieldValue.getType()
					).put(
						"values", JSONFactoryUtil.createJSONObject()
					);
				}

				JSONObject processedFieldJSONObject =
					ddmFormFieldTypeReportProcessor.process(
						ddmFormFieldValue,
						JSONFactoryUtil.createJSONObject(
							fieldJSONObject.toJSONString()),
						formInstanceRecordId,
						DDMFormInstanceReportConstants.
							EVENT_ADD_RECORD_VERSION);

				dataJSONObject.put(fieldName, processedFieldJSONObject);
			}
		}

		return dataJSONObject;
	}

	private final DDMContentLocalService _ddmContentLocalService;
	private DDMFormFieldTypeReportProcessorTracker
		_ddmFormFieldTypeReportProcessorTracker =
			new DDMFormFieldTypeReportProcessorTracker();
	private final DDMFormInstanceLocalService _ddmFormInstanceLocalService;
	private final DDMFormInstanceRecordLocalService
		_ddmInstanceRecordLocalService;
	private final DDMFormInstanceRecordVersionLocalService
		_ddmInstanceRecordVersionLocalService;
	private final DDMFormInstanceReportLocalService
		_ddmInstanceReportLocalService;
	private final DDMStructureLocalService _ddmStructureLocalService;

	private class DDMFormFieldTypeReportProcessorTracker {

		public DDMFormFieldTypeReportProcessor
			getDDMFormFieldTypeReportProcessor(String type) {

			if (StringUtil.equals(type, "checkbox_multiple") ||
				StringUtil.equals(type, "select")) {

				return new CheckboxMultipleDDMFormFieldTypeReportProcessor();
			}
			else if (StringUtil.equals(type, "color") ||
					 StringUtil.equals(type, "date") ||
					 StringUtil.equals(type, "text")) {

				TextDDMFormFieldTypeReportProcessor
					textDDMFormFieldTypeReportProcessor =
						new TextDDMFormFieldTypeReportProcessor();

				textDDMFormFieldTypeReportProcessor.
					setDDMFormInstanceRecordLocalService(
						_ddmInstanceRecordLocalService);

				return textDDMFormFieldTypeReportProcessor;
			}
			else if (StringUtil.equals(type, "grid")) {
				GridDDMFormFieldTypeReportProcessor
					gridDDMFormFieldTypeReportProcessor =
						new GridDDMFormFieldTypeReportProcessor();

				gridDDMFormFieldTypeReportProcessor.
					setDDMFormInstanceRecordLocalService(
						_ddmInstanceRecordLocalService);

				return gridDDMFormFieldTypeReportProcessor;
			}
			else if (StringUtil.equals(type, "numeric")) {
				NumericDDMFormFieldTypeReportProcessor
					numericDDMFormFieldTypeReportProcessor =
						new NumericDDMFormFieldTypeReportProcessor();

				numericDDMFormFieldTypeReportProcessor.
					setDDMFormInstanceRecordLocalService(
						_ddmInstanceRecordLocalService);

				return numericDDMFormFieldTypeReportProcessor;
			}
			else if (StringUtil.equals(type, "radio")) {
				return new RadioDDMFormFieldTypeReportProcessor();
			}

			return null;
		}

	}

}