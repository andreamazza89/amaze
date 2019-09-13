package com.mrdigitalpartners.oprah.utils

import java.sql.Timestamp
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

fun utcTimeNow(): ZonedDateTime = Instant.now().atZone(ZoneId.of("UTC"))

fun ZonedDateTime.truncatedToDays(): ZonedDateTime = this.truncatedTo(ChronoUnit.DAYS)

fun ZonedDateTime.addWeeks(weeks: Int): ZonedDateTime = this.plusWeeks(weeks.toLong())

fun Timestamp.toUtc(): ZonedDateTime =
    ZonedDateTime.ofInstant(this.toInstant(), ZoneId.of("UTC"))
