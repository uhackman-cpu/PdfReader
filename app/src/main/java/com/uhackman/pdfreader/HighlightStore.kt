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
    val createdAt: Long,
    val startChar: Int = -1,
    val endChar: Int = -1,
    val prefixText: String = "",
    val suffixText: String = "",
    val groupId: String = ""
)

data class HighlightDraft(
    val pageIndex: Int,
    val startGlyph: Int,
    val endGlyph: Int,
    val text: String,
    val color: Int,
    val note: String,
    val startChar: Int,
    val endChar: Int,
    val prefixText: String,
    val suffixText: String,
    val groupId: String
)

class HighlightStore(context: Context) :
    SQLiteOpenHelper(context, "reader_annotations.db", null, 3) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE highlights (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                document_key TEXT NOT NULL,
                page_index INTEGER NOT NULL,
                start_glyph INTEGER NOT NULL,
                end_glyph INTEGER NOT NULL,
                start_char INTEGER NOT NULL DEFAULT -1,
                end_char INTEGER NOT NULL DEFAULT -1,
                prefix_text TEXT NOT NULL DEFAULT '',
                suffix_text TEXT NOT NULL DEFAULT '',
                group_id TEXT NOT NULL DEFAULT '',
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
        db.execSQL(
            "CREATE INDEX idx_highlights_document_group " +
                "ON highlights(document_key, group_id)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE highlights ADD COLUMN start_char INTEGER NOT NULL DEFAULT -1")
            db.execSQL("ALTER TABLE highlights ADD COLUMN end_char INTEGER NOT NULL DEFAULT -1")
            db.execSQL("ALTER TABLE highlights ADD COLUMN prefix_text TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE highlights ADD COLUMN suffix_text TEXT NOT NULL DEFAULT ''")
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE highlights ADD COLUMN group_id TEXT NOT NULL DEFAULT ''")
            db.execSQL(
                "CREATE INDEX idx_highlights_document_group " +
                    "ON highlights(document_key, group_id)"
            )
        }
    }

    fun add(
        documentKey: String, pageIndex: Int, startGlyph: Int, endGlyph: Int,
        text: String, color: Int, note: String, startChar: Int, endChar: Int,
        prefixText: String, suffixText: String, groupId: String = ""
    ): Long {
        val values = ContentValues().apply {
            put("document_key", documentKey)
            put("page_index", pageIndex)
            put("start_glyph", minOf(startGlyph, endGlyph))
            put("end_glyph", maxOf(startGlyph, endGlyph))
            put("start_char", startChar)
            put("end_char", endChar)
            put("prefix_text", prefixText)
            put("suffix_text", suffixText)
            put("group_id", groupId)
            put("selected_text", text)
            put("color", color)
            put("note", note)
            put("created_at", System.currentTimeMillis())
        }
        return writableDatabase.insert("highlights", null, values)
    }

    /** Inserts all page fragments atomically as one logical selection. */
    fun addBatch(documentKey: String, drafts: List<HighlightDraft>): List<Highlight> {
        if (drafts.isEmpty()) return emptyList()
        val db = writableDatabase
        val createdAt = System.currentTimeMillis()
        val inserted = mutableListOf<Highlight>()
        db.beginTransaction()
        try {
            for (draft in drafts) {
                val values = ContentValues().apply {
                    put("document_key", documentKey)
                    put("page_index", draft.pageIndex)
                    put("start_glyph", minOf(draft.startGlyph, draft.endGlyph))
                    put("end_glyph", maxOf(draft.startGlyph, draft.endGlyph))
                    put("start_char", draft.startChar)
                    put("end_char", draft.endChar)
                    put("prefix_text", draft.prefixText)
                    put("suffix_text", draft.suffixText)
                    put("group_id", draft.groupId)
                    put("selected_text", draft.text)
                    put("color", draft.color)
                    put("note", draft.note)
                    put("created_at", createdAt)
                }
                val id = db.insertOrThrow("highlights", null, values)
                inserted.add(
                    Highlight(
                        id, draft.pageIndex, minOf(draft.startGlyph, draft.endGlyph),
                        maxOf(draft.startGlyph, draft.endGlyph), draft.text, draft.color,
                        draft.note, createdAt, draft.startChar, draft.endChar,
                        draft.prefixText, draft.suffixText, draft.groupId
                    )
                )
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        return inserted
    }

    fun list(documentKey: String): List<Highlight> {
        val result = mutableListOf<Highlight>()
        readableDatabase.query(
            "highlights", null, "document_key = ?", arrayOf(documentKey),
            null, null, "page_index ASC, start_glyph ASC"
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
                        createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("created_at")),
                        startChar = cursor.getInt(cursor.getColumnIndexOrThrow("start_char")),
                        endChar = cursor.getInt(cursor.getColumnIndexOrThrow("end_char")),
                        prefixText = cursor.getString(cursor.getColumnIndexOrThrow("prefix_text")),
                        suffixText = cursor.getString(cursor.getColumnIndexOrThrow("suffix_text")),
                        groupId = cursor.getString(cursor.getColumnIndexOrThrow("group_id"))
                    )
                )
            }
        }
        return result
    }

    fun delete(id: Long): Int =
        writableDatabase.delete("highlights", "id = ?", arrayOf(id.toString()))
}
