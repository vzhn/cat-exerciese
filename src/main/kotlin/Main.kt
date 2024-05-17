package org.example

data class Obj(val name: String, val id: Int)
data class Morphism(val name: String, val id: Int)

class Cat(val name: String) {
  private val objects = mutableSetOf<Obj>()
  private val morphisms = mutableSetOf<Morphism>()
  private val morphismToSource = mutableMapOf<Morphism, Obj>()
  private val morphismToTarget = mutableMapOf<Morphism, Obj>()
  private val objToIdentityMorphism = mutableMapOf<Obj, Morphism>()

  private val morphismCompositions = mutableMapOf<Morphism, Pair<Morphism, Morphism>>()
  private val compositionsToMorphisms = mutableMapOf<Pair<Morphism, Morphism>, Morphism>()

  val obj get(): Set<Obj> = objects
  val hom get(): Set<Morphism> = morphisms

  fun newMorphism(name: String = "", source: Obj, target: Obj): Morphism {
    val mid = morphisms.size + 1
    val m = Morphism(name.takeIf(String::isNotEmpty) ?: "Mor #$mid", mid)
    morphisms.add(m)
    morphismToSource[m] = source
    morphismToTarget[m] = target
    return m
  }

  fun newObject(name: String = ""): Pair<Obj, Morphism> {
    val objId = objects.size + 1
    val obj = Obj(name.takeIf(String::isNotEmpty) ?: "Obj #$objId", objId)
    objects.add(obj)

    val morphism = newMorphism("identity for $objId", obj, obj)
    objToIdentityMorphism[obj] = morphism
    return obj to morphism
  }

  fun source(m: Morphism): Obj = morphismToSource.getValue(m)
  fun target(m: Morphism): Obj = morphismToTarget.getValue(m)
  fun identity(o: Obj): Morphism = objToIdentityMorphism.getValue(o)
  fun isIdentity(m: Morphism): Boolean = m == objToIdentityMorphism.getValue(source(m))
  fun isComposition(m: Morphism): Boolean = morphismCompositions.containsKey(m)
  fun decompose(gf: Morphism): Pair<Morphism, Morphism> = morphismCompositions.getValue(gf)

  private fun isEqualByIdentity(a: Morphism, b: Morphism): Boolean {
    if (!isComposition(a)) return false
    // al * ar = a
    val (al, ar) = decompose(a)

    // al {idB} * ar = b
    // al * ar {idA} = b
    return isIdentity(al) && isEq(ar, b) || isIdentity(ar) && isEq(al, b)
  }

  private fun isEqualByComposition(a: Morphism, b: Morphism): Boolean {
    // g1 * f1 =? g2 * f2
    if (!isComposition(a) || !isComposition(b)) return false
    val (g1, f1) = decompose(a)
    val (g2, f2) = decompose(b)
    if (isEq(g1, g2) && isEq(f1, f2)) return true

    // associativity case
    // g1 * (fl * fr) =? (gl * gr) * f2
    if (!isComposition(f1)) return false
    if (!isComposition(g2)) return false

    val (fl, fr) = decompose(f1)
    val (gl, gr) = decompose(g2)
    return isEq(g1, gl) && isEq(fl, gr) && isEq(fr, f2)
  }

  fun isEq(a: Morphism, b: Morphism): Boolean {
    if (a == b) return true
    if (source(a) != source(b) || target(a) != target(b)) return false

    if (isEqualByIdentity(a, b) || isEqualByIdentity(b, a)) return true
    if (isEqualByComposition(a, b) || isEqualByComposition(b, a)) return true
    return false
  }

  fun compose(after: Morphism, before: Morphism): Morphism {
    val src1 = source(before)
    val target1 = target(before)
    val src2 = source(after)
    val target2 = target(after)
    if (target1 != src2) throw IllegalArgumentException("illegal composition")

    if (compositionsToMorphisms.containsKey(after to before)) {
      return compositionsToMorphisms.getValue(after to before)
    }

    val m = newMorphism("(${after.name} * ${before.name})", src1, target2)
    compositionsToMorphisms[after to before] = m
    morphismCompositions[m] = after to before
    return m
  }
}

fun main() {
  println("Hello World!")
}