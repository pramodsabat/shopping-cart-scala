package com.siriusxm.example.service

import com.siriusxm.example.models.{ProductInfo, SearchType}
import zio.{Scope, ZIO}
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}

object ProductServiceImplSpec extends ZIOSpecDefault {
  val cornflakes = ProductInfo("cornflakes", 4.70f)
  val frosties = ProductInfo("frosties", 4.80f)
  val weetabix = ProductInfo("weetabix", 5.80f)
  val cheerios = ProductInfo("cheerios", 2.80f)
  val shreddies = ProductInfo("shreddies", 1.80f)

  val validProducts = Set(cheerios.title, cornflakes.title, frosties.title, shreddies.title, weetabix.title)

  override def spec: Spec[TestEnvironment & Scope, Any] = suite("test product service implementation")(
    test("test findProductPrice for valid products") {
      ZIO.foreach(validProducts) { name =>
        ProductServiceImpl.findProductPrice(name, SearchType.NAME)
          .map(price => assertTrue(price > 0f))
      }.map(_.reduce(_ && _))
    },
    test("test findProductPrice should return 0f for no product found from the api call") {
      val invalidProducts = Set("tomato", "invalid", "11")
      ZIO.foreach(invalidProducts) { name =>
        ProductServiceImpl
          .findProductPrice(name, SearchType.NAME)
          .map(price => assertTrue(price == 0.0f))
      }.map(_.reduce(_ && _))
    },
    test("test findProductPrice should return 0f when the API throws exception") {
      val invalidProducts = Set("unsupported")
      ZIO.foreach(invalidProducts) { name =>
        ProductServiceImpl
          .findProductPrice(name, SearchType.NAME)
          .map(price => assertTrue(price == 0.0f))
      }.map(_.reduce(_ && _))
    },
    test("test findProductPrice fails for invalid product names") {
      val invalidProducts = Set(null, "")
      ZIO.foreach(invalidProducts) { name =>
        ProductServiceImpl
          .findProductPrice(name, SearchType.NAME)
          .exit
          .map(exit => assertTrue(exit.isFailure))
      }.map(_.reduce(_ && _))
    },
  )
}
