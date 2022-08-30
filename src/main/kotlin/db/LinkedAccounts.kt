package com.mcdiamondfire.verifybot.db

import org.jetbrains.exposed.dao.id.IdTable

object LinkedAccounts : IdTable<String>("linked_accounts") {
	val secretKey = varchar("secret_key", 8).uniqueIndex().nullable()
	val playerUuid = varchar("player_uuid", 36).entityId()
	val playerName = varchar("player_name", 16)
	val discordId = varchar("discord_id", 64)

	override val id = playerUuid
}
