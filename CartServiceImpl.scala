package com.siriusxm.example.service

import zio.{Ref, Task, UIO}

import java.math.RoundingMode
import scala.math.BigDecimal

import com.siriusxm.example.models.{ProductInfo, SearchType}
import com.siriusxm.example.service.ProductServiceImpl.findProductPrice

object CartServiceImpl extends CartService {
  
  def addItem(data: Ref[Entries], name: String, quantity: Int): Task[Unit] =
    for {
      price <- findProductPrice(name, SearchType.NAME)
      _ <- addItemInfo(data, ProductInfo(name, price), quantity)
    } yield ()

  /**
   * Add ProductInfo[name, price] & quantity
   * @param data refers to the cart
   * @param product refers to the product with all attributes
   * @param quantity refers to the number of products in the cart
   * @return
   */
  def addItemInfo(data: Ref[Entries], product: ProductInfo, quantity: Int): Task[Unit] = {
    data.modify { entries =>
      entries.get(product.title) match // Check if the product already exists in the cart
        case Some((existingPrice, existingCount)) =>
          (Some, entries.updated(product.title, (existingPrice, existingCount + quantity))) // If the product exists, update the count.
        case None =>
          (None, entries.updated(product.title, (product.price, quantity))) // If the product doesn't exist, add it.
    }
  }
  
  def itemCount(data: Ref[Entries]): UIO[Int] = data.get.map(item => item.values.map { case (_, count) => count }.sum)
  
  def itemLineCount(data: Ref[Entries]): UIO[Int] = data.get.map(_.size)
  
  def subtotal(data: Ref[Entries]): UIO[BigDecimal] =
    data.get.map { entries =>
      entries
        .foldLeft(BigDecimal(0)) { case (sum, (_, (price, count))) => sum + BigDecimal(price) * count}
        .setScale(DECIMAL_SCALE, RoundingMode)
    }
  
  def taxPayable(data: Ref[Entries]): UIO[BigDecimal] = 
    subtotal(data).map(st => (st * TAX_RATE).setScale(DECIMAL_SCALE, RoundingMode))
  
  def totalPayable(data: Ref[Entries]): UIO[BigDecimal] =
    for {
      st <- subtotal(data)
      tax <- taxPayable(data)
    } yield (st + tax).setScale(DECIMAL_SCALE, RoundingMode)
}