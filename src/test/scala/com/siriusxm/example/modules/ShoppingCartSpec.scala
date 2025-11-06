package com.siriusxm.example.modules

import zio.Scope
import zio.test.Assertion.equalTo
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assert}

import com.siriusxm.example.models.{ProductInfo, ShoppingCart}
import com.siriusxm.example.service.CartServiceImpl

object ShoppingCartSpec extends ZIOSpecDefault {
  val DEFAULT_PRICE = 0f

  val cheerios = ProductInfo("cheerios", 1.90f)
  val cornflakes = ProductInfo("cornflakes", 4.70f)
  val frosties = ProductInfo("frosties", 4.80f)
  val bread = ProductInfo("bread", 1.80f)
  val oats = ProductInfo("oats", 5.80f)
  val banana = ProductInfo("banana", 5.80f)

  val entries = Map(
    cheerios.title -> (1.90f, 2),
    cornflakes.title -> (4.70f, 3),
    frosties.title -> (4.80f, 1)
  )

  override def spec: Spec[TestEnvironment & Scope, Any] = suite("testing shopping cart")(
    test("test newCart with empty cart") {
      for {
        cart <- ShoppingCart.newCart
        entries <- cart.entries.get
      } yield assert(entries)(equalTo(Map.empty))
    },
    test("test newCart with 3 items, price and quantity set") {
      for {
        cart <- ShoppingCart.newCart
        _ <- CartServiceImpl.addItemInfo(cart.entries, cheerios, 2)
        _ <- CartServiceImpl.addItemInfo(cart.entries, cornflakes, 3)
        _ <- CartServiceImpl.addItemInfo(cart.entries, frosties, 1)
        result <- cart.entries.get
      } yield assert(result)(equalTo(entries))
    },
    test("test newCart with 2 items without price") {
      for {
        cart <- ShoppingCart.newCart
        _ <- CartServiceImpl.addItem(cart.entries, bread.title, 2)
        _ <- CartServiceImpl.addItem(cart.entries, oats.title, 2)
        _ <- CartServiceImpl.addItem(cart.entries, banana.title, 2)
        result <- cart.entries.get
      } yield assert(result)(equalTo(
        Map(
          bread.title -> (DEFAULT_PRICE, 2),
          oats.title-> (DEFAULT_PRICE, 2),
          banana.title-> (DEFAULT_PRICE, 2)
        ))
      )
    }
  )
}