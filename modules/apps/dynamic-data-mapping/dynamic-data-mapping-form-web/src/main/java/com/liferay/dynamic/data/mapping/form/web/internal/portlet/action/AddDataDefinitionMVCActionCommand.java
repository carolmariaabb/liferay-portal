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

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action;

import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.dto.v2_0.DataLayout;
import com.liferay.data.engine.rest.resource.exception.DataDefinitionValidationException;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormContextDeserializer;
import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormContextDeserializerRequest;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceSettings;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN,
		"mvc.command.name=/admin/add_data_definition"
	},
	service = MVCActionCommand.class
)
public class AddDataDefinitionMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	public boolean processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {

		try {
			return super.processAction(actionRequest, actionResponse);
		}
		catch (PortletException portletException) {
			if (portletException.getCause() instanceof
					DataDefinitionValidationException) {

				DataDefinitionValidationException
					dataDefinitionValidationException =
						(DataDefinitionValidationException)
							portletException.getCause();

				SessionErrors.add(
					actionRequest,
					dataDefinitionValidationException.getClass());
			}
			else {
				throw portletException;
			}
		}

		return false;
	}

	protected DDMFormInstance addFormInstance(
			PortletRequest portletRequest, long ddmStructureId)
		throws Exception {

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DDMFormInstance.class.getName(), portletRequest);

		DDMStructure ddmStructure = ddmStructureLocalService.fetchDDMStructure(
			ddmStructureId);

		DDMFormValues settingsDDMFormValues = getSettingsDDMFormValues(
			portletRequest);

		return ddmFormInstanceLocalService.addFormInstance(
			ddmStructure.getUserId(), ddmStructure.getGroupId(),
			ddmStructure.getStructureId(), ddmStructure.getNameMap(),
			ddmStructure.getDescriptionMap(), settingsDDMFormValues,
			serviceContext);
	}

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long groupId = ParamUtil.getLong(actionRequest, "groupId");

		DataDefinitionResource dataDefinitionResource =
			DataDefinitionResource.builder(
			).user(
				themeDisplay.getUser()
			).build();

		DataDefinition dataDefinition = DataDefinition.toDTO(
			ParamUtil.getString(actionRequest, "dataDefinition"));

		dataDefinition.setDefaultDataLayout(
			DataLayout.toDTO(ParamUtil.getString(actionRequest, "dataLayout")));

		try {
			DataDefinition newDataDefinition =
				dataDefinitionResource.postSiteDataDefinitionByContentType(
					groupId, "forms", dataDefinition);

			DDMFormInstance ddmFormInstance = addFormInstance(
				actionRequest, newDataDefinition.getId());

			LiferayPortletURL portletURL = PortletURLFactoryUtil.create(
				actionRequest, themeDisplay.getPpid(),
				PortletRequest.RENDER_PHASE);

			portletURL.setParameter(
				"formInstanceId",
				String.valueOf(ddmFormInstance.getFormInstanceId()));

			actionRequest.setAttribute(WebKeys.REDIRECT, portletURL.toString());
		}
		catch (DataDefinitionValidationException
					dataDefinitionValidationException) {

			SessionErrors.add(
				actionRequest, dataDefinitionValidationException.getClass());
		}
	}

	protected DDMFormValues getSettingsDDMFormValues(
			PortletRequest portletRequest)
		throws PortalException {

		String settingsContext = ParamUtil.getString(
			portletRequest, "serializedSettingsContext");

		return ddmFormTemplateContextToDDMFormValues.deserialize(
			DDMFormContextDeserializerRequest.with(
				DDMFormFactory.create(DDMFormInstanceSettings.class),
				settingsContext));
	}

	@Reference
	protected DDMFormInstanceLocalService ddmFormInstanceLocalService;

	@Reference(
		target = "(dynamic.data.mapping.form.builder.context.deserializer.type=formValues)"
	)
	protected DDMFormContextDeserializer<DDMFormValues>
		ddmFormTemplateContextToDDMFormValues;

	@Reference
	protected DDMStructureLocalService ddmStructureLocalService;

}