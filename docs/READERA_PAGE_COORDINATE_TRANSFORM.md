# ReadEra page-coordinate transform trace

This report reuses the existing successful JADX artifact; no APK re-analysis was repeated.

## Artifact inventory
- `REPORT.md`
- `apk_components.txt`
- `jadx.log`
- `java/resources/AndroidManifest.xml`
- `java/resources/DebugProbesKt.bin`
- `java/resources/META-INF/BNDLTOOL.RSA`
- `java/resources/META-INF/BNDLTOOL.SF`
- `java/resources/META-INF/FastDoubleParser-LICENSE`
- `java/resources/META-INF/FastDoubleParser-ThirdParty-LICENSE`
- `java/resources/META-INF/MANIFEST.MF`
- `java/resources/META-INF/Schubfach-LICENSE`
- `java/resources/META-INF/androidx.activity_activity.version`
- `java/resources/META-INF/androidx.annotation_annotation-experimental.version`
- `java/resources/META-INF/androidx.appcompat_appcompat-resources.version`
- `java/resources/META-INF/androidx.appcompat_appcompat.version`
- `java/resources/META-INF/androidx.arch.core_core-runtime.version`
- `java/resources/META-INF/androidx.asynclayoutinflater_asynclayoutinflater.version`
- `java/resources/META-INF/androidx.browser_browser.version`
- `java/resources/META-INF/androidx.cardview_cardview.version`
- `java/resources/META-INF/androidx.compose.runtime_runtime-annotation.version`
- `java/resources/META-INF/androidx.constraintlayout_constraintlayout.version`
- `java/resources/META-INF/androidx.coordinatorlayout_coordinatorlayout.version`
- `java/resources/META-INF/androidx.core_core-ktx.version`
- `java/resources/META-INF/androidx.core_core-viewtree.version`
- `java/resources/META-INF/androidx.core_core.version`
- `java/resources/META-INF/androidx.cursoradapter_cursoradapter.version`
- `java/resources/META-INF/androidx.customview_customview-poolingcontainer.version`
- `java/resources/META-INF/androidx.customview_customview.version`
- `java/resources/META-INF/androidx.documentfile_documentfile.version`
- `java/resources/META-INF/androidx.drawerlayout_drawerlayout.version`
- `java/resources/META-INF/androidx.dynamicanimation_dynamicanimation.version`
- `java/resources/META-INF/androidx.fragment_fragment.version`
- `java/resources/META-INF/androidx.interpolator_interpolator.version`
- `java/resources/META-INF/androidx.legacy_legacy-preference-v14.version`
- `java/resources/META-INF/androidx.legacy_legacy-support-core-ui.version`
- `java/resources/META-INF/androidx.legacy_legacy-support-core-utils.version`
- `java/resources/META-INF/androidx.legacy_legacy-support-v4.version`
- `java/resources/META-INF/androidx.lifecycle_lifecycle-extensions.version`
- `java/resources/META-INF/androidx.lifecycle_lifecycle-livedata-core-ktx.version`
- `java/resources/META-INF/androidx.lifecycle_lifecycle-livedata-core.version`
- `java/resources/META-INF/androidx.lifecycle_lifecycle-livedata.version`
- `java/resources/META-INF/androidx.lifecycle_lifecycle-process.version`
- `java/resources/META-INF/androidx.lifecycle_lifecycle-runtime.version`
- `java/resources/META-INF/androidx.lifecycle_lifecycle-service.version`
- `java/resources/META-INF/androidx.lifecycle_lifecycle-viewmodel-savedstate.version`
- `java/resources/META-INF/androidx.lifecycle_lifecycle-viewmodel.version`
- `java/resources/META-INF/androidx.loader_loader.version`
- `java/resources/META-INF/androidx.localbroadcastmanager_localbroadcastmanager.version`
- `java/resources/META-INF/androidx.media_media.version`
- `java/resources/META-INF/androidx.navigationevent_navigationevent.version`
- `java/resources/META-INF/androidx.preference_preference.version`
- `java/resources/META-INF/androidx.print_print.version`
- `java/resources/META-INF/androidx.profileinstaller_profileinstaller.version`
- `java/resources/META-INF/androidx.recyclerview_recyclerview.version`
- `java/resources/META-INF/androidx.room_room-runtime.version`
- `java/resources/META-INF/androidx.savedstate_savedstate.version`
- `java/resources/META-INF/androidx.slidingpanelayout_slidingpanelayout.version`
- `java/resources/META-INF/androidx.sqlite_sqlite-framework.version`
- `java/resources/META-INF/androidx.sqlite_sqlite.version`
- `java/resources/META-INF/androidx.startup_startup-runtime.version`
- `java/resources/META-INF/androidx.swiperefreshlayout_swiperefreshlayout.version`
- `java/resources/META-INF/androidx.tracing_tracing-ktx.version`
- `java/resources/META-INF/androidx.tracing_tracing.version`
- `java/resources/META-INF/androidx.transition_transition.version`
- `java/resources/META-INF/androidx.vectordrawable_vectordrawable-animated.version`
- `java/resources/META-INF/androidx.vectordrawable_vectordrawable.version`
- `java/resources/META-INF/androidx.versionedparcelable_versionedparcelable.version`
- `java/resources/META-INF/androidx.viewpager2_viewpager2.version`
- `java/resources/META-INF/androidx.viewpager_viewpager.version`
- `java/resources/META-INF/androidx.window_window-core.version`
- `java/resources/META-INF/androidx.window_window-java.version`
- `java/resources/META-INF/androidx.window_window.version`
- `java/resources/META-INF/androidx.work_work-runtime.version`
- `java/resources/META-INF/androidx/annotation/annotation/LICENSE.txt`
- `java/resources/META-INF/androidx/constraintlayout/constraintlayout-core/LICENSE.txt`
- `java/resources/META-INF/androidx/lifecycle/lifecycle-common-java8/LICENSE.txt`
- `java/resources/META-INF/androidx/lifecycle/lifecycle-common/LICENSE.txt`
- `java/resources/META-INF/androidx/room/room-common/LICENSE.txt`
- `java/resources/META-INF/com.google.android.material_material.version`
- `java/resources/META-INF/com/android/build/gradle/app-metadata.properties`
- `java/resources/META-INF/kotlinx_coroutines_android.version`
- `java/resources/META-INF/kotlinx_coroutines_core.version`
- `java/resources/META-INF/services/ca.q`
- `java/resources/META-INF/services/com.fasterxml.jackson.core.JsonFactory`
- `java/resources/META-INF/services/com.fasterxml.jackson.core.ObjectCodec`
- `java/resources/META-INF/services/x9.i0`
- `java/resources/META-INF/version-control-info.textproto`
- `java/resources/androidsupportmultidexversion.txt`
- `java/resources/assets/dexopt/baseline.prof`
- `java/resources/assets/dexopt/baseline.profm`
- `java/resources/assets/eracomic/license.txt`
- `java/resources/assets/eracomic/readme.txt`
- `java/resources/assets/eradjvu/license.txt`
- `java/resources/assets/eradjvu/readme.txt`
- `java/resources/assets/eraepub/fonts/cmex10.ttf`
- `java/resources/assets/eraepub/fonts/cmmi10.ttf`
- `java/resources/assets/eraepub/fonts/cmr10.ttf`
- `java/resources/assets/eraepub/fonts/cmsy10.ttf`
- `java/resources/assets/eraepub/fonts/msam10.ttf`
- `java/resources/assets/eraepub/fonts/msbm10.ttf`
- `java/resources/assets/eraepub/fonts/special.ttf`
- `java/resources/assets/eraepub/license.txt`
- `java/resources/assets/eraepub/readme.txt`
- `java/resources/assets/eramobi/license.txt`
- `java/resources/assets/eramobi/readme.txt`
- `java/resources/assets/erapdf/fonts/Dingbats.cff`
- `java/resources/assets/erapdf/fonts/NimbusMonoPS-Bold.cff`
- `java/resources/assets/erapdf/fonts/NimbusMonoPS-BoldItalic.cff`
- `java/resources/assets/erapdf/fonts/NimbusMonoPS-Italic.cff`
- `java/resources/assets/erapdf/fonts/NimbusMonoPS-Regular.cff`
- `java/resources/assets/erapdf/fonts/NimbusRoman-Bold.cff`
- `java/resources/assets/erapdf/fonts/NimbusRoman-BoldItalic.cff`
- `java/resources/assets/erapdf/fonts/NimbusRoman-Italic.cff`
- `java/resources/assets/erapdf/fonts/NimbusRoman-Regular.cff`
- `java/resources/assets/erapdf/fonts/NimbusSans-Bold.cff`
- `java/resources/assets/erapdf/fonts/NimbusSans-BoldOblique.cff`
- `java/resources/assets/erapdf/fonts/NimbusSans-Oblique.cff`
- `java/resources/assets/erapdf/fonts/NimbusSans-Regular.cff`
- `java/resources/assets/erapdf/fonts/StandardSymbolsPS.cff`
- `java/resources/assets/erapdf/license.txt`
- `java/resources/assets/erapdf/readme.txt`
- `java/resources/books.properties`
- `java/resources/classes.dex`
- `java/resources/classes2.dex`
- `java/resources/com/google/api/client/googleapis/google-api-client.properties`
- `java/resources/com/google/api/client/googleapis/google.jks`
- `java/resources/core-common.properties`
- `java/resources/feature-delivery.properties`
- `java/resources/firebase-analytics.properties`
- `java/resources/firebase-annotations.properties`
- `java/resources/firebase-auth-interop.properties`
- `java/resources/firebase-common.properties`
- `java/resources/firebase-components.properties`
- `java/resources/firebase-crashlytics.properties`
- `java/resources/firebase-encoders-json.properties`
- `java/resources/firebase-encoders.properties`
- `java/resources/firebase-installations-interop.properties`
- `java/resources/firebase-installations.properties`
- `java/resources/firebase-measurement-connector.properties`
- `java/resources/firebase-storage.properties`
- `java/resources/google-http-client.properties`
- `java/resources/kotlin/annotation/annotation.kotlin_builtins`
- `java/resources/kotlin/collections/collections.kotlin_builtins`
- `java/resources/kotlin/concurrent/atomics/atomics.kotlin_builtins`
- `java/resources/kotlin/coroutines/coroutines.kotlin_builtins`
- `java/resources/kotlin/internal/internal.kotlin_builtins`
- `java/resources/kotlin/kotlin.kotlin_builtins`
- `java/resources/kotlin/ranges/ranges.kotlin_builtins`
- `java/resources/kotlin/reflect/reflect.kotlin_builtins`
- `java/resources/mozilla/public-suffix-list.txt`
- `java/resources/okhttp3/internal/publicsuffix/NOTICE`
- `java/resources/okhttp3/internal/publicsuffix/publicsuffixes.gz`
- `java/resources/org/apache/commons/codec/language/bm/ash_approx_any.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_approx_common.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_approx_cyrillic.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_approx_english.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_approx_french.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_approx_german.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_approx_hebrew.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_approx_hungarian.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_approx_polish.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_approx_romanian.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_approx_russian.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_approx_spanish.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_exact_any.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_exact_approx_common.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_exact_common.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_exact_cyrillic.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_exact_english.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_exact_french.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_exact_german.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_exact_hebrew.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_exact_hungarian.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_exact_polish.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_exact_romanian.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_exact_russian.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_exact_spanish.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_hebrew_common.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_lang.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_languages.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_rules_any.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_rules_cyrillic.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_rules_english.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_rules_french.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_rules_german.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_rules_hebrew.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_rules_hungarian.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_rules_polish.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_rules_romanian.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_rules_russian.txt`
- `java/resources/org/apache/commons/codec/language/bm/ash_rules_spanish.txt`
- `java/resources/org/apache/commons/codec/language/bm/gen_approx_any.txt`
- `java/resources/org/apache/commons/codec/language/bm/gen_approx_arabic.txt`
- `java/resources/org/apache/commons/codec/language/bm/gen_approx_common.txt`
- `java/resources/org/apache/commons/codec/language/bm/gen_approx_cyrillic.txt`
- `java/resources/org/apache/commons/codec/language/bm/gen_approx_czech.txt`
- `java/resources/org/apache/commons/codec/language/bm/gen_approx_dutch.txt`
- `java/resources/org/apache/commons/codec/language/bm/gen_approx_english.txt`
- `java/resources/org/apache/commons/codec/language/bm/gen_approx_french.txt`
- `java/resources/org/apache/commons/codec/language/bm/gen_approx_german.txt`

