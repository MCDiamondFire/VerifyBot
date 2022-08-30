package com.mcdiamondfire.verifybot.db

import com.mcdiamondfire.verifybot.Config
import org.jetbrains.exposed.sql.Database

val DATABASE = Database.connect(Config.db.url, user = Config.db.username, password = Config.db.password)
