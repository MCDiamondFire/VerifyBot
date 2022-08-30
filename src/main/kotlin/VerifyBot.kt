package com.mcdiamondfire.verifybot

import com.mcdiamondfire.verifybot.db.DATABASE
import com.mcdiamondfire.verifybot.db.LinkedAccounts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.Modal
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.jetbrains.exposed.sql.update
import java.util.concurrent.TimeUnit


fun main() {
	val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
	val events = MutableSharedFlow<GenericEvent>()

	JDABuilder.createLight(Config.token)
		.setEventManager(FlowEventManager(scope, events))
		.build()

	events.filterIsInstance<ButtonInteractionEvent>()
		.filter { it.componentId == "open_verify_prompt" }
		.onEach { event ->
			event.replyModal(
				Modal.create("verify_modal", "Enter Your Information")
					.addActionRow(
						TextInput.create("username", "Minecraft Username", TextInputStyle.SHORT)
							.setMinLength(3)
							.setMaxLength(16)
							.build()
					)
					.addActionRow(
						TextInput.create("secret_key", "Verification Code", TextInputStyle.SHORT)
							.setPlaceholder("Type /verify on mcdiamondfire.com to get a code...")
							.build()
					)

					.build()
			).queue()
		}
		.launchIn(scope)



	events.filterIsInstance<ModalInteractionEvent>()
		.filter { it.modalId == "verify_modal" }
		.onEach { event ->
			val username = event.values.find { it.id == "username" }!!.asString
			val key = event.values.find { it.id == "secret_key" }!!.asString

			event.deferReply(true)

			// Find the verified role
			val verifiedRole = event.guild!!.getRoleById(Config.verifiedRole)
				?: return@onEach event.reply("$ERR Internal error").setEphemeral(true).queue()

			// Launch a transaction
			suspendedTransactionAsync(db = DATABASE) {
				val res = LinkedAccounts.select(LinkedAccounts.secretKey eq key).singleOrNull()

				if (res == null) {
					event.reply("$ERR There is no account associated with this code.")
						.setEphemeral(true)
						.queue()
					return@suspendedTransactionAsync
				}

				val name = res[LinkedAccounts.playerName]
				val uuid = res[LinkedAccounts.playerUuid]

				if (name.equals(username, true)) {
					// Remove the key, set the Discord id
					LinkedAccounts.update({ LinkedAccounts.playerUuid eq uuid }) {
						it[secretKey] = null
						it[discordId] = event.user.id
					}

					event.reply("$OK Your account has been successfully verified!") // Reply
						.setEphemeral(true)

						.delay(5, TimeUnit.SECONDS)
						.and(event.guild!!.addRoleToMember(event.user, verifiedRole)) // Add the role
						.and(event.member!!.modifyNickname(name)) // Set their nickname

						.queue()
				} else {
					event.reply("$ERR Your username did not match that associated with the code.")
						.setEphemeral(true)
						.queue()
				}
			}.await()
		}
		.launchIn(scope)
}
