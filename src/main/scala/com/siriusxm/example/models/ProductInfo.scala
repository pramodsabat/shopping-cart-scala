package com.siriusxm.example.models

import zio.json.{DeriveJsonDecoder, JsonDecoder}

case class ProductInfo(title: String, price: Float)

object ProductInfo {
  /**
   * Used for converting case class to json
   * @return
   */
  given decoder: JsonDecoder[ProductInfo] = DeriveJsonDecoder.gen[ProductInfo]
}