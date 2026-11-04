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
- Supports focus-aware pinch zoom and panning; after a zoom gesture it requests sharper page tiles (up to 288 DPI) without losing selection overlays. At high zoom, only tiles intersecting the visible page viewport plus a one-tile prefetch margin are rendered directly into tile-sized bitmaps through PDFRenderer.renderPageToGraphics; panning requests newly visible tiles. No intermediate full-page high-resolution bitmap is allocated.
- Uses a continuous RecyclerView page stream with recycled page views, on-demand page rendering, and one-page prefetch around the viewport.
- Keeps rendered bitmaps in a lease-aware 64 MiB LRU cache and extracted text/glyph geometry in a separate lease-aware 24 MiB weighted cache. Search and highlight restoration scan pages incrementally instead of retaining the whole document's glyph data.
- Uses PDFRenderer's displayed crop-box dimensions (including 90/270 degree axis swaps) and page-rotation-adjusted TextPosition coordinates. Drawing and touch hit-testing share one forward/inverse normalized-page transform.

Scanned PDFs containing only page images have no text layer in this milestone; OCR is not yet included.

## Build

The Android CI GitHub Actions workflow runs unit tests and assembles a debug APK on pushes to main and on manual dispatch. The APK is uploaded as the pdfreader-debug-apk artifact.

The app uses PDFBox-Android, an Apache-2.0 Android port of Apache PDFBox. PDFRenderer produces Android bitmaps and PDFTextStripper exposes positioned text, which this app uses to build its own selection geometry.

## Architecture

- PdfTextExtractor: extracts per-glyph text and normalized rectangles.
- PdfPageView: draws the page, highlight overlays, search matches, selection, and handles zoom/pan/drag input.
- PageText: maps character ranges to positioned glyph ranges.
- HighlightStore: persists document-scoped annotations in SQLite.
- PdfPageAdapter: recycled continuous page stream with per-page viewport sizing.
- PageTextCache: bounded weighted cache for extracted text and glyph geometry.
- PageBitmapCache: bounded lease-aware bitmap cache for base pages and visible high-resolution tiles.
- PdfRegionTileRenderer: renders requested 512-pixel viewport tiles directly into small bitmaps using PDFRenderer's crop/rotation-aware page transform.
- MainActivity: document picker, lazy page loading, streaming search, highlight list, and save-note flow.

See docs/READERA_BEHAVIOR_SPEC.md for the evidence-based behavior model and remaining limits of the static analysis.
