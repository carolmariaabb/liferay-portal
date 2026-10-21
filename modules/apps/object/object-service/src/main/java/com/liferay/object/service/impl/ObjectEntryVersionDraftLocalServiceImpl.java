/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.impl;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.object.model.ObjectEntryVersionDraft;
import com.liferay.object.service.base.ObjectEntryVersionDraftLocalServiceBaseImpl;
import com.liferay.object.service.persistence.ObjectEntryPersistence;
import com.liferay.object.service.persistence.ObjectEntryVersionPersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "model.class.name=com.liferay.object.model.ObjectEntryVersionDraft",
	service = AopService.class
)
public class ObjectEntryVersionDraftLocalServiceImpl
	extends ObjectEntryVersionDraftLocalServiceBaseImpl {

	@Override
	public ObjectEntryVersionDraft addObjectEntryVersionDraft(
			long userId, long objectEntryId, int version)
		throws PortalException {

		ObjectEntryVersionDraft objectEntryVersionDraft =
			objectEntryVersionDraftPersistence.create(
				counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		objectEntryVersionDraft.setCompanyId(user.getCompanyId());
		objectEntryVersionDraft.setUserId(user.getUserId());
		objectEntryVersionDraft.setUserName(user.getFullName());

		ObjectEntryVersion objectEntryVersion =
			_objectEntryVersionPersistence.findByOEI_V(objectEntryId, version);

		objectEntryVersionDraft.setObjectDefinitionId(
			objectEntryVersion.getObjectDefinitionId());

		objectEntryVersionDraft.setObjectEntryId(objectEntryId);
		objectEntryVersionDraft.setContent(objectEntryVersion.getContent());
		objectEntryVersionDraft.setVersion(version);
		objectEntryVersionDraft.setStatus(WorkflowConstants.STATUS_DRAFT);

		return objectEntryVersionDraftPersistence.update(
			objectEntryVersionDraft);
	}

	@Override
	public void deleteObjectEntryVersionDraftByObjectDefinitionId(
		long objectDefinitionId) {

		objectEntryVersionDraftPersistence.removeByObjectDefinitionId(
			objectDefinitionId);
	}

	@Override
	public void deleteObjectEntryVersionDrafts(long objectEntryId) {
		objectEntryVersionDraftPersistence.removeByObjectEntryId(objectEntryId);
	}

	@Override
	public ObjectEntryVersionDraft getObjectEntryVersionDraft(
			String externalReferenceCode, long companyId,
			long objectDefinitionId)
		throws PortalException {

		return objectEntryVersionDraftPersistence.findByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId);
	}

	@Override
	public ObjectEntryVersionDraft updateObjectEntryVersionDraft(
			long userId, long objectEntryVersionDraftId, String content,
			ServiceContext serviceContext)
		throws PortalException {

		ObjectEntryVersionDraft objectEntryVersionDraft =
			objectEntryVersionDraftPersistence.findByPrimaryKey(
				objectEntryVersionDraftId);

		objectEntryVersionDraft.setModifiedDate(
			serviceContext.getModifiedDate(null));
		objectEntryVersionDraft.setContent(content);
		objectEntryVersionDraft.setStatus(WorkflowConstants.STATUS_DRAFT);

		objectEntryVersionDraft = objectEntryVersionDraftPersistence.update(
			objectEntryVersionDraft);

		if (serviceContext.getWorkflowAction() ==
				WorkflowConstants.ACTION_PUBLISH) {

			boolean workflowEnabled = WorkflowThreadLocal.isEnabled();

			try {
				WorkflowThreadLocal.setEnabled(true);

				ObjectEntry objectEntry =
					_objectEntryPersistence.findByPrimaryKey(
						objectEntryVersionDraft.getObjectEntryId());

				WorkflowHandlerRegistryUtil.startWorkflowInstance(
					objectEntryVersionDraft.getCompanyId(),
					objectEntry.getNonzeroGroupId(), userId,
					ObjectEntryVersionDraft.class.getName(),
					objectEntryVersionDraft.getObjectEntryVersionDraftId(),
					objectEntryVersionDraft, serviceContext);
			}
			finally {
				WorkflowThreadLocal.setEnabled(workflowEnabled);
			}
		}

		return objectEntryVersionDraft;
	}

	@Override
	public ObjectEntryVersionDraft updateStatus(
			long userId, long objectEntryVersionDraftId, int status,
			ServiceContext serviceContext)
		throws PortalException {

		ObjectEntryVersionDraft objectEntryVersionDraft =
			objectEntryVersionDraftPersistence.findByPrimaryKey(
				objectEntryVersionDraftId);

		if (status == WorkflowConstants.STATUS_APPROVED) {
			status = WorkflowConstants.STATUS_READY_TO_PUBLISH;
		}

		objectEntryVersionDraft.setStatus(status);

		User user = _userLocalService.getUser(userId);

		objectEntryVersionDraft.setStatusByUserId(user.getUserId());
		objectEntryVersionDraft.setStatusByUserName(user.getFullName());

		objectEntryVersionDraft.setStatusDate(
			serviceContext.getModifiedDate(null));

		return objectEntryVersionDraftPersistence.update(
			objectEntryVersionDraft);
	}

	@Reference
	private ObjectEntryPersistence _objectEntryPersistence;

	@Reference
	private ObjectEntryVersionPersistence _objectEntryVersionPersistence;

	@Reference
	private UserLocalService _userLocalService;

}