# ReadEra behavior study — independent implementation specification

## Scope and method

The repository contains the supplied ReadEra APK split set and six successful GitHub Actions analysis runs. JADX output and AArch64 disassembly were inspected. These notes describe observable data contracts and control flow only. No ReadEra source, assets, or implementation code is reused by this app.

## Evidence inventory

| Analysis | Run | Result |
|---|---:|---|
| APK/JADX static analysis | [41656897695](https://github.com/uhackman-cpu/PdfReader/actions/runs/41656897695) | 6,368 Java source files and static report |
| Native JNI trace | [41660603685](https://github.com/uhackman-cpu/PdfReader/actions/runs/41660603685) | Native library inventory and JNI strings/symbols |
| JNI disassembly | [41661101834](https://github.com/uhackman-cpu/PdfReader/actions/runs/41661101834) | Page, text, search, XPath and render entry points |
| Rectangle helper trace | [41664477813](https://github.com/uhackman-cpu/PdfReader/actions/runs/41664477813) | Native rectangle and parser helper ranges |
| Serialization helper trace | [41664987239](https://github.com/uhackman-cpu/PdfReader/actions/runs/41664987239) | Typed stream reader/writer and cache helper |
| Text/selection geometry trace | [41665644823](https://github.com/uhackman-cpu/PdfReader/actions/runs/41665644823) | Text tuple, search tuple and geometry JNI disassembly |

The current repository was checked before implementation: the default branch was main; the only source-controlled payloads were the three APK splits and six analysis workflows. There was no Android app module or Android build workflow.

## 1. Text extraction contract

JniDoc.pageTextJni() is wrapped by JniDoc.m19140z(). The Java consumer in AbstractC3568c.m15410g1():

1. Treats the result as a flat list whose length must be divisible by five.
2. Reads four consecutive Float values.
3. Reads a String as the fifth value.
4. Creates one C3927i for each five-value record.

C3927i extends RectF; its constructor assigns the four floats to left, top, right, and bottom, and stores the associated text plus a sequence index. This establishes the Java-side rectangle ordering and per-text-unit geometry contract. Coordinates are subsequently scaled against a page rectangle by AbstractC2468y.m11551a() before drawing.

The native pageTextJni disassembly reads four type-4 values followed by a type-5 string repeatedly. This agrees with the Java five-value grouping. The exact internal text-layout engine and all coordinate-normalization edge cases were not established.

## 2. Selection and drag behavior

The Java selection model is C2467x:

- m11517C(page,x,y) performs a text hit test and creates the initial selection from the hit text unit.
- m11545v(page,x,y) extends a word-oriented selection.
- m11544s(page,x,y,...) extends a character-oriented selection.
- m11539n() assembles selected text across the ordered page map.
- m11540o() returns selected geometry grouped by page.
- m11536i() paints selection rectangles; m11537j() paints selection handles.

AbstractC2468y supplies hit-testing and text-boundary helpers. The inspected logic accounts for whitespace, newline records, punctuation, letter/digit boundaries, mixed writing systems, and page transitions. Selection can span adjacent pages; the model orders page entries before assembling text and rectangles.

Touch handling in AbstractGestureDetectorOnGestureListenerC2452i.m11423d0() confirms the user-facing modes: long press starts word selection; moving while in word mode calls m11545v; character mode calls m11544s; touching an existing selection can enter character/handle adjustment. ReadSurface.onTouchEvent() forwards touch events to that gesture controller.

## 3. Search

JniDoc.searchTextJni() is consumed as groups of three values by AbstractC3568c.mo15417n0(): integer, string, string. searchRectJni() is consumed in groups of five values: four floats and a string, then converted to C3925g rectangles by AbstractC3568c.m15402T0().

The exact meaning of every search tuple field was not inferred beyond those Java consumers. Text search and geometry search are separate operations: a match can be navigated as a text result and painted using its rectangle data.

## 4. Persistent highlights and annotations

C3930l is the saved text-annotation model. It carries selected text (f15702D), optional note (f15703E), color/style index (f15704F), start/end page and position/anchor fields inherited from C3928j, and a stable UUID (f15714z) inherited from AbstractC3934p.

AbstractC2663j.m12109d() creates the model from the current C2467x selection and AbstractC2748x0.m12198a() anchor builder. The model is added to the document's annotation list and an asynchronous persistence operation is scheduled.

AbstractC6590e2.m26555C() and m26568P() dispatch create/update work. m26613s() builds SQLite ContentValues containing selected text, UUID, note, style, start page, sort position, serialized location JSON, and timestamps, then calls C3261g.m13937N3(). Updates use m26612r() and C3261g.m13893E4(). This supports the conclusion that ReadEra maintains app-side annotation records; it does not prove that the PDF file is rewritten with embedded markup.

## 5. Restore and rectangle lookup

C3684b.mo15477S0() asks the page model for saved annotation locations and routes them through AbstractC3568c.m15390D0() / m15389B0(). The Java consumer groups the native response into four-float-plus-string records and creates C3920b objects (rectangle, associated string, style index). Those rectangles are normalized against the page viewport for drawing.

bookmarkRectJni() was traced further:

- It looks up or creates a page-keyed native cache node (page number at node offset 0x20; cache root near 0x158720) under a mutex.
- It obtains the Java string argument and places it into a typed request record. The helper at 0x8279c constructs a type-5 string record; 0x8325c serializes the request record list.
- The response path reads a type marker (0x29) and then repeatedly reads four type-4 numeric values and one type-5 string.
- The Java caller confirms those five values are interpreted as RectF(left,top,right,bottom) plus an associated string; the caller supplies the annotation style separately.

This closes the wrapper/serialization gap: bookmarkRectJni() is a page-cached request/response bridge that resolves saved anchors to rectangle records. It is not, by itself, the glyph-layout algorithm.

### Remaining native uncertainty

The exact command handler behind that typed request, the internal anchor parser, and the final glyph-to-rectangle calculation were not fully recovered. The worker/engine dispatch is behind native stream/cache machinery, and the disassembled JNI wrapper does not expose the complete geometry calculation. The PDF engine identity also remains unproven: strings in liberapdf.so resemble PDF engine internals, but they are insufficient to assert a specific upstream engine. These gaps do not block an independent reader because the app can use its own text extraction and geometry model.

## 6. Rendering and zoom

The PDF page renderer path uses JniDoc.pageOpenJni() / pageRenderJni() and returns a ByteBuffer wrapped as JniBitmap. C3684b.m16019O1() prepares a page/tile transform before rendering. ReadSurface is a GLSurfaceView; it uses a GL11 canvas, dirty rendering (setRenderMode(0)), queued GL work, and explicit render requests. The inspected gesture code adjusts scroll around the pinch focus and supports a bounded zoom range (1x–8x).

The exact native rasterizer implementation is not needed for the independent app. The independent first milestone uses a separate PDF library and Android bitmap rendering, then draws selection and annotation overlays in the same page coordinate space.

## 7. Document-wide highlight navigation

The saved annotation list is held on the document model (C5139l.f20213b0). The annotation navigator in C4902x uses that list, exposes its count/index, and selecting an item dispatches a navigation event for the chosen C3930l. AbstractC2663j.m12114i() also iterates the document annotation list to collect all selected text. This confirms that document-wide annotation navigation is separate from the on-page highlight overlay.

## Independent implementation decisions

- Use positioned PDF text and glyph rectangles; the page bitmap is only the visual raster, not the text model.
- Long-press starts Unicode-aware word selection; dragging extends the word range, and the two endpoint handles refine the selection at text-unit/character level.
- A 64×64 uniform spatial grid with an expanding-ring nearest-neighbour search keeps touch hit-testing from scanning every glyph on each move.
- Search extracted document text and map character matches back to positioned glyph rectangles.
- Persist document-scoped selections, selected text, color, optional note, and surrounding prefix/suffix context in SQLite.
- Restore saved highlights by resolving the selected phrase against fresh page text and ranking repeated occurrences with the saved context; old glyph indexes remain a fast hint.
- Render pages independently and apply focus-aware pinch zoom and pan.
- After a pinch gesture, request a sharper page raster (up to 288 DPI), capped by a 12-megapixel page-area budget; replace the bitmap without resetting zoom, pan, selection, or overlays.

The independent app now uses continuous vertical scrolling with recycled page views, lazy text/bitmap caches, cross-page selection, combined selected text, and per-page persistent highlight anchors grouped as one document selection. Rotated/cropped coordinate transforms use the renderer's displayed page dimensions. OCR for scanned PDFs and tiled rendering remain future work. Physical-device touch and rendering validation is still outstanding.


## 8. Persisted selection-anchor lifecycle — additional Java-side evidence

The saved annotation is not anchored only by a raw character offset. The Java-side position model keeps two XPath-like boundary strings plus page/index fallbacks:

- `C3928j.f15697t` and `f15698u` hold the two boundary XPath strings; `f15687j` and `f15688k` hold their resolved text-unit indexes/pages; `f15699v` is passed as the XPath mode/type.
- `AbstractC2748x0.m12198a()` builds both boundary anchors from the active selection. `AbstractC2468y.m11574x()` and `m11555e()` use the selected endpoint records' `C3927i.f15685j` values and distinct endpoint markers (0 and 1). The same builder captures the selected endpoint page and a normalized reading-position point.
- `JniDoc.pageXPathToIndexJni()` resolves a stored boundary XPath back to a current text-unit index. `pageXPathFromIndexJni()` regenerates an XPath from a page/index when the saved XPath no longer resolves or disagrees with the stored index.
- Legacy rectangle-based position strings can be converted with `pageXPathFromRectJni()`; `pageXPathListToIndexJni()` converts a batch of serialized anchor entries to indexes.
- `pageSimpleTextJni()` is called through the page model's page/start/end range accessor, separate from the five-value positioned-text stream.
- The page annotation renderer constructs a composite page + two-boundary-anchor + mode request for `bookmarkRectJni()`. The JNI wrapper returns groups of four floats plus a string; Java turns those into normalized rectangle records and applies annotation style separately.

This establishes an anchor-resolution and fallback lifecycle at the Java/JNI boundary. It still does not reveal the native command handler's complete XPath grammar or the final glyph-layout routine.

## 9. Selection gesture and endpoint UI details

- The initial word-selection constructor `C2467x.m11517C()` is invoked from the LONG_PRESS path after a page text hit test. A normal initial press does not itself create a ReadEra text selection.
- In word mode, drag updates call `m11545v()`; in character mode they call `m11544s()`. The latter also receives a movement/scale parameter and adjusts the active endpoint record.
- `m11536i()` draws the selected text-unit rectangles. `m11537j()` draws the two endpoint handles for character selection; the word-selection mode suppresses those handles.
- `m11539n()` assembles selected text in page order, while `m11540o()` returns a page-keyed map of selected rectangles. The data model can therefore represent selections crossing page boundaries even though the first independent app milestone is page-at-a-time.

## 10. Analysis closure and explicit limits

The behavior needed to design an independent text-selection/highlight flow is now sufficiently specified: positioned text records, word/character selection modes, endpoint handles, cross-page selection data, text search and search rectangles, two-boundary persistent anchors with index/rectangle fallback, page-level highlight rectangles, document-wide annotation navigation, and the render/zoom interaction path are all covered by Java consumers and JNI contracts.

The remaining unknowns are implementation internals rather than missing user-visible behavior: the exact native anchor-command handler and XPath grammar, its final glyph-to-rectangle algorithm, and the precise upstream PDF engine identity. The available wrapper disassembly does not establish these facts. Further guessing would be unsafe and is not required for an independent implementation using its own PDF text/layout engine. These points remain explicitly unverified.


## 11. Page-normalized geometry and zoom transform — additional trace

A targeted workflow reused the completed JADX artifact rather than repeating APK decompilation: [coordinate-transform trace run 41716068087](https://github.com/uhackman-cpu/PdfReader/actions/runs/41716068087). The extracted source windows are preserved in [READERA_PAGE_COORDINATE_TRANSFORM.md](READERA_PAGE_COORDINATE_TRANSFORM.md).

The Java consumers establish the following coordinate contract:

- `AbstractC2468y.m11551a(rect, pageRect)` treats each text/highlight rectangle as page-normalized: it multiplies left/right by the page rectangle width, top/bottom by its height, then offsets by the page rectangle's left/top.
- `C2454k.mo11421c0()` performs the inverse transform for touch input. At base zoom it subtracts the page rectangle origin and divides by its width/height. At scaled zoom it uses the zoomed page rectangle returned by `C0823q.m5719p0(zoom)`.
- `C0823q.m5721q0()` returns cached base page bounds at 1x. In vertical layout it scales all four page-bound coordinates by the zoom factor; horizontal/spread layout delegates to a separate layout helper.
- `C3684b.mo15415k()` calls `JniDoc.m19131q()` / `pageGetSizeJni()` with an `int[2]`; on success it uses those two integers to construct the page-size object passed into page layout and rendering.

No explicit CropBox/MediaBox or page-rotation adjustment appears in these Java coordinate consumers. The page dimensions and text rectangles arrive from the native document layer, so crop/rotation normalization is handled below this Java viewport transform or inside the native page geometry. The exact native crop/rotation calculation remains unverified.

**Independent implementation consequence:** keep text geometry normalized to the same rendered-page rectangle, and use one forward transform for painting plus its exact inverse for hit-testing. Derive the rendered page aspect ratio from the PDF renderer's actual output (including page rotation), not from an assumed unrotated media box.
