package com.siriusxm.example.service

import com.typesafe.config.ConfigFactory
import zio.ZIO

trait ProductService {
  protected val productUrl: String = ConfigFactory.load().getString("config.productUrl")

  /**
   * Find product price based on search type
   * @param productName whose details need to be fetched
   * @param searchType  defines the type of search
   * @return product price of type float
   */
  def findProductPrice(productName: String, searchType: String): ZIO[Any, Throwable, Float]
}