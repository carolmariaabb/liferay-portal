/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.object.entry.preview;

import com.liferay.launch.internal.entry.manager.ObjectEntryLaunchEntryManager;
import com.liferay.object.entry.preview.ObjectEntryPreviewVersionProvider;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.object.model.ObjectEntryVersionDraft;
import com.liferay.object.service.ObjectEntryVersionDraftLocalService;
import com.liferay.object.service.ObjectEntryVersionLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.preview.PreviewableResolverUtil;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(service = ObjectEntryPreviewVersionProvider.class)
public class ObjectEntryPreviewVersionProviderImpl
	implements ObjectEntryPreviewVersionProvider {

	@Override
	public ObjectEntryVersion fetchPreviewObjectEntryVersion(
		ObjectEntry objectEntry) {

		Map<Serializable, Serializable> previewableMap =
			PreviewableResolverUtil.getPreviewableMap(ObjectEntry.class);

		if (MapUtil.isEmpty(previewableMap)) {
			return null;
		}

		long headObjectEntryId = objectEntry.getObjectEntryId();

		if (objectEntry.getHeadObjectEntryId() > 0) {
			headObjectEntryId = objectEntry.getHeadObjectEntryId();
		}

		Serializable previewPrimaryKey = previewableMap.get(headObjectEntryId);

		if (!(previewPrimaryKey instanceof String)) {
			return null;
		}

		String previewKey = (String)previewPrimaryKey;

		if (previewKey.startsWith(
				ObjectEntryLaunchEntryManager.PREVIEW_KEY_PREFIX_DRAFT)) {

			return _toObjectEntryVersion(
				headObjectEntryId,
				_objectEntryVersionDraftLocalService.
					fetchObjectEntryVersionDraft(
						GetterUtil.getLong(
							StringUtil.removeSubstring(
								previewKey,
								ObjectEntryLaunchEntryManager.
									PREVIEW_KEY_PREFIX_DRAFT))));
		}

		if (previewKey.startsWith(
				ObjectEntryLaunchEntryManager.PREVIEW_KEY_PREFIX_VERSION)) {

			return _objectEntryVersionLocalService.fetchObjectEntryVersion(
				GetterUtil.getLong(
					StringUtil.removeSubstring(
						previewKey,
						ObjectEntryLaunchEntryManager.
							PREVIEW_KEY_PREFIX_VERSION)));
		}

		return null;
	}

	private ObjectEntryVersion _toObjectEntryVersion(
		long objectEntryId, ObjectEntryVersionDraft objectEntryVersionDraft) {

		if (objectEntryVersionDraft == null) {
			return null;
		}

		ObjectEntryVersion objectEntryVersion =
			_objectEntryVersionLocalService.createObjectEntryVersion(0);

		objectEntryVersion.setCompanyId(objectEntryVersionDraft.getCompanyId());
		objectEntryVersion.setUserId(objectEntryVersionDraft.getUserId());
		objectEntryVersion.setUserName(objectEntryVersionDraft.getUserName());
		objectEntryVersion.setCreateDate(
			objectEntryVersionDraft.getCreateDate());
		objectEntryVersion.setModifiedDate(
			objectEntryVersionDraft.getModifiedDate());
		objectEntryVersion.setObjectDefinitionId(
			objectEntryVersionDraft.getObjectDefinitionId());
		objectEntryVersion.setObjectEntryId(objectEntryId);
		objectEntryVersion.setContent(objectEntryVersionDraft.getContent());
		objectEntryVersion.setDisplayDate(
			objectEntryVersionDraft.getDisplayDate());
		objectEntryVersion.setExpirationDate(
			objectEntryVersionDraft.getExpirationDate());
		objectEntryVersion.setReviewDate(
			objectEntryVersionDraft.getReviewDate());
		objectEntryVersion.setVersion(objectEntryVersionDraft.getBaseVersion());
		objectEntryVersion.setStatus(WorkflowConstants.STATUS_DRAFT);

		return objectEntryVersion;
	}

	@Reference
	private ObjectEntryVersionDraftLocalService
		_objectEntryVersionDraftLocalService;

	@Reference
	private ObjectEntryVersionLocalService _objectEntryVersionLocalService;

}