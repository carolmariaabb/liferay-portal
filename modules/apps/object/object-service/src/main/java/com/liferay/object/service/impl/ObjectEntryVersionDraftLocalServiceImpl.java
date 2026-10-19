/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.impl;

import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.object.model.ObjectEntryVersionDraft;
import com.liferay.object.service.ObjectEntryVersionLocalService;
import com.liferay.object.service.base.ObjectEntryVersionDraftLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(
	property = "model.class.name=com.liferay.object.model.ObjectEntryVersionDraft",
	service = AopService.class
)
public class ObjectEntryVersionDraftLocalServiceImpl
	extends ObjectEntryVersionDraftLocalServiceBaseImpl {

	@Override
	public ObjectEntryVersionDraft addObjectEntryVersionDraft(
			long userId, long objectEntryId, int baseVersion)
		throws PortalException {

		ObjectEntryVersion objectEntryVersion =
			_objectEntryVersionLocalService.getObjectEntryVersion(
				objectEntryId, baseVersion);

		User user = _userLocalService.getUser(userId);

		ObjectEntryVersionDraft objectEntryVersionDraft =
			objectEntryVersionDraftPersistence.create(
				counterLocalService.increment());

		objectEntryVersionDraft.setCompanyId(objectEntryVersion.getCompanyId());
		objectEntryVersionDraft.setUserId(user.getUserId());
		objectEntryVersionDraft.setUserName(user.getFullName());
		objectEntryVersionDraft.setObjectDefinitionId(
			objectEntryVersion.getObjectDefinitionId());
		objectEntryVersionDraft.setObjectEntryId(objectEntryId);
		objectEntryVersionDraft.setBaseVersion(baseVersion);
		objectEntryVersionDraft.setContent(objectEntryVersion.getContent());
		objectEntryVersionDraft.setDisplayDate(
			objectEntryVersion.getDisplayDate());
		objectEntryVersionDraft.setExpirationDate(
			objectEntryVersion.getExpirationDate());
		objectEntryVersionDraft.setReviewDate(
			objectEntryVersion.getReviewDate());

		return objectEntryVersionDraftPersistence.update(
			objectEntryVersionDraft);
	}

	@Override
	public void deleteObjectEntryVersionDraftsByObjectDefinitionId(
		long objectDefinitionId) {

		objectEntryVersionDraftPersistence.removeByObjectDefinitionId(
			objectDefinitionId);
	}

	@Override
	public void deleteObjectEntryVersionDraftsByObjectEntryId(
		long objectEntryId) {

		objectEntryVersionDraftPersistence.removeByObjectEntryId(objectEntryId);
	}

	@Override
	public List<ObjectEntryVersionDraft> getObjectEntryVersionDrafts(
		long objectEntryId) {

		return objectEntryVersionDraftPersistence.findByObjectEntryId(
			objectEntryId);
	}

	@Override
	public ObjectEntryVersionDraft updateObjectEntryVersionDraft(
			long userId, long objectEntryVersionDraftId, String content)
		throws PortalException {

		ObjectEntryVersionDraft objectEntryVersionDraft =
			objectEntryVersionDraftPersistence.findByPrimaryKey(
				objectEntryVersionDraftId);

		User user = _userLocalService.getUser(userId);

		objectEntryVersionDraft.setUserId(user.getUserId());
		objectEntryVersionDraft.setUserName(user.getFullName());

		objectEntryVersionDraft.setModifiedDate(new Date());
		objectEntryVersionDraft.setContent(content);

		return objectEntryVersionDraftPersistence.update(
			objectEntryVersionDraft);
	}

	@Reference
	private ObjectEntryVersionLocalService _objectEntryVersionLocalService;

	@Reference
	private UserLocalService _userLocalService;

}