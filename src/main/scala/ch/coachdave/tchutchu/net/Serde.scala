package ch.coachdave.tchutchu.net

import ch.coachdave.tchutchu.SortedBag

import java.util.regex.Pattern
import scala.reflect.ClassTag
import scala.jdk.CollectionConverters.*

trait Serde[C]:

  def serialize(e: C): String
  def deserialize(s: String): C


object Serde:

  def of[C](ser: C => String, deser: String => C): Serde[C] =
    new Serde[C] {
      override def serialize(e: C): String = ser(e)

      override def deserialize(s: String): C = deser(s)
    }

  def oneOf[C](enums: Seq[C]): Serde[Option[C]] =
    new Serde[Option[C]] {
      override def serialize(e: Option[C]): String = if e.isEmpty then "" else Integer.toString(enums.indexOf(e.get))

      override def deserialize(s: String): Option[C] = if s == "" then None else Some(enums(Integer.parseInt(s)))
    }

  def oneOfNoNull[C](enums: Seq[C]): Serde[C] =
    new Serde[C] {
      override def serialize(e: C): String = Integer.toString(enums.indexOf(e))

      override def deserialize(s: String): C = enums(Integer.parseInt(s))
    }

  def listOf[C:ClassTag](serde: Serde[C], sep: String): Serde[List[C]] =
    new Serde[List[C]] {
      override def serialize(l: List[C]): String = if l.isEmpty then "" else l.map(e => serde.serialize(e)).mkString(sep)

      override def deserialize(s: String): List[C] = if s == "" then Nil else s.split(Pattern.quote(sep)).map(serde.deserialize).toList
    }

  def bagOf[C <: Comparable[C]:ClassTag](serde: Serde[C], sep: String): Serde[SortedBag[C]] =
    new Serde[SortedBag[C]] {
      override def serialize(sb: SortedBag[C]): String = if sb.size() == 0 then "" else sb.toList.asScala.map(serde.serialize).mkString(sep)

      override def deserialize(s: String): SortedBag[C] = if s == "" then SortedBag.of() else
        SortedBag.of(s.split(Pattern.quote(sep)).map(serde.deserialize).toList.asJava)
    }
