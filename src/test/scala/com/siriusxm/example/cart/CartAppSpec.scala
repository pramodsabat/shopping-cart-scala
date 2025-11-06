package com.siriusxm.example.cart

import com.siriusxm.example.cart.CartApp
import zio.Scope

import zio.test.Assertion.containsString
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertCompletes, assertZIO, assert}

object CartAppSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment & Scope, Any] = suite("cart application run")(
    test("test the run function run successfully without error") {
      for {
        _ <- CartApp.run // Run the application
      } yield assertCompletes // Assert that it completes without errors
    },
    test("test the run function run successfully for all given products") {
      val resultEffect = CartApp.run

      assertZIO(resultEffect)(
        containsString("frosties -> 4.99 | " +
          "cornflakes -> 2.52 | " +
          "cheerios -> 8.43 | " +
          "weetabix -> 9.98 | " +
          "shreddies -> 4.68"))
    }
  )
}
