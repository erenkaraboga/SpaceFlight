package com.spaceflight.core.database

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromAuthors(authors: List<String>): String = authors.joinToString(SEPARATOR)

    @TypeConverter
    fun toAuthors(value: String): List<String> =
        if (value.isEmpty()) emptyList() else value.split(SEPARATOR)

    private companion object {
        /** A control character, so it can never collide with a real author name. */
        const val SEPARATOR = "\u001F"
    }
}
