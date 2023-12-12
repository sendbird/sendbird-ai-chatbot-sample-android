package com.sendbird.jerry.dx.chatbot_turorial

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.sendbird.android.channel.BaseChannel
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.message.BaseMessage
import com.sendbird.jerry.dx.chatbot_turorial.databinding.CardViewMessageBinding
import com.sendbird.uikit.activities.adapter.MessageListAdapter
import com.sendbird.uikit.activities.viewholder.MessageViewHolder
import com.sendbird.uikit.log.Logger
import com.sendbird.uikit.model.MessageListUIParams
import com.sendbird.uikit.utils.DateUtils
import com.sendbird.uikit.utils.ViewUtils
import kotlinx.serialization.json.Json

class CardViewAdapter(
    channel: GroupChannel,
    uiParams: MessageListUIParams
) : MessageListAdapter(channel, uiParams) {

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isCardView()) return CARD_VIEW_TYPE
        else super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        if (viewType == CARD_VIEW_TYPE) {
            val inflater = LayoutInflater.from(parent.context)
            return CardViewHolder(
                CardViewMessageBinding.inflate(inflater, parent, false)
            )
        }
        return super.onCreateViewHolder(parent, viewType)
    }

    private class CardViewHolder(
        val binding: CardViewMessageBinding
    ) : MessageViewHolder(binding.root) {

        val cardViews = listOf(binding.cardView1, binding.cardView2, binding.cardView3)
        val titles = listOf(binding.title1, binding.title2, binding.title3)
        val descriptions = listOf(binding.description1, binding.description2, binding.description3)
        val itemsList = listOf(binding.items1, binding.items2, binding.items3)

        @SuppressLint("SetTextI18n")
        override fun bind(channel: BaseChannel, message: BaseMessage, params: MessageListUIParams) {
            super.bind(channel, message, params)
            binding.tvMessage.text = message.message
            binding.tvNickname.text = message.sender?.nickname
            binding.tvSentAt.text = DateUtils.formatTime(binding.root.context, message.createdAt)
            ViewUtils.drawProfile(binding.ivProfileView, message)

            val functionCalls = message.messageData()?.functionCalls ?: emptyList()
            when (val call = functionCalls.firstOrNull()) {
                is OrderListFunctionCall -> {
                    binding.tvMessage.text = "Here is your order list:"
                    cardViews.forEach { it.isVisible = false }
                    call.orders.forEachIndexed { index, order ->
                        cardViews[index].isVisible = true
                        titles[index].text = "${order.status.emoji} Order #${order.id}"
                        descriptions[index].text = order.status.name
                        itemsList[index].text = order.items.joinToString(",")
                    }
                }

                is OrderDetailsFunctionCall -> {
                    binding.tvMessage.text = "Here are the details for Order #3:"
                    titles[0].text = "Order #${call.orderDetails.id} by ${call.orderDetails.customerName}"
                    descriptions[0].text = """
                        - Status: ${call.orderDetails.status.name}
                        - Estimated delivery date: ${call.orderDetails.estimatedDeliveryDate}
                    """.trimIndent()
                    itemsList[0].text = """
                        - Items: ${call.orderDetails.items.joinToString(",")}
                        - Total price: $${call.orderDetails.purchasePrice}
                    """.trimIndent()

                    binding.cardView2.isVisible = false
                    binding.cardView3.isVisible = false
                }
                else -> {}
            }
        }

        override fun getClickableViewMap(): MutableMap<String, View> {
            return mutableMapOf(
                "card_view" to binding.root
            )
        }
    }

    companion object {
        const val CARD_VIEW_TYPE = 1001
    }
}

private fun BaseMessage.isCardView(): Boolean {
    //return extendedMessage.containsKey("custom_view")
    // Parse data as Something, and check if it is not empty
//    return messageData()?.functionCalls?.any {
//        it.name == "get_order_list"
//    } ?: false
    return messageData()?.functionCalls?.isNotEmpty() == true
}

private fun BaseMessage.messageData(): MessageData? {
    val messageData = try {
        Json.decodeFromString<MessageData>(data)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
    return messageData
}

