/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EventSource} from 'eventsource';
import React, {useCallback, useEffect, useRef, useState} from 'react';

import {createEventSource, postChatMessage} from '../api';
import {ChatMessage, WidgetConfig} from '../types';
import AssistantMessage from './AssistantMessage';
import ChatFooter from './ChatFooter';
import ChatHeader from './ChatHeader';
import ChatInput from './ChatInput';
import ChatIntro from './ChatIntro';
import ErrorMessage from './ErrorMessage';
import GeneratingIndicator from './GeneratingIndicator';
import {ChatIcon, CloseIcon} from './Icons';
import QuickActions from './QuickActions';
import UserMessage from './UserMessage';

interface ChatWidgetProps {
	config: WidgetConfig;
}

export default function ChatWidget({config}: ChatWidgetProps) {
	const [generating, setGenerating] = useState(false);
	const [messages, setMessages] = useState<ChatMessage[]>([]);
	const [open, setOpen] = useState(false);
	const [quickActionsVisible, setQuickActionsVisible] = useState(true);

	const eventSourceRef = useRef<EventSource | null>(null);
	const eventSourceReference = useRef<string | null>(null);
	const messagesEndRef = useRef<HTMLDivElement>(null);

	useEffect(() => {
		createEventSource().then((eventSource) => {
			if (!eventSource) {
				return;
			}

			eventSourceRef.current = eventSource;

			eventSourceRef.current.addEventListener(
				'Chat Message Sent',
				(event) => {
					const dataJSON = JSON.parse(
						(event as MessageEvent).data
					);

					setMessages((prev) => [
						...prev,
						{sender: 'assistant', text: dataJSON['data']},
					]);

					setGenerating(false);
				}
			);

			eventSourceRef.current.addEventListener(
				'Subscribe',
				(event) => {
					eventSourceReference.current = (
						event as MessageEvent
					).data;
				}
			);
		});

		return () => {
			eventSourceRef.current?.close();
			eventSourceRef.current = null;
		};
	}, []);

	useEffect(() => {
		messagesEndRef.current?.scrollIntoView({behavior: 'smooth'});
	}, [messages, generating]);

	const handleToggle = useCallback(() => {
		setOpen((prev) => !prev);
	}, []);

	const sendMessage = useCallback((text: string) => {
		if (!eventSourceReference.current) {
			return;
		}

		setMessages((prev) => [...prev, {sender: 'user', text}]);
		setQuickActionsVisible(false);
		setGenerating(true);

		postChatMessage(eventSourceReference.current, text).catch(
			(error) => {
				console.error(
					'[AI Hub Chat] Failed to send message:',
					error
				);

				setMessages((prev) => [
					...prev,
					{sender: 'error', text: ''},
				]);
				setGenerating(false);
			}
		);
	}, []);

	return (
		<>
			<div className={'aihub-panel' + (open ? ' open' : '')}>
				<ChatHeader
					logoUrl={config.logoUrl}
					onClose={handleToggle}
					subtitle={config.subtitle}
					title={config.title}
				/>

				<div className="aihub-messages">
					<ChatIntro
						introMessage={config.introMessage}
						logoUrl={config.logoUrl}
						subtitle={config.subtitle}
						title={config.title}
					/>

					{quickActionsVisible && (
						<QuickActions
							actions={config.quickActions}
							onAction={sendMessage}
						/>
					)}

					{messages.map((msg, index) => {
						if (msg.sender === 'assistant') {
							return (
								<AssistantMessage
									key={index}
									text={msg.text}
								/>
							);
						}

						if (msg.sender === 'error') {
							return <ErrorMessage key={index} />;
						}

						return (
							<UserMessage
								key={index}
								text={msg.text}
								userName={config.userName}
							/>
						);
					})}

					{generating && <GeneratingIndicator />}

					<div ref={messagesEndRef} />
				</div>

				<ChatInput
					disabled={generating}
					onSubmit={sendMessage}
					placeholder={
						config.placeholder || 'Ask a question\u2026'
					}
				/>

				<ChatFooter
					privacyPolicyUrl={config.privacyPolicyUrl}
				/>
			</div>

			<button
				aria-label={
					open ? 'Close AI Assistant' : 'Open AI Assistant'
				}
				className="aihub-toggle"
				onClick={handleToggle}
			>
				{open ? <CloseIcon /> : <ChatIcon />}
			</button>
		</>
	);
}
