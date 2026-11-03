# ReadEra page-coordinate transform trace

Reuses the completed JADX artifact; APK decompilation is not repeated.

## Relevant decompiled class paths
- `java/sources/ec/AbstractC2468y.java`
- `java/sources/ec/C2454k.java`
- `java/sources/org/readera/jni/JniDoc.java`
- `java/sources/p024bc/C0823q.java`
- `java/sources/p132jb/AbstractC3568c.java`
- `java/sources/p146kb/C3684b.java`

## Page model, size and viewport source windows
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
56:         return c3927i.m17005k() && !c3927i2.m17005k();
57:     }
...
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
259:     }
260: 
...
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
377:     private static C2450g m11566p(C3569c0 c3569c0, int i10, float f10, float f11, boolean z10, boolean z11) {
378:         int i11 = ((C3569c0.a) c3569c0.f13537f.get(1)).f13542a;
...
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
465:         }
466:         return m11562l(c3569c0, i14, i15, i13, z10, z11);
...
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
614:         return c3927i.m17005k() && !c3927i2.m17005k();
615:     }
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
28: import p171mb.C3932n;
29: import p171mb.C3936r;
...
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
...
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
...
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
...
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
600:     /*
601:         Code decompiled incorrectly, please refer to instructions dump.
...
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
...
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

### `java/sources/org/readera/jni/JniDoc.java`

```java
...
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
276: 
277:     private static native int setConfigJni(int i10, int[] iArr, String[] strArr);
...
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
```

### `java/sources/p024bc/C0823q.java`

```java
...
1: package p024bc;
2: 
3: import ac.EnumC0161c;
4: import android.graphics.RectF;
5: import ec.C2451h;
6: import java.lang.ref.SoftReference;
7: import java.util.ArrayList;
8: import java.util.Collections;
9: import java.util.HashMap;
10: import java.util.HashSet;
11: import java.util.Iterator;
12: import java.util.List;
13: import java.util.Map;
14: import lc.AbstractC3834u;
15: import lc.C3826m;
16: import lc.C3827n;
17: import org.readera.App;
18: import p132jb.C3569c0;
19: import p132jb.C3592y;
20: import p171mb.C3919a;
21: import p171mb.C3920b;
22: import p171mb.C3921c;
23: import p171mb.C3922d;
24: import p171mb.C3923e;
25: import p171mb.C3925g;
26: import p171mb.C3926h;
27: import p171mb.C3929k;
...
75:     /* renamed from: j */
76:     private volatile C3569c0 f5223j;
77: 
78:     /* renamed from: k */
79:     private volatile C3827n f5224k;
80: 
81:     /* renamed from: l */
82:     private volatile C3826m f5225l;
83: 
84:     /* renamed from: m */
85:     private volatile RectF f5226m;
86: 
87:     /* renamed from: n */
88:     private C0811f0 f5227n;
89: 
90:     /* renamed from: v */
91:     private volatile SoftReference f5235v;
92: 
93:     /* renamed from: x */
94:     private volatile String[] f5237x;
95: 
96:     /* renamed from: z */
97:     private volatile SoftReference f5239z;
98: 
99:     /* renamed from: g */
100:     private int f5220g = 1;
101: 
102:     /* renamed from: o */
103:     private volatile List f5228o = new ArrayList();
104: 
105:     /* renamed from: p */
106:     private volatile List f5229p = new ArrayList();
107: 
108:     /* renamed from: q */
...
126:     /* renamed from: y */
127:     private volatile int f5238y = -1;
128: 
129:     /* renamed from: B */
130:     private volatile int f5208B = -1;
131: 
132:     /* renamed from: D */
133:     private volatile List f5210D = null;
134: 
135:     /* renamed from: E */
136:     private volatile RectF f5211E = null;
137: 
138:     /* renamed from: G */
139:     private final Map f5213G = new HashMap();
140: 
141:     /* renamed from: bc.q$a */
142:     public enum a {
143:         f5240f,
144:         f5241j,
145:         f5242k
146:     }
147: 
148:     public C0823q(Thread thread, int i10, a aVar) {
149:         this.f5215b = thread;
150:         this.f5216c = i10;
151:         this.f5214a = aVar;
152:         C0824r c0824r = new C0824r(this);
153:         this.f5218e = c0824r;
154:         this.f5219f = new C0824r(this);
155:         C0824r[] c0824rArr = new C0824r[C0813g0.f5129i];
156:         this.f5221h = c0824rArr;
157:         c0824rArr[0] = c0824r;
158:     }
159: 
...
327:             C6181L c6181l4 = f5206H;
328:             String m28332a5 = AbstractC6989a.m28332a(-492263690152323L);
329:             Object[] objArr5 = new Object[1];
330:             objArr5[z12 ? 1 : 0] = c3921c;
331:             c6181l4.m25236L(m28332a5, objArr5);
332:         }
333:         return c3921c;
334:     }
335: 
336:     /* renamed from: c0 */
337:     private RectF m5636c0(int i10, float f10, float f11, float f12) {
338:         float f13 = (i10 / 2.0f) / f12;
339:         float width = f13 / this.f5226m.width();
340:         float height = f13 / this.f5226m.height();
341:         RectF rectF = new RectF();
342:         rectF.left = Math.max(f10 - width, 0.0f);
343:         rectF.top = Math.max(f11 - height, 0.0f);
344:         rectF.right = Math.min(f10 + width, 1.0f);
345:         rectF.bottom = Math.min(f11 + height, 1.0f);
346:         return rectF;
347:     }
348: 
349:     /* renamed from: f */
350:     private List m5637f(List list) {
351:         if (list.isEmpty()) {
352:             return new ArrayList();
353:         }
354:         if (App.f17415f) {
355:             C6181L.m25231x(AbstractC6989a.m28332a(-489510616115587L), Integer.valueOf(this.f5216c), Integer.valueOf(list.size()));
356:         }
357:         HashMap hashMap = new HashMap();
358:         Iterator it = list.iterator();
359:         while (it.hasNext()) {
360:             C3921c c3921c = (C3921c) it.next();
361:             String str = c3921c.f15675j.f20162k + AbstractC6989a.m28332a(-489708184611203L) + c3921c.f15676k + AbstractC6989a.m28332a(-489716774545795L) + c3921c.f15682f;
362:             C3592y c3592y = (C3592y) hashMap.get(str);
363:             if (c3592y == null) {
364:                 c3592y = new C3592y(c3921c);
...
395:                 }
396:             }
397:         }
398:         if (App.f17415f) {
399:             C6181L.m25231x(AbstractC6989a.m28332a(-490094731667843L), Integer.valueOf(this.f5216c), Integer.valueOf(arrayList2.size()));
400:         }
401:         return arrayList2;
402:     }
403: 
404:     /* renamed from: h */
405:     private float m5638h(RectF rectF) {
406:         return (rectF.right - rectF.left) * (rectF.bottom - rectF.top);
407:     }
408: 
409:     /* renamed from: j */
410:     private boolean m5639j(List list, String str) {
411:         Iterator it = list.iterator();
412:         while (it.hasNext()) {
413:             if (((String) it.next()).equals(str)) {
414:                 return true;
415:             }
416:         }
417:         return false;
418:     }
419: 
420:     /* renamed from: u */
421:     private C3930l m5640u(String str) {
422:         for (C3930l c3930l : this.f5229p) {
423:             if (c3930l.mo17020j().equals(str)) {
424:                 return c3930l;
425:             }
426:         }
427:         return null;
428:     }
429: 
430:     /* renamed from: v */
431:     public static RectF m5641v(RectF rectF, float f10, float f11) {
432:         float min = Math.min(AbstractC3834u.f15296q / rectF.width(), AbstractC3834u.f15296q / rectF.height());
433:         RectF rectF2 = new RectF();
434:         rectF2.left = Math.max(f10 - min, 0.0f);
435:         rectF2.top = Math.max(f11 - min, 0.0f);
436:         rectF2.right = f10 + min;
437:         rectF2.bottom = f11 + min;
438:         return rectF2;
439:     }
440: 
441:     /* renamed from: A */
442:     public float m5642A() {
443:         return this.f5226m.height();
444:     }
445: 
446:     /* renamed from: A0 */
447:     public void m5643A0() {
448:         m5705i();
449:         if (this.f5220g > 1) {
450:             for (int i10 = 1; i10 < this.f5220g; i10++) {
451:                 C0824r c0824r = this.f5221h[i10];
452:                 if (c0824r != null) {
453:                     c0824r.m5746h();
454:                     this.f5221h[i10] = null;
455:                 }
456:             }
...
485:             if (i10 <= 0 || this.f5221h[i10] != null) {
486:                 break;
487:             } else {
488:                 this.f5220g = i10 - 1;
489:             }
490:         }
491:         this.f5220g = i10 + 1;
492:     }
493: 
494:     /* renamed from: C */
495:     public RectF m5646C() {
496:         return this.f5226m;
497:     }
498: 
499:     /* renamed from: C0 */
500:     public void m5647C0(C3929k c3929k) {
501:         if (this.f5228o.contains(c3929k)) {
502:             this.f5228o.remove(c3929k);
503:             m5708k();
504:             if (App.f17415f) {
505:                 C6181L.m25212N(AbstractC6989a.m28332a(-483450417260931L), Integer.valueOf(this.f5228o.size()));
506:             }
507:         }
508:     }
509: 
510:     /* renamed from: D */
511:     public C3930l m5648D(float f10, float f11) {
512:         List<C3920b> m5650E = m5650E();
513:         if (m5650E != null && !m5650E.isEmpty()) {
514:             ArrayList arrayList = new ArrayList();
515:             for (C3920b c3920b : m5650E) {
516:                 if (c3920b.contains(f10, f11)) {
517:                     arrayList.add(c3920b);
518:                 }
...
584:                         m5699e = m5699e2;
585:                     }
586:                     i10 = i14;
587:                     z10 = z11 ? 1 : 0;
588:                 }
589:                 if (App.f17415f) {
590:                     f5206H.m25235K(AbstractC6989a.m28332a(-487337362663811L) + f10 + AbstractC6989a.m28332a(-487487686519171L) + f11 + AbstractC6989a.m28332a(-487504866388355L) + str);
591:                 }
592:                 return m5640u(str);
593:             }
594:             RectF m5641v = m5641v(this.f5226m, f10, f11);
595:             C3920b c3920b6 = null;
596:             for (C3920b c3920b7 : m5650E) {
597:                 if (RectF.intersects(c3920b7, m5641v)) {
598:                     i10++;
599:                     c3920b6 = c3920b7;
600:                 }
601:             }
602:             if (i10 == 1) {
603:                 if (App.f17415f) {
604:                     f5206H.m25235K(AbstractC6989a.m28332a(-487513456322947L) + f10 + AbstractC6989a.m28332a(-487663780178307L) + f11 + AbstractC6989a.m28332a(-487680960047491L) + c3920b6);
605:                 }
606:                 return m5640u(c3920b6.f15682f);
607:             }
608:             if (App.f17415f) {
609:                 f5206H.m25235K(AbstractC6989a.m28332a(-487689549982083L) + f10 + AbstractC6989a.m28332a(-487814104033667L) + f11 + AbstractC6989a.m28332a(-487831283902851L) + this);
610:             }
611:         }
612:         return null;
613:     }
614: 
615:     /* renamed from: D0 */
616:     public void m5649D0(C3930l c3930l) {
617:         if (this.f5229p.contains(c3930l)) {
618:             this.f5229p.remove(c3930l);
619:             m5651E0(c3930l.mo17020j());
620:             if (App.f17415f) {
...
630: 
631:     /* renamed from: E0 */
632:     public void m5651E0(String str) {
633:         List<C3920b> m5650E = m5650E();
634:         ArrayList arrayList = new ArrayList();
635:         ArrayList arrayList2 = new ArrayList();
636:         if (str == null || m5650E == null) {
637:             return;
638:         }
639:         for (C3920b c3920b : m5650E) {
640:             String str2 = ((RectF) c3920b).left + AbstractC6989a.m28332a(-483832669350275L) + ((RectF) c3920b).top + AbstractC6989a.m28332a(-483841259284867L) + ((RectF) c3920b).right + AbstractC6989a.m28332a(-483849849219459L) + ((RectF) c3920b).bottom;
641:             if (!str.equals(c3920b.f15682f) || m5639j(arrayList2, str2)) {
642:                 arrayList.add(c3920b);
643:             } else {
644:                 arrayList2.add(str2);
645:             }
646:         }
647:         if (App.f17415f) {
648:             C6181L.m25212N(AbstractC6989a.m28332a(-483858439154051L), Integer.valueOf(m5650E.size()), Integer.valueOf(arrayList.size()));
649:         }
650:         m5661J0(arrayList);
651:     }
652: 
653:     /* renamed from: F */
654:     public List m5652F() {
655:         return this.f5229p;
656:     }
657: 
658:     /* renamed from: F0 */
659:     public void m5653F0(C3930l c3930l) {
660:         if (this.f5230q.indexOf(c3930l) == -1) {
661:             return;
662:         }
663:         this.f5230q.remove(c3930l);
...
666:         }
667:     }
668: 
669:     /* renamed from: G */
670:     public C3930l m5654G(float f10, float f11, float f12) {
671:         List m5658I = m5658I();
672:         List m5650E = m5650E();
673:         if (m5658I == null || m5650E == null || m5658I.isEmpty() || m5650E.isEmpty()) {
674:             return null;
675:         }
676:         RectF m5641v = m5641v(this.f5226m, f10, f11);
677:         ArrayList arrayList = new ArrayList();
678:         Iterator it = m5658I.iterator();
679:         while (it.hasNext()) {
680:             String mo17020j = ((C3930l) it.next()).mo17020j();
681:             Iterator it2 = m5650E.iterator();
682:             while (true) {
683:                 if (it2.hasNext()) {
684:                     C3920b c3920b = (C3920b) it2.next();
685:                     if (mo17020j.equals(c3920b.f15682f)) {
686:                         arrayList.add(c3920b);
687:                         break;
688:                     }
689:                 }
690:             }
691:         }
692:         int size = arrayList.size();
693:         int i10 = 0;
694:         C3920b c3920b2 = null;
695:         int i11 = 0;
696:         while (i11 < size) {
697:             Object obj = arrayList.get(i11);
698:             i11++;
699:             C3920b c3920b3 = (C3920b) obj;
700:             RectF m5656H = m5656H(((RectF) c3920b3).left, ((RectF) c3920b3).top, f12);
701:             if (m5656H.contains(f10, f11)) {
702:                 if (App.f17415f) {
703:                     f5206H.m25236L(AbstractC6989a.m28332a(-485542066334083L), Float.valueOf(f10), Float.valueOf(f11), c3920b3);
704:                 }
705:                 return m5640u(c3920b3.f15682f);
706:             }
707:             if (RectF.intersects(m5656H, m5641v)) {
708:                 if (App.f17415f) {
709:                     f5206H.m25236L(AbstractC6989a.m28332a(-485705275091331L), Float.valueOf(f10), Float.valueOf(f11), c3920b3);
710:                 }
711:                 i10++;
712:                 c3920b2 = c3920b3;
713:             }
714:         }
715:         if (i10 == 1) {
716:             return m5640u(c3920b2.f15682f);
717:         }
718:         if (!App.f17415f) {
719:             return null;
720:         }
721:         f5206H.m25236L(AbstractC6989a.m28332a(-485881368750467L), Float.valueOf(f10), Float.valueOf(f11), m5641v);
722:         return null;
723:     }
724: 
725:     /* renamed from: G0 */
726:     public void m5655G0(C3931m c3931m) {
727:         if (this.f5233t.contains(c3931m)) {
728:             if (this.f5233t.remove(c3931m)) {
729:                 if (App.f17415f) {
730:                     C6181L.m25212N(AbstractC6989a.m28332a(-483055280269699L), c3931m.toString());
...
733:                 C6181L.m25221l(AbstractC6989a.m28332a(-483166949419395L));
734:             }
735:             m5716o();
736:             if (App.f17415f) {
737:                 C6181L.m25212N(AbstractC6989a.m28332a(-483308683340163L), Integer.valueOf(this.f5233t.size()));
738:             }
739:         }
740:     }
741: 
742:     /* renamed from: H */
743:     public RectF m5656H(float f10, float f11, float f12) {
744:         return m5636c0(AbstractC3834u.m16696a(48.0f), f10, f11, f12);
745:     }
746: 
747:     /* renamed from: H0 */
748:     public void m5657H0(List list) {
749:         this.f5231r = list;
750:         if (App.f17415f) {
751:             C6181L.m25211M(AbstractC6989a.m28332a(-487839873837443L) + this.f5216c);
752:             Iterator it = list.iterator();
753:             while (it.hasNext()) {
754:                 C6181L.m25211M(AbstractC6989a.m28332a(-487938658085251L) + ((C3919a) it.next()));
755:             }
756:         }
757:     }
758: 
759:     /* renamed from: I */
760:     public List m5658I() {
761:         return this.f5230q;
762:     }
763: 
764:     /* renamed from: I0 */
765:     public void m5659I0(RectF rectF) {
766:         this.f5226m = rectF;
767:     }
768: 
769:     /* renamed from: J */
770:     public List m5660J() {
771:         return this.f5234u;
772:     }
773: 
774:     /* renamed from: J0 */
775:     public void m5661J0(List list) {
776:         this.f5232s = list;
777:         if (App.f17415f) {
778:             C6181L.m25211M(AbstractC6989a.m28332a(-488260780632451L) + this.f5216c);
779:             Iterator it = list.iterator();
780:             while (it.hasNext()) {
781:                 C6181L.m25211M(AbstractC6989a.m28332a(-488359564880259L) + ((C3920b) it.next()));
782:             }
783:         }
784:     }
785: 
786:     /* renamed from: K */
787:     public List m5662K() {
788:         return this.f5233t;
...
827:         this.f5239z = null;
828:         this.f5207A = null;
829:     }
830: 
831:     /* renamed from: M */
832:     public C3921c m5666M(float f10, float f11, float f12) {
833:         List<C3921c> m5678T = m5678T();
834:         if (m5678T == null || m5678T.isEmpty()) {
835:             return null;
836:         }
837:         RectF m5641v = m5641v(this.f5226m, f10, f11);
838:         ArrayList arrayList = new ArrayList();
839:         HashSet hashSet = new HashSet();
840:         for (C3921c c3921c : m5678T) {
841:             if (c3921c.f15675j.m21779H() && !hashSet.contains(c3921c.f15682f)) {
842:                 hashSet.add(c3921c.f15682f);
843:                 arrayList.add(c3921c);
844:             }
845:         }
846:         int size = arrayList.size();
847:         int i10 = 0;
848:         C3921c c3921c2 = null;
849:         int i11 = 0;
850:         while (i11 < size) {
851:             Object obj = arrayList.get(i11);
852:             i11++;
853:             C3921c c3921c3 = (C3921c) obj;
854:             RectF m5668N = m5668N(((RectF) c3921c3).left, ((RectF) c3921c3).bottom, f12);
855:             if (m5668N.contains(f10, f11)) {
856:                 if (App.f17415f) {
857:                     f5206H.m25236L(AbstractC6989a.m28332a(-486078937246083L), Float.valueOf(f10), Float.valueOf(f11), c3921c3);
858:                 }
859:                 return c3921c3;
860:             }
861:             if (RectF.intersects(m5668N, m5641v)) {
862:                 if (App.f17415f) {
863:                     f5206H.m25236L(AbstractC6989a.m28332a(-486242146003331L), Float.valueOf(f10), Float.valueOf(f11), c3921c3);
864:                 }
865:                 i10++;
866:                 c3921c2 = c3921c3;
867:             }
868:         }
869:         if (i10 == 1) {
870:             return c3921c2;
871:         }
872:         if (!App.f17415f) {
873:             return null;
874:         }
875:         f5206H.m25236L(AbstractC6989a.m28332a(-486418239662467L), Float.valueOf(f10), Float.valueOf(f11), m5641v);
876:         return null;
877:     }
878: 
879:     /* renamed from: M0 */
880:     public void m5667M0(List list) {
881:         if (App.f17415f) {
882:             C6181L.m25212N(AbstractC6989a.m28332a(-490292300163459L), Integer.valueOf(this.f5216c), Integer.valueOf(list.size()));
883:             Iterator it = list.iterator();
884:             while (it.hasNext()) {
885:                 C6181L.m25211M(AbstractC6989a.m28332a(-490446918986115L) + ((C3921c) it.next()));
886:             }
887:         }
888:         this.f5207A = new SoftReference(m5637f(list));
889:         this.f5239z = new SoftReference(list);
890:     }
891: 
892:     /* renamed from: N */
893:     public RectF m5668N(float f10, float f11, float f12) {
894:         return m5636c0(AbstractC3834u.m16696a(40.0f), f10, f11, f12);
895:     }
896: 
897:     /* renamed from: N0 */
898:     public void m5669N0(C3827n c3827n) {
899:         this.f5224k = c3827n;
900:     }
901: 
902:     /* renamed from: O0 */
903:     public void m5670O0(List list) {
904:         this.f5212F = new SoftReference(list);
905:         if (App.f17415f) {
906:             C6181L.m25211M(AbstractC6989a.m28332a(-493522115570051L) + this.f5216c);
907:             Iterator it = list.iterator();
908:             while (it.hasNext()) {
909:                 C6181L.m25211M(AbstractC6989a.m28332a(-493608014915971L) + ((C3922d) it.next()));
910:             }
911:         }
912:     }
913: 
914:     /* renamed from: P0 */
915:     public void m5671P0(List list) {
916:         this.f5222i = list;
...
973:             }
974:             if (arrayList.size() == 1) {
975:                 if (z10) {
976:                     f5206H.m25243t(AbstractC6989a.m28332a(-490880710683011L), ((C3921c) arrayList.get(0)).f15675j);
977:                 }
978:                 return (C3921c) arrayList.get(0);
979:             }
980:             if (arrayList.size() > 1) {
981:                 return m5635P(arrayList);
982:             }
983:             RectF m5641v = m5641v(this.f5226m, f10, f11);
984:             C3921c c3921c2 = null;
985:             for (C3921c c3921c3 : m5678T) {
986:                 if (RectF.intersects(c3921c3, m5641v)) {
987:                     i10++;
988:                     c3921c2 = c3921c3;
989:                 }
990:             }
991:             if (i10 == 1) {
992:                 if (App.f17415f) {
993:                     f5206H.m25235K(AbstractC6989a.m28332a(-491026739571075L) + f10 + AbstractC6989a.m28332a(-491202833230211L) + f11 + AbstractC6989a.m28332a(-491220013099395L) + c3921c2);
994:                 }
995:                 return c3921c2;
996:             }
997:             if (App.f17415f) {
998:                 f5206H.m25235K(AbstractC6989a.m28332a(-491228603033987L) + f10 + AbstractC6989a.m28332a(-491378926889347L) + f11 + AbstractC6989a.m28332a(-491396106758531L) + this);
999:             }
1000:         }
1001:         return null;
1002:     }
1003: 
1004:     /* renamed from: S0 */
1005:     public void m5677S0(RectF rectF) {
1006:         this.f5211E = rectF;
1007:     }
1008: 
1009:     /* renamed from: T */
1010:     public List m5678T() {
1011:         if (this.f5239z == null) {
1012:             return null;
1013:         }
1014:         return (List) this.f5239z.get();
1015:     }
1016: 
1017:     /* renamed from: T0 */
1018:     public void m5679T0(int i10) {
1019:         this.f5208B = i10;
1020:     }
1021: 
1022:     /* renamed from: U */
1023:     public C3827n m5680U() {
1024:         return this.f5224k;
1025:     }
1026: 
1027:     /* renamed from: U0 */
1028:     public void m5681U0(List list) {
...
1033:             while (it.hasNext()) {
1034:                 C6181L.m25211M(AbstractC6989a.m28332a(-488548543441283L) + ((C3926h) it.next()));
1035:             }
1036:         }
1037:     }
1038: 
1039:     /* renamed from: V */
1040:     public String m5682V(float f10, float f11) {
1041:         List<C3922d> m5684W = m5684W();
1042:         if (m5684W != null && !m5684W.isEmpty()) {
1043:             RectF m5641v = m5641v(this.f5226m, f10, f11);
1044:             int i10 = 0;
1045:             C3922d c3922d = null;
1046:             for (C3922d c3922d2 : m5684W) {
1047:                 if (c3922d2.contains(f10, f11)) {
1048:                     if (App.f17415f) {
1049:                         f5206H.m25235K(AbstractC6989a.m28332a(-493693914261891L) + f10 + AbstractC6989a.m28332a(-493801288444291L) + f11 + AbstractC6989a.m28332a(-493818468313475L) + c3922d2);
1050:                     }
1051:                     return c3922d2.f15677f;
1052:                 }
1053:                 if (RectF.intersects(c3922d2, m5641v)) {
1054:                     i10++;
1055:                     c3922d = c3922d2;
1056:                 }
1057:             }
1058:             if (i10 == 1) {
1059:                 if (App.f17415f) {
1060:                     f5206H.m25235K(AbstractC6989a.m28332a(-493827058248067L) + f10 + AbstractC6989a.m28332a(-493964497201539L) + f11 + AbstractC6989a.m28332a(-493981677070723L) + c3922d);
1061:                 }
1062:                 return c3922d.f15677f;
1063:             }
1064:             if (App.f17415f) {
1065:                 f5206H.m25235K(AbstractC6989a.m28332a(-493990267005315L) + f10 + AbstractC6989a.m28332a(-494101936155011L) + f11 + AbstractC6989a.m28332a(-494119116024195L) + this);
1066:             }
1067:         }
1068:         return null;
1069:     }
1070: 
1071:     /* renamed from: V0 */
1072:     public void m5683V0(List list) {
1073:         this.f5210D = list;
1074:     }
1075: 
1076:     /* renamed from: W */
...
1083: 
1084:     /* renamed from: W0 */
1085:     public void m5685W0(C0811f0 c0811f0) {
1086:         this.f5227n = c0811f0;
1087:     }
1088: 
1089:     /* renamed from: X */
1090:     public C3923e m5686X(float f10, float f11) {
1091:         List<C3923e> list = this.f5222i;
1092:         if (list != null && !list.isEmpty()) {
1093:             RectF m5641v = m5641v(this.f5226m, f10, f11);
1094:             int i10 = 0;
1095:             C3923e c3923e = null;
1096:             for (C3923e c3923e2 : list) {
1097:                 if (c3923e2.f15679x.contains(f10, f11)) {
1098:                     if (App.f17415f) {
1099:                         f5206H.m25235K(AbstractC6989a.m28332a(-482174811974019L) + f10 + AbstractC6989a.m28332a(-482277891189123L) + f11 + AbstractC6989a.m28332a(-482295071058307L) + c3923e2);
1100:                     }
1101:                     return c3923e2;
1102:                 }
1103:                 if (RectF.intersects(c3923e2.f15679x, m5641v)) {
1104:                     i10++;
1105:                     c3923e = c3923e2;
1106:                 }
1107:             }
1108:             if (i10 == 1) {
1109:                 if (App.f17415f) {
1110:                     f5206H.m25235K(AbstractC6989a.m28332a(-482303660992899L) + f10 + AbstractC6989a.m28332a(-482436804979075L) + f11 + AbstractC6989a.m28332a(-482453984848259L) + c3923e);
1111:                 }
1112:                 return c3923e;
1113:             }
1114:             if (App.f17415f) {
1115:                 f5206H.m25235K(AbstractC6989a.m28332a(-482462574782851L) + f10 + AbstractC6989a.m28332a(-482569948965251L) + f11 + AbstractC6989a.m28332a(-482587128834435L) + this);
1116:             }
1117:         }
1118:         return null;
1119:     }
1120: 
1121:     /* renamed from: X0 */
1122:     public void m5687X0(C3569c0 c3569c0) {
1123:         this.f5223j = c3569c0;
1124:     }
1125: 
1126:     /* renamed from: Y */
...
1243:                 f10 += m5638h(c3920b);
1244:             }
1245:         }
1246:         return f10;
1247:     }
1248: 
1249:     /* renamed from: e0 */
1250:     public C3925g m5700e0(float f10, float f11) {
1251:         List<C3925g> m5701f0 = m5701f0();
1252:         if (m5701f0 != null && !m5701f0.isEmpty()) {
1253:             RectF m5641v = m5641v(this.f5226m, f10, f11);
1254:             int i10 = 0;
1255:             C3925g c3925g = null;
1256:             for (C3925g c3925g2 : m5701f0) {
1257:                 if (c3925g2.contains(f10, f11)) {
1258:                     if (App.f17415f) {
1259:                         f5206H.m25235K(AbstractC6989a.m28332a(-488819126380931L) + f10 + AbstractC6989a.m28332a(-488947975399811L) + f11 + AbstractC6989a.m28332a(-488965155268995L) + c3925g2);
1260:                     }
1261:                     return c3925g2;
1262:                 }
1263:                 if (RectF.intersects(c3925g2, m5641v)) {
1264:                     i10++;
1265:                     c3925g = c3925g2;
1266:                 }
1267:             }
1268:             if (i10 == 1) {
1269:                 if (App.f17415f) {
1270:                     f5206H.m25235K(AbstractC6989a.m28332a(-488973745203587L) + f10 + AbstractC6989a.m28332a(-489132658993539L) + f11 + AbstractC6989a.m28332a(-489149838862723L) + c3925g);
1271:                 }
1272:                 return c3925g;
1273:             }
1274:             if (App.f17415f) {
1275:                 f5206H.m25235K(AbstractC6989a.m28332a(-489158428797315L) + f10 + AbstractC6989a.m28332a(-489291572783491L) + f11 + AbstractC6989a.m28332a(-489308752652675L) + this);
1276:             }
1277:         }
1278:         return null;
1279:     }
1280: 
1281:     /* renamed from: f0 */
1282:     public List m5701f0() {
1283:         if (this.f5235v == null) {
1284:             return null;
1285:         }
1286:         return (List) this.f5235v.get();
...
1314:     }
1315: 
1316:     /* renamed from: i */
1317:     void m5705i() {
1318:         if (App.f17415f && Thread.currentThread() != this.f5215b) {
1319:             throw new IllegalStateException();
1320:         }
1321:     }
1322: 
1323:     /* renamed from: i0 */
1324:     public RectF m5706i0() {
1325:         return this.f5211E;
1326:     }
1327: 
1328:     /* renamed from: j0 */
1329:     public int m5707j0() {
1330:         return this.f5208B;
1331:     }
1332: 
1333:     /* renamed from: k */
1334:     public void m5708k() {
1335:         this.f5231r = null;
1336:     }
1337: 
1338:     /* renamed from: k0 */
1339:     public List m5709k0() {
1340:         if (this.f5209C == null) {
1341:             return null;
1342:         }
1343:         return (List) this.f5209C.get();
1344:     }
1345: 
1346:     /* renamed from: l */
1347:     public void m5710l() {
...
1406:     /* renamed from: p */
1407:     public void m5718p() {
1408:         if (App.f17415f) {
1409:             C6181L.m25212N(AbstractC6989a.m28332a(-484687367842179L), Integer.valueOf(this.f5233t.size()));
1410:         }
1411:         this.f5233t = new ArrayList();
1412:         this.f5234u = null;
1413:     }
1414: 
1415:     /* renamed from: p0 */
1416:     public RectF m5719p0(float f10) {
1417:         RectF rectF = new RectF();
1418:         m5721q0(f10, rectF);
1419:         return rectF;
1420:     }
1421: 
1422:     /* renamed from: q */
1423:     public void m5720q() {
1424:         if (this.f5210D == null) {
1425:             return;
1426:         }
1427:         if (App.f17415f) {
1428:             C6181L.m25212N(AbstractC6989a.m28332a(-484829101762947L), Integer.valueOf(this.f5210D.size()));
1429:         }
1430:         this.f5210D = null;
1431:         this.f5208B = -1;
1432:     }
1433: 
1434:     /* renamed from: q0 */
1435:     public void m5721q0(float f10, RectF rectF) {
1436:         if (f10 == 1.0f) {
1437:             rectF.set(this.f5226m);
1438:             return;
1439:         }
1440:         if (C7004c.m28402b().f25479O == EnumC0161c.VERTICAL) {
1441:             rectF.left = this.f5226m.left * f10;
1442:             rectF.right = this.f5226m.right * f10;
1443:             rectF.top = this.f5226m.top * f10;
1444:             rectF.bottom = this.f5226m.bottom * f10;
1445:             return;
1446:         }
1447:         C0811f0 c0811f0 = this.f5227n;
1448:         if (c0811f0 != null) {
1449:             c0811f0.m5568i(this, f10, rectF);
1450:             return;
1451:         }
1452:         float m16674b = this.f5224k.m16674b();
1453:         float m16673a = this.f5224k.m16673a();
1454:         float f11 = (this.f5226m.left * 2.0f) + m16674b;
1455:         float f12 = (this.f5226m.top * 2.0f) + m16673a;
1456:         float f13 = m16674b * f10;
1457:         float f14 = m16673a * f10;
1458:         RectF rectF2 = new RectF(0.0f, 0.0f, f13, f14);
1459:         if (f11 > f13) {
1460:             rectF2.offset((f11 - f13) / 2.0f, 0.0f);
1461:         }
1462:         if (f12 > f14) {
1463:             rectF2.offset(0.0f, (f12 - f14) / 2.0f);
1464:         }
1465:         if (App.f17415f) {
1466:             C6181L.m25212N(AbstractC6989a.m28332a(-482045962955139L), this.f5226m, rectF2);
1467:         }
1468:         rectF.left = rectF2.left;
1469:         rectF.top = rectF2.top;
1470:         rectF.right = rectF2.right;
1471:         rectF.bottom = rectF2.bottom;
1472:     }
1473: 
1474:     /* renamed from: r */
1475:     public void m5722r(C3930l c3930l) {
1476:         if (this.f5229p.indexOf(c3930l) == -1) {
1477:             return;
1478:         }
1479:         m5724s(c3930l.mo17020j(), c3930l.f15704F);
1480:         if (App.f17415f) {
1481:             C6181L.m25212N(AbstractC6989a.m28332a(-483708115298691L), Integer.valueOf(this.f5229p.size()));
...
1489: 
1490:     /* renamed from: s */
1491:     public void m5724s(String str, int i10) {
1492:         List<C3920b> m5650E = m5650E();
1493:         ArrayList arrayList = new ArrayList();
1494:         ArrayList arrayList2 = new ArrayList();
1495:         if (str == null || m5650E == null) {
1496:             return;
1497:         }
1498:         for (C3920b c3920b : m5650E) {
1499:             String str2 = ((RectF) c3920b).left + AbstractC6989a.m28332a(-484013057976707L) + ((RectF) c3920b).top + AbstractC6989a.m28332a(-484021647911299L) + ((RectF) c3920b).right + AbstractC6989a.m28332a(-484030237845891L) + ((RectF) c3920b).bottom;
1500:             if (str.equals(c3920b.f15682f) && !m5639j(arrayList2, str2)) {
1501:                 arrayList2.add(str2);
1502:                 c3920b.f15674j = i10;
1503:             }
1504:             arrayList.add(c3920b);
1505:         }
1506:         if (App.f17415f) {
1507:             C6181L.m25212N(AbstractC6989a.m28332a(-484038827780483L), Integer.valueOf(m5650E.size()), Integer.valueOf(arrayList.size()));
1508:         }
1509:         m5661J0(arrayList);
1510:     }
1511: 
1512:     /* renamed from: s0 */
1513:     public boolean m5725s0() {
1514:         if (this.f5228o.size() == 0) {
1515:             return false;
1516:         }
1517:         Iterator it = this.f5228o.iterator();
1518:         while (it.hasNext()) {
1519:             if (((C3929k) it.next()).f15687j == this.f5216c) {
1520:                 return true;
1521:             }
1522:         }
1523:         return false;
1524:     }
1525: 
1526:     /* renamed from: t */
1527:     public void m5726t(C0824r c0824r) {
1528:         m5705i();
1529:         int m5742c = c0824r.m5742c();
1530:         int i10 = 0;
1531:         while (true) {
1532:             RectF[] rectFArr = C0824r.f5244p;
1533:             if (i10 >= rectFArr.length) {
1534:                 this.f5220g = Math.max(this.f5220g, m5742c);
1535:                 return;
1536:             }
1537:             C0824r[] c0824rArr = this.f5221h;
1538:             if (c0824rArr[m5742c] == null) {
1539:                 c0824rArr[m5742c] = new C0824r(this, c0824r, m5742c, rectFArr[i10]);
1540:             }
1541:             i10++;
1542:             m5742c++;
1543:         }
1544:     }
1545: 
1546:     /* renamed from: t0 */
1547:     public boolean m5727t0() {
1548:         return this.f5229p.size() > 0;
1549:     }
1550: 
1551:     public String toString() {
1552:         return AbstractC6989a.m28332a(-494381109029251L) + this.f5216c + AbstractC6989a.m28332a(-494432648636803L) + this.f5226m + '}';
1553:     }
1554: 
1555:     /* renamed from: u0 */
...
1559: 
1560:     /* renamed from: v0 */
1561:     public boolean m5729v0() {
1562:         return this.f5233t.size() > 0;
1563:     }
1564: 
1565:     /* renamed from: w */
1566:     public C3929k m5730w(float f10, float f11, float f12) {
1567:         List list = this.f5228o;
1568:         if (list != null && !list.isEmpty()) {
1569:             RectF m5641v = m5641v(this.f5226m, f10, f11);
1570:             RectF m5732x = m5732x(f12);
1571:             if (m5732x.contains(f10, f11)) {
1572:                 if (App.f17415f) {
1573:                     f5206H.m25236L(AbstractC6989a.m28332a(-484970835683715L), Float.valueOf(f10), Float.valueOf(f11), m5732x);
1574:                 }
1575:                 return (C3929k) list.get(0);
1576:             }
1577:             if (RectF.intersects(m5732x, m5641v)) {
1578:                 if (App.f17415f) {
1579:                     f5206H.m25236L(AbstractC6989a.m28332a(-485134044440963L), Float.valueOf(f10), Float.valueOf(f11), m5732x);
1580:                 }
1581:                 return (C3929k) list.get(0);
1582:             }
1583:             if (App.f17415f) {
1584:                 f5206H.m25236L(AbstractC6989a.m28332a(-485310138100099L), Float.valueOf(f10), Float.valueOf(f11), m5641v, m5732x);
1585:             }
1586:         }
1587:         return null;
1588:     }
1589: 
1590:     /* renamed from: w0 */
1591:     public boolean m5731w0() {
1592:         return this.f5237x != null && this.f5237x.length > 0;
1593:     }
1594: 
1595:     /* renamed from: x */
1596:     public RectF m5732x(float f10) {
1597:         int m16696a = AbstractC3834u.m16696a(48.0f);
1598:         RectF rectF = new RectF();
1599:         float f11 = m16696a / f10;
1600:         rectF.left = (this.f5226m.width() - f11) / this.f5226m.width();
1601:         rectF.right = 1.0f;
1602:         rectF.top = 0.0f;
1603:         rectF.bottom = f11 / this.f5226m.height();
1604:         return rectF;
1605:     }
1606: 
1607:     /* renamed from: x0 */
1608:     public boolean m5733x0() {
1609:         return m5723r0() || m5727t0() || m5729v0();
1610:     }
1611: 
1612:     /* renamed from: y */
1613:     public List m5734y() {
1614:         return this.f5231r;
1615:     }
1616: 
1617:     /* renamed from: y0 */
1618:     public void m5735y0(int i10, int i11, String str) {
1619:         this.f5213G.put(new C2451h(i10, i11), str);
1620:     }
1621: 
```

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
26: import p146kb.C3684b;
27: import p171mb.C3919a;
...
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
553:     }
554: 
...
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
1043:                 boolean z10 = App.f17415f;
1044:                 if (z10) {
```

### `java/sources/p146kb/C3684b.java`

```java
...
1: package p146kb;
2: 
3: import ac.EnumC0159a;
4: import ac.EnumC0161c;
5: import ac.EnumC0176r;
6: import android.graphics.Bitmap;
7: import android.graphics.Matrix;
8: import android.graphics.RectF;
9: import android.util.SparseArray;
10: import java.io.File;
11: import java.nio.ByteBuffer;
12: import java.util.ArrayList;
13: import java.util.Collections;
14: import java.util.HashSet;
15: import java.util.Iterator;
16: import java.util.List;
17: import java.util.Locale;
18: import java.util.Set;
19: import java.util.concurrent.TimeUnit;
20: import lc.AbstractC3819f;
21: import lc.C3826m;
22: import lc.C3827n;
23: import org.readera.App;
24: import org.readera.codec.exception.OreDefaultException;
25: import org.readera.exception.DocCodecException;
26: import org.readera.jni.JniBitmap;
27: import org.readera.jni.JniDoc;
28: import p024bc.C0811f0;
29: import p024bc.C0823q;
30: import p024bc.C0824r;
31: import p024bc.C0825s;
...
55: import za.AbstractC6989a;
56: 
57: /* renamed from: kb.b */
58: /* loaded from: classes.dex */
59: public class C3684b extends AbstractC3568c {
60: 
61:     /* renamed from: d0 */
62:     public static final C6181L f14014d0 = new C6181L(C3684b.class.getSimpleName());
63: 
64:     /* renamed from: e0 */
65:     private static final RectF f14015e0 = new RectF(0.0f, 0.0f, 1.0f, 1.0f);
66: 
67:     /* renamed from: f0 */
68:     private static final RectF f14016f0 = new RectF(0.0f, 0.0f, 0.5f, 1.0f);
69: 
70:     /* renamed from: g0 */
71:     private static final RectF f14017g0 = new RectF(0.5f, 0.0f, 1.0f, 1.0f);
72: 
73:     /* renamed from: O */
74:     private boolean f14018O;
75: 
76:     /* renamed from: P */
77:     private boolean f14019P;
78: 
79:     /* renamed from: Q */
80:     private boolean f14020Q;
81: 
82:     /* renamed from: R */
83:     private EnumC0159a f14021R;
84: 
85:     /* renamed from: S */
86:     private final C3683a f14022S;
87: 
88:     /* renamed from: T */
89:     private int f14023T;
90: 
91:     /* renamed from: U */
92:     private final C3687e f14024U;
93: 
94:     /* renamed from: V */
...
152:         return true;
153:     }
154: 
155:     /* renamed from: B1 */
156:     private boolean m16010B1(C3928j c3928j, AbstractC3924f abstractC3924f, String str, String str2) {
157:         float[] m17019i = c3928j.m17019i(str);
158:         boolean z10 = App.f17415f;
159:         if (z10) {
160:             C6181L.m25211M(AbstractC6989a.m28332a(-68011115620739L) + abstractC3924f);
161:         }
162:         if (((RectF) abstractC3924f).left == 0.0f || ((RectF) abstractC3924f).top == 0.0f) {
163:             if (z10) {
164:                 C6181L.m25212N(AbstractC6989a.m28332a(-68161439476099L), str2);
165:             }
166:             return false;
167:         }
168:         Locale locale = Locale.US;
169:         float parseFloat = Float.parseFloat(String.format(locale, AbstractC6989a.m28332a(-68359007971715L), Float.valueOf(((RectF) abstractC3924f).left)).substring(0, 6));
170:         float parseFloat2 = Float.parseFloat(String.format(locale, AbstractC6989a.m28332a(-68380482808195L), Float.valueOf(((RectF) abstractC3924f).top)).substring(0, 6));
171:         if (parseFloat == m17019i[0] && parseFloat2 == m17019i[1]) {
172:             if (z10) {
173:                 C6181L.m25212N(AbstractC6989a.m28332a(-68401957644675L), str2, str, Float.valueOf(m17019i[0]), Float.valueOf(m17019i[1]), Float.valueOf(parseFloat), Float.valueOf(parseFloat2));
174:             }
175:             return true;
176:         }
177:         if (z10) {
178:             C6181L.m25223n(AbstractC6989a.m28332a(-68642475813251L), str2, str, Float.valueOf(m17019i[0]), Float.valueOf(m17019i[1]), Float.valueOf(parseFloat), Float.valueOf(parseFloat2));
179:         }
180:         return false;
181:     }
182: 
183:     /* renamed from: C1 */
184:     private void m16011C1() {
185:         boolean z10 = App.f17415f;
186:         if (z10) {
187:             f14014d0.mo15586c(AbstractC6989a.m28332a(-63492810025347L));
188:         }
189:         C7016e c7016e = this.f13509E;
190:         C0811f0[] m5561o = C0811f0.m5561o(this.f13514J, c7016e.f25571n, EnumC0176r.m506g(c7016e.f25572o));
191:         if (z10) {
192:             f14014d0.mo15587d(AbstractC6989a.m28332a(-63638838913411L), Integer.valueOf(m5561o.length));
193:         }
...
199:             return;
200:         }
201:         int m25958d = AbstractC6392i.m25958d(this.f13507C);
202:         for (C0811f0 c0811f0 : m5561o) {
203:             C0823q c0823q = c0811f0.f5121b;
204:             C0823q c0823q2 = c0811f0.f5122c;
205:             if (c0823q != null && c0823q2 != null) {
206:                 int[] iArr = this.f13516L;
207:                 int i10 = iArr[c0823q.f5216c];
208:                 int i11 = iArr[c0823q2.f5216c];
209:                 RectF m16044a = this.f14025V.m16044a(i10, c0823q.f5214a);
210:                 RectF m16044a2 = this.f14025V.m16044a(i11, c0823q2.f5214a);
211:                 if (m16044a != null && m16044a2 != null) {
212:                     C3826m m16039a = this.f14024U.m16039a(i10);
213:                     C3826m m16039a2 = this.f14024U.m16039a(i11);
214:                     c0811f0.m5574q(C0811f0.m5562r(m16039a, m16039a2, m16044a, m16044a2));
215:                     m16020P1(c0823q, i10, m16039a, m25958d);
216:                     m16020P1(c0823q2, i11, m16039a2, m25958d);
217:                     c0811f0.m5575s();
218:                 } else if (App.f17415f) {
219:                     f14014d0.m25235K(AbstractC6989a.m28332a(-63948076558723L));
220:                 }
221:             } else if (App.f17415f) {
222:                 f14014d0.m25235K(AbstractC6989a.m28332a(-63789162768771L));
223:             }
224:         }
225:         if (App.f17415f) {
226:             f14014d0.mo15586c(AbstractC6989a.m28332a(-64141350087043L));
227:         }
228:     }
229: 
230:     /* renamed from: D1 */
231:     private void m16012D1(C3936r c3936r) {
232:         c3936r.f15687j = this.f13517M[c3936r.f15696s];
233:         if (c3936r.f15690m >= 0.5f && this.f13514J.f5260a[c3936r.f15687j].f5214a == C0823q.a.f5241j) {
...
261:                 C6181L.m25223n(AbstractC6989a.m28332a(-67461359806851L), c3928j.f15698u, m17024n);
262:             }
263:         }
264:         if (m17023m == null || m17024n == null) {
265:             return null;
266:         }
267:         return i10 + AbstractC6989a.m28332a(-67573028956547L) + m17023m + AbstractC6989a.m28332a(-67581618891139L) + m17024n + AbstractC6989a.m28332a(-67590208825731L) + 3;
268:     }
269: 
270:     /* renamed from: I1 */
271:     public static RectF m16014I1(C0824r c0824r, float f10, boolean z10) {
272:         C0823q c0823q = c0824r.f5251g;
273:         if (z10) {
274:             if (c0824r.f5259o == null) {
275:                 c0824r.m5740a();
276:             }
277:             return c0824r.f5259o;
278:         }
279:         C0823q.a aVar = c0823q.f5214a;
280:         RectF rectF = aVar == C0823q.a.f5241j ? f14016f0 : aVar == C0823q.a.f5242k ? f14017g0 : f14015e0;
281:         if (f10 == 1.0f) {
282:             return rectF;
283:         }
284:         Matrix m16663a = AbstractC3819f.m16663a();
285:         m16663a.postScale(rectF.width(), 1.0f);
286:         m16663a.postTranslate(rectF.left, 0.0f);
287:         RectF rectF2 = new RectF();
288:         m16663a.mapRect(rectF2, c0824r.f5258n);
289:         return rectF2;
290:     }
291: 
292:     /* renamed from: J1 */
293:     private RectF m16015J1(C0824r c0824r, int i10, C3826m c3826m, float f10) {
294:         if (!this.f14018O) {
295:             return m16014I1(c0824r, f10, false);
296:         }
297:         C0823q c0823q = c0824r.f5251g;
298:         if (c0823q.f5218e.f5259o != null) {
299:             if (c0824r.f5259o == null) {
300:                 c0824r.m5740a();
301:             }
302:             return c0824r.f5259o;
303:         }
304:         if (c0823q.m5713m0() == null || !c0823q.m5713m0().m5572n()) {
305:             m16021Q1(c0823q, i10, c3826m);
306:         } else {
307:             m16024T1(c0823q.m5713m0());
308:         }
309:         c0824r.m5740a();
310:         return c0824r.f5259o;
311:     }
312: 
313:     /* renamed from: K1 */
314:     private RectF m16016K1(C0823q c0823q, int i10, C3826m c3826m) {
315:         boolean m16037c = this.f14027X.m16037c(i10, false);
316:         if (!m16018M1(c0823q, i10, !m16037c)) {
317:             return null;
318:         }
319:         RectF m16014I1 = m16014I1(c0823q.f5218e, 1.0f, false);
320:         float[] fArr = new float[4];
321:         if (JniDoc.m19137w(this.f13532w, i10, c3826m.m16672b(), c3826m.m16671a(), m16014I1.left, m16014I1.top, m16014I1.right, m16014I1.bottom, fArr) == 1) {
322:             if (!m16037c) {
323:                 m16032N1(i10);
324:             }
325:             return new RectF(fArr[0], fArr[1], fArr[2], fArr[3]);
326:         }
327:         this.f14028Y.add(Integer.valueOf(i10));
328:         mo15418o1();
329:         c0823q.f5217d = true;
330:         return null;
331:     }
332: 
333:     /* renamed from: L1 */
334:     private String m16017L1(int i10, float[] fArr, boolean z10) {
335:         String m19099A = JniDoc.m19099A(this.f13532w, i10, fArr[0], fArr[1], z10);
336:         if (m19099A == null || m19099A.equals(AbstractC6989a.m28332a(-67598798760323L)) || m19099A.isEmpty()) {
337:             if (!App.f17415f) {
338:                 return null;
339:             }
340:             C6181L.m25223n(AbstractC6989a.m28332a(-67607388694915L), Integer.valueOf(i10), Float.valueOf(fArr[0]), Float.valueOf(fArr[1]));
341:             return null;
342:         }
343:         if (App.f17415f) {
344:             C6181L.m25212N(AbstractC6989a.m28332a(-67770597452163L), Integer.valueOf(i10), Float.valueOf(fArr[0]), Float.valueOf(fArr[1]), m19099A);
345:         }
346:         int indexOf = m19099A.indexOf(AbstractC6989a.m28332a(-67916626340227L));
347:         return indexOf == -1 ? m19099A : m19099A.substring(0, indexOf);
348:     }
...
361:                 return false;
362:             }
363:             if (!z10) {
364:                 this.f14027X.m16038d(i10);
365:             }
366:         }
367:         return true;
368:     }
369: 
370:     /* renamed from: O1 */
371:     private JniBitmap m16019O1(C0823q c0823q, int i10, C3826m c3826m, int i11, int i12, RectF rectF, boolean z10) {
372:         if (this.f13527r == EnumC5145o.PDF) {
373:             int m16671a = c3826m.m16671a();
374:             int m16672b = c3826m.m16672b();
375:             Matrix m16663a = AbstractC3819f.m16663a();
376:             float f10 = i11;
377:             float f11 = i12;
378:             m16663a.postScale(f10 / m16672b, f11 / m16671a);
379:             m16663a.postTranslate((-rectF.left) * f10, (-rectF.top) * f11);
380:             m16663a.postScale(1.0f / rectF.width(), 1.0f / rectF.height());
381:             m16663a.getValues(this.f14031b0);
382:             float[] fArr = this.f14030a0;
383:             float[] fArr2 = this.f14031b0;
384:             fArr[0] = fArr2[0];
385:             fArr[1] = fArr2[3];
386:             fArr[2] = fArr2[1];
387:             fArr[3] = fArr2[4];
388:             fArr[4] = fArr2[2];
389:             fArr[5] = fArr2[5];
390:         } else {
391:             float[] fArr3 = this.f14030a0;
392:             fArr3[0] = rectF.left;
393:             fArr3[1] = rectF.top;
394:             fArr3[2] = rectF.width();
...
404:         c0823q.f5217d = false;
405:         JniBitmap jniBitmap = new JniBitmap(m19135u, i11, i12);
406:         jniBitmap.m19092e(this.f14021R, this.f13527r);
407:         return jniBitmap;
408:     }
409: 
410:     /* renamed from: P1 */
411:     private void m16020P1(C0823q c0823q, int i10, C3826m c3826m, int i11) {
412:         float f10;
413:         float f11;
414:         RectF m16044a = this.f14025V.m16044a(i10, c0823q.f5214a);
415:         c0823q.f5218e.f5259o = m16044a;
416:         int m16672b = c3826m.m16672b();
417:         int m16671a = c3826m.m16671a();
418:         if (!this.f14018O || m16044a == null) {
419:             if (c0823q.f5214a != C0823q.a.f5240f) {
420:                 m16672b /= 2;
421:             }
422:             f10 = m16672b;
423:             f11 = m16671a;
424:         } else {
425:             if (c0823q.m5713m0() != null) {
426:                 m16044a = c0823q.m5713m0().m5567h(c0823q, m16044a);
427:                 c0823q.f5218e.f5259o = m16044a;
428:                 if (App.f17415f) {
429:                     f14014d0.m25236L(AbstractC6989a.m28332a(-63325306300803L), this.f14025V.m16044a(i10, c0823q.f5214a), m16044a);
430:                 }
431:             }
432:             f10 = m16672b * m16044a.width();
433:             f11 = m16671a * m16044a.height();
434:         }
435:         c0823q.m5673Q0(AbstractC6392i.m25957c(this.f13507C, f10, f11, i11));
436:         int m16672b2 = this.f13507C.m16672b();
437:         int m16671a2 = this.f13507C.m16671a();
...
459:         float f17 = f10 * (f16 / f11);
460:         if (f15 > f16) {
461:             c0823q.m5669N0(new C3827n(f17, f16));
462:         } else {
463:             c0823q.m5669N0(new C3827n(f13, f15));
464:         }
465:     }
466: 
467:     /* renamed from: Q1 */
468:     private void m16021Q1(C0823q c0823q, int i10, C3826m c3826m) {
469:         RectF m16016K1 = m16016K1(c0823q, i10, c3826m);
470:         if (m16016K1 != null) {
471:             this.f14025V.m16045b(i10, c0823q.f5214a, m16016K1);
472:         }
473:         m16020P1(c0823q, i10, c3826m, AbstractC6392i.m25958d(this.f13507C));
474:     }
475: 
476:     /* renamed from: R1 */
477:     private void m16022R1(List list) {
478:         if (App.f17415f) {
479:             C6181L.m25211M(AbstractC6989a.m28332a(-65279516420483L));
480:         }
481:         Iterator it = list.iterator();
482:         while (it.hasNext()) {
483:             for (C3936r c3936r : ((C5138k0) it.next()).m21810a()) {
484:                 m16033U1(c3936r);
485:                 m16012D1(c3936r);
486:             }
487:         }
488:         if (App.f17415f) {
489:             C6181L.m25211M(AbstractC6989a.m28332a(-65382595635587L));
490:         }
491:     }
492: 
...
497:         }
498:         int max = Math.max(this.f13514J.f5263d.f5216c, i10 - 1);
499:         int min = Math.min(this.f13514J.f5264e.f5216c, i10 + 1);
500:         if (this.f13508D.f25494V0) {
501:             while (max <= min) {
502:                 m16026x1(max);
503:                 max++;
504:             }
505:             return;
506:         }
507:         RectF rectF = new RectF(0.0f, 0.0f, 1.0f, 1.0f);
508:         int[] iArr = this.f13516L;
509:         int i11 = iArr[min];
510:         for (int i12 = iArr[max]; i12 <= i11; i12++) {
511:             m16025w1(i12, rectF);
512:         }
513:     }
514: 
515:     /* renamed from: T1 */
516:     private void m16024T1(C0811f0 c0811f0) {
517:         if (App.f17415f) {
518:             f14014d0.m25236L(AbstractC6989a.m28332a(-68882993981827L), Integer.valueOf(c0811f0.f5120a));
519:         }
520:         C0823q c0823q = c0811f0.f5121b;
521:         C0823q c0823q2 = c0811f0.f5122c;
522:         int[] iArr = this.f13516L;
523:         int i10 = iArr[c0823q.f5216c];
524:         int i11 = iArr[c0823q2.f5216c];
525:         C3826m m16039a = this.f14024U.m16039a(i10);
526:         C3826m m16039a2 = this.f14024U.m16039a(i11);
527:         RectF m16044a = this.f14025V.m16044a(i10, c0823q.f5214a);
528:         RectF m16044a2 = this.f14025V.m16044a(i11, c0823q2.f5214a);
529:         if (m16044a == null) {
530:             m16044a = m16016K1(c0823q, i10, m16039a);
531:         }
532:         if (m16044a2 == null) {
533:             m16044a2 = m16016K1(c0823q2, i11, m16039a2);
534:         }
535:         if (m16044a != null) {
536:             this.f14025V.m16045b(i10, c0823q.f5214a, m16044a);
537:         }
538:         if (m16044a2 != null) {
539:             this.f14025V.m16045b(i11, c0823q2.f5214a, m16044a2);
540:         }
541:         int m25958d = AbstractC6392i.m25958d(this.f13507C);
542:         if (m16044a != null && m16044a2 != null) {
543:             c0811f0.m5574q(C0811f0.m5562r(m16039a, m16039a2, m16044a, m16044a2));
544:         }
545:         m16020P1(c0823q, i10, m16039a, m25958d);
546:         m16020P1(c0823q2, i11, m16039a2, m25958d);
547:         c0811f0.m5575s();
548:     }
549: 
550:     /* renamed from: w1 */
551:     private void m16025w1(int i10, RectF rectF) {
552:         if (App.f17415f) {
553:             f14014d0.m25243t(AbstractC6989a.m28332a(-69071972542851L), Integer.valueOf(i10), rectF.toShortString());
554:         }
555:         if (JniDoc.m19139y(this.f13532w, i10, rectF.top, rectF.bottom) != 1) {
556:             this.f14028Y.add(Integer.valueOf(i10));
557:             mo15418o1();
558:         }
559:     }
560: 
561:     /* renamed from: x1 */
562:     private void m16026x1(int i10) {
563:         if (App.f17415f) {
564:             f14014d0.m25236L(AbstractC6989a.m28332a(-67238021507459L), Integer.valueOf(i10));
565:             if (!this.f13508D.f25494V0) {
566:                 throw new IllegalStateException();
567:             }
568:         }
569:         int i11 = this.f13516L[i10];
570:         C3826m m16039a = this.f14024U.m16039a(i11);
571:         RectF rectF = (RectF) this.f14026W.get(i11);
572:         if (rectF == null && (rectF = m16027y1(this.f13514J.f5260a[i10], i11, m16039a)) != null) {
573:             this.f14026W.put(i11, rectF);
574:         }
575:         m16025w1(i11, rectF);
576:     }
577: 
578:     /* renamed from: y1 */
579:     private RectF m16027y1(C0823q c0823q, int i10, C3826m c3826m) {
580:         if (App.f17415f) {
581:             f14014d0.m25243t(AbstractC6989a.m28332a(-68994663131523L), Integer.valueOf(i10));
582:         }
583:         boolean m16037c = this.f14027X.m16037c(i10, false);
584:         if (!m16018M1(c0823q, i10, !m16037c)) {
585:             return null;
586:         }
587:         RectF rectF = new RectF(0.0f, 0.0f, 1.0f, 1.0f);
588:         float[] fArr = new float[4];
589:         if (JniDoc.m19138x(this.f13532w, i10, c3826m.m16672b(), c3826m.m16671a(), rectF.left, rectF.top, rectF.right, rectF.bottom, fArr) == 1) {
590:             if (!m16037c) {
591:                 m16032N1(i10);
592:             }
593:             return new RectF(fArr[0], fArr[1], fArr[2], fArr[3]);
594:         }
595:         this.f14028Y.add(Integer.valueOf(i10));
596:         mo15418o1();
597:         c0823q.f5217d = true;
598:         return null;
599:     }
600: 
601:     /* renamed from: z1 */
602:     private boolean m16028z1(long j10) {
603:         return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - j10) > 1;
604:     }
605: 
606:     @Override // p132jb.InterfaceC3575h
607:     /* renamed from: A0 */
608:     public C3937s mo15467A0(C3928j c3928j) {
609:         if (App.f17415f) {
610:             f14014d0.m25238P(AbstractC6989a.m28332a(-67143532226947L), c3928j);
611:         }
612:         if (c3928j.f15687j == -1 || c3928j.f15697t == null) {
613:             return null;
614:         }
615:         int i10 = this.f13516L[c3928j.f15687j];
616:         m16023S1(c3928j.f15687j);
...
624:         int i10 = c0823q.f5216c;
625:         int i11 = iArr[i10];
626:         m16023S1(i10);
627:         List<C3926h> m15404U0 = m15404U0(i11);
628:         if (App.f17415f) {
629:             C6181L.m25212N(AbstractC6989a.m28332a(-65966711187843L), Integer.valueOf(c0823q.f5216c), Integer.valueOf(m15404U0.size()));
630:         }
631:         if (m15404U0.isEmpty() || !(this.f14018O || this.f14019P)) {
632:             return m15404U0;
633:         }
634:         RectF m16015J1 = m16015J1(c0823q.f5218e, i11, this.f14024U.m16039a(i11), 1.0f);
635:         if (m16015J1 == null) {
636:             return Collections.EMPTY_LIST;
637:         }
638:         ArrayList arrayList = new ArrayList();
639:         for (C3926h c3926h : m15404U0) {
640:             float f10 = m16015J1.right;
641:             float f11 = ((RectF) c3926h).left;
642:             if (f10 >= f11) {
643:                 float f12 = m16015J1.left;
644:                 if (f12 <= ((RectF) c3926h).right) {
645:                     arrayList.add(new C3926h((f11 - f12) / m16015J1.width(), (((RectF) c3926h).top - m16015J1.top) / m16015J1.height(), (((RectF) c3926h).right - m16015J1.left) / m16015J1.width(), (((RectF) c3926h).bottom - m16015J1.top) / m16015J1.height(), c3926h.f15682f));
646:                 }
647:             }
648:         }
649:         return arrayList;
650:     }
651: 
652:     @Override // p132jb.InterfaceC3575h
653:     /* renamed from: E */
654:     public List mo15469E(C0823q c0823q) {
655:         if (this.f13527r.m21975f(EnumC5145o.CBR, EnumC5145o.CBZ)) {
656:             return new ArrayList();
657:         }
658:         int[] iArr = this.f13516L;
659:         int i10 = c0823q.f5216c;
660:         int i11 = iArr[i10];
661:         m16023S1(i10);
662:         List<C3937s> m15405W0 = m15405W0(i11);
663:         if (App.f17415f) {
664:             C6181L.m25212N(AbstractC6989a.m28332a(-66409092819331L), Integer.valueOf(c0823q.f5216c), Integer.valueOf(i11), Integer.valueOf(m15405W0.size()));
665:         }
666:         if (m15405W0.isEmpty() || !this.f14019P) {
667:             return m15405W0;
668:         }
...
708:             str = i10 + AbstractC6989a.m28332a(-64635271326083L) + split[1] + AbstractC6989a.m28332a(-64643861260675L) + split[2];
709:         }
710:         super.mo15392G(c3928j, str);
711:     }
712: 
713:     @Override // p132jb.InterfaceC3575h
714:     /* renamed from: G0 */
715:     public List mo15470G0(C0823q c0823q, C3928j c3928j) {
716:         ArrayList arrayList = new ArrayList();
717:         for (C3919a c3919a : m16031G1(c0823q, Collections.singletonList(c3928j))) {
718:             arrayList.add(new C3926h(((RectF) c3919a).left, ((RectF) c3919a).top, ((RectF) c3919a).right, ((RectF) c3919a).bottom, c3919a.f15682f));
719:         }
720:         return arrayList;
721:     }
722: 
723:     /* renamed from: G1 */
724:     public List m16031G1(C0823q c0823q, List list) {
725:         int i10 = this.f13516L[c0823q.f5216c];
726:         List<C3919a> m15416l0 = m15416l0(i10, list);
727:         if (App.f17415f) {
728:             C6181L.m25212N(AbstractC6989a.m28332a(-65481379883395L), Integer.valueOf(c0823q.f5216c), Integer.valueOf(list.size()), Integer.valueOf(m15416l0.size()));
729:         }
730:         if (m15416l0.isEmpty() || !(this.f14018O || this.f14019P)) {
731:             return m15416l0;
732:         }
733:         RectF m16015J1 = m16015J1(c0823q.f5218e, i10, this.f14024U.m16039a(i10), 1.0f);
734:         if (m16015J1 == null) {
735:             return Collections.EMPTY_LIST;
736:         }
737:         ArrayList arrayList = new ArrayList();
738:         for (C3919a c3919a : m15416l0) {
739:             float f10 = m16015J1.right;
740:             float f11 = ((RectF) c3919a).left;
741:             if (f10 >= f11) {
742:                 float f12 = m16015J1.left;
743:                 if (f12 <= ((RectF) c3919a).right) {
744:                     arrayList.add(new C3919a((f11 - f12) / m16015J1.width(), (((RectF) c3919a).top - m16015J1.top) / m16015J1.height(), (((RectF) c3919a).right - m16015J1.left) / m16015J1.width(), (((RectF) c3919a).bottom - m16015J1.top) / m16015J1.height(), c3919a.f15682f));
745:                 }
746:             }
747:         }
748:         return arrayList;
749:     }
750: 
751:     @Override // p132jb.AbstractC3568c
752:     /* renamed from: I */
753:     public C3570d mo15394I(C7004c c7004c, C7016e c7016e, C3826m c3826m) {
754:         C3570d c3570d = new C3570d();
755:         InterfaceC3575h.a aVar = this.f13526q;
756:         if (aVar == InterfaceC3575h.a.READING) {
757:             this.f14020Q = c7004c.f25515h0 && c3826m.m16672b() > c3826m.m16671a();
758:             this.f14018O = c7004c.f25483Q;
759:             this.f14019P = c7004c.f25485R;
760:             this.f14021R = c7004c.f25481P;
761:             this.f13506B = c7004c.f25479O;
762:             if (this.f13527r.m21975f(EnumC5145o.PDF)) {
763:                 EnumC0159a enumC0159a = this.f14021R;
764:                 EnumC0159a enumC0159a2 = EnumC0159a.TWILIGHT;
765:                 if (enumC0159a.m474d(enumC0159a2)) {
766:                     c3570d.put(105, Integer.toString(enumC0159a2.f348j));
767:                     c3570d.put(111, Integer.toString(enumC0159a2.f349k));
...
808:     public List mo15471K(C0823q c0823q) {
809:         return Collections.EMPTY_LIST;
810:     }
811: 
812:     @Override // p132jb.InterfaceC3575h
813:     /* renamed from: K0 */
814:     public void mo15472K0(C3928j c3928j) {
815:         if (this.f14018O || this.f14019P) {
816:             C0823q c0823q = this.f13514J.f5260a[c3928j.f15687j];
817:             int i10 = this.f13516L[c0823q.f5216c];
818:             RectF m16015J1 = m16015J1(c0823q.f5218e, i10, this.f14024U.m16039a(i10), 1.0f);
819:             float width = (c3928j.f15690m - m16015J1.left) / m16015J1.width();
820:             float height = (c3928j.f15691n - m16015J1.top) / m16015J1.height();
821:             if (App.f17415f) {
822:                 C6181L.m25212N(AbstractC6989a.m28332a(-64287378975107L), Integer.valueOf(c0823q.f5216c), Float.valueOf(c3928j.f15690m), Float.valueOf(width), Float.valueOf(c3928j.f15691n), Float.valueOf(height));
823:             }
824:             c3928j.f15690m = width;
825:             c3928j.f15691n = height;
826:         }
827:     }
828: 
829:     @Override // p132jb.InterfaceC3575h
830:     /* renamed from: L0 */
831:     public C3569c0 mo15473L0(C0823q c0823q) {
832:         int i10 = this.f13516L[c0823q.f5216c];
833:         List<C3927i> m15410g1 = m15410g1(i10);
834:         if (m15410g1.isEmpty() || !(this.f14018O || this.f14019P)) {
835:             return C3569c0.m15435t(m15410g1);
836:         }
837:         RectF m16015J1 = m16015J1(c0823q.f5218e, i10, this.f14024U.m16039a(i10), 1.0f);
838:         if (m16015J1 == null) {
839:             return C3569c0.m15435t(Collections.EMPTY_LIST);
840:         }
841:         ArrayList arrayList = new ArrayList();
842:         for (C3927i c3927i : m15410g1) {
843:             float f10 = m16015J1.right;
844:             float f11 = ((RectF) c3927i).left;
845:             if (f10 >= f11) {
846:                 float f12 = m16015J1.left;
847:                 if (f12 <= ((RectF) c3927i).right) {
848:                     arrayList.add(new C3927i((f11 - f12) / m16015J1.width(), (((RectF) c3927i).top - m16015J1.top) / m16015J1.height(), (((RectF) c3927i).right - m16015J1.left) / m16015J1.width(), (((RectF) c3927i).bottom - m16015J1.top) / m16015J1.height(), c3927i.f15684f, c3927i.f15685j));
849:                 }
850:             }
851:         }
852:         return C3569c0.m15435t(arrayList);
853:     }
854: 
855:     @Override // p132jb.InterfaceC3575h
856:     /* renamed from: N0 */
857:     public int mo15474N0(boolean z10) {
858:         return JniDoc.m19106H(this.f13532w, z10);
859:     }
860: 
861:     /* renamed from: N1 */
862:     public void m16032N1(int i10) {
863:         if (this.f14028Y.contains(Integer.valueOf(i10)) || JniDoc.m19130p(this.f13532w, i10) == 1) {
864:             return;
865:         }
866:         this.f14028Y.add(Integer.valueOf(i10));
867:         mo15418o1();
868:     }
869: 
870:     @Override // p132jb.InterfaceC3575h
871:     /* renamed from: O0 */
872:     public JniBitmap mo15475O0(C0823q c0823q, C0824r c0824r, float f10) {
873:         RectF m16015J1;
874:         int i10 = this.f13516L[c0823q.f5216c];
875:         C3826m m16039a = this.f14024U.m16039a(i10);
876:         if (!m16018M1(c0823q, i10, false) || (m16015J1 = m16015J1(c0824r, i10, m16039a, f10)) == null) {
877:             return null;
878:         }
879:         RectF m5743e = c0824r.m5743e(f10, c0823q.m5719p0(f10));
880:         int round = Math.round(m5743e.width());
881:         int round2 = Math.round(m5743e.height());
882:         boolean z10 = App.f17415f;
883:         if (z10) {
884:             f14014d0.mo15586c(AbstractC6989a.m28332a(-69441339730307L));
885:         }
886:         JniBitmap m16019O1 = m16019O1(c0823q, i10, m16039a, round, round2, m16015J1, false);
887:         if (z10) {
888:             f14014d0.mo15586c(AbstractC6989a.m28332a(-69548713912707L));
889:         }
890:         return m16019O1;
891:     }
892: 
893:     @Override // p132jb.AbstractC3568c
894:     /* renamed from: Q */
895:     protected List mo15399Q(C0823q c0823q, String str, int i10) {
896:         int i11 = this.f13516L[c0823q.f5216c];
897:         List<C3925g> m15402T0 = m15402T0(i11, str, i10);
898:         if (m15402T0.isEmpty() || !(this.f14018O || this.f14019P)) {
899:             return m15402T0;
900:         }
901:         RectF m16015J1 = m16015J1(c0823q.f5218e, i11, this.f14024U.m16039a(i11), 1.0f);
902:         if (m16015J1 == null) {
903:             return Collections.EMPTY_LIST;
904:         }
905:         ArrayList arrayList = new ArrayList();
906:         for (C3925g c3925g : m15402T0) {
907:             float f10 = m16015J1.right;
908:             float f11 = ((RectF) c3925g).left;
909:             if (f10 >= f11) {
910:                 float f12 = m16015J1.left;
911:                 if (f12 <= ((RectF) c3925g).right) {
912:                     arrayList.add(new C3925g((f11 - f12) / m16015J1.width(), (((RectF) c3925g).top - m16015J1.top) / m16015J1.height(), (((RectF) c3925g).right - m16015J1.left) / m16015J1.width(), (((RectF) c3925g).bottom - m16015J1.top) / m16015J1.height(), c3925g.f15683f));
913:                 }
914:             }
915:         }
916:         return arrayList;
917:     }
918: 
919:     @Override // p132jb.InterfaceC3575h
920:     /* renamed from: Q0 */
921:     public RectF mo15476Q0(C0823q c0823q) {
922:         RectF m16015J1;
923:         boolean z10 = App.f17415f;
924:         if (z10) {
925:             f14014d0.m25243t(AbstractC6989a.m28332a(-66172869618051L), Integer.valueOf(c0823q.f5216c));
926:         }
927:         m16023S1(c0823q.f5216c);
928:         if (!this.f13508D.f25494V0) {
929:             return new RectF(0.0f, 0.0f, 1.0f, 1.0f);
930:         }
931:         int i10 = this.f13516L[c0823q.f5216c];
932:         RectF rectF = (RectF) this.f14026W.get(i10);
933:         if (rectF != null) {
934:             return ((this.f14018O || this.f14019P) && (m16015J1 = m16015J1(c0823q.f5218e, i10, this.f14024U.m16039a(i10), 1.0f)) != null) ? new RectF((rectF.left - m16015J1.left) / m16015J1.width(), (rectF.top - m16015J1.top) / m16015J1.height(), (rectF.right - m16015J1.left) / m16015J1.width(), (rectF.bottom - m16015J1.top) / m16015J1.height()) : rectF;
935:         }
936:         if (z10) {
937:             f14014d0.m25241k(AbstractC6989a.m28332a(-66263063931267L), Integer.valueOf(c0823q.f5216c));
938:         }
939:         return new RectF(0.0f, 0.0f, 1.0f, 1.0f);
940:     }
941: 
942:     @Override // p132jb.InterfaceC3575h
943:     /* renamed from: S0 */
944:     public List mo15477S0(C0823q c0823q) {
945:         int i10 = this.f13516L[c0823q.f5216c];
946:         List m5652F = c0823q.m5652F();
947:         List<C3920b> m15390D0 = m15390D0(i10, m5652F);
948:         if (App.f17415f) {
949:             C6181L.m25212N(AbstractC6989a.m28332a(-65700423215491L), Integer.valueOf(c0823q.f5216c), Integer.valueOf(m5652F.size()), Integer.valueOf(m15390D0.size()));
950:         }
951:         if (m15390D0.isEmpty() || !(this.f14018O || this.f14019P)) {
952:             return m15390D0;
953:         }
954:         RectF m16015J1 = m16015J1(c0823q.f5218e, i10, this.f14024U.m16039a(i10), 1.0f);
955:         if (m16015J1 == null) {
956:             return Collections.EMPTY_LIST;
957:         }
958:         ArrayList arrayList = new ArrayList();
959:         for (C3920b c3920b : m15390D0) {
960:             float f10 = m16015J1.right;
961:             float f11 = ((RectF) c3920b).left;
962:             if (f10 >= f11) {
963:                 float f12 = m16015J1.left;
964:                 if (f12 <= ((RectF) c3920b).right) {
965:                     arrayList.add(new C3920b((f11 - f12) / m16015J1.width(), (((RectF) c3920b).top - m16015J1.top) / m16015J1.height(), (((RectF) c3920b).right - m16015J1.left) / m16015J1.width(), (((RectF) c3920b).bottom - m16015J1.top) / m16015J1.height(), c3920b.f15682f, c3920b.f15674j));
966:                 }
967:             }
968:         }
969:         return arrayList;
970:     }
971: 
972:     /* renamed from: U1 */
973:     protected void m16033U1(C3928j c3928j) {
974:         float[] m17019i = c3928j.m17019i(c3928j.f15697t);
975:         c3928j.f15690m = m17019i[0];
976:         c3928j.f15691n = m17019i[1];
977:     }
978: 
979:     @Override // p132jb.InterfaceC3575h
980:     /* renamed from: W */
981:     public void mo15478W(C3928j c3928j) {
982:         if (c3928j.f15699v == -1) {
983:             if (this.f13527r == EnumC5145o.PDF) {
984:                 c3928j.f15699v = 3;
985:             } else {
986:                 c3928j.f15699v = 2;
987:             }
988:         }
...
1062:             C3936r c3936r = (C3936r) it.next();
1063:             m16012D1(c3936r);
1064:             c3936r.f15693p = mo15482a1;
1065:             c3936r.f15689l = this.f13514J.f5262c;
1066:         }
1067:     }
1068: 
1069:     @Override // p132jb.InterfaceC3575h
1070:     /* renamed from: h0 */
1071:     public JniBitmap mo15485h0() {
1072:         RectF m16015J1;
1073:         int[] iArr = new int[2];
1074:         if (JniDoc.m19131q(this.f13532w, 0, iArr) != 1) {
1075:             return null;
1076:         }
1077:         C3826m c3826m = new C3826m(iArr[0], iArr[1]);
1078:         C0823q c0823q = new C0823q(this.f13528s, 0, C0823q.a.f5240f);
1079:         m16020P1(c0823q, 0, c3826m, AbstractC6392i.m25958d(this.f13507C));
1080:         if (!m16018M1(c0823q, 0, false) || (m16015J1 = m16015J1(c0823q.f5218e, 0, c3826m, 1.0f)) == null) {
1081:             return null;
1082:         }
1083:         int round = Math.round(c0823q.m5680U().m16674b());
1084:         int round2 = Math.round(c0823q.m5680U().m16673a());
1085:         boolean z10 = App.f17415f;
1086:         if (z10) {
1087:             f14014d0.mo15586c(AbstractC6989a.m28332a(-69166461823363L));
1088:         }
1089:         JniBitmap m16019O1 = m16019O1(c0823q, 0, c3826m, round, round2, m16015J1, false);
1090:         if (z10) {
1091:             f14014d0.mo15586c(AbstractC6989a.m28332a(-69303900776835L));
1092:         }
1093:         return m16019O1;
1094:     }
1095: 
1096:     @Override // p132jb.AbstractC3568c
1097:     /* renamed from: j */
1098:     protected C3923e mo15414j(int i10, RectF rectF, String str, String str2, int i11, float f10, float f11) {
1099:         C3923e mo15414j = super.mo15414j(i10, rectF, str, str2, i11, f10, f11);
1100:         mo15414j.f15696s = i11;
1101:         return mo15414j;
1102:     }
1103: 
1104:     @Override // p132jb.InterfaceC3575h
1105:     /* renamed from: j1 */
1106:     public List mo15486j1() {
1107:         return Collections.EMPTY_LIST;
1108:     }
1109: 
1110:     @Override // p132jb.AbstractC3568c
1111:     /* renamed from: k */
1112:     protected void mo15415k() {
1113:         if (this.f14024U.m16041c(this.f14023T)) {
1114:             int[] iArr = new int[2];
1115:             for (int i10 = 0; i10 < this.f14023T; i10++) {
1116:                 if (this.f14024U.m16039a(i10) == null) {
1117:                     if (this.f14028Y.contains(Integer.valueOf(i10))) {
1118:                         this.f14024U.m16042d(i10, this.f13507C);
1119:                     } else if (JniDoc.m19131q(this.f13532w, i10, iArr) == 1) {
1120:                         this.f14024U.m16042d(i10, new C3826m(iArr[0], iArr[1]));
1121:                     } else {
1122:                         this.f14024U.m16042d(i10, this.f13507C);
1123:                         this.f14028Y.add(Integer.valueOf(i10));
1124:                         mo15418o1();
1125:                     }
1126:                 }
1127:             }
1128:         }
1129:         ArrayList arrayList = new ArrayList();
1130:         ArrayList arrayList2 = new ArrayList();
1131:         int m25958d = AbstractC6392i.m25958d(this.f13507C);
1132:         boolean m506g = EnumC0176r.m506g(this.f13509E.f25572o);
1133:         int i11 = 0;
1134:         for (int i12 = 0; i12 < this.f14023T; i12++) {
1135:             C3826m m16039a = this.f14024U.m16039a(i12);
1136:             if (!this.f14019P || m16039a.m16672b() / m16039a.m16671a() < 1.2f) {
1137:                 C0823q c0823q = new C0823q(this.f13528s, i11, C0823q.a.f5240f);
1138:                 m16020P1(c0823q, i12, m16039a, m25958d);
1139:                 arrayList.add(c0823q);
1140:                 arrayList2.add(Integer.valueOf(i12));
1141:                 i11++;
1142:             } else {
...
1309:     public List mo15425s0(C0823q c0823q) {
1310:         int i10 = this.f13516L[c0823q.f5216c];
1311:         List<C3923e> list = (List) this.f14029Z.get(i10);
1312:         if (list == null) {
1313:             list = m15398O(this.f14023T, i10);
1314:             this.f14029Z.put(i10, list);
1315:         }
1316:         if (list.isEmpty() || !(this.f14018O || this.f14019P)) {
1317:             return list;
1318:         }
1319:         RectF m16015J1 = m16015J1(c0823q.f5218e, i10, this.f14024U.m16039a(i10), 1.0f);
1320:         if (m16015J1 == null) {
1321:             return Collections.EMPTY_LIST;
1322:         }
1323:         ArrayList arrayList = new ArrayList();
1324:         for (C3923e c3923e : list) {
1325:             float f10 = m16015J1.right;
1326:             RectF rectF = c3923e.f15679x;
1327:             float f11 = rectF.left;
1328:             if (f10 >= f11) {
1329:                 float f12 = m16015J1.left;
1330:                 if (f12 <= rectF.right) {
1331:                     arrayList.add(new C3923e(c3923e, new RectF((f11 - f12) / m16015J1.width(), (c3923e.f15679x.top - m16015J1.top) / m16015J1.height(), (c3923e.f15679x.right - m16015J1.left) / m16015J1.width(), (c3923e.f15679x.bottom - m16015J1.top) / m16015J1.height())));
1332:                 }
1333:             }
1334:         }
1335:         return arrayList;
1336:     }
1337: 
1338:     @Override // p132jb.AbstractC3568c
1339:     /* renamed from: t1 */
1340:     protected void mo15427t1(int i10) {
1341:         super.mo15427t1(i10);
1342:         this.f14023T = i10;
1343:     }
1344: 
1345:     @Override // p132jb.InterfaceC3575h
1346:     /* renamed from: u */
1347:     public List mo15490u(C0823q c0823q) {
1348:         return m16031G1(c0823q, c0823q.m5662K());
1349:     }
1350: 
1351:     @Override // p132jb.InterfaceC3575h
1352:     /* renamed from: v */
1353:     public String mo15491v(C0823q c0823q, int i10, int i11) {
1354:         return m15400S(c0823q.f5216c, i10, i11);
1355:     }
1356: 
1357:     @Override // p132jb.InterfaceC3575h
1358:     /* renamed from: x */
1359:     public JniBitmap mo15492x(C0823q c0823q) {
1360:         RectF m16015J1;
1361:         int i10 = this.f13516L[c0823q.f5216c];
1362:         C3826m m16039a = this.f14024U.m16039a(i10);
1363:         boolean m16037c = this.f14027X.m16037c(i10, false);
1364:         if (!m16018M1(c0823q, i10, !m16037c) || (m16015J1 = m16015J1(c0823q.f5218e, i10, m16039a, 1.0f)) == null) {
1365:             return null;
1366:         }
1367:         C3826m m5693a0 = c0823q.m5693a0();
1368:         int m16672b = m5693a0.m16672b();
1369:         int m16671a = m5693a0.m16671a();
1370:         boolean z10 = App.f17415f;
1371:         if (z10) {
1372:             f14014d0.mo15586c(AbstractC6989a.m28332a(-69656088095107L));
1373:         }
1374:         JniBitmap m16019O1 = m16019O1(c0823q, i10, m16039a, m16672b, m16671a, m16015J1, true);
1375:         if (z10) {
1376:             f14014d0.mo15586c(AbstractC6989a.m28332a(-69780642146691L));
1377:         }
1378:         if (!m16037c) {
1379:             m16032N1(i10);
1380:         }
1381:         return m16019O1;
1382:     }
1383: 
```

## Cross-class call sites for page dimensions and viewport rectangles
### `java/sources/ec/AbstractGestureDetectorOnGestureListenerC2452i.java`

```java
...
319:     }
320: 
321:     /* renamed from: e0 */
322:     private RectF m11387e0(RectF rectF, C0823q c0823q, RectF rectF2, float f10) {
323:         RectF rectF3 = new RectF(rectF2);
324:         RectF m5719p0 = c0823q.m5719p0(f10);
325:         rectF3.left = (m5719p0.left + (rectF3.left * m5719p0.width())) - rectF.left;
326:         rectF3.top = (m5719p0.top + (rectF3.top * m5719p0.height())) - rectF.top;
327:         rectF3.right = (m5719p0.left + (rectF3.right * m5719p0.width())) - rectF.left;
328:         rectF3.bottom = (m5719p0.top + (rectF3.bottom * m5719p0.height())) - rectF.top;
329:         return rectF3;
330:     }
331: 
332:     /* renamed from: g */
333:     private void m11388g(C2467x c2467x) {
334:         if (c2467x.m11541p()) {
335:             return;
...
1707:                 }
1708:                 for (C0813g0 c0813g02 = c0813g0.f5135f; z10 && c0813g02 != null; c0813g02 = c0813g02.f5135f) {
1709:                     c0823q7.m5645B0(c0813g02);
1710:                 }
1711:                 C0824r[] c0824rArr2 = c0823q7.f5221h;
1712:                 RectF m5719p0 = c0823q7.m5719p0(f10);
1713:                 ArrayList arrayList7 = new ArrayList();
1714:                 int i43 = c0813g0.f5132c;
1715:                 int i44 = max2;
1716:                 while (i43 < c0813g0.f5133d) {
1717:                     C0824r c0824r4 = c0824rArr2[i43];
1718:                     if (c0824r4 == null) {
1719:                         i18 = min2;
1720:                         c0823q7.m5726t(c0823q7.m5717o0(i43, true));
1721:                         c0824r4 = c0824rArr2[i43];
1722:                     } else {
1723:                         i18 = min2;
...
2519:                 boolean z11 = m11387e0.top > 0.0f && m11387e0.bottom < rectF.height();
2520:                 boolean z12 = App.f17415f;
2521:                 if (z12) {
2522:                     i13 = 1;
2523:                     i12 = 0;
2524:                     f9948H.m25236L("updateSelectionActions bounds: %s, hitCheck: %s, viewport: %s, hor: %b, ver:%b", c0823q.m5719p0(f10), m11387e0, rectF, Boolean.valueOf(z10), Boolean.valueOf(z11));
2525:                 } else {
2526:                     i12 = 0;
2527:                     i13 = 1;
2528:                 }
2529:                 if (z10 && z11) {
2530:                     float f14 = m11387e0.top;
2531:                     float f15 = m11387e0.bottom;
2532:                     float f16 = m11387e0.left;
2533:                     if (c0823q != aVar2.f10032a) {
2534:                         if (z12) {
2535:                             f9948H.m25235K("updateSelectionActions visibleNormal 1");
```

### `java/sources/ec/C2453j.java`

```java
...
69:     }
70: 
71:     /* renamed from: E0 */
72:     private Rect m11465E0(int i10) {
73:         C0825s pages = this.f9965d.getPages();
74:         RectF m5719p0 = pages.f5260a[i10].m5719p0(this.f9974m);
75:         return new Rect(Math.round(m5719p0.left), Math.round(m5719p0.top), Math.round(m5719p0.right), Math.round(m5719p0.bottom));
76:     }
77: 
78:     @Override // ec.AbstractGestureDetectorOnGestureListenerC2452i
79:     /* renamed from: A0 */
80:     protected int mo11393A0(AbstractC2444a.b bVar, MotionEvent motionEvent, C3826m c3826m) {
81:         AbstractC2444a.m11348q(EnumC0161c.VERTICAL, c3826m);
82:         int m11345n = AbstractC2444a.m11345n(bVar, motionEvent.getX(), motionEvent.getY());
83:         if ((m11345n == R.id.d_ && (C7004c.m28402b().f25547x0 || C2610b.m11974j(this.f9964c) != null)) || (m11345n == R.id.du && C7004c.m28402b().f25545w0)) {
84:             return m11345n;
85:         }
...
161:     public void m11469G0(C0825s c0825s, C3928j c3928j) {
162:         m11425f();
163:         int m5754q = C0825s.m5754q(c0825s.f5262c, c3928j.f15686f);
164:         this.f9975n = 1.0f;
165:         this.f9974m = 1.0f;
166:         RectF m5719p0 = c0825s.f5260a[m5754q].m5719p0(1.0f);
167:         mo11458x0();
168:         m11432j0(0, 0, (int) m5719p0.left, (int) m5719p0.top, 0);
169:     }
170: 
171:     @Override // ec.AbstractGestureDetectorOnGestureListenerC2452i
172:     /* renamed from: L */
173:     public boolean mo11403L(C3928j c3928j) {
174:         C0823q m20160F0;
175:         C2446c c2446c = (C2446c) this.f9968g.m21408f(C2446c.class);
176:         if (c2446c == null || c3928j.f15687j < c2446c.f9917c || c3928j.f15687j > c2446c.f9918d || (m20160F0 = this.f9965d.m20160F0(c3928j.f15687j)) == null) {
177:             return false;
...
180:         int m11434k0 = m11434k0();
181:         int m11436l0 = m11436l0();
182:         float f10 = c3928j.f15690m;
183:         float f11 = c3928j.f15691n;
184:         RectF rectF = new RectF(m11434k0, m11436l0, m11434k0 + m11398F.m16672b(), m11436l0 + m11398F.m16671a());
185:         RectF m5719p0 = m20160F0.m5719p0(this.f9974m);
186:         RectF rectF2 = new RectF(m5719p0);
187:         int m16696a = AbstractC3834u.m16696a(30.0f);
188:         float f12 = rectF2.bottom;
189:         float f13 = rectF.bottom;
190:         if (f12 >= f13) {
191:             f12 = f13 - m16696a;
192:         }
193:         rectF2.bottom = f12;
194:         rectF2.right = Math.min(rectF2.right, rectF.right);
195:         rectF2.offset(-rectF.left, -rectF.top);
196:         float width = (m5719p0.left + (f10 * m5719p0.width())) - rectF.left;
...
281:     }
282: 
283:     @Override // ec.AbstractGestureDetectorOnGestureListenerC2452i
284:     /* renamed from: V */
285:     public void mo11413V(C3810b c3810b, C3826m c3826m) {
286:         RectF m5646C = c3810b.f15243b.m5646C();
287:         mo11452u0(c3810b.f15242a, c3826m, c3810b.f15243b);
288:         m11405N();
289:         int m10885b = this.f9973l.m10885b(this.f9969h, c3810b, m5646C);
290:         if (m10885b != 0) {
291:             m11432j0(m11434k0(), m11436l0(), 0, m10885b, 0);
292:         }
293:         m11450t0();
294:     }
295: 
296:     /* JADX WARN: Removed duplicated region for block: B:12:0x00f5  */
297:     @Override // ec.AbstractGestureDetectorOnGestureListenerC2452i
...
331:                 float f10 = c3928j.f15690m;
332:                 float f11 = c3928j.f15691n;
333:                 float f12 = c3928j.f15692o;
334:                 this.f9975n = f12;
335:                 this.f9974m = f12;
336:                 RectF m5719p0 = c0825s.f5260a[i10].m5719p0(f12);
337:                 int round = Math.round(m5719p0.left + (f10 * m5719p0.width()));
338:                 int round2 = Math.round(m5719p0.top + (f11 * m5719p0.height()));
339:                 mo11458x0();
340:                 m11432j0(0, 0, round, round2, 0);
341:             }
342:         } else {
343:             if (c3928j.m17025o()) {
344:                 if (z11) {
345:                     AbstractGestureDetectorOnGestureListenerC2452i.f9948H.m25235K(AbstractC6989a.m28332a(-501283121473923L));
346:                 }
347:                 z10 = false;
...
381:         float f12 = rectF.left + f10;
382:         float f13 = rectF.top + f11;
383:         for (int i10 = c2446c.f9917c; i10 <= c2446c.f9918d; i10++) {
384:             C0823q m20160F0 = this.f9965d.m20160F0(i10);
385:             if (m20160F0 != null) {
386:                 RectF m5719p0 = m20160F0.m5719p0(this.f9974m);
387:                 float width = (f12 - m5719p0.left) / m5719p0.width();
388:                 float height = (f13 - m5719p0.top) / m5719p0.height();
389:                 if (m5719p0.contains(f12, f13) && m11423d0(bVar, m20160F0, width, height)) {
390:                     return true;
391:                 }
392:             }
393:         }
394:         return false;
395:     }
396: 
397:     @Override // ec.AbstractGestureDetectorOnGestureListenerC2452i
...
433: 
434:     @Override // ec.AbstractGestureDetectorOnGestureListenerC2452i
435:     /* renamed from: u0 */
436:     public void mo11452u0(C0825s c0825s, C3826m c3826m, C0823q c0823q) {
437:         int i10 = c0823q == null ? 0 : c0823q.f5216c;
438:         float f10 = c0823q == null ? 0.0f : c0823q.m5646C().top;
439:         if (i10 == 0 && f10 == 0.0f) {
440:             int m16671a = c3826m.m16671a();
441:             float f11 = 0.0f;
442:             for (int i11 = i10; i11 < c0825s.f5262c; i11++) {
443:                 f11 += c0825s.f5260a[i11].m5680U().m16673a();
444:                 if (m16671a < f11) {
445:                     break;
446:                 }
447:                 f11 += 3.0f;
448:             }
449:             float f12 = m16671a;
...
494:         C3932n c3932n = this.f9984w;
495:         boolean z13 = z12;
496:         C3937s c3937s = this.f9985x;
497:         this.f9973l.m10893j(pages.f5260a[m11466C0], rectF, f10);
498:         m11450t0();
499:         RectF m5719p0 = pages.f5260a[m11466C0].m5719p0(f10);
500:         float width = (rectF.left - m5719p0.left) / m5719p0.width();
501:         float height = (rectF.top - m5719p0.top) / m5719p0.height();
502:         C0813g0 m5576a = C0813g0.m5576a(f10);
503:         C2453j c2453j = this;
504:         c2453j.m11462z0(cVar, rectF, c2467x, c3930l, c3932n, m11466C0, mo11467D0, f10);
505:         c2453j.m11454v0(c3928j, m11466C0, mo11467D0, width, height, f10, z13);
506:         RectF rectF2 = rectF;
507:         c2453j.m11442p0(cVar, rectF2, c2467x, m11466C0, mo11467D0, m5576a, f10, z11, i10, i11, c3937s);
508:         C2467x c2467x2 = c2467x;
509:         C0813g0 c0813g0 = m5576a;
510:         float f11 = f10;
...
513:         HashMap hashMap = new HashMap();
514:         int i14 = i13;
515:         while (i14 <= mo11467D0) {
516:             C0823q c0823q = pages.f5260a[i14];
517:             int i15 = i14;
518:             RectF m5719p02 = c0823q.m5719p0(f11);
519:             RectF m11437m = c2453j.m11437m(rectF2, m5719p02);
520:             int i16 = i13;
521:             HashMap hashMap2 = hashMap;
522:             RectF rectF3 = rectF2;
523:             c2453j.m11455w(rectF3, c0823q, m5719p02, m11437m, c0813g0, f11);
524:             float f12 = f11;
525:             C2453j c2453j2 = c2453j;
526:             C2467x c2467x3 = c2467x2;
527:             C2343a c2343a = canvas;
528:             c2467x3.m11536i(c2343a, cVar, c0823q, m11437m, c2453j2.m11397E(c3930l));
529:             c2453j2.mo11443q(c0823q, m11437m, str, i12);
...
580:         m11405N();
581:         C0825s pages = this.f9965d.getPages();
582:         C3826m m11398F = m11398F();
583:         int m16672b = m11398F.m16672b();
584:         int m16671a = m11398F.m16671a();
585:         float f10 = pages != null ? this.f9965d.getPages().f5264e.m5719p0(this.f9974m).bottom : m16671a;
586:         this.f9978q = 0;
587:         this.f9979r = 0;
588:         this.f9980s = ((int) (m16672b * this.f9974m)) - m16672b;
589:         int i10 = ((int) f10) - m16671a;
590:         this.f9981t = i10;
591:         this.f9981t = Math.max(0, i10);
592:         m11450t0();
593:     }
594: }
```

### `java/sources/ec/C2456m.java`

```java
...
108:             float f12 = rectF.left + f10;
109:             float f13 = rectF.top + f11;
110:             C0811f0 c0811f0 = this.f10014U[this.f10009P];
111:             C0823q c0823q = c0811f0.f5121b;
112:             if (c0823q != null) {
113:                 RectF m5719p0 = c0823q.m5719p0(this.f9974m);
114:                 float width = (f12 - m5719p0.left) / m5719p0.width();
115:                 float height = (f13 - m5719p0.top) / m5719p0.height();
116:                 if (m5719p0.contains(f12, f13)) {
117:                     return m11423d0(bVar, c0823q, width, height);
118:                 }
119:             }
120:             C0823q c0823q2 = c0811f0.f5122c;
121:             if (c0823q2 != null) {
122:                 RectF m5719p02 = c0823q2.m5719p0(this.f9974m);
123:                 float width2 = (f12 - m5719p02.left) / m5719p02.width();
124:                 float height2 = (f13 - m5719p02.top) / m5719p02.height();
125:                 if (m5719p02.contains(f12, f13)) {
126:                     return m11423d0(bVar, c0823q2, width2, height2);
127:                 }
128:             }
129:         }
130:         return false;
131:     }
132: 
133:     @Override // ec.C2454k, ec.AbstractGestureDetectorOnGestureListenerC2452i
...
295:             C2343a c2343a5 = canvas;
296:             C0811f0 c0811f0 = c2456m.f10014U[i24];
297:             C0823q c0823q6 = c0811f0.f5121b;
298:             C0823q c0823q7 = c0811f0.f5122c;
299:             if (c0823q6 != null) {
300:                 RectF m5719p0 = c0823q6.m5719p0(f13);
301:                 RectF m11437m = c2456m.m11437m(rectF4, m5719p0);
302:                 c0823q = c0823q7;
303:                 c2456m.m11455w(rectF4, c0823q6, m5719p0, m11437m, m5576a, f13);
304:                 rectF = rectF4;
305:                 f10 = f13;
306:                 c0813g0 = m5576a;
307:                 c2467x2.m11536i(c2343a5, cVar5, c0823q6, m11437m, m11397E);
308:                 i14 = m11397E;
309:                 c2467x2.m11537j(c2343a5, c2456m.f9965d.getGuiTextures(), cVar5, c0823q6, m11437m);
310:                 cVar5 = cVar5;
311:                 c2343a5 = c2343a5;
...
323:                 c0813g0 = m5576a;
324:                 f10 = f13;
325:             }
326:             C0823q c0823q8 = c0823q;
327:             if (c0823q8 != null) {
328:                 RectF m5719p02 = c0823q8.m5719p0(f10);
329:                 RectF m11437m2 = c2456m.m11437m(rectF, m5719p02);
330:                 float f14 = f10;
331:                 RectF rectF5 = rectF;
332:                 c2456m.m11455w(rectF5, c0823q8, m5719p02, m11437m2, c0813g0, f14);
333:                 rectF = rectF5;
334:                 f10 = f14;
335:                 C2343a c2343a6 = c2343a5;
336:                 C2467x c2467x3 = c2467x2;
337:                 AbstractGestureDetectorOnGestureListenerC2452i.c cVar6 = cVar5;
338:                 c2467x3.m11536i(c2343a6, cVar6, c0823q8, m11437m2, i14);
339:                 c2467x3.m11537j(c2343a6, c2456m.f9965d.getGuiTextures(), cVar6, c0823q8, m11437m2);
...
369:                     C0811f0 c0811f02 = c2456m.f10014U[i23];
370:                     c0823q2 = c0811f02.f5121b;
371:                     C0823q c0823q9 = c0811f02.f5122c;
372:                     if (c0823q2 != null) {
373:                         C2343a c2343a7 = c2343a;
374:                         RectF m5719p03 = c0823q2.m5719p0(f10);
375:                         i18 = i23;
376:                         i19 = i25;
377:                         RectF mo11475C0 = i19 > i18 ? c2456m.mo11475C0(rectF, m5719p03) : c2456m.m11476D0(rectF, m5719p03);
378:                         if (z17 && AbstractGestureDetectorOnGestureListenerC2452i.f9952L) {
379:                             z11 = false;
380:                             C6181L c6181l = AbstractGestureDetectorOnGestureListenerC2452i.f9948H;
381:                             float f17 = f10;
382:                             Object[] objArr = new Object[i13];
383:                             objArr[0] = m5719p03;
384:                             objArr[1] = rectF;
385:                             objArr[2] = mo11475C0;
...
430:                         i19 = i25;
431:                         c2343a2 = c2343a;
432:                         c0823q3 = c0823q9;
433:                     }
434:                     if (c0823q3 != null) {
435:                         RectF m5719p04 = c0823q3.m5719p0(f10);
436:                         RectF mo11475C02 = i19 > i18 ? c2456m.mo11475C0(rectF, m5719p04) : c2456m.m11476D0(rectF, m5719p04);
437:                         if (z17 && AbstractGestureDetectorOnGestureListenerC2452i.f9952L) {
438:                             C6181L c6181l2 = AbstractGestureDetectorOnGestureListenerC2452i.f9948H;
439:                             Object[] objArr2 = new Object[3];
440:                             objArr2[z11 ? 1 : 0] = m5719p04;
441:                             objArr2[1] = rectF;
442:                             objArr2[2] = mo11475C02;
443:                             c6181l2.m25236L(str, objArr2);
444:                         }
445:                         float f18 = f10;
446:                         RectF rectF7 = rectF;
...
514:         } else {
515:             C0811f0 c0811f03 = c2456m.f10014U[i25];
516:             C0823q c0823q12 = c0811f03.f5121b;
517:             C0823q c0823q13 = c0811f03.f5122c;
518:             if (c0823q12 != null) {
519:                 RectF m5719p05 = c0823q12.m5719p0(f13);
520:                 RectF m11437m3 = c2456m.m11437m(rectF4, m5719p05);
521:                 c0823q5 = c0823q13;
522:                 c2456m.m11455w(rectF4, c0823q12, m5719p05, m11437m3, m5576a, f13);
523:                 rectF3 = rectF4;
524:                 f12 = f13;
525:                 c0813g03 = m5576a;
526:                 c2467x2.m11536i(canvas, cVar4, c0823q12, m11437m3, m11397E);
527:                 i20 = m11397E;
528:                 c2467x2.m11537j(canvas, c2456m.f9965d.getGuiTextures(), cVar4, c0823q12, m11437m3);
529:                 cVar3 = cVar4;
530:                 c2343a4 = canvas;
...
543:                 c0813g03 = m5576a;
544:                 rectF3 = rectF4;
545:                 f12 = f13;
546:             }
547:             if (c0823q5 != null) {
548:                 RectF m5719p06 = c0823q5.m5719p0(f12);
549:                 RectF m11437m4 = c2456m.m11437m(rectF3, m5719p06);
550:                 float f19 = f12;
551:                 RectF rectF9 = rectF3;
552:                 C0823q c0823q14 = c0823q5;
553:                 c2456m.m11455w(rectF9, c0823q14, m5719p06, m11437m4, c0813g03, f19);
554:                 C2343a c2343a9 = c2343a4;
555:                 C2467x c2467x6 = c2467x2;
556:                 AbstractGestureDetectorOnGestureListenerC2452i.c cVar9 = cVar3;
557:                 c2467x6.m11536i(c2343a9, cVar9, c0823q14, m11437m4, i20);
558:                 c2467x6.m11537j(c2343a9, c2456m.f9965d.getGuiTextures(), cVar9, c0823q14, m11437m4);
559:                 c2343a4 = c2343a9;
```

### `java/sources/ec/C2458o.java`

```java
...
45:         if (c0823q == null) {
46:             this.f10015S = C0811f0.m5558b(c0825s);
47:         }
48:         int i10 = 0;
49:         int i11 = c0823q == null ? 0 : c0823q.f5216c;
50:         float f11 = c0823q == null ? 0.0f : c0823q.m5646C().top;
51:         if (c0823q != null) {
52:             return;
53:         }
54:         int m16672b = c3826m.m16672b();
55:         while (true) {
56:             C0811f0[] c0811f0Arr = this.f10015S;
57:             if (i10 >= c0811f0Arr.length) {
58:                 return;
59:             }
60:             C0811f0 c0811f0 = c0811f0Arr[i10];
61:             C0823q c0823q2 = c0811f0.f5121b;
```

### `java/sources/p024bc/C0811f0.java`

```java
...
203:         float f18 = f12 > f14 ? (f12 - f14) / 2.0f : 0.0f;
204:         RectF rectF2 = new RectF(0.0f, 0.0f, f15, f16);
205:         rectF2.offset(f17, 0.0f);
206:         rectF2.offset(0.0f, f18);
207:         if (App.f17415f) {
208:             C6181L.m25212N("Page getZoomedBounds %s -> %s", this.f5121b.m5646C(), rectF2);
209:         }
210:         rectF.left = rectF2.left;
211:         rectF.top = rectF2.top;
212:         rectF.right = rectF2.right;
213:         rectF.bottom = rectF2.bottom;
214:     }
215: 
216:     /* renamed from: f */
217:     public int m5565f() {
218:         C0823q c0823q = this.f5121b;
219:         if (c0823q == null && this.f5122c == null) {
...
288:         float f18 = f12 > f14 ? (f12 - f14) / 2.0f : 0.0f;
289:         RectF rectF2 = new RectF(0.0f, 0.0f, f15, f16);
290:         rectF2.offset(f17 + m16674b2, 0.0f);
291:         rectF2.offset(0.0f, f18);
292:         if (App.f17415f) {
293:             C6181L.m25212N("Page getZoomedBounds %s -> %s", this.f5122c.m5646C(), rectF2);
294:         }
295:         rectF.left = rectF2.left;
296:         rectF.top = rectF2.top;
297:         rectF.right = rectF2.right;
298:         rectF.bottom = rectF2.bottom;
299:     }
300: 
301:     /* renamed from: l */
302:     public RectF m5570l(float f10) {
303:         RectF rectF = new RectF();
304:         m5571m(f10, rectF);
```

### `java/sources/p039d/C2282a.java`

```java
...
466:         }
467:         C0823q c0823q2 = c3810b.f15242a.f5260a[c0823q.f5216c];
468:         if (c0823q2 != c0823q) {
469:             throw new IllegalStateException("p != page");
470:         }
471:         RectF m5719p0 = c0823q2.m5719p0(this.f9508i);
472:         RectF rectF2 = this.f9506g;
473:         float f10 = rectF2.top;
474:         return f10 == this.f9507h.top ? Math.round(m5719p0.top - f10) : Math.round(m5719p0.bottom - rectF2.bottom);
475:     }
476: 
477:     /* renamed from: c */
478:     public boolean m10886c() {
479:         if (m10892i()) {
480:             return false;
481:         }
482:         int i10 = this.f9500a;
...
546:     }
547: 
548:     /* renamed from: j */
549:     public void m10893j(C0823q c0823q, RectF rectF, float f10) {
550:         this.f9505f = c0823q;
551:         this.f9506g = c0823q.m5719p0(f10);
552:         this.f9507h = rectF;
553:         this.f9508i = f10;
554:     }
555: 
556:     /* renamed from: k */
557:     public void m10894k(int i10, int i11, int i12, int i13, int i14) {
558:         this.f9500a = 0;
559:         this.f9501b.m10914s(i10, i12, i14);
560:         this.f9502c.m10914s(i11, i13, i14);
561:     }
562: 
```

## Questions answered by this evidence
- Normalized rectangle to viewport transform
- Viewport to normalized touch-coordinate transform at base zoom and scaled zoom
- pageGetSizeJni result-array call sites and page dimension source
- Whether rotation/crop offsets are applied in Java or remain inside native page geometry