package com.siriusxm.example.cart

import com.siriusxm.example.models.SearchType
import com.siriusxm.example.service.ProductServiceImpl
import zio.{Unsafe, ZIO, ZIOAppDefault}

/**
 * The starter object
 */
object CartApp extends ZIOAppDefault {
  /**
   * Static set of products a user wanted to add
   */
  val products = Set(
    "cheerios",
    "cornflakes",
    "frosties",
    "shreddies",
    "weetabix"
  )

  /**
   * @param products which need to be added in the cart
   * @return product and price key-value pair (product -> price) or throw exception when failed
   */
  private def getProductInfo(products: Set[String]): Map[String, Float] = {
    val productInfo: ZIO[Any, Throwable, Map[String, Float]] =
      ZIO
        .foreach(products) { product =>
          ProductServiceImpl
            .findProductPrice(product, SearchType.NAME)
            .map(product -> _)}.map(_.toMap)

    Unsafe.unsafe { implicit unsafe =>
      runtime.unsafe.run(productInfo).getOrThrowFiberFailure()
    }
  }

  /**
   * @return product information in case of successful or exception when no product found
   */
  def run: ZIO[Any, Throwable, String] = {
    getProductInfo(products).mkString(" | ") match {
      case productInfo if productInfo.nonEmpty =>
        ZIO.logInfo(s"Product Info - $products and output - $productInfo")
        ZIO.succeed(productInfo)
      case productInfo if productInfo.isEmpty => ZIO.fail(new IllegalArgumentException("No product info found"))
    }
  }
}