package com.siriusxm.example.models

import zio.{Ref, UIO}

import com.siriusxm.example.service.CartServiceImpl.Entries

/**
 * Shopping cart class holding the items in the entity reference
 * @param entries refers to the cart
 */
class ShoppingCart(val entries: Ref[Entries])

object ShoppingCart {
  /**
   * Create instance of shopping cart
   * @return
   */
  def newCart: UIO[ShoppingCart] = Ref.make(Map.empty).map(new ShoppingCart(_))
}