## Matching Java source windows
### `java/sources/p132jb/AbstractC3568c.java`

```java
...
1: package p132jb;
2: 
3: import ac.EnumC0161c;
4: import android.graphics.RectF;
5: import android.os.ParcelFileDescriptor;
6: import java.io.File;
7: import java.io.IOException;
8: import java.util.ArrayList;
9: import java.util.Collections;
10: import java.util.HashSet;
11: import java.util.Iterator;
12: import java.util.List;
13: import java.util.Set;
14: import java.util.concurrent.atomic.AtomicBoolean;
15: import java.util.concurrent.locks.ReentrantLock;
16: import lc.AbstractExecutorC3837x;
17: import lc.C3826m;
18: import org.readera.App;
19: import org.readera.codec.exception.OreDefaultException;
20: import org.readera.codec.exception.OreRestartException;
21: import org.readera.jni.JniDoc;
22: import p024bc.C0823q;
23: import p024bc.C0825s;
24: import p079fc.AbstractC2646g0;
25: import p132jb.InterfaceC3575h;
...
276:                 if (App.f17415f) {
277:                     f13504N.m25237O(AbstractC6989a.m28332a(-50290080557443L));
278:                 }
279:                 this.f13515K = Collections.EMPTY_LIST;
280:             } else {
281:                 C6181L.m25211M(AbstractC6989a.m28332a(-50384569837955L) + arrayList.size() + AbstractC6989a.m28332a(-50436109445507L));
282:                 this.f13515K = arrayList;
283:             }
284:         }
285:     }
286: 
287:     /* renamed from: B0 */
288:     protected List m15389B0(int i10, String str, int i11) {
289:         boolean z10 = App.f17415f;
290:         int i12 = 0;
291:         if (z10) {
292:             f13504N.m25238P(AbstractC6989a.m28332a(-54610817657219L), Integer.valueOf(i10), str);
293:         }
294:         ArrayList m19115a = JniDoc.m19115a(this.f13532w, str);
295:         if (m19115a == null) {
296:             if (z10) {
297:                 f13504N.m25239i(AbstractC6989a.m28332a(-54786911316355L) + this.f13534y + AbstractC6989a.m28332a(-54932940204419L) + i10);
298:             }
299:             this.f13524o.add(Integer.valueOf(i10));
300:             mo15418o1();
301:             return Collections.EMPTY_LIST;
302:         }
303:         if (m19115a.isEmpty()) {
304:             if (z10) {
305:                 f13504N.m25239i(AbstractC6989a.m28332a(-54963004975491L) + i10);
306:             }
307:             return Collections.EMPTY_LIST;
308:         }
309:         if (z10) {
...
321:             int i13 = i12 + 4;
322:             float floatValue4 = ((Float) m19115a.get(i12 + 3)).floatValue();
323:             i12 += 5;
324:             arrayList.add(new C3920b(floatValue, floatValue2, floatValue3, floatValue4, (String) m19115a.get(i13), i11));
325:         }
326:         if (App.f17415f) {
327:             f13504N.m25237O(AbstractC6989a.m28332a(-55379616803203L) + arrayList.size());
328:         }
329:         return arrayList;
330:     }
331: 
332:     /* renamed from: D0 */
333:     protected List m15390D0(int i10, List list) {
334:         if (this.f13524o.contains(Integer.valueOf(i10))) {
335:             C6181L.m25211M(AbstractC6989a.m28332a(-52733916948867L));
336:             return Collections.EMPTY_LIST;
337:         }
338:         if (list == null || list.size() == 0) {
339:             C6181L.m25211M(AbstractC6989a.m28332a(-52901420673411L));
340:             return Collections.EMPTY_LIST;
341:         }
342:         ArrayList arrayList = new ArrayList();
343:         for (int i11 = 0; i11 < list.size(); i11++) {
344:             C3930l c3930l = (C3930l) list.get(i11);
345:             boolean z10 = App.f17415f;
346:             if (z10) {
347:                 C6181L.m25211M(AbstractC6989a.m28332a(-53073219365251L) + c3930l);
348:             }
349:             List mo15391F0 = mo15391F0(i10, c3930l);
350:             if (z10) {
351:                 C6181L.m25212N(AbstractC6989a.m28332a(-53240723089795L), Integer.valueOf(mo15391F0.size()));
352:             }
353:             arrayList.addAll(mo15391F0);
354:         }
355:         C6181L.m25212N(AbstractC6989a.m28332a(-53446881520003L), Integer.valueOf(arrayList.size()));
356:         return arrayList;
357:     }
358: 
359:     /* renamed from: F0 */
360:     protected List mo15391F0(int i10, C3930l c3930l) {
361:         return m15389B0(i10, i10 + AbstractC6989a.m28332a(-53683104721283L) + c3930l.m17023m() + AbstractC6989a.m28332a(-53691694655875L) + c3930l.m17024n() + AbstractC6989a.m28332a(-53700284590467L) + c3930l.m17022l(), c3930l.f15704F);
362:     }
363: 
364:     /* renamed from: G */
365:     protected void mo15392G(C3928j c3928j, String str) {
366:         if (c3928j.f15698u != null || c3928j.f15695r == null) {
367:             return;
368:         }
369:         if (!c3928j.m17017e()) {
370:             if (App.f17415f) {
371:                 C6181L.m25212N(AbstractC6989a.m28332a(-57174913132931L), c3928j.f15695r, Integer.valueOf(c3928j.f15688k));
372:             }
373:             c3928j.f15695r = null;
374:             return;
375:         }
376:         String m19101C = JniDoc.m19101C(this.f13532w, str);
377:         if (m19101C != null && !m19101C.equals(AbstractC6989a.m28332a(-57351006792067L)) && !m19101C.equals(AbstractC6989a.m28332a(-57359596726659L))) {
378:             if (App.f17415f) {
379:                 C6181L.m25212N(AbstractC6989a.m28332a(-57505625614723L), c3928j.f15695r, m19101C);
380:             }
381:             c3928j.f15695r = null;
382:             c3928j.f15698u = m19101C;
...
510:         while (i13 < m19133s.size()) {
511:             float floatValue = ((Float) m19133s.get(i13)).floatValue();
512:             float floatValue2 = ((Float) m19133s.get(i13 + 1)).floatValue();
513:             float floatValue3 = ((Float) m19133s.get(i13 + 2)).floatValue();
514:             float floatValue4 = ((Float) m19133s.get(i13 + 3)).floatValue();
515:             int intValue = ((Integer) m19133s.get(i13 + 4)).intValue();
516:             String str = (String) m19133s.get(i13 + 5);
517:             int intValue2 = ((Integer) m19133s.get(i13 + 6)).intValue();
518:             float floatValue5 = ((Float) m19133s.get(i13 + 7)).floatValue();
519:             int i14 = i13 + 9;
520:             float floatValue6 = ((Float) m19133s.get(i13 + 8)).floatValue();
521:             if (intValue == 1) {
522:                 c3923e2 = abstractC3568c.mo15414j(i10, new RectF(floatValue, floatValue2, floatValue3, floatValue4), str, str, intValue2, floatValue5, floatValue6);
523:                 c3923e2.f15693p = mo15482a1;
524:                 i12 = 1;
525:             } else {
526:                 if (str != null) {
527:                     i12 = 1;
528:                     c3923e = new C3923e(new RectF(floatValue, floatValue2, floatValue3, floatValue4), intValue, str, str.startsWith(AbstractC6989a.m28332a(-56079696472451L)) ? str.replaceFirst(AbstractC6989a.m28332a(-56139826014595L), AbstractC6989a.m28332a(-56199955556739L)) : str.startsWith(AbstractC6989a.m28332a(-56234315295107L)) ? str.replaceFirst(AbstractC6989a.m28332a(-56272970000771L), AbstractC6989a.m28332a(-56311624706435L)) : str.trim(), intValue2, floatValue5, floatValue6);
529:                 } else {
530:                     i12 = 1;
531:                     c3923e = new C3923e(new RectF(floatValue, floatValue2, floatValue3, floatValue4), intValue, null, null, intValue2, floatValue5, floatValue6);
532:                 }
533:                 c3923e2 = c3923e;
534:             }
535:             if (App.f17415f) {
536:                 String str2 = c3923e2.f15678A;
537:                 if (intValue == i12) {
538:                     if (floatValue5 != 0.0f || floatValue6 != 0.0f) {
539:                         f13504N.m25237O(AbstractC6989a.m28332a(-56345984444803L) + i11 + AbstractC6989a.m28332a(-56427588823427L) + c3923e2);
540:                     }
541:                 } else if (intValue != 2) {
542:                     f13504N.m25237O(AbstractC6989a.m28332a(-56633747253635L) + i11 + AbstractC6989a.m28332a(-56715351632259L) + c3923e2);
543:                 } else if (!str2.startsWith(AbstractC6989a.m28332a(-56436178758019L)) && !str2.startsWith(AbstractC6989a.m28332a(-56470538496387L)) && !str2.startsWith(AbstractC6989a.m28332a(-56509193202051L))) {
544:                     f13504N.m25237O(AbstractC6989a.m28332a(-56543552940419L) + i11 + AbstractC6989a.m28332a(-56625157319043L) + c3923e2);
545:                 }
546:             }
547:             arrayList.add(c3923e2);
548:             abstractC3568c = this;
549:             i13 = i14;
550:         }
551:         boolean z10 = App.f17415f;
552:         return arrayList;
...
886:     public boolean mo15409g0(C7004c c7004c, C7016e c7016e, C3826m c3826m) {
887:         try {
888:             mo15421q1(c7004c, c7016e, c3826m);
889:             return true;
890:         } catch (OreDefaultException e10) {
891:             C6181L.m25206F(e10);
892:             mo15418o1();
893:             return false;
894:         }
895:     }
896: 
897:     /* renamed from: g1 */
898:     protected List m15410g1(int i10) {
899:         if (this.f13522m.contains(Integer.valueOf(i10))) {
900:             return Collections.EMPTY_LIST;
901:         }
902:         ArrayList m19140z = JniDoc.m19140z(this.f13532w, i10);
903:         if (m19140z == null) {
904:             if (App.f17415f) {
905:                 f13504N.m25237O(AbstractC6989a.m28332a(-50479059118467L) + this.f13534y + AbstractC6989a.m28332a(-50590728268163L) + i10);
906:             }
907:             this.f13522m.add(Integer.valueOf(i10));
908:             mo15418o1();
909:             return Collections.EMPTY_LIST;
910:         }
911:         if (m19140z.isEmpty()) {
912:             return Collections.EMPTY_LIST;
913:         }
914:         if (App.f17415f) {
915:             int size = m19140z.size();
916:             if (size % 5 != 0) {
917:                 throw new IllegalStateException();
918:             }
919:             f13504N.mo15586c(AbstractC6989a.m28332a(-50620793039235L) + size + AbstractC6989a.m28332a(-50749642058115L) + (size / 5));
...
1009:                 C6181L.m25206F(new IllegalStateException());
1010:             } else {
1011:                 Iterator it = mo15399Q(c0823q, str, 1).iterator();
1012:                 while (it.hasNext()) {
1013:                     arrayList.add(new C3921c((C3925g) it.next(), m12032J, str));
1014:                 }
1015:             }
1016:         }
1017:         return arrayList;
1018:     }
1019: 
1020:     /* renamed from: j */
1021:     protected C3923e mo15414j(int i10, RectF rectF, String str, String str2, int i11, float f10, float f11) {
1022:         C3923e c3923e = new C3923e(rectF, 1, str, str2, i11, f10, f11);
1023:         c3923e.f15686f = C0825s.m5755r(i10, i11);
1024:         c3923e.f15689l = i10;
1025:         return c3923e;
1026:     }
1027: 
1028:     /* renamed from: k */
1029:     protected abstract void mo15415k();
1030: 
1031:     /* renamed from: l0 */
1032:     protected List m15416l0(int i10, List list) {
1033:         if (this.f13524o.contains(Integer.valueOf(i10))) {
1034:             return Collections.EMPTY_LIST;
1035:         }
1036:         if (list == null || list.isEmpty()) {
1037:             return Collections.EMPTY_LIST;
1038:         }
1039:         ArrayList arrayList = new ArrayList();
1040:         for (int i11 = 0; i11 < list.size(); i11++) {
1041:             C3928j c3928j = (C3928j) list.get(i11);
1042:             if (c3928j.f15688k >= 0) {
```

