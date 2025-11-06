package com.siriusxm.example.service

import sttp.client4.httpclient.zio.HttpClientZioBackend
import sttp.client4.ziojson.asJson
import sttp.client4.{Request, UriContext, basicRequest}
import sttp.model.StatusCode
import zio.ZIO

import com.siriusxm.example.models.{ProductInfo, SearchType}

/**
 * Implementation class to handle all product related services
 */
object ProductServiceImpl extends ProductService {
  val DEFAULT_PRICE = 0f

  
  def findProductPrice(productName: String, searchType: String): ZIO[Any, Throwable, Float] =
    if (productName == null || productName.isEmpty)
      ZIO.fail(new IllegalArgumentException("ProductName must be non-empty String"))
    else
      searchType match {
        case SearchType.NAME =>
          val request = basicRequest.get(uri"$productUrl/${productName.toLowerCase}.json")
          findProductPriceByName(request)
        case _ => ZIO.fail(new IllegalArgumentException("No search type defined"))
      }

  /**
   * Find the product price based on the name
   * @param request with the body which need to be sent
   * @return product price or throw exception if not found
   */
  private def findProductPriceByName(request: Request[Either[String, String]]): ZIO[Any, Throwable, Float] =
    ZIO.scoped {
      for {
        httpClient <- HttpClientZioBackend.scoped() // Open HTTP client connection
        response <- request.response(asJson[ProductInfo]).send(httpClient) // Send the request
        price <- response.code match {
          case StatusCode.Ok =>
            response.body match
              case Left(error) =>
                ZIO.logError(s"Request Url: ${request.uri}, Response Code: ${response.code}, Failed to find price: $error")
                  *> ZIO.succeed(0f)
              case Right(productInfo) =>
                ZIO.logInfo(s"Request Url: ${request.uri}, Code: ${response.code}, Body: ${response.body}")
                  *> ZIO.succeed(productInfo.price)
          case StatusCode.NotFound =>
            ZIO.logError(s"Request Url: ${request.uri}, Response Code: ${response.code}, Resource Not Found")
              *> ZIO.succeed(DEFAULT_PRICE)
        }
      } yield price
    }
}