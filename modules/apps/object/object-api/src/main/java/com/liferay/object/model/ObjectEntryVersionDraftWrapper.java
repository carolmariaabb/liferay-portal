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
		attributes.put(
			"objectEntryVersionDraftId", getObjectEntryVersionDraftId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("objectDefinitionId", getObjectDefinitionId());
		attributes.put("objectEntryId", getObjectEntryId());
		attributes.put("baseVersion", getBaseVersion());
		attributes.put("content", getContent());
		attributes.put("displayDate", getDisplayDate());
		attributes.put("expirationDate", getExpirationDate());
		attributes.put("reviewDate", getReviewDate());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
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

		Integer baseVersion = (Integer)attributes.get("baseVersion");

		if (baseVersion != null) {
			setBaseVersion(baseVersion);
		}

		String content = (String)attributes.get("content");

		if (content != null) {
			setContent(content);
		}

		Date displayDate = (Date)attributes.get("displayDate");

		if (displayDate != null) {
			setDisplayDate(displayDate);
		}

		Date expirationDate = (Date)attributes.get("expirationDate");

		if (expirationDate != null) {
			setExpirationDate(expirationDate);
		}

		Date reviewDate = (Date)attributes.get("reviewDate");

		if (reviewDate != null) {
			setReviewDate(reviewDate);
		}
	}

	@Override
	public ObjectEntryVersionDraft cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the base version of this object entry version draft.
	 *
	 * @return the base version of this object entry version draft
	 */
	@Override
	public int getBaseVersion() {
		return model.getBaseVersion();
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
	 * Returns the display date of this object entry version draft.
	 *
	 * @return the display date of this object entry version draft
	 */
	@Override
	public Date getDisplayDate() {
		return model.getDisplayDate();
	}

	/**
	 * Returns the expiration date of this object entry version draft.
	 *
	 * @return the expiration date of this object entry version draft
	 */
	@Override
	public Date getExpirationDate() {
		return model.getExpirationDate();
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
	 * Returns the review date of this object entry version draft.
	 *
	 * @return the review date of this object entry version draft
	 */
	@Override
	public Date getReviewDate() {
		return model.getReviewDate();
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

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the base version of this object entry version draft.
	 *
	 * @param baseVersion the base version of this object entry version draft
	 */
	@Override
	public void setBaseVersion(int baseVersion) {
		model.setBaseVersion(baseVersion);
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
	 * Sets the display date of this object entry version draft.
	 *
	 * @param displayDate the display date of this object entry version draft
	 */
	@Override
	public void setDisplayDate(Date displayDate) {
		model.setDisplayDate(displayDate);
	}

	/**
	 * Sets the expiration date of this object entry version draft.
	 *
	 * @param expirationDate the expiration date of this object entry version draft
	 */
	@Override
	public void setExpirationDate(Date expirationDate) {
		model.setExpirationDate(expirationDate);
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
	 * Sets the review date of this object entry version draft.
	 *
	 * @param reviewDate the review date of this object entry version draft
	 */
	@Override
	public void setReviewDate(Date reviewDate) {
		model.setReviewDate(reviewDate);
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
// LIFERAY-SERVICE-BUILDER-HASH:704247676