### `java/sources/ec/C2454k.java`

```java
...
1: package ec;
2: 
3: import ac.EnumC0159a;
4: import ac.EnumC0161c;
5: import android.graphics.Color;
6: import android.graphics.RectF;
7: import android.view.MotionEvent;
8: import android.view.animation.AccelerateInterpolator;
9: import ec.AbstractC2444a;
10: import ec.AbstractGestureDetectorOnGestureListenerC2452i;
11: import lb.C3810b;
12: import lc.AbstractC3818e;
13: import lc.AbstractC3834u;
14: import lc.C3826m;
15: import lc.C3827n;
16: import org.apache.commons.compress.archivers.zip.UnixStat;
17: import org.readera.App;
18: import org.readera.premium.R;
19: import org.readera.read.ReadActivity;
20: import org.readera.read.widget.ReadSurface;
21: import p024bc.C0813g0;
22: import p024bc.C0823q;
23: import p024bc.C0825s;
24: import p039d.C2282a;
25: import p052dc.C2343a;
26: import p171mb.C3928j;
27: import p171mb.C3930l;
...
53:     protected int f10012S;
54: 
55:     /* renamed from: T */
56:     protected final C2282a f10013T;
57: 
58:     public C2454k(ReadActivity readActivity, ReadSurface readSurface, boolean z10, C7016e c7016e) {
59:         super(readActivity, readSurface, true, z10, !z10, c7016e);
60:         this.f10008O = 1;
61:         this.f10013T = new C2282a(readActivity, new AccelerateInterpolator());
62:     }
63: 
64:     /* renamed from: F0 */
65:     private int m11470F0(int i10, boolean z10) {
66:         if (z10 && !C7004c.m28402b().f25463G) {
67:             return 0;
68:         }
69:         int m16713r = AbstractC3834u.m16713r(Math.abs(i10));
70:         if (m16713r < 100) {
71:             return 100;
72:         }
73:         if (m16713r < 400) {
74:             return 250;
75:         }
76:         if (m16713r < 650) {
77:             return 320;
78:         }
79:         return UnixStat.DEFAULT_FILE_PERM;
80:     }
81: 
82:     /* renamed from: N0 */
83:     private boolean m11471N0(float f10, float f11) {
84:         if (!this.f9972k || this.f9974m == 1.0f || this.f9969h == AbstractGestureDetectorOnGestureListenerC2452i.c.TURN) {
85:             return false;
86:         }
87:         boolean z10 = ((double) Math.abs(f11 / f10)) < 0.1d;
88:         if (this.f9956B.f25568k && !this.f9957C && z10) {
89:             return false;
90:         }
91:         if (f10 < 0.0f && m11434k0() == this.f9978q && z10) {
92:             return false;
93:         }
94:         return (f10 > 0.0f && m11434k0() == this.f9980s && z10) ? false : true;
95:     }
96: 
97:     /* renamed from: P0 */
98:     private void m11472P0(int i10) {
99:         if (App.f17415f) {
100:             AbstractGestureDetectorOnGestureListenerC2452i.f9948H.m25236L(AbstractC6989a.m28332a(-504959613479299L), Integer.valueOf(i10));
101:         }
102:         this.f10013T.m10894k(0, 0, i10, 0, 0);
103:         this.f10013T.m10886c();
104:     }
105: 
106:     /* renamed from: Q0 */
107:     private boolean m11473Q0(boolean z10) {
108:         m11405N();
109:         int i10 = z10 ? this.f10009P + 1 : this.f10009P - 1;
110:         boolean z11 = (i10 >= 0 && i10 < mo11478G0()) && (this.f9972k || (this.f9974m > 1.0f ? 1 : (this.f9974m == 1.0f ? 0 : -1)) <= 0) && !this.f9957C;
111:         m11450t0();
112:         if (App.f17415f && AbstractGestureDetectorOnGestureListenerC2452i.f9952L) {
113:             AbstractGestureDetectorOnGestureListenerC2452i.f9948H.m25243t(AbstractC6989a.m28332a(-505006858119555L), Boolean.valueOf(z11));
114:         }
115:         return z11;
116:     }
117: 
118:     @Override // ec.AbstractGestureDetectorOnGestureListenerC2452i
119:     /* renamed from: A0 */
120:     protected int mo11393A0(AbstractC2444a.b bVar, MotionEvent motionEvent, C3826m c3826m) {
121:         AbstractC2444a.m11348q(EnumC0161c.HORIZONTAL, c3826m);
122:         int m11345n = AbstractC2444a.m11345n(bVar, motionEvent.getX(), motionEvent.getY());
123:         if ((m11345n == R.id.d_ && C7004c.m28402b().f25547x0) || (m11345n == R.id.du && C7004c.m28402b().f25545w0)) {
124:             return m11345n;
125:         }
126:         int m11346o = AbstractC2444a.m11346o(bVar, motionEvent.getX(), motionEvent.getY());
127:         if ((m11346o == R.id.f0 || m11346o == R.id.f1) && !C7004c.m28402b().f25467I) {
128:             return 0;
129:         }
130:         if (C7004c.m28402b().f25471K == 1 && m11407P()) {
131:             if (m11346o == R.id.f1) {
132:                 return R.id.f0;
133:             }
134:             if (m11346o == R.id.f0) {
135:                 return R.id.f1;
136:             }
137:         }
138:         return m11346o;
139:     }
140: 
141:     /* renamed from: B0 */
142:     protected void mo11474B0(C2343a c2343a, int i10) {
143:         c2343a.m11040H(-i10, 0.0f);
144:     }
145: 
146:     @Override // ec.AbstractGestureDetectorOnGestureListenerC2452i
147:     /* renamed from: C */
148:     protected EnumC0161c mo11395C() {
149:         return EnumC0161c.HORIZONTAL;
150:     }
151: 
152:     /* renamed from: C0 */
153:     protected RectF mo11475C0(RectF rectF, RectF rectF2) {
154:         if (App.f17415f && AbstractGestureDetectorOnGestureListenerC2452i.f9952L) {
155:             AbstractGestureDetectorOnGestureListenerC2452i.f9948H.m25235K(AbstractC6989a.m28332a(-506312528177539L));
156:         }
157:         if (this.f9974m <= 8.0f) {
158:             return m11437m(rectF, rectF2);
159:         }
160:         RectF rectF3 = new RectF(rectF2);
161:         rectF3.offsetTo((rectF3.left - rectF.left) - (rectF3.right - rectF.right), rectF3.top - rectF.top);
162:         this.f9965d.getCanvas().m11038C(rectF3);
163:         return rectF3;
164:     }
165: 
166:     /* renamed from: D0 */
167:     protected RectF m11476D0(RectF rectF, RectF rectF2) {
168:         if (App.f17415f && AbstractGestureDetectorOnGestureListenerC2452i.f9952L) {
169:             AbstractGestureDetectorOnGestureListenerC2452i.f9948H.m25235K(AbstractC6989a.m28332a(-506235218766211L));
170:         }
171:         return m11437m(rectF, rectF2);
172:     }
173: 
174:     /* renamed from: E0 */
175:     protected boolean mo11477E0(float f10) {
176:         return f10 < 0.0f;
177:     }
178: 
179:     /* renamed from: G0 */
180:     protected int mo11478G0() {
181:         return this.f9965d.getPages().f5262c;
182:     }
183: 
184:     /* renamed from: H0 */
185:     protected RectF mo11479H0(float f10) {
186:         return this.f9965d.getPages().f5260a[this.f10009P].m5719p0(f10);
187:     }
188: 
189:     /* renamed from: I0 */
190:     public void m11480I0(C0825s c0825s, C3928j c3928j) {
191:         m11425f();
192:         int m5754q = C0825s.m5754q(c0825s.f5262c, c3928j.f15686f);
193:         mo11485O0(m5754q);
194:         this.f9975n = 1.0f;
195:         this.f9974m = 1.0f;
196:         RectF m5719p0 = c0825s.f5260a[m5754q].m5719p0(1.0f);
197:         mo11458x0();
198:         m11432j0(0, 0, (int) m5719p0.left, (int) m5719p0.top, 0);
199:     }
200: 
201:     /* renamed from: J0 */
202:     protected void mo11481J0() {
203:         if (App.f17415f && AbstractGestureDetectorOnGestureListenerC2452i.f9952L) {
204:             AbstractGestureDetectorOnGestureListenerC2452i.f9948H.m25235K(AbstractC6989a.m28332a(-505728412625283L));
205:         }
206:         m11472P0(0);
207:     }
208: 
209:     /* renamed from: K0 */
210:     protected int mo11482K0(int i10, float f10) {
211:         return AbstractC3818e.m16654c(i10 - ((int) f10), 0, m11399G());
212:     }
213: 
214:     @Override // ec.AbstractGestureDetectorOnGestureListenerC2452i
215:     /* renamed from: L */
216:     public boolean mo11403L(C3928j c3928j) {
217:         return this.f10009P == c3928j.f15687j;
218:     }
219: 
220:     /* renamed from: L0 */
221:     protected boolean mo11483L0(float f10) {
222:         return f10 > 0.0f;
223:     }
224: 
225:     /* renamed from: M0 */
226:     protected void mo11484M0() {
227:         if (App.f17415f && AbstractGestureDetectorOnGestureListenerC2452i.f9952L) {
228:             AbstractGestureDetectorOnGestureListenerC2452i.f9948H.m25235K(AbstractC6989a.m28332a(-505668283083139L));
229:         }
230:         m11472P0(m11399G());
231:     }
232: 
233:     @Override // ec.AbstractGestureDetectorOnGestureListenerC2452i
234:     /* renamed from: O */
235:     public void mo11406O(boolean z10) {
236:         AbstractGestureDetectorOnGestureListenerC2452i.c cVar;
237:         m11405N();
238:         boolean z11 = App.f17415f;
239:         if (z11 && AbstractGestureDetectorOnGestureListenerC2452i.f9952L) {
240:             AbstractGestureDetectorOnGestureListenerC2452i.f9948H.mo15587d(AbstractC6989a.m28332a(-505071282628995L), Boolean.valueOf(z10));
241:         }
242:         AbstractGestureDetectorOnGestureListenerC2452i.c cVar2 = this.f9969h;
243:         if (cVar2 == AbstractGestureDetectorOnGestureListenerC2452i.c.SCALE) {
244:             m11450t0();
245:             return;
246:         }
247:         if (cVar2 == AbstractGestureDetectorOnGestureListenerC2452i.c.TURN || cVar2 == (cVar = AbstractGestureDetectorOnGestureListenerC2452i.c.AUTOTURN) || !m11473Q0(z10)) {
248:             if (z11 && AbstractGestureDetectorOnGestureListenerC2452i.f9952L) {
249:                 AbstractGestureDetectorOnGestureListenerC2452i.f9948H.mo15586c(AbstractC6989a.m28332a(-505148592040323L));
250:             }
251:             m11450t0();
252:             return;
253:         }
254:         m11441o0(cVar);
255:         int m11399G = m11399G();
256:         if (z10) {
257:             if (z11 && AbstractGestureDetectorOnGestureListenerC2452i.f9952L) {
258:                 AbstractGestureDetectorOnGestureListenerC2452i.f9948H.mo15586c(AbstractC6989a.m28332a(-505466419620227L));
259:             }
260:             int i10 = this.f10009P;
261:             this.f10012S = i10 + 1;
262:             this.f10010Q = i10;
263:             this.f10011R = i10 + 1;
264:             this.f10013T.m10894k(m11399G, 0, -m11399G, 0, m11470F0(m11399G, true));
265:         } else {
266:             if (z11 && AbstractGestureDetectorOnGestureListenerC2452i.f9952L) {
267:                 AbstractGestureDetectorOnGestureListenerC2452i.f9948H.mo15586c(AbstractC6989a.m28332a(-505565203868035L));
268:             }
269:             int i11 = this.f10009P;
270:             this.f10012S = i11 - 1;
271:             this.f10010Q = i11 - 1;
272:             this.f10011R = i11;
273:             this.f10013T.m10894k(0, 0, m11399G, 0, m11470F0(m11399G, true));
274:         }
275:         this.f10013T.m10886c();
276:         m11450t0();
277:         this.f9965d.requestRender();
278:     }
279: 
280:     /* renamed from: O0 */
281:     protected void mo11485O0(int i10) {
282:         this.f10009P = i10;
283:         this.f10011R = i10;
284:         this.f10010Q = i10;
285:     }
286: 
287:     @Override // ec.AbstractGestureDetectorOnGestureListenerC2452i
288:     /* renamed from: V */
289:     public void mo11413V(C3810b c3810b, C3826m c3826m) {
290:         mo11452u0(c3810b.f15242a, c3826m, c3810b.f15243b);
291:     }
292: 
293:     /* JADX WARN: Removed duplicated region for block: B:12:0x0100  */
294:     @Override // ec.AbstractGestureDetectorOnGestureListenerC2452i
295:     /* renamed from: W */
296:     /*
297:         Code decompiled incorrectly, please refer to instructions dump.
298:     */
299:     public boolean mo11414W(C0825s c0825s, C3928j c3928j) {
300:         boolean z10;
301:         boolean z11 = App.f17415f;
302:         if (z11) {
303:             AbstractGestureDetectorOnGestureListenerC2452i.f9948H.m25236L(AbstractC6989a.m28332a(-502786360027523L), c3928j);
304:         }
305:         m11405N();
306:         m11441o0(AbstractGestureDetectorOnGestureListenerC2452i.c.JUMP);
307:         this.f9976o = -1;
308:         this.f9977p = -1;
309:         if (c3928j.f15686f < 0.0d) {
310:             if (z11) {
311:                 AbstractGestureDetectorOnGestureListenerC2452i.f9948H.m25235K(AbstractC6989a.m28332a(-502889439242627L));
312:             }
313:             mo11485O0(0);
314:             this.f9975n = 1.0f;
315:             this.f9974m = 1.0f;
316:             mo11458x0();
317:             m11432j0(0, 0, 0, 0, 0);
318:         } else if (c3928j.f15693p == m11392A()) {
319:             if (c3928j.f15687j < 0 || c3928j.f15687j >= c0825s.f5262c) {
320:                 if (z11) {
321:                     AbstractGestureDetectorOnGestureListenerC2452i.f9948H.m25239i(AbstractC6989a.m28332a(-503022583228803L));
322:                 }
323:                 m11480I0(c0825s, c3928j);
324:             } else {
325:                 if (z11) {
326:                     AbstractGestureDetectorOnGestureListenerC2452i.f9948H.m25235K(AbstractC6989a.m28332a(-503263101397379L));
327:                 }
328:                 mo11485O0(c3928j.f15687j);
329:                 float f10 = c3928j.f15690m;
330:                 float f11 = c3928j.f15691n;
331:                 float f12 = c3928j.f15692o;
332:                 this.f9975n = f12;
333:                 this.f9974m = f12;
334:                 RectF m5719p0 = c0825s.f5260a[c3928j.f15687j].m5719p0(this.f9974m);
335:                 int round = Math.round(m5719p0.left + (f10 * m5719p0.width()));
336:                 int round2 = Math.round(m5719p0.top + (f11 * m5719p0.height()));
337:                 mo11458x0();
338:                 m11432j0(0, 0, round, round2, 0);
339:             }
340:         } else {
341:             if (c3928j.m17025o()) {
342:                 if (z11) {
343:                     AbstractGestureDetectorOnGestureListenerC2452i.f9948H.m25235K(AbstractC6989a.m28332a(-503361885645187L));
344:                 }
345:                 z10 = false;
346:                 m11450t0();
347:                 this.f9965d.requestRender();
348:                 if (z11) {
349:                     AbstractGestureDetectorOnGestureListenerC2452i.f9948H.m25236L(AbstractC6989a.m28332a(-503688303159683L), Boolean.valueOf(z10), c3928j);
350:                 }
351:                 return z10;
352:             }
353:             if (z11) {
354:                 AbstractGestureDetectorOnGestureListenerC2452i.f9948H.m25235K(AbstractC6989a.m28332a(-503499324598659L));
355:             }
356:             m11480I0(c0825s, c3928j);
357:         }
358:         z10 = true;
359:         m11450t0();
360:         this.f9965d.requestRender();
361:         if (z11) {
362:         }
363:         return z10;
364:     }
365: 
366:     @Override // ec.AbstractGestureDetectorOnGestureListenerC2452i
367:     /* renamed from: b0 */
368:     protected void mo11419b0() {
369:         m11425f();
370:         super.mo11419b0();
371:         if (this.f9969h == AbstractGestureDetectorOnGestureListenerC2452i.c.TURN) {
372:             m11441o0(AbstractGestureDetectorOnGestureListenerC2452i.c.AUTOTURN);
373:             int m10890g = this.f10013T.m10890g();
374:             int m11399G = m11399G();
375:             if (m10890g < m11399G / 2) {
376:                 this.f10012S = this.f10011R;
377:                 this.f10013T.m10894k(m10890g, 0, -m10890g, 0, m11470F0(m10890g, false));
378:             } else {
379:                 this.f10012S = this.f10010Q;
380:                 int i10 = m11399G - m10890g;
381:                 this.f10013T.m10894k(m10890g, 0, i10, 0, m11470F0(i10, false));
382:             }
383:             this.f10013T.m10886c();
384:         }
385:     }
386: 
387:     @Override // ec.AbstractGestureDetectorOnGestureListenerC2452i
388:     /* renamed from: c0 */
389:     protected boolean mo11421c0(AbstractC2444a.b bVar, float f10, float f11) {
390:         C0823q m20160F0;
391:         m11425f();
392:         AbstractGestureDetectorOnGestureListenerC2452i.c cVar = this.f9969h;
393:         if (cVar != AbstractGestureDetectorOnGestureListenerC2452i.c.SCROLL && cVar != AbstractGestureDetectorOnGestureListenerC2452i.c.SCALE && cVar != AbstractGestureDetectorOnGestureListenerC2452i.c.BRIGHTNESS_SWIPE && cVar != AbstractGestureDetectorOnGestureListenerC2452i.c.TURN && cVar != AbstractGestureDetectorOnGestureListenerC2452i.c.AUTOTURN && (m20160F0 = this.f9965d.m20160F0(this.f10009P)) != null && m20160F0.m5646C() != null) {
394:             if (this.f9974m <= 1.0f) {
395:                 RectF m5646C = m20160F0.m5646C();
396:                 return m11423d0(bVar, m20160F0, (f10 - m5646C.left) / m5646C.width(), (f11 - m5646C.top) / m5646C.height());
397:             }
398:             int m11434k0 = m11434k0();
399:             int m11436l0 = m11436l0();
400:             C3826m m11398F = m11398F();
401:             RectF rectF = new RectF(m11434k0, m11436l0, m11434k0 + m11398F.m16672b(), m11436l0 + m11398F.m16671a());
402:             float f12 = rectF.left + f10;
403:             float f13 = rectF.top + f11;
404:             RectF m5719p0 = m20160F0.m5719p0(this.f9974m);
405:             float width = (f12 - m5719p0.left) / m5719p0.width();
406:             float height = (f13 - m5719p0.top) / m5719p0.height();
407:             if (m5719p0.contains(f12, f13)) {
408:                 return m11423d0(bVar, m20160F0, width, height);
409:             }
410:         }
411:         return false;
412:     }
413: 
414:     @Override // ec.AbstractGestureDetectorOnGestureListenerC2452i
415:     /* renamed from: j */
416:     public void mo11431j(C2343a c2343a, EnumC0159a enumC0159a) {
417:         if (C7004c.m28402b().f25519j0) {
418:             if (enumC0159a.f347f) {
419:                 c2343a.m11042d(-14540254);
420:                 return;
421:             } else {
422:                 c2343a.m11042d(-16777216);
423:                 return;
424:             }
425:         }
426:         if (enumC0159a == EnumC0159a.SEPIA) {
427:             c2343a.m11042d(-335160);
428:         } else if (enumC0159a == EnumC0159a.SEPIA_CONTRAST) {
429:             c2343a.m11042d(-728374);
430:         } else {
431:             c2343a.m11042d(enumC0159a.f349k);
432:         }
433:     }
434: 
435:     @Override // ec.AbstractGestureDetectorOnGestureListenerC2452i, android.view.GestureDetector.OnGestureListener
436:     public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f10, float f11) {
437:         boolean z10 = App.f17415f;
438:         if (z10) {
439:             AbstractGestureDetectorOnGestureListenerC2452i.f9948H.m25235K(AbstractC6989a.m28332a(-504585951324547L));
440:         }
441:         m11405N();
442:         if (!m11430i0(motionEvent2)) {
443:             if (z10 && AbstractGestureDetectorOnGestureListenerC2452i.f9952L) {
444:                 AbstractGestureDetectorOnGestureListenerC2452i.f9948H.m25235K(AbstractC6989a.m28332a(-504620311062915L));
445:             }
446:             m11450t0();
447:             return true;
448:         }
449:         if (this.f9969h == AbstractGestureDetectorOnGestureListenerC2452i.c.BRIGHTNESS_SWIPE) {
450:             m11450t0();
451:             return true;
452:         }
453:         if (m11471N0(f10, f11)) {
454:             if (z10 && AbstractGestureDetectorOnGestureListenerC2452i.f9952L) {
455:                 AbstractGestureDetectorOnGestureListenerC2452i.f9948H.m25235K(AbstractC6989a.m28332a(-504736275179907L));
456:             }
457:             super.onFling(motionEvent, motionEvent2, f10, f11);
458:             m11450t0();
459:             return true;
460:         }
461:         boolean mo11477E0 = mo11477E0(f10);
462:         this.f9964c.m19950k1(true);
463:         this.f9964c.m19952l1();
464:         if (this.f9969h != AbstractGestureDetectorOnGestureListenerC2452i.c.TURN) {
465:             if (z10 && AbstractGestureDetectorOnGestureListenerC2452i.f9952L) {
466:                 AbstractGestureDetectorOnGestureListenerC2452i.f9948H.mo15586c(AbstractC6989a.m28332a(-504869419166083L));
467:             }
468:             mo11406O(mo11477E0);
469:             m11450t0();
470:             return true;
471:         }
472:         m11441o0(AbstractGestureDetectorOnGestureListenerC2452i.c.AUTOTURN);
473:         int m10890g = this.f10013T.m10890g();
474:         int m11399G = m11399G();
475:         if (mo11477E0) {
476:             this.f10012S = this.f10011R;
477:             this.f10013T.m10894k(m10890g, 0, -m10890g, 0, m11470F0(m10890g, false));
478:         } else {
479:             this.f10012S = this.f10010Q;
480:             int i10 = m11399G - m10890g;
481:             this.f10013T.m10894k(m10890g, 0, i10, 0, m11470F0(i10, false));
482:         }
483:         this.f10013T.m10886c();
484:         m11450t0();
485:         return true;
486:     }
487: 
488:     @Override // ec.AbstractGestureDetectorOnGestureListenerC2452i, android.view.GestureDetector.OnGestureListener
489:     public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f10, float f11) {
490:         boolean z10 = App.f17415f;
491:         if (z10) {
492:             AbstractGestureDetectorOnGestureListenerC2452i.f9948H.m25235K(AbstractC6989a.m28332a(-503804267276675L));
493:         }
494:         m11405N();
495:         if (!m11430i0(motionEvent2)) {
496:             if (z10 && AbstractGestureDetectorOnGestureListenerC2452i.f9952L) {
497:                 AbstractGestureDetectorOnGestureListenerC2452i.f9948H.m25235K(AbstractC6989a.m28332a(-503842921982339L));
498:             }
499:             m11450t0();
500:             return true;
501:         }
502:         AbstractGestureDetectorOnGestureListenerC2452i.c cVar = this.f9969h;
503:         AbstractGestureDetectorOnGestureListenerC2452i.c cVar2 = AbstractGestureDetectorOnGestureListenerC2452i.c.TURN;
504:         if (cVar != cVar2 && m11416Y(motionEvent, f10, f11)) {
505:             if (z10 && AbstractGestureDetectorOnGestureListenerC2452i.f9952L) {
506:                 AbstractGestureDetectorOnGestureListenerC2452i.f9948H.m25235K(AbstractC6989a.m28332a(-503958886099331L));
507:             }
508:             m11401I();
509:             m11450t0();
510:             return true;
511:         }
512:         if (AbstractC2444a.m11347p(motionEvent.getX(), motionEvent.getY(), f10, f11)) {
513:             if (z10) {
514:                 AbstractGestureDetectorOnGestureListenerC2452i.f9948H.m25235K(AbstractC6989a.m28332a(-504040490477955L));
515:             }
516:             m11420c();
517:             m11450t0();
518:             return true;
519:         }
520:         m11401I();
521:         boolean mo11483L0 = mo11483L0(f10);
522:         if (this.f9969h != cVar2 && !m11473Q0(mo11483L0)) {
523:             if (z10 && AbstractGestureDetectorOnGestureListenerC2452i.f9952L) {
524:                 AbstractGestureDetectorOnGestureListenerC2452i.f9948H.m25235K(AbstractC6989a.m28332a(-504139274725763L));
525:             }
526:             super.onScroll(motionEvent, motionEvent2, f10, f11);
527:             m11450t0();
528:             return true;
529:         }
530:         if (this.f9969h != cVar2) {
531:             if (z10 && AbstractGestureDetectorOnGestureListenerC2452i.f9952L) {
532:                 AbstractGestureDetectorOnGestureListenerC2452i.f9948H.m25235K(AbstractC6989a.m28332a(-504336843221379L));
533:             }
534:             if (m11471N0(f10, f11)) {
535:                 if (z10 && AbstractGestureDetectorOnGestureListenerC2452i.f9952L) {
536:                     AbstractGestureDetectorOnGestureListenerC2452i.f9948H.m25236L(AbstractC6989a.m28332a(-504427037534595L), Float.valueOf(f10), Float.valueOf(f11));
537:                 }
538:                 super.onScroll(motionEvent, motionEvent2, f10, f11);
539:                 m11450t0();
540:                 return true;
541:             }
542:             m11441o0(cVar2);
543:             this.f9964c.m19950k1(true);
544:             this.f9964c.m19952l1();
545:             if (mo11483L0) {
546:                 int i10 = this.f10009P;
547:                 this.f10010Q = i10;
548:                 this.f10011R = i10 + 1;
549:                 mo11484M0();
550:             } else {
551:                 int i11 = this.f10009P;
552:                 this.f10010Q = i11 - 1;
553:                 this.f10011R = i11;
554:                 mo11481J0();
555:             }
556:         }
557:         int m10890g = this.f10013T.m10890g();
558:         int mo11482K0 = mo11482K0(m10890g, f10);
559:         if (m10890g != mo11482K0) {
560:             m11472P0(mo11482K0);
561:         }
562:         m11450t0();
563:         return true;
564:     }
565: 
566:     @Override // ec.AbstractGestureDetectorOnGestureListenerC2452i
567:     /* renamed from: u0 */
568:     public void mo11452u0(C0825s c0825s, C3826m c3826m, C0823q c0823q) {
569:         int i10 = c0823q == null ? 0 : c0823q.f5216c;
570:         int length = c0823q == null ? c0825s.f5260a.length : i10 + 1;
571:         int m16672b = c3826m.m16672b();
572:         int m16671a = c3826m.m16671a();
573:         while (i10 < length) {
574:             C0823q c0823q2 = c0825s.f5260a[i10];
575:             C3827n m5680U = c0823q2.m5680U();
576:             float m16674b = m5680U.m16674b();
577:             float m16673a = m5680U.m16673a();
578:             RectF rectF = new RectF(0.0f, 0.0f, m16674b, m16673a);
579:             float f10 = m16672b;
580:             if (f10 > m16674b) {
581:                 rectF.offset((f10 - m16674b) / 2.0f, 0.0f);
582:             }
583:             float f11 = m16671a;
584:             if (f11 > m16673a) {
585:                 rectF.offset(0.0f, (f11 - m16673a) / 2.0f);
586:             }
587:             c0823q2.m5659I0(rectF);
588:             i10++;
589:         }
590:     }
591: 
592:     /* JADX WARN: Removed duplicated region for block: B:68:0x025a  */
593:     /* JADX WARN: Removed duplicated region for block: B:72:0x0283  */
594:     /* JADX WARN: Removed duplicated region for block: B:74:0x0292  */
595:     /* JADX WARN: Removed duplicated region for block: B:79:0x02ef  */
596:     /* JADX WARN: Removed duplicated region for block: B:82:0x028a  */
597:     /* JADX WARN: Removed duplicated region for block: B:83:0x025e  */
598:     @Override // ec.AbstractGestureDetectorOnGestureListenerC2452i
599:     /* renamed from: v */
...
601:         Code decompiled incorrectly, please refer to instructions dump.
602:     */
603:     public void mo11453v(C3928j c3928j, int i10, C3936r c3936r, int i11) {
604:         int max;
605:         int i12;
606:         int i13;
607:         AbstractGestureDetectorOnGestureListenerC2452i.c cVar;
608:         int i14;
609:         int i15;
610:         int i16;
611:         boolean z10;
612:         boolean z11;
613:         RectF rectF;
614:         RectF m11476D0;
615:         AbstractGestureDetectorOnGestureListenerC2452i.c cVar2;
616:         C2454k c2454k;
617:         float f10;
618:         C2343a c2343a;
619:         RectF rectF2;
620:         RectF rectF3;
621:         C0813g0 c0813g0;
622:         C2454k c2454k2;
623:         boolean z12;
624:         C0825s pages = this.f9965d.getPages();
625:         m11405N();
626:         AbstractGestureDetectorOnGestureListenerC2452i.c cVar3 = this.f9969h;
627:         this.f10013T.m10886c();
628:         this.f9973l.m10886c();
629:         int m11434k0 = m11434k0();
630:         int m11436l0 = m11436l0();
631:         int i17 = this.f9986y;
632:         String str = this.f9987z;
633:         String str2 = this.f9955A;
634:         float f11 = this.f9974m;
635:         boolean z13 = true;
636:         boolean z14 = f11 == this.f9975n;
637:         int m16672b = m11398F().m16672b();
638:         RectF rectF4 = new RectF(m11434k0, m11436l0, m11434k0 + m16672b, m11436l0 + r4.m16671a());
639:         AbstractGestureDetectorOnGestureListenerC2452i.c cVar4 = AbstractGestureDetectorOnGestureListenerC2452i.c.TURN;
640:         if (cVar3 == cVar4 || cVar3 == AbstractGestureDetectorOnGestureListenerC2452i.c.AUTOTURN) {
641:             int min = Math.min(this.f10010Q, this.f10011R);
642:             max = Math.max(this.f10010Q, this.f10011R);
643:             i12 = min;
644:         } else {
645:             max = this.f10009P;
646:             i12 = max;
647:         }
648:         boolean z15 = (i12 == this.f9976o && max == this.f9977p) ? false : true;
649:         this.f9976o = i12;
650:         this.f9977p = max;
651:         int m10890g = this.f10013T.m10890g();
652:         int i18 = this.f10012S;
653:         int i19 = this.f10010Q;
654:         int i20 = this.f10011R;
655:         int i21 = this.f10009P;
656:         int i22 = max;
657:         C2467x c2467x = this.f9982u;
658:         int i23 = i12;
659:         C3930l c3930l = this.f9983v;
660:         C3932n c3932n = this.f9984w;
661:         boolean z16 = z14;
662:         C3937s c3937s = this.f9985x;
663:         m11450t0();
664:         RectF m5719p0 = pages.f5260a[i23].m5719p0(f11);
665:         boolean z17 = App.f17415f;
666:         if (z17 && AbstractGestureDetectorOnGestureListenerC2452i.f9952L) {
667:             i14 = 3;
668:             i13 = m10890g;
669:             cVar = cVar3;
670:             AbstractGestureDetectorOnGestureListenerC2452i.f9948H.mo15587d(AbstractC6989a.m28332a(-505792837134723L), Integer.valueOf(i21), Integer.valueOf(i19), Integer.valueOf(i20), m5719p0.toString());
671:         } else {
672:             i13 = m10890g;
673:             cVar = cVar3;
674:             i14 = 3;
675:         }
676:         float width = (rectF4.left - m5719p0.left) / m5719p0.width();
677:         float height = (rectF4.top - m5719p0.top) / m5719p0.height();
678:         C0813g0 m5576a = C0813g0.m5576a(f11);
679:         m11462z0(cVar, rectF4, c2467x, c3930l, c3932n, i23, i22, f11);
680:         m11454v0(c3928j, i23, i22, width, height, f11, z15);
681:         AbstractGestureDetectorOnGestureListenerC2452i.c cVar5 = cVar;
682:         m11442p0(cVar5, rectF4, c2467x, i23, i22, m5576a, f11, z16, i10, i11, c3937s);
683:         C2343a canvas = this.f9965d.getCanvas();
684:         int m11397E = m11397E(c3930l);
685:         if (cVar5 == cVar4 || cVar5 == AbstractGestureDetectorOnGestureListenerC2452i.c.AUTOTURN) {
686:             C0823q c0823q = pages.f5260a[i20];
687:             RectF m5719p02 = c0823q.m5719p0(f11);
688:             RectF m11437m = m11437m(rectF4, m5719p02);
689:             m11455w(rectF4, c0823q, m5719p02, m11437m, m5576a, f11);
690:             c2467x.m11536i(canvas, cVar5, c0823q, m11437m, m11397E);
691:             c2467x.m11537j(canvas, this.f9965d.getGuiTextures(), cVar5, c0823q, m11437m);
692:             mo11443q(c0823q, m11437m, str, i17);
693:             m11449t(c0823q, m11437m, str2);
694:             m11457x(c0823q, m11437m, i10, c3936r);
695:             m11451u(c0823q, m11437m, i11, str2);
696:             m11459y(c0823q, m11437m, c3937s);
697:             canvas.m11043e();
698:             if (this.f9958D) {
699:                 AbstractC2444a.m11342k(canvas, this.f9965d.getGuiTextures());
700:             }
701:             if (cVar5 != AbstractGestureDetectorOnGestureListenerC2452i.c.AUTOTURN) {
702:                 i15 = m16672b;
703:                 i16 = i13;
704:             } else if (i20 == i18) {
705:                 i16 = i13;
706:                 i15 = m16672b;
707:                 if (i16 < i15 * 0.02f) {
708:                     z10 = true;
709:                     i16 = 0;
710:                     float f12 = i15 / 2.0f;
711:                     float f13 = i16;
712:                     canvas.m11042d(Color.argb(f13 > f12 ? 60 : (int) ((f13 / f12) * 60.0f), 0, 0, 0));
713:                     canvas.m11056x();
714:                     mo11474B0(canvas, i15 - i16);
715:                     C0823q c0823q2 = pages.f5260a[i19];
716:                     RectF m5719p03 = c0823q2.m5719p0(f11);
717:                     z11 = false;
718:                     if (i21 > i19) {
719:                         rectF = rectF4;
720:                         m11476D0 = mo11475C0(rectF, m5719p03);
721:                     } else {
722:                         rectF = rectF4;
723:                         m11476D0 = m11476D0(rectF, m5719p03);
724:                     }
725:                     if (z17 || !AbstractGestureDetectorOnGestureListenerC2452i.f9952L) {
726:                         cVar2 = cVar5;
727:                         c2454k = this;
728:                         f10 = f11;
729:                         c2343a = canvas;
730:                         rectF2 = rectF;
731:                         rectF3 = m11476D0;
732:                         c0813g0 = m5576a;
733:                     } else {
734:                         cVar2 = cVar5;
735:                         C6181L c6181l = AbstractGestureDetectorOnGestureListenerC2452i.f9948H;
736:                         String m28332a = AbstractC6989a.m28332a(-506037650270595L);
737:                         Object[] objArr = new Object[i14];
738:                         objArr[0] = m5719p03;
739:                         objArr[1] = rectF;
740:                         objArr[2] = m11476D0;
741:                         c6181l.m25236L(m28332a, objArr);
742:                         rectF2 = rectF;
743:                         rectF3 = m11476D0;
744:                         c2454k = this;
745:                         f10 = f11;
746:                         c0813g0 = m5576a;
747:                         c0823q2 = c0823q2;
748:                         c2343a = canvas;
749:                     }
750:                     c2454k.m11455w(rectF2, c0823q2, m5719p03, rectF3, c0813g0, f10);
751:                     c2454k2 = c2454k;
752:                     C0823q c0823q3 = c0823q2;
753:                     C2343a c2343a2 = c2343a;
754:                     c2467x.m11536i(c2343a2, cVar2, c0823q3, rectF3, m11397E);
755:                     c2454k2.mo11443q(c0823q3, rectF3, str, i17);
756:                     c2454k2.m11449t(c0823q3, rectF3, str2);
757:                     c2454k2.m11457x(c0823q3, rectF3, i10, c3936r);
758:                     c2454k2.m11451u(c0823q3, rectF3, i11, str2);
759:                     c2454k2.m11459y(c0823q3, rectF3, c3937s);
760:                     c2343a2.m11043e();
761:                     if (c2454k2.f9958D) {
762:                         AbstractC2444a.m11342k(c2343a2, c2454k2.f9965d.getGuiTextures());
763:                     }
764:                     c2467x.m11537j(c2343a2, c2454k2.f9965d.getGuiTextures(), cVar2, c0823q3, rectF3);
765:                     c2343a2.m11055v();
766:                     z12 = z10;
767:                 }
768:             } else {
769:                 i15 = m16672b;
770:                 i16 = i13;
771:                 if (i15 - i16 < i15 * 0.02f) {
772:                     i16 = i15;
773:                     z10 = true;
774:                     float f122 = i15 / 2.0f;
775:                     float f132 = i16;
776:                     canvas.m11042d(Color.argb(f132 > f122 ? 60 : (int) ((f132 / f122) * 60.0f), 0, 0, 0));
777:                     canvas.m11056x();
778:                     mo11474B0(canvas, i15 - i16);
779:                     C0823q c0823q22 = pages.f5260a[i19];
780:                     RectF m5719p032 = c0823q22.m5719p0(f11);
781:                     z11 = false;
782:                     if (i21 > i19) {
783:                     }
784:                     if (z17) {
785:                     }
786:                     cVar2 = cVar5;
787:                     c2454k = this;
788:                     f10 = f11;
789:                     c2343a = canvas;
790:                     rectF2 = rectF;
791:                     rectF3 = m11476D0;
792:                     c0813g0 = m5576a;
793:                     c2454k.m11455w(rectF2, c0823q22, m5719p032, rectF3, c0813g0, f10);
794:                     c2454k2 = c2454k;
795:                     C0823q c0823q32 = c0823q22;
796:                     C2343a c2343a22 = c2343a;
797:                     c2467x.m11536i(c2343a22, cVar2, c0823q32, rectF3, m11397E);
798:                     c2454k2.mo11443q(c0823q32, rectF3, str, i17);
799:                     c2454k2.m11449t(c0823q32, rectF3, str2);
800:                     c2454k2.m11457x(c0823q32, rectF3, i10, c3936r);
801:                     c2454k2.m11451u(c0823q32, rectF3, i11, str2);
802:                     c2454k2.m11459y(c0823q32, rectF3, c3937s);
803:                     c2343a22.m11043e();
804:                     if (c2454k2.f9958D) {
805:                     }
806:                     c2467x.m11537j(c2343a22, c2454k2.f9965d.getGuiTextures(), cVar2, c0823q32, rectF3);
807:                     c2343a22.m11055v();
808:                     z12 = z10;
809:                 }
810:             }
811:             z10 = false;
812:             float f1222 = i15 / 2.0f;
813:             float f1322 = i16;
814:             canvas.m11042d(Color.argb(f1322 > f1222 ? 60 : (int) ((f1322 / f1222) * 60.0f), 0, 0, 0));
815:             canvas.m11056x();
816:             mo11474B0(canvas, i15 - i16);
817:             C0823q c0823q222 = pages.f5260a[i19];
818:             RectF m5719p0322 = c0823q222.m5719p0(f11);
819:             z11 = false;
820:             if (i21 > i19) {
821:             }
822:             if (z17) {
823:             }
824:             cVar2 = cVar5;
825:             c2454k = this;
826:             f10 = f11;
827:             c2343a = canvas;
828:             rectF2 = rectF;
829:             rectF3 = m11476D0;
830:             c0813g0 = m5576a;
831:             c2454k.m11455w(rectF2, c0823q222, m5719p0322, rectF3, c0813g0, f10);
832:             c2454k2 = c2454k;
833:             C0823q c0823q322 = c0823q222;
834:             C2343a c2343a222 = c2343a;
835:             c2467x.m11536i(c2343a222, cVar2, c0823q322, rectF3, m11397E);
836:             c2454k2.mo11443q(c0823q322, rectF3, str, i17);
837:             c2454k2.m11449t(c0823q322, rectF3, str2);
838:             c2454k2.m11457x(c0823q322, rectF3, i10, c3936r);
839:             c2454k2.m11451u(c0823q322, rectF3, i11, str2);
840:             c2454k2.m11459y(c0823q322, rectF3, c3937s);
841:             c2343a222.m11043e();
842:             if (c2454k2.f9958D) {
843:             }
844:             c2467x.m11537j(c2343a222, c2454k2.f9965d.getGuiTextures(), cVar2, c0823q322, rectF3);
845:             c2343a222.m11055v();
846:             z12 = z10;
847:         } else {
848:             C0823q c0823q4 = pages.f5260a[i21];
849:             RectF m5719p04 = c0823q4.m5719p0(f11);
850:             RectF m11437m2 = m11437m(rectF4, m5719p04);
851:             m11455w(rectF4, c0823q4, m5719p04, m11437m2, m5576a, f11);
852:             c2454k2 = this;
853:             c2467x.m11536i(canvas, cVar5, c0823q4, m11437m2, m11397E);
854:             c2467x.m11537j(canvas, c2454k2.f9965d.getGuiTextures(), cVar5, c0823q4, m11437m2);
855:             c2454k2.mo11443q(c0823q4, m11437m2, str, i17);
856:             c2454k2.m11449t(c0823q4, m11437m2, str2);
857:             c2454k2.m11457x(c0823q4, m11437m2, i10, c3936r);
858:             c2454k2.m11451u(c0823q4, m11437m2, i11, str2);
859:             c2454k2.m11459y(c0823q4, m11437m2, c3937s);
860:             canvas.m11043e();
861:             if (c2454k2.f9958D) {
862:                 AbstractC2444a.m11342k(canvas, c2454k2.f9965d.getGuiTextures());
863:             }
864:             z12 = false;
865:             z11 = false;
866:         }
867:         c2454k2.m11405N();
868:         boolean m10886c = c2454k2.f9973l.m10886c();
869:         boolean m10886c2 = c2454k2.f10013T.m10886c();
870:         if (c2454k2.f9969h == AbstractGestureDetectorOnGestureListenerC2452i.c.AUTOTURN && (!m10886c2 || z12)) {
871:             c2454k2.m11441o0(AbstractGestureDetectorOnGestureListenerC2452i.c.IDLE);
872:             c2454k2.f9982u = c2454k2.f9982u.m11547z(pages, pages.f5260a[c2454k2.f10012S], c2454k2.f9974m);
873:             c2454k2.f10009P = c2454k2.f10012S;
874:             if (z12) {
875:                 c2454k2.f10013T.m10884a();
876:             }
877:             m10886c2 = true;
878:         }
879:         int i24 = c2454k2.f9986y;
880:         if (i24 > 0) {
881:             c2454k2.f9986y = i24 - 1;
882:         }
883:         boolean z18 = (m10886c || m10886c2 || c2454k2.f9986y > 0) ? true : z11;
884:         if (m10886c || c2454k2.f9969h != AbstractGestureDetectorOnGestureListenerC2452i.c.AUTOSCROLL) {
885:             z13 = z18;
886:         } else {
887:             c2454k2.m11441o0(AbstractGestureDetectorOnGestureListenerC2452i.c.IDLE);
888:         }
889:         c2454k2.m11460y0();
890:         c2454k2.m11450t0();
891:         if (z13) {
892:             c2454k2.f9965d.requestRender();
893:         }
894:     }
895: 
896:     @Override // ec.AbstractGestureDetectorOnGestureListenerC2452i
897:     /* renamed from: x0 */
898:     public void mo11458x0() {
899:         m11405N();
900:         this.f9965d.getPages();
901:         C3826m m11398F = m11398F();
902:         int m16671a = m11398F.m16671a();
903:         int m16672b = m11398F.m16672b();
904:         RectF mo11479H0 = mo11479H0(this.f9974m);
905:         this.f9978q = 0;
906:         this.f9979r = 0;
907:         this.f9980s = Math.max(0, ((int) mo11479H0.right) - m16672b);
908:         this.f9981t = Math.max(0, ((int) mo11479H0.bottom) - m16671a);
909:         m11450t0();
910:     }
911: }
```

