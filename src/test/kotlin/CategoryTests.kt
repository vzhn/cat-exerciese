import org.example.Cat
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CategoryTests {
  @Test
  fun basics() {
    val cat = Cat("Sample")
    val (a, idA) = cat.newObject("A")
    val (b, idB) = cat.newObject("B")
    val (c, idC) = cat.newObject("C")

    val f = cat.newMorphism("f", a, b)
    val g = cat.newMorphism("g", b, c)
    val gf = cat.compose(g, f)

    with(cat) {
      assertEquals(a, source(idA))
      assertEquals(b, source(idB))
      assertEquals(c, source(idC))

      assertEquals(a, target(idA))
      assertEquals(b, target(idB))
      assertEquals(c, target(idC))

      assertEquals(idA, identity(a))
      assertEquals(idB, identity(b))
      assertEquals(idC, identity(c))

      assert(isIdentity(idA))
      assert(isIdentity(idB))
      assert(isIdentity(idC))

      assertFalse(isIdentity(f))
      assertFalse(isIdentity(g))
      assertFalse(isIdentity(gf))

      assertEquals(a, source(f))
      assertEquals(b, target(f))

      assertEquals(b, source(g))
      assertEquals(c, target(g))

      assertEquals(a, source(gf))
      assertEquals(c, target(gf))

      val (g1, f1) = decompose(gf)
      assertEquals(g1, g)
      assertEquals(f1, f)
    }

    println(cat)
  }

  @Test
  fun eq_id_morphism() {
    val cat = Cat("Sample")
    val (a, idA) = cat.newObject("A")
    val (b, idB) = cat.newObject("B")

    val f = cat.newMorphism("f", a, b)

    // f ∘ 1a = f
    assertTrue(cat.isEq(cat.compose(f, idA), f))
    assertTrue(cat.isEq(f, cat.compose(f, idA)))

    // 1b ∘ f = f
    assertTrue(cat.isEq(f, cat.compose(idB, f)))
    assertTrue(cat.isEq(cat.compose(idB, f), f))

    // 1b ∘ f = f ∘ 1a
    assertTrue(cat.isEq(cat.compose(f, idA), cat.compose(idB, f)))
    assertTrue(cat.isEq(cat.compose(idB, f), cat.compose(f, idA)))
  }

  @Test
  fun eq_composition() {
    val cat = Cat("Sample")
    val (a, idA) = cat.newObject("A")
    val (b, idB) = cat.newObject("B")
    val (c, idC) = cat.newObject("C")
    val (d, idD) = cat.newObject("D")

    val ab = cat.newMorphism("ab", a, b)
    val bc = cat.newMorphism("bc", b, c)
    val cd = cat.newMorphism("cd", c, d)

    assertTrue(cat.isEq(
      cat.compose(cd, cat.compose(bc, ab)),
      cat.compose(cat.compose(cd, bc), ab))
    )

    assertTrue(cat.isEq(
      cat.compose(cat.compose(cd, bc), ab),
      cat.compose(cd, cat.compose(bc, ab)))
    )
  }
}