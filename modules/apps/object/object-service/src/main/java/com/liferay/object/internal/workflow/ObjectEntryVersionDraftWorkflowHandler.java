/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.workflow;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryVersionDraft;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryVersionDraftLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.BaseWorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;

import java.io.Serializable;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(
	property = "model.class.name=com.liferay.object.model.ObjectEntryVersionDraft",
	service = WorkflowHandler.class
)
public class ObjectEntryVersionDraftWorkflowHandler
	extends BaseWorkflowHandler<ObjectEntryVersionDraft> {

	@Override
	public String getClassName() {
		return ObjectEntryVersionDraft.class.getName();
	}

	@Override
	public String getTitle(long classPK, Locale locale) {
		try {
			ObjectEntryVersionDraft objectEntryVersionDraft =
				_objectEntryVersionDraftLocalService.getObjectEntryVersionDraft(
					classPK);

			ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
				objectEntryVersionDraft.getObjectEntryId());

			return objectEntry.getTitleValue();
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}

			return StringPool.BLANK;
		}
	}

	@Override
	public String getType(Locale locale) {
		return ResourceActionsUtil.getModelResource(locale, getClassName());
	}

	@Override
	public WorkflowDefinitionLink getWorkflowDefinitionLink(
			long companyId, long groupId, long classPK)
		throws PortalException {

		ObjectEntryVersionDraft objectEntryVersionDraft =
			_objectEntryVersionDraftLocalService.getObjectEntryVersionDraft(
				classPK);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectEntryVersionDraft.getObjectDefinitionId());

		WorkflowHandler<?> workflowHandler =
			WorkflowHandlerRegistryUtil.getWorkflowHandler(
				objectDefinition.getClassName());

		if (workflowHandler == null) {
			return null;
		}

		return workflowHandler.getWorkflowDefinitionLink(
			companyId, groupId, objectEntryVersionDraft.getObjectEntryId());
	}

	@Override
	public boolean isVisible(Group group) {
		return false;
	}

	@Override
	public ObjectEntryVersionDraft updateStatus(
			int status, Map<String, Serializable> workflowContext)
		throws PortalException {

		return _objectEntryVersionDraftLocalService.updateStatus(
			GetterUtil.getLong(
				(String)workflowContext.get(WorkflowConstants.CONTEXT_USER_ID)),
			GetterUtil.getLong(
				(String)workflowContext.get(
					WorkflowConstants.CONTEXT_ENTRY_CLASS_PK)),
			status, (ServiceContext)workflowContext.get("serviceContext"));
	}

	@Override
	public ObjectEntryVersionDraft updateStatus(
			ObjectEntryVersionDraft objectEntryVersionDraft, int status,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		return _objectEntryVersionDraftLocalService.updateStatus(
			GetterUtil.getLong(
				(String)workflowContext.get(WorkflowConstants.CONTEXT_USER_ID)),
			objectEntryVersionDraft.getObjectEntryVersionDraftId(), status,
			(ServiceContext)workflowContext.get("serviceContext"));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryVersionDraftWorkflowHandler.class);

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectEntryVersionDraftLocalService
		_objectEntryVersionDraftLocalService;

}