### `java/sources/ec/AbstractC2468y.java`

```java
...
1: package ec;
2: 
3: import android.graphics.PointF;
4: import android.graphics.RectF;
5: import java.util.HashMap;
6: import java.util.Map;
7: import org.readera.App;
8: import p024bc.C0823q;
9: import p024bc.C0825s;
10: import p132jb.C3569c0;
11: import p171mb.C3927i;
12: import unzen.android.utils.C6181L;
13: import za.AbstractC6989a;
14: 
15: /* renamed from: ec.y */
16: /* loaded from: classes2.dex */
17: public abstract class AbstractC2468y {
18: 
19:     /* renamed from: a */
20:     private static final C6181L f10042a = new C6181L(AbstractC6989a.m28332a(-525635586042243L));
21: 
22:     /* renamed from: a */
23:     public static RectF m11551a(RectF rectF, RectF rectF2) {
24:         RectF rectF3 = new RectF(rectF);
25:         rectF3.left *= rectF2.width();
26:         rectF3.right *= rectF2.width();
27:         rectF3.top *= rectF2.height();
28:         rectF3.bottom *= rectF2.height();
29:         rectF3.offset(rectF2.left, rectF2.top);
30:         return rectF3;
31:     }
32: 
33:     /* renamed from: b */
34:     private static boolean m11552b(RectF rectF, float f10, float f11) {
35:         float f12 = rectF.left;
36:         float f13 = rectF.right;
37:         if (f12 >= f13) {
38:             return false;
39:         }
40:         float f14 = rectF.top;
41:         float f15 = rectF.bottom;
42:         return f14 < f15 && f10 >= f12 && f10 <= f13 && f11 >= f14 && f11 <= f15;
43:     }
44: 
45:     /* JADX WARN: Multi-variable type inference failed */
46:     /* renamed from: c */
47:     public static boolean m11553c(C3569c0 c3569c0, int i10) {
48:         if (i10 == c3569c0.size() - 1) {
49:             return true;
50:         }
51:         C3927i c3927i = (C3927i) c3569c0.get(i10);
52:         C3927i c3927i2 = (C3927i) c3569c0.get(i10 + 1);
53:         if (c3927i.m16998d()) {
54:             return true;
55:         }
...
57:     }
58: 
59:     /* renamed from: d */
60:     public static int m11554d(C2467x c2467x) {
61:         if (c2467x.m11541p()) {
62:             return -1;
63:         }
64:         return (c2467x.f10025c.m11549b() ? c2467x.f10025c : c2467x.f10024b).f10032a.f5216c;
65:     }
66: 
67:     /* JADX WARN: Multi-variable type inference failed */
68:     /* renamed from: e */
69:     public static String m11555e(C2467x c2467x) {
70:         if (c2467x.m11541p()) {
71:             return null;
72:         }
73:         C0823q c0823q = (c2467x.f10025c.m11549b() ? c2467x.f10025c : c2467x.f10024b).f10032a;
74:         if (c0823q == null) {
75:             return null;
76:         }
77:         C2450g c2450g = (C2450g) c2467x.f10023a.get(c0823q);
78:         if (c2450g.isEmpty()) {
79:             return null;
80:         }
81:         StringBuilder sb2 = new StringBuilder();
82:         C3927i c3927i = (C3927i) c2450g.get(c2450g.size() - 1);
83:         sb2.append(c0823q.f5216c);
84:         sb2.append(AbstractC6989a.m28332a(-525489557154179L));
85:         sb2.append(c3927i.f15685j);
86:         sb2.append(AbstractC6989a.m28332a(-525498147088771L));
87:         sb2.append(1);
88:         if (App.f17415f) {
89:             C6181L.m25211M(AbstractC6989a.m28332a(-525506737023363L) + c3927i.f15685j);
90:         }
...
139:         if (c2467x.m11541p()) {
140:             return null;
141:         }
142:         C0823q c0823q = (c2467x.f10025c.m11550c() ? c2467x.f10025c : c2467x.f10024b).f10032a;
143:         if (c0823q == null) {
144:             return null;
145:         }
146:         C2450g c2450g = (C2450g) c2467x.f10023a.get(c0823q);
147:         if (c2450g.isEmpty()) {
148:             return null;
149:         }
150:         C3927i c3927i = (C3927i) c2450g.get(0);
151:         PointF pointF = new PointF(((RectF) c3927i).left, ((RectF) c3927i).top);
152:         if (!c2450g.f9940t && !c2450g.f9942v) {
153:             return pointF;
154:         }
155:         pointF.x = ((RectF) c3927i).right;
156:         return pointF;
157:     }
158: 
159:     /* JADX WARN: Multi-variable type inference failed */
160:     /* renamed from: h */
161:     public static boolean m11558h(C3569c0 c3569c0, int i10, int i11) {
162:         while (i10 <= i11) {
163:             C3927i c3927i = (C3927i) c3569c0.get(i10);
164:             if (!c3927i.f15684f.isEmpty() && c3927i.m17001g()) {
165:                 return true;
166:             }
167:             i10++;
168:         }
169:         return false;
170:     }
171: 
172:     /* JADX WARN: Multi-variable type inference failed */
173:     /* renamed from: i */
174:     public static boolean m11559i(C3569c0 c3569c0, int i10) {
175:         C3927i c3927i = (C3927i) c3569c0.get(i10);
176:         if (i10 < c3569c0.size() - 1) {
177:             C3927i c3927i2 = (C3927i) c3569c0.get(i10 + 1);
178:             if (((RectF) c3927i2).top == ((RectF) c3927i).top && ((RectF) c3927i2).left < ((RectF) c3927i).left) {
179:                 return true;
180:             }
181:         }
182:         if (i10 <= 0) {
183:             return false;
184:         }
185:         C3927i c3927i3 = (C3927i) c3569c0.get(i10 - 1);
186:         return ((RectF) c3927i3).top == ((RectF) c3927i).top && ((RectF) c3927i3).left > ((RectF) c3927i).left;
187:     }
188: 
189:     /* JADX WARN: Multi-variable type inference failed */
190:     /* renamed from: j */
191:     public static boolean m11560j(C3569c0 c3569c0, int i10) {
192:         C3927i c3927i = (C3927i) c3569c0.get(i10);
193:         if (AbstractC6989a.m28332a(-523500987296131L).equals(c3927i.f15684f)) {
194:             if (i10 > 0) {
195:                 C3927i c3927i2 = (C3927i) c3569c0.get(i10 - 1);
196:                 if (AbstractC6989a.m28332a(-523509577230723L).equals(c3927i2.f15684f)) {
197:                     if (((RectF) c3927i2).top >= ((RectF) c3927i).top && ((RectF) c3927i).left < ((RectF) c3927i2).left) {
198:                         if (App.f17415f) {
199:                             C6181L.m25212N(AbstractC6989a.m28332a(-523518167165315L), Boolean.TRUE);
200:                         }
201:                         return true;
202:                     }
203:                 } else if (((RectF) c3927i2).left == ((RectF) c3927i).left && ((RectF) c3927i2).top < ((RectF) c3927i).top) {
204:                     if (App.f17415f) {
205:                         C6181L.m25212N(AbstractC6989a.m28332a(-523732915530115L), Boolean.TRUE);
206:                     }
207:                     return true;
208:                 }
209:             } else if (c3569c0.size() > 1) {
210:                 C3927i c3927i3 = (C3927i) c3569c0.get(i10 + 1);
211:                 if (((RectF) c3927i3).top == ((RectF) c3927i).top && ((RectF) c3927i).left > ((RectF) c3927i3).left) {
212:                     if (App.f17415f) {
213:                         C6181L.m25212N(AbstractC6989a.m28332a(-523951958862211L), Boolean.TRUE);
214:                     }
215:                     return true;
216:                 }
217:             }
218:             if (App.f17415f) {
219:                 C6181L.m25212N(AbstractC6989a.m28332a(-524145232390531L), Boolean.FALSE);
220:             }
221:             return false;
222:         }
223:         if (i10 < c3569c0.size() - 1) {
224:             C3927i c3927i4 = (C3927i) c3569c0.get(i10 + 1);
225:             if (((RectF) c3927i4).left == ((RectF) c3927i).left && ((RectF) c3927i4).top > ((RectF) c3927i).top) {
226:                 if (App.f17415f) {
227:                     C6181L.m25212N(AbstractC6989a.m28332a(-524364275722627L), Boolean.TRUE);
228:                 }
229:                 return true;
230:             }
231:             if (App.f17415f) {
232:                 C6181L.m25212N(AbstractC6989a.m28332a(-524510304610691L), Boolean.FALSE, c3927i, c3927i4);
233:             }
234:         }
235:         if (i10 > 0) {
236:             C3927i c3927i5 = (C3927i) c3569c0.get(i10 - 1);
237:             if (((RectF) c3927i5).left == ((RectF) c3927i).left && ((RectF) c3927i5).top < ((RectF) c3927i).top) {
238:                 if (AbstractC6989a.m28332a(-524733642910083L).equals(c3927i5.f15684f)) {
239:                     if (App.f17415f) {
240:                         C6181L.m25212N(AbstractC6989a.m28332a(-524742232844675L), Boolean.FALSE);
241:                     }
242:                     return false;
243:                 }
244:                 if (App.f17415f) {
245:                     C6181L.m25212N(AbstractC6989a.m28332a(-524956981209475L), Boolean.TRUE);
246:                 }
247:                 return true;
248:             }
249:             if (App.f17415f) {
250:                 C6181L.m25212N(AbstractC6989a.m28332a(-525111600032131L), Boolean.FALSE, c3927i, c3927i5);
251:             }
252:         }
253:         return false;
254:     }
255: 
256:     /* renamed from: k */
257:     public static C2450g m11561k(C3569c0 c3569c0, int i10, int i11, int i12) {
258:         return new C2450g(c3569c0, i10, i11, i12);
...
299:         int i15;
300:         C3927i c3927i = (C3927i) c3569c0.get(i10);
301:         if (m11552b(c3927i, f10, f11)) {
302:             if (App.f17415f) {
303:                 f10042a.m25236L(AbstractC6989a.m28332a(-522233971943811L), Integer.valueOf(i10), c3569c0.m15440x(i10));
304:             }
305:             return m11562l(c3569c0, i10, i10, 0, z10, z11);
306:         }
307:         if (m11560j(c3569c0, i10)) {
308:             return m11567q(c3569c0, i10, f10, f11, z10, i11, i12, z11);
309:         }
310:         int i16 = -1;
311:         if (f11 >= ((RectF) c3927i).top) {
312:             if (f11 > ((RectF) c3927i).bottom) {
313:                 i13 = 1;
314:                 if (i13 != 1) {
315:                     i14 = i10 + 1;
316:                     int i17 = i10;
317:                     while (i14 <= i12) {
318:                         C3927i c3927i2 = (C3927i) c3569c0.get(i14);
319:                         float f12 = ((RectF) c3927i2).top;
320:                         if (f12 > f11) {
321:                             break;
322:                         }
323:                         if (f12 < f11 && ((RectF) c3927i2).bottom > f11 && ((RectF) c3927i2).left < f10 && ((RectF) c3927i2).right > f10) {
324:                             break;
325:                         }
326:                         i17 = i14;
327:                         i14++;
328:                     }
329:                     i14 = i17;
330:                     i15 = i10;
331:                 } else {
332:                     int i18 = i10;
333:                     for (int i19 = i10 - 1; i19 >= i11; i19--) {
334:                         C3927i c3927i3 = (C3927i) c3569c0.get(i19);
335:                         float f13 = ((RectF) c3927i3).bottom;
336:                         if (f13 < f11) {
337:                             break;
338:                         }
339:                         if (((RectF) c3927i3).top < f11 && f13 > f11 && ((RectF) c3927i3).left < f10 && ((RectF) c3927i3).right > f10) {
340:                             i15 = i19;
341:                             i14 = i10;
342:                             break;
343:                         }
344:                         i18 = i19;
345:                     }
346:                     i14 = i10;
347:                     i15 = i18;
348:                 }
349:                 if (App.f17415f) {
350:                     f10042a.m25236L(AbstractC6989a.m28332a(-522371410897283L), Integer.valueOf(i10), c3569c0.m15440x(i10), Integer.valueOf(i15), c3569c0.m15440x(i15), Integer.valueOf(i14), c3569c0.m15440x(i14), Integer.valueOf(i13));
351:                 }
352:                 return m11562l(c3569c0, i15, i14, i13, z10, z11);
353:             }
354:             if (f10 >= ((RectF) c3927i).left) {
355:                 if (((RectF) c3927i).right >= f10) {
356:                     if (App.f17415f) {
357:                         throw new IllegalStateException();
358:                     }
359:                     return null;
360:                 }
361:             }
362:         }
363:         i13 = i16;
364:         if (i13 != 1) {
365:         }
366:         if (App.f17415f) {
367:         }
368:         return m11562l(c3569c0, i15, i14, i13, z10, z11);
369:     }
370: 
371:     /* renamed from: o */
372:     private static C2450g m11565o(C3569c0 c3569c0, int i10, float f10, float f11, boolean z10, boolean z11) {
373:         return m11564n(c3569c0, i10, f10, f11, z10, 0, c3569c0.size() - 1, z11);
374:     }
375: 
376:     /* renamed from: p */
...
395:     /* JADX WARN: Removed duplicated region for block: B:21:0x009f  */
396:     /* JADX WARN: Removed duplicated region for block: B:33:0x0062  */
397:     /* JADX WARN: Removed duplicated region for block: B:5:0x0029  */
398:     /* renamed from: q */
399:     /*
400:         Code decompiled incorrectly, please refer to instructions dump.
401:     */
402:     private static C2450g m11567q(C3569c0 c3569c0, int i10, float f10, float f11, boolean z10, int i11, int i12, boolean z11) {
403:         int i13;
404:         int i14;
405:         int i15;
406:         C3927i c3927i = (C3927i) c3569c0.get(i10);
407:         if (f10 >= ((RectF) c3927i).left) {
408:             i13 = -1;
409:             if (((RectF) c3927i).right >= f10 && f11 >= ((RectF) c3927i).top) {
410:                 if (f11 <= ((RectF) c3927i).bottom) {
411:                     if (App.f17415f) {
412:                         throw new IllegalStateException();
413:                     }
414:                     return null;
415:                 }
416:             }
417:             if (i13 != 1) {
418:                 int i16 = i10 + 1;
419:                 int i17 = i10;
420:                 while (i16 <= i12) {
421:                     C3927i c3927i2 = (C3927i) c3569c0.get(i16);
422:                     float f12 = ((RectF) c3927i2).right;
423:                     if (f12 >= f10) {
424:                         if (f12 > f10 && ((RectF) c3927i2).left < f10 && ((RectF) c3927i2).top < f11 && ((RectF) c3927i2).bottom > f11) {
425:                             break;
426:                         }
427:                         i17 = i16;
428:                         i16++;
429:                     } else {
430:                         break;
431:                     }
432:                 }
433:                 i16 = i17;
434:                 i15 = i16;
435:                 i14 = i10;
436:             } else {
437:                 i14 = i10 - 1;
438:                 int i18 = i10;
439:                 while (i14 >= i11) {
440:                     C3927i c3927i3 = (C3927i) c3569c0.get(i14);
441:                     float f13 = ((RectF) c3927i3).left;
442:                     if (f13 <= f10) {
443:                         if (f13 < f10 && ((RectF) c3927i3).right > f10 && ((RectF) c3927i3).top < f11 && ((RectF) c3927i3).bottom > f11) {
444:                             i15 = i10;
445:                             break;
446:                         }
447:                         i18 = i14;
448:                         i14--;
449:                     } else {
450:                         break;
451:                     }
452:                 }
453:                 i15 = i10;
454:                 i14 = i18;
455:             }
456:             if (App.f17415f) {
457:                 f10042a.m25236L(AbstractC6989a.m28332a(-522697828411779L), Integer.valueOf(i10), c3569c0.m15440x(i10), Integer.valueOf(i14), c3569c0.m15440x(i14), Integer.valueOf(i15), c3569c0.m15440x(i15), Integer.valueOf(i13));
458:             }
459:             return m11562l(c3569c0, i14, i15, i13, z10, z11);
460:         }
461:         i13 = 1;
462:         if (i13 != 1) {
463:         }
464:         if (App.f17415f) {
...
569:     /* renamed from: t */
570:     public static int m11570t(C0823q c0823q, float f10, float f11, boolean z10) {
571:         C3569c0 m5715n0 = c0823q.m5715n0();
572:         if (m5715n0 == null || m5715n0.isEmpty()) {
573:             return -1;
574:         }
575:         return m11571u(c0823q, m5715n0, f10, f11, z10);
576:     }
577: 
578:     /* JADX WARN: Multi-variable type inference failed */
579:     /* renamed from: u */
580:     private static int m11571u(C0823q c0823q, C3569c0 c3569c0, float f10, float f11, boolean z10) {
581:         RectF m5641v = C0823q.m5641v(c0823q.m5646C(), f10, f11);
582:         int i10 = -1;
583:         int i11 = 0;
584:         for (int i12 = 0; i12 < c3569c0.size(); i12++) {
585:             C3927i c3927i = (C3927i) c3569c0.get(i12);
586:             if (c3927i.contains(f10, f11)) {
587:                 if (z10 || !c3927i.m17012t()) {
588:                     return i12;
589:                 }
590:                 return -1;
591:             }
592:             if (RectF.intersects(c3927i, m5641v) && (z10 || !c3927i.m17012t())) {
593:                 i11++;
594:                 i10 = i12;
595:             }
596:         }
597:         if (i11 == 1) {
598:             return i10;
599:         }
600:         return -1;
601:     }
602: 
603:     /* JADX WARN: Multi-variable type inference failed */
604:     /* renamed from: v */
605:     public static boolean m11572v(C3569c0 c3569c0, int i10) {
606:         if (i10 == 0) {
607:             return true;
608:         }
609:         C3927i c3927i = (C3927i) c3569c0.get(i10);
610:         C3927i c3927i2 = (C3927i) c3569c0.get(i10 - 1);
611:         if (c3927i.m16998d()) {
612:             return true;
613:         }
...
615:     }
616: 
617:     /* renamed from: w */
618:     public static int m11573w(C2467x c2467x) {
619:         if (c2467x.m11541p()) {
620:             return -1;
621:         }
622:         return (c2467x.f10025c.m11550c() ? c2467x.f10025c : c2467x.f10024b).f10032a.f5216c;
623:     }
624: 
625:     /* JADX WARN: Multi-variable type inference failed */
626:     /* renamed from: x */
627:     public static String m11574x(C2467x c2467x) {
628:         if (c2467x.m11541p()) {
629:             return null;
630:         }
631:         C0823q c0823q = (c2467x.f10025c.m11550c() ? c2467x.f10025c : c2467x.f10024b).f10032a;
632:         if (c0823q == null) {
633:             return null;
634:         }
635:         C2450g c2450g = (C2450g) c2467x.f10023a.get(c0823q);
636:         if (c2450g.isEmpty()) {
637:             return null;
638:         }
639:         StringBuilder sb2 = new StringBuilder();
640:         C3927i c3927i = (C3927i) c2450g.get(0);
641:         sb2.append(c0823q.f5216c);
642:         sb2.append(AbstractC6989a.m28332a(-525334938331523L));
643:         sb2.append(c3927i.f15685j);
644:         sb2.append(AbstractC6989a.m28332a(-525343528266115L));
645:         sb2.append(0);
646:         if (App.f17415f) {
647:             C6181L.m25211M(AbstractC6989a.m28332a(-525352118200707L) + c3927i.f15685j);
648:         }
```

