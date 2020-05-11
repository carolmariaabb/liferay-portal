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

import ClayModal from 'clay-modal';
import {makeFetch} from 'dynamic-data-mapping-form-renderer/js/util/fetch.es';
import dom from 'metal-dom';
import {EventHandler} from 'metal-events';
import Component, {Config} from 'metal-jsx';

import Email from './Email.es';
import Link from './Link.es';

class ShareFormModal extends Component {
	attached() {
		this._eventHandler.add(
			dom.on(
				'.nav-item > .lfr-ddm-share-url-button',
				'click',
				this._handleShareButtonClicked.bind(this)
			)
		);
	}

	close() {
		this.refs.shareFormModalRef.emit('hide');
	}

	created() {
		this._eventHandler = new EventHandler();
		this._fetchEmailAddresses();
	}

	disposeInternal() {
		super.disposeInternal();
		this._eventHandler.removeAllListeners();
	}

	open() {
		this.refs.shareFormModalRef.refs.emailRef.init();
		this.refs.shareFormModalRef.show();
	}

	render() {
		const {spritemap} = this.props;
		const {emailAddresses} = this.state;

		return (
			<div class="share-form-modal">
				<ClayModal
					body={
						<div class="share-form-modal-items">
							<div class="share-form-modal-item">
								<div class="popover-header">
									{Liferay.Language.get('link')}
								</div>
								<div class="popover-body">
									{
										<Link
											spritemap={spritemap}
											url={this.props.url}
										/>
									}
								</div>
							</div>
							<div class="share-form-modal-item">
								<div class="popover-header">
									{Liferay.Language.get('email')}
								</div>
								<div class="popover-body">
									{
										<Email
											emailAddresses={emailAddresses}
											localizedName={
												this.props.localizedName
											}
											ref="emailRef"
											spritemap={spritemap}
										/>
									}
								</div>
							</div>
						</div>
					}
					events={{
						clickButton: this._handleClickFooterButton.bind(this),
					}}
					footerButtons={[
						{
							alignment: 'right',
							label: Liferay.Language.get('cancel'),
							style: 'secondary',
							type: 'close',
						},
						{
							alignment: 'right',
							label: Liferay.Language.get('done'),
							style: 'primary',
							type: 'button',
						},
					]}
					ref={'shareFormModalRef'}
					size={'lg'}
					spritemap={spritemap}
					title={Liferay.Language.get('share')}
				/>
			</div>
		);
	}

	_fetchEmailAddresses() {
		const {emailAddressesURL} = this.props;

		makeFetch({
			method: 'GET',
			url: emailAddressesURL,
		})
			.then((responseData) => {
				if (!this.isDisposed()) {
					this.setState({
						emailAddresses: responseData.map((data) => {
							return {
								label: data.emailAddress,
								value: data.emailAddress,
							};
						}),
					});
				}
			})
			.catch((error) => {
				throw new Error(error);
			});
	}

	_handleClickFooterButton(event) {
		if (event.target.classList.contains('btn-primary')) {
			this.close();
		}
	}

	_handleShareButtonClicked() {
		this.open();
	}
}

ShareFormModal.PROPS = {

	/**
	 * @default undefined
	 * @instance
	 * @memberof ShareFormModal
	 * @type {!string}
	 */
	emailAddressesURL: Config.string(),

	/**
	 * @default undefined
	 * @instance
	 * @memberof ShareFormModal
	 * @type {!object}
	 */
	localizedName: Config.object(),

	/**
	 * @default undefined
	 * @instance
	 * @memberof ShareFormModal
	 * @type {!string}
	 */
	portletNamespace: Config.string(),

	/**
	 * @default undefined
	 * @instance
	 * @memberof ShareFormModal
	 * @type {!string}
	 */
	shareFormLinkURL: Config.string(),

	/**
	 * @default undefined
	 * @instance
	 * @memberof ShareFormModal
	 * @type {!string}
	 */
	spritemap: Config.string(),

	/**
	 * @default undefined
	 * @instance
	 * @memberof ShareFormModal
	 * @type {!string}
	 */
	url: Config.string(),
};

ShareFormModal.STATE = {

	/**
	 * @default undefined
	 * @instance
	 * @memberof ShareFormModal
	 * @type {!array}
	 */
	emailAddresses: Config.array(),
};

export default ShareFormModal;
