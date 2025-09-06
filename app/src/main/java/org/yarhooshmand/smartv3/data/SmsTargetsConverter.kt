package org.yarhooshmand.smartv3.data

import androidx.room.TypeConverter

class SmsTargetsConverter {
    @TypeConverter fun fromList(list: List<String>?): String? =
        list?.joinToString(",") { it.trim() }
    @TypeConverter fun toList(csv: String?): List<String> =
        csv?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
}
