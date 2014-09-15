package carryx.amazon.api.stackable

/**
 * @author alari (name.alari@gmail.com)
 * @since 01.11.13 13:45
 *
 * @see
 */
trait OffersRG extends RG with OfferSummaryRG {
  self: AmazonItem =>

  abstract override def rgName = buildName(super.rgName, "Offers")

  private lazy val ofn = (node \ "Offers").headOption

  lazy val offersTotal = ofn.map(int("TotalOffers", _)).getOrElse(0)
  lazy val offersTotalPages = ofn.map(int("TotalOfferPages", _)).getOrElse(0)
  lazy val offersMoreUrl = ofn.map(text("MoreOffersUrl", _))

  lazy val offers: Seq[OffersRG.Offer] =
    ofn.map(_ \ "Offer").getOrElse(Seq.empty).map {
      o =>
        for {
          listing <- (o \ "OfferListing").headOption
          attributes <- (o \ "OfferAttributes").headOption
          availabilityAttrs <- (listing \ "AvailabilityAttributes").headOption
        } yield OffersRG.Offer(
          text("Condition", attributes),
          text("OfferListingId", listing),
          Price.build(listing \ "Price" head),
          text("Availability", listing),
          text("AvailabilityType", availabilityAttrs),
          int("MaximumHours", availabilityAttrs),
          int("MinimumHours", availabilityAttrs),

          int("IsEligibleForSuperSaverShipping", listing) > 0
        )
    }.collect {
      case Some(o) => o
    }

}

object OffersRG {

  case class Offer(
                    condition: String,
                    listingId: String,
                    price: Price,
                    availability: String,
                    availabilityType: String,
                    availableMaxHours: Int,
                    availableMinHours: Int,
                    isEligibleForSuperSaverShipping: Boolean
                    )


}