# Independent PDF Reader

A native Android PDF reader built independently from documented PDF-reading behavior. No ReadEra source code, assets, or binaries are included.

## First working milestone

- Opens PDFs through Android's document picker.
- Renders actual PDF pages with PDFBox-Android, not a screenshot-only page model.
- Extracts selectable text positions; long-press selects a Unicode-aware word, finger-drag extends selection, and endpoint handles refine it.
- Uses a per-page spatial index for responsive glyph hit-testing on dense text pages.
- Searches extracted text across the document and jumps to matches.
- Saves highlights and optional notes in local SQLite, keyed to the selected document URI.
- Restores saved highlights when a document is reopened.
- Shows a document-wide highlights list; selecting an item jumps to its page.
- Supports focus-aware pinch zoom and panning; after a zoom gesture it requests a sharper page render (up to 288 DPI) without losing selection overlays.
- Uses previous/next page navigation for this initial milestone. Continuous scrolling and richer selection handles are follow-up work.

Scanned PDFs containing only page images have no text layer in this milestone; OCR is not yet included.

## Build

The Android CI GitHub Actions workflow runs unit tests and assembles a debug APK on pushes to main and on manual dispatch. The APK is uploaded as the pdfreader-debug-apk artifact.

The app uses PDFBox-Android, an Apache-2.0 Android port of Apache PDFBox. PDFRenderer produces Android bitmaps and PDFTextStripper exposes positioned text, which this app uses to build its own selection geometry.

## Architecture

- PdfTextExtractor: extracts per-glyph text and normalized rectangles.
- PdfPageView: draws the page, highlight overlays, search matches, selection, and handles zoom/pan/drag input.
- PageText: maps character ranges to positioned glyph ranges.
- HighlightStore: persists document-scoped annotations in SQLite.
- MainActivity: document picker, page navigation, search, highlight list, and save-note flow.

See docs/READERA_BEHAVIOR_SPEC.md for the evidence-based behavior model and remaining limits of the static analysis.