### `java/sources/org/readera/jni/JniDoc.java`

```java
...
210: 
211:     /* renamed from: p */
212:     public static int m19130p(int i10, int i11) {
213:         return pageFreeJni(i10, i11);
214:     }
215: 
216:     private static native String pageDictCheckJni(int i10, int i11);
217: 
218:     private static native int pageDictRegJni(int i10, String str);
219: 
220:     private static native int pageFreeJni(int i10, int i11);
221: 
222:     private static native int pageGetSizeJni(int i10, int i11, int[] iArr);
223: 
224:     private static native ArrayList<Object> pageImagesJni(int i10, int i11);
225: 
226:     private static native ArrayList<Object> pageLinksJni(int i10, int i11);
227: 
228:     private static native int pageOpenJni(int i10, int i11, boolean z10);
229: 
230:     private static native ByteBuffer pageRenderJni(int i10, int i11, int i12, int i13, float[] fArr, boolean z10);
231: 
232:     private static native String pageSimpleTextJni(int i10, String str);
233: 
234:     private static native int pageSmartCropJni(int i10, int i11, float f10, float f11, float f12, float f13, float f14, float f15, float[] fArr);
235: 
236:     private static native int pageSpeechCropJni(int i10, int i11, float f10, float f11, float f12, float f13, float f14, float f15, float[] fArr);
237: 
238:     private static native int pageSpeechIngestJni(int i10, int i11, float f10, float f11);
239: 
240:     private static native ArrayList<Object> pageTextJni(int i10, int i11);
241: 
242:     private static native String pageXPathFromCoordsJni(int i10, int i11, float f10, float f11, boolean z10);
243: 
244:     private static native String pageXPathFromIndexJni(int i10, int i11);
245: 
246:     private static native String pageXPathFromRectJni(int i10, String str);
247: 
248:     private static native String pageXPathListToIndexJni(int i10, String str);
249: 
250:     private static native int pageXPathToIndexJni(int i10, String str, int i11);
251: 
252:     /* renamed from: q */
253:     public static int m19131q(int i10, int i11, int[] iArr) {
254:         return pageGetSizeJni(i10, i11, iArr);
255:     }
256: 
257:     /* renamed from: r */
258:     public static ArrayList m19132r(int i10, int i11) {
259:         return pageImagesJni(i10, i11);
260:     }
261: 
262:     private static native int rarExtractJni(int i10, String str, int i11, int i12, String str2);
263: 
264:     private static native ArrayList<Object> rarInfoJni(int i10, String str, int i11);
265: 
266:     /* renamed from: s */
267:     public static ArrayList m19133s(int i10, int i11) {
268:         return pageLinksJni(i10, i11);
269:     }
270: 
271:     private static native int searchCountJni(int i10, boolean z10);
272: 
273:     private static native ArrayList<Object> searchRectJni(int i10, String str);
274: 
275:     private static native ArrayList<Object> searchTextJni(int i10, String str);
...
284: 
285:     private static native ArrayList<Object> speechStartJni(int i10, int i11, String str);
286: 
287:     private static native ArrayList<Object> speechTextJni(int i10, int i11);
288: 
289:     /* renamed from: t */
290:     public static int m19134t(int i10, int i11, boolean z10) {
291:         return pageOpenJni(i10, i11, z10);
292:     }
293: 
294:     /* renamed from: u */
295:     public static ByteBuffer m19135u(int i10, int i11, int i12, int i13, float[] fArr, boolean z10) {
296:         return pageRenderJni(i10, i11, i12, i13, fArr, z10);
297:     }
298: 
299:     /* renamed from: v */
300:     public static String m19136v(int i10, String str) {
301:         return pageSimpleTextJni(i10, str);
302:     }
303: 
304:     /* renamed from: w */
305:     public static int m19137w(int i10, int i11, float f10, float f11, float f12, float f13, float f14, float f15, float[] fArr) {
306:         return pageSmartCropJni(i10, i11, f10, f11, f12, f13, f14, f15, fArr);
307:     }
308: 
309:     /* renamed from: x */
310:     public static int m19138x(int i10, int i11, float f10, float f11, float f12, float f13, float f14, float f15, float[] fArr) {
311:         return pageSpeechCropJni(i10, i11, f10, f11, f12, f13, f14, f15, fArr);
312:     }
313: 
314:     /* renamed from: y */
315:     public static int m19139y(int i10, int i11, float f10, float f11) {
316:         return pageSpeechIngestJni(i10, i11, f10, f11);
317:     }
318: 
319:     /* renamed from: z */
320:     public static ArrayList m19140z(int i10, int i11) {
321:         return pageTextJni(i10, i11);
322:     }
323: }
```

