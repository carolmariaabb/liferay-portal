/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link ObjectEntryVersionDraft}.
 * </p>
 *
 * @author Marco Leo
 * @see ObjectEntryVersionDraft
 * @generated
 */
public class ObjectEntryVersionDraftWrapper
	extends BaseModelWrapper<ObjectEntryVersionDraft>
	implements ModelWrapper<ObjectEntryVersionDraft>, ObjectEntryVersionDraft {

	public ObjectEntryVersionDraftWrapper(
		ObjectEntryVersionDraft objectEntryVersionDraft) {

		super(objectEntryVersionDraft);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put(
			"objectEntryVersionDraftId", getObjectEntryVersionDraftId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("objectDefinitionId", getObjectDefinitionId());
		attributes.put("objectEntryId", getObjectEntryId());
		attributes.put("content", getContent());
		attributes.put("version", getVersion());
		attributes.put("status", getStatus());
		attributes.put("statusByUserId", getStatusByUserId());
		attributes.put("statusByUserName", getStatusByUserName());
		attributes.put("statusDate", getStatusDate());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		String externalReferenceCode = (String)attributes.get(
			"externalReferenceCode");

		if (externalReferenceCode != null) {
			setExternalReferenceCode(externalReferenceCode);
		}

		Long objectEntryVersionDraftId = (Long)attributes.get(
			"objectEntryVersionDraftId");

		if (objectEntryVersionDraftId != null) {
			setObjectEntryVersionDraftId(objectEntryVersionDraftId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long userId = (Long)attributes.get("userId");

		if (userId != null) {
			setUserId(userId);
		}

		String userName = (String)attributes.get("userName");

		if (userName != null) {
			setUserName(userName);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		Long objectDefinitionId = (Long)attributes.get("objectDefinitionId");

		if (objectDefinitionId != null) {
			setObjectDefinitionId(objectDefinitionId);
		}

		Long objectEntryId = (Long)attributes.get("objectEntryId");

		if (objectEntryId != null) {
			setObjectEntryId(objectEntryId);
		}

		String content = (String)attributes.get("content");

		if (content != null) {
			setContent(content);
		}

		Integer version = (Integer)attributes.get("version");

		if (version != null) {
			setVersion(version);
		}

		Integer status = (Integer)attributes.get("status");

		if (status != null) {
			setStatus(status);
		}

		Long statusByUserId = (Long)attributes.get("statusByUserId");

		if (statusByUserId != null) {
			setStatusByUserId(statusByUserId);
		}

		String statusByUserName = (String)attributes.get("statusByUserName");

		if (statusByUserName != null) {
			setStatusByUserName(statusByUserName);
		}

		Date statusDate = (Date)attributes.get("statusDate");

		if (statusDate != null) {
			setStatusDate(statusDate);
		}
	}

	@Override
	public ObjectEntryVersionDraft cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this object entry version draft.
	 *
	 * @return the company ID of this object entry version draft
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the content of this object entry version draft.
	 *
	 * @return the content of this object entry version draft
	 */
	@Override
	public String getContent() {
		return model.getContent();
	}

	/**
	 * Returns the create date of this object entry version draft.
	 *
	 * @return the create date of this object entry version draft
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the external reference code of this object entry version draft.
	 *
	 * @return the external reference code of this object entry version draft
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the modified date of this object entry version draft.
	 *
	 * @return the modified date of this object entry version draft
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this object entry version draft.
	 *
	 * @return the mvcc version of this object entry version draft
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the object definition ID of this object entry version draft.
	 *
	 * @return the object definition ID of this object entry version draft
	 */
	@Override
	public long getObjectDefinitionId() {
		return model.getObjectDefinitionId();
	}

	/**
	 * Returns the object entry ID of this object entry version draft.
	 *
	 * @return the object entry ID of this object entry version draft
	 */
	@Override
	public long getObjectEntryId() {
		return model.getObjectEntryId();
	}

	/**
	 * Returns the object entry version draft ID of this object entry version draft.
	 *
	 * @return the object entry version draft ID of this object entry version draft
	 */
	@Override
	public long getObjectEntryVersionDraftId() {
		return model.getObjectEntryVersionDraftId();
	}

	/**
	 * Returns the primary key of this object entry version draft.
	 *
	 * @return the primary key of this object entry version draft
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the status of this object entry version draft.
	 *
	 * @return the status of this object entry version draft
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the status by user ID of this object entry version draft.
	 *
	 * @return the status by user ID of this object entry version draft
	 */
	@Override
	public long getStatusByUserId() {
		return model.getStatusByUserId();
	}

	/**
	 * Returns the status by user name of this object entry version draft.
	 *
	 * @return the status by user name of this object entry version draft
	 */
	@Override
	public String getStatusByUserName() {
		return model.getStatusByUserName();
	}

	/**
	 * Returns the status by user uuid of this object entry version draft.
	 *
	 * @return the status by user uuid of this object entry version draft
	 */
	@Override
	public String getStatusByUserUuid() {
		return model.getStatusByUserUuid();
	}

	/**
	 * Returns the status date of this object entry version draft.
	 *
	 * @return the status date of this object entry version draft
	 */
	@Override
	public Date getStatusDate() {
		return model.getStatusDate();
	}

	/**
	 * Returns the user ID of this object entry version draft.
	 *
	 * @return the user ID of this object entry version draft
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this object entry version draft.
	 *
	 * @return the user name of this object entry version draft
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this object entry version draft.
	 *
	 * @return the user uuid of this object entry version draft
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the version of this object entry version draft.
	 *
	 * @return the version of this object entry version draft
	 */
	@Override
	public int getVersion() {
		return model.getVersion();
	}

	/**
	 * Returns <code>true</code> if this object entry version draft is approved.
	 *
	 * @return <code>true</code> if this object entry version draft is approved; <code>false</code> otherwise
	 */
	@Override
	public boolean isApproved() {
		return model.isApproved();
	}

	/**
	 * Returns <code>true</code> if this object entry version draft is denied.
	 *
	 * @return <code>true</code> if this object entry version draft is denied; <code>false</code> otherwise
	 */
	@Override
	public boolean isDenied() {
		return model.isDenied();
	}

	/**
	 * Returns <code>true</code> if this object entry version draft is a draft.
	 *
	 * @return <code>true</code> if this object entry version draft is a draft; <code>false</code> otherwise
	 */
	@Override
	public boolean isDraft() {
		return model.isDraft();
	}

	/**
	 * Returns <code>true</code> if this object entry version draft is expired.
	 *
	 * @return <code>true</code> if this object entry version draft is expired; <code>false</code> otherwise
	 */
	@Override
	public boolean isExpired() {
		return model.isExpired();
	}

	/**
	 * Returns <code>true</code> if this object entry version draft is inactive.
	 *
	 * @return <code>true</code> if this object entry version draft is inactive; <code>false</code> otherwise
	 */
	@Override
	public boolean isInactive() {
		return model.isInactive();
	}

	/**
	 * Returns <code>true</code> if this object entry version draft is incomplete.
	 *
	 * @return <code>true</code> if this object entry version draft is incomplete; <code>false</code> otherwise
	 */
	@Override
	public boolean isIncomplete() {
		return model.isIncomplete();
	}

	/**
	 * Returns <code>true</code> if this object entry version draft is pending.
	 *
	 * @return <code>true</code> if this object entry version draft is pending; <code>false</code> otherwise
	 */
	@Override
	public boolean isPending() {
		return model.isPending();
	}

	/**
	 * Returns <code>true</code> if this object entry version draft is scheduled.
	 *
	 * @return <code>true</code> if this object entry version draft is scheduled; <code>false</code> otherwise
	 */
	@Override
	public boolean isScheduled() {
		return model.isScheduled();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the company ID of this object entry version draft.
	 *
	 * @param companyId the company ID of this object entry version draft
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the content of this object entry version draft.
	 *
	 * @param content the content of this object entry version draft
	 */
	@Override
	public void setContent(String content) {
		model.setContent(content);
	}

	/**
	 * Sets the create date of this object entry version draft.
	 *
	 * @param createDate the create date of this object entry version draft
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the external reference code of this object entry version draft.
	 *
	 * @param externalReferenceCode the external reference code of this object entry version draft
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the modified date of this object entry version draft.
	 *
	 * @param modifiedDate the modified date of this object entry version draft
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this object entry version draft.
	 *
	 * @param mvccVersion the mvcc version of this object entry version draft
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the object definition ID of this object entry version draft.
	 *
	 * @param objectDefinitionId the object definition ID of this object entry version draft
	 */
	@Override
	public void setObjectDefinitionId(long objectDefinitionId) {
		model.setObjectDefinitionId(objectDefinitionId);
	}

	/**
	 * Sets the object entry ID of this object entry version draft.
	 *
	 * @param objectEntryId the object entry ID of this object entry version draft
	 */
	@Override
	public void setObjectEntryId(long objectEntryId) {
		model.setObjectEntryId(objectEntryId);
	}

	/**
	 * Sets the object entry version draft ID of this object entry version draft.
	 *
	 * @param objectEntryVersionDraftId the object entry version draft ID of this object entry version draft
	 */
	@Override
	public void setObjectEntryVersionDraftId(long objectEntryVersionDraftId) {
		model.setObjectEntryVersionDraftId(objectEntryVersionDraftId);
	}

	/**
	 * Sets the primary key of this object entry version draft.
	 *
	 * @param primaryKey the primary key of this object entry version draft
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the status of this object entry version draft.
	 *
	 * @param status the status of this object entry version draft
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the status by user ID of this object entry version draft.
	 *
	 * @param statusByUserId the status by user ID of this object entry version draft
	 */
	@Override
	public void setStatusByUserId(long statusByUserId) {
		model.setStatusByUserId(statusByUserId);
	}

	/**
	 * Sets the status by user name of this object entry version draft.
	 *
	 * @param statusByUserName the status by user name of this object entry version draft
	 */
	@Override
	public void setStatusByUserName(String statusByUserName) {
		model.setStatusByUserName(statusByUserName);
	}

	/**
	 * Sets the status by user uuid of this object entry version draft.
	 *
	 * @param statusByUserUuid the status by user uuid of this object entry version draft
	 */
	@Override
	public void setStatusByUserUuid(String statusByUserUuid) {
		model.setStatusByUserUuid(statusByUserUuid);
	}

	/**
	 * Sets the status date of this object entry version draft.
	 *
	 * @param statusDate the status date of this object entry version draft
	 */
	@Override
	public void setStatusDate(Date statusDate) {
		model.setStatusDate(statusDate);
	}

	/**
	 * Sets the user ID of this object entry version draft.
	 *
	 * @param userId the user ID of this object entry version draft
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this object entry version draft.
	 *
	 * @param userName the user name of this object entry version draft
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this object entry version draft.
	 *
	 * @param userUuid the user uuid of this object entry version draft
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the version of this object entry version draft.
	 *
	 * @param version the version of this object entry version draft
	 */
	@Override
	public void setVersion(int version) {
		model.setVersion(version);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected ObjectEntryVersionDraftWrapper wrap(
		ObjectEntryVersionDraft objectEntryVersionDraft) {

		return new ObjectEntryVersionDraftWrapper(objectEntryVersionDraft);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1235811002