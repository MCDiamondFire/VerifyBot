package com.mcdiamondfire.verifybot

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.IEventManager

class FlowEventManager(
	private val scope: CoroutineScope,
	private val flow: MutableSharedFlow<GenericEvent>
) : IEventManager {

	override fun handle(event: GenericEvent) {
		scope.launch {
			flow.emit(event)
		}
	}

	// <editor-fold desc="> Unsupported">
	@Suppress("NOTHING_TO_INLINE") // inline for stack trace
	private inline fun unsupported(): Nothing = throw UnsupportedOperationException()

	override fun register(listener: Any): Nothing = unsupported()

	override fun unregister(listener: Any): Nothing = unsupported()

	override fun getRegisteredListeners(): Nothing = unsupported()
	// </editor-fold>
}