### `java/sources/org/readera/read/widget/ReadSurface.java`

```java
...
1629:         if (App.f17415f) {
1630:             C6181L.m25211M("ReadSurface requestRangeText");
1631:         }
1632:         queueEvent(new Runnable() { // from class: fc.t3
1633:             @Override // java.lang.Runnable
1634:             public final void run() {
1635:                 ReadSurface.m20110a(ReadSurface.this, c0825s, i10, i11);
1636:             }
1637:         });
1638:     }
1639: 
1640:     @Override // android.opengl.GLSurfaceView.Renderer
1641:     public void onDrawFrame(GL10 gl10) {
1642:         if (isInEditMode()) {
1643:             return;
1644:         }
1645:         C2343a c2343a = this.f18539l;
1646:         if (c2343a == null) {
1647:             if (App.f17415f) {
1648:                 throw new IllegalStateException();
1649:             }
1650:             return;
1651:         }
1652:         c2343a.m11045h();
1653:         this.f18539l.m11057y(-1);
1654:         if (this.f18543p == null) {
1655:             this.f18539l.m11042d(-16777216);
1656:         } else if (this.f18547t && this.f18548u) {
1657:             this.f18546s.mo11431j(this.f18539l, this.f18543p);
1658:             this.f18546s.mo11453v(this.f18541n.f20208Y, this.f18550w, this.f18530E, this.f18551x);
1659:         } else {
1660:             this.f18539l.m11042d(this.f18543p.f349k);
1661:         }
1662:         this.f18539l.m11055v();
...
1794:             return;
1795:         }
1796:         this.f18538k = Thread.currentThread();
1797:         setRenderMode(0);
1798:         Process.setThreadPriority(-4);
1799:         this.f18539l = new C2343a((GL11) gl10);
1800:         if (z10) {
1801:             f18525K.m25235K("GL-DEBUG: onSurfaceCreated OK");
1802:         }
1803:     }
1804: 
1805:     @Override // android.view.View
1806:     public boolean onTouchEvent(MotionEvent motionEvent) {
1807:         if (this.f18532G > 100) {
1808:             this.f18531F.invalidate();
1809:             this.f18532G = 0;
1810:         }
1811:         this.f18532G++;
1812:         if (this.f18536f.m19964s1()) {
1813:             this.f18546s.m11408Q();
1814:         }
1815:         if (!this.f18547t || !this.f18548u) {
1816:             return false;
1817:         }
1818:         this.f18546s.m11418a0(motionEvent);
1819:         this.f18536f.m19909C1();
1820:         return true;
1821:     }
1822: 
1823:     /* renamed from: p1 */
1824:     public void m20191p1(final C5139l c5139l) {
1825:         FutureTask futureTask = new FutureTask(new Callable() { // from class: fc.h2
1826:             @Override // java.util.concurrent.Callable
1827:             public final Object call() {
```

## Explicit questions
- Does the Java consumer normalize coordinates against the unrotated crop box or rotation-adjusted page dimensions?
- Where are page width/height and crop offsets obtained?
- Is bitmap orientation handled before or after text-rectangle normalization?
- Which exact coordinate transform is evidenced, and which cases remain unknown?