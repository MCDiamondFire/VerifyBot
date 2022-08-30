package com.mcdiamondfire.verifybot

import com.akuleshov7.ktoml.Toml
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import java.io.File

private interface ConfigModel {
	val token: String
	val verifiedRole: Long
	val db: DatabaseConfig
}

@Serializable
data class DatabaseConfig(val username: String, val password: String, val url: String)

@Serializable
private data class ConfigImpl(
	override val token: String,
	override val verifiedRole: Long,
	@SerialName("database")
	override val db: DatabaseConfig) : ConfigModel

object Config : ConfigModel by Toml.decodeFromString<ConfigImpl>(File("config.toml").readText())
