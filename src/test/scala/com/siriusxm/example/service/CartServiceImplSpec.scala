package com.siriusxm.example.service

import zio.Scope
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}
import com.siriusxm.example.models.{ProductInfo, ShoppingCart}

object CartServiceImplSpec extends ZIOSpecDefault {
  val cornflakes = ProductInfo("cornflakes", 4.70f)
  val frosties = ProductInfo("frosties", 4.80f)
  val weetabix = ProductInfo("weetabix", 5.80f)

  override def spec: Spec[TestEnvironment & Scope, Any] = suite("test cart service implementation")(
    test("test addItem, add 2 × cornflakes @ 2.52 each,\n" +
      "add 1 × weetabix @ 9.98 each,\n" +
      "subtotal = 15.02,\n" +
      "tax = 1.88,\n" +
      "total = 16.90,\n" +
      "total No. of items in cart = 3 (2 + 1),\n" +
      "total No. of products in cart = 2") {
      for cart <- ShoppingCart.newCart
          _ <- CartServiceImpl.addItem(cart.entries, cornflakes.title, 2)
          _ <- CartServiceImpl.addItem(cart.entries, weetabix.title, 1)
          itemCount <- CartServiceImpl.itemCount(cart.entries)
          itemLineCount <- CartServiceImpl.itemLineCount(cart.entries)
          subtotal <- CartServiceImpl.subtotal(cart.entries)
          tax <- CartServiceImpl.taxPayable(cart.entries)
          total <- CartServiceImpl.totalPayable(cart.entries)
      yield assertTrue((itemCount, itemLineCount, subtotal, tax, total) == (3, 2, 15.02, 1.88, 16.90))
    },

    test("test addItem, add 4 x cornflakes & 2 x frosties") {
      for cart <- ShoppingCart.newCart
          _ <- CartServiceImpl.addItem(cart.entries, cornflakes.title, 2)
          _ <- CartServiceImpl.addItem(cart.entries, cornflakes.title, 2)
          _ <- CartServiceImpl.addItem(cart.entries, frosties.title, 2)
          itemCount <- CartServiceImpl.itemCount(cart.entries)
          itemLineCount <- CartServiceImpl.itemLineCount(cart.entries)
          subtotal <- CartServiceImpl.subtotal(cart.entries)
          tax <- CartServiceImpl.taxPayable(cart.entries)
          total <- CartServiceImpl.totalPayable(cart.entries)
      yield assertTrue((itemCount, itemLineCount, subtotal, tax, total) == (6, 2, 20.06, 2.51, 22.57))
    },
  )
}