package com.uhackman.pdfreader

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Highlight(
    val id: Long,
    val pageIndex: Int,
    val startGlyph: Int,
    val endGlyph: Int,
    val text: String,
    val color: Int,
    val note: String,
    val createdAt: Long
)

class HighlightStore(context: Context) :
    SQLiteOpenHelper(context, "reader_annotations.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE highlights (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                document_key TEXT NOT NULL,
                page_index INTEGER NOT NULL,
                start_glyph INTEGER NOT NULL,
                end_glyph INTEGER NOT NULL,
                selected_text TEXT NOT NULL,
                color INTEGER NOT NULL,
                note TEXT NOT NULL DEFAULT '',
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            "CREATE INDEX idx_highlights_document_page " +
                "ON highlights(document_key, page_index, start_glyph)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit

    fun add(
        documentKey: String,
        pageIndex: Int,
        startGlyph: Int,
        endGlyph: Int,
        text: String,
        color: Int,
        note: String
    ): Long {
        val values = ContentValues().apply {
            put("document_key", documentKey)
            put("page_index", pageIndex)
            put("start_glyph", minOf(startGlyph, endGlyph))
            put("end_glyph", maxOf(startGlyph, endGlyph))
            put("selected_text", text)
            put("color", color)
            put("note", note)
            put("created_at", System.currentTimeMillis())
        }
        return writableDatabase.insert("highlights", null, values)
    }

    fun list(documentKey: String): List<Highlight> {
        val result = mutableListOf<Highlight>()
        readableDatabase.query(
            "highlights",
            null,
            "document_key = ?",
            arrayOf(documentKey),
            null,
            null,
            "page_index ASC, start_glyph ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result.add(
                    Highlight(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        pageIndex = cursor.getInt(cursor.getColumnIndexOrThrow("page_index")),
                        startGlyph = cursor.getInt(cursor.getColumnIndexOrThrow("start_glyph")),
                        endGlyph = cursor.getInt(cursor.getColumnIndexOrThrow("end_glyph")),
                        text = cursor.getString(cursor.getColumnIndexOrThrow("selected_text")),
                        color = cursor.getInt(cursor.getColumnIndexOrThrow("color")),
                        note = cursor.getString(cursor.getColumnIndexOrThrow("note")),
                        createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("created_at"))
                    )
                )
            }
        }
        return result
    }

    fun delete(id: Long): Int =
        writableDatabase.delete("highlights", "id = ?", arrayOf(id.toString()))
}
