package wee.digital.fpa.repository.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SocketResultResp(
    @SerializedName("Event")
    @Expose
    var event: String = "",

    @SerializedName("PaymentID")
    @Expose
    var paymentId: String = "",

    @SerializedName("Amount")
    @Expose
    var amount: String = "",

    @SerializedName("PaymentRequestTimeout")
    @Expose
    val timeOut: Int = 0
)