package com.sendbird.jerry.dx.chatbot_turorial

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

// Enum class for the status values
@Serializable
enum class OrderStatus(val emoji: String) {
    @SerialName("delivered")
    Delivered("✅"),
    @SerialName("delivering")
    Delivering("🚚"),
    @SerialName("preparing")
    Preparing("⏳");
}

@Serializable
data class MessageData(
    @SerialName("metadatas") val metadataList: List<String>,
    @SerialName("respond_mesg_id") val respondMessageId: Long,
    @SerialName("not_valid") val notValid: Boolean,
    @SerialName("function_calls") val functionCalls: List<FunctionCall>
)

// Map<String, String>


// Define the class to represent the deserialized response_text field
@Serializable
data class Order(
    val id: String,
    val status: OrderStatus,
    val items: List<String>
)

@Serializable
@SerialName("get_order_list")
data class OrderListFunctionCall(
    @SerialName("status_code") override val statusCode: Int,
    @SerialName("name") override val name: String,
    @SerialName("response_text") val orders: List<Order>
) : FunctionCall()

@Serializable
@SerialName("get_order_details")
data class OrderDetailsFunctionCall(
    @SerialName("status_code") override val statusCode: Int,
    @SerialName("name") override val name: String,
    @SerialName("response_text") val orderDetails: OrderDetails
) : FunctionCall()

@Serializable
data class OrderDetails(
    val id: String,
    val status: OrderStatus,
    val items: List<String>,
    @SerialName("createdDate") val createdDate: String,
    @SerialName("estimatedDeliveryDate") val estimatedDeliveryDate: String,
    @SerialName("order_date") val orderDate: String?,
    @SerialName("purchasePrice") val purchasePrice: Int,
    @SerialName("customer_name") val customerName: String
)

@Serializable(with = FunctionCallSerializer::class)
sealed class FunctionCall {
    abstract val statusCode: Int
    abstract val name: String
}


// Define a custom serializer for the function_calls to handle the JSON string in response_text
object FunctionCallSerializer : KSerializer<FunctionCall> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("FunctionCall")

    override fun serialize(encoder: Encoder, value: FunctionCall) {
        // Custom serialization logic if needed
    }

    override fun deserialize(decoder: Decoder): FunctionCall {
        // Assume the decoder is a JSON decoder
        val jsonDecoder = decoder as? JsonDecoder ?: throw SerializationException("Expected JsonDecoder")
        val jsonObject = jsonDecoder.decodeJsonElement().jsonObject

        val statusCode = jsonObject["status_code"]?.jsonPrimitive?.intOrNull ?: throw SerializationException("Missing status_code")
        val name = jsonObject["name"]?.jsonPrimitive?.content ?: throw SerializationException("Missing name")
        val responseText = jsonObject["response_text"]?.jsonPrimitive?.content ?: throw SerializationException("Missing response_text")

        return when (name) {
            "get_order_list" -> {
                OrderListFunctionCall(
                    statusCode = statusCode,
                    name = name,
                    orders = Json.decodeFromString(responseText)
                )
            }

            "get_order_details" -> {
                OrderDetailsFunctionCall(
                    statusCode = statusCode,
                    name = name,
                    orderDetails = Json.decodeFromString(responseText)
                )
            }

            else -> throw SerializationException("Unknown name: $name")
        }
    }
}


