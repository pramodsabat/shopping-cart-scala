package com.siriusxm.example.service

import zio.{Ref, Task, UIO}

trait CartService {
  protected val TAX_RATE = 0.125d // Tax payable, charged at 12.5% on the subtotal
  protected val DECIMAL_SCALE = 2 // Money rounded to 2 decimal places
  protected val RoundingMode: BigDecimal.RoundingMode.Value = BigDecimal.RoundingMode.UP

  private type ProductTitle = String
  private type Price = Float
  private type Quantity = Int
  
  type Entries = Map[ProductTitle, (Price, Quantity)] // Cart holding all items

  /**
   * Add item by product name & quantity, findPriceByProductTitle provides price value if it exists
   *
   * @param data     refers to the cart
   * @param name     refers to the name of the product
   * @param quantity refers to number of products in the cart
   * @return
   */
  def addItem(data: Ref[Entries], name: String, quantity: Int): Task[Unit]

  /**
   * Calculate number of items in the cart (Repeated items are ignored)
   * @param data refers to the cart
   * @return total items
   */
  def itemCount(data: Ref[Entries]): UIO[Int]

  /**
   * Calculate number of items in the cart (Repeated items are ignored)
   * @param data refers to the cart
   * @return total items
   */
  def itemLineCount(data: Ref[Entries]): UIO[Int]

  /**
   * Calculate subtotal of products in the cart
   * @param data refers to the cart
   * @return
   */
  def subtotal(data: Ref[Entries]): UIO[BigDecimal]

  /**
   * Calculate the total tax payable
   * @param data refers to the cart
   * @return
   */
  def taxPayable(data: Ref[Entries]): UIO[BigDecimal]

  /**
   * Total Payable amount including all charges
   * @param data refers to the cart
   * @return
   */
  def totalPayable(data: Ref[Entries]): UIO[BigDecimal]
}