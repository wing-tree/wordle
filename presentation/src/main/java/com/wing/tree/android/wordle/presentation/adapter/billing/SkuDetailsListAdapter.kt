package com.wing.tree.android.wordle.presentation.adapter.billing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.SkuDetails
import com.wing.tree.android.wordle.presentation.databinding.SkuDetailsItemBinding
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

class SkuDetailsListAdapter(private val onItemClick: (SkuDetails) -> Unit) : ListAdapter<SkuDetails, SkuDetailsListAdapter.ViewHolder>(DiffCallback()) {
    inner class ViewHolder(private val viewBinding: SkuDetailsItemBinding): RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: SkuDetails) {
            with(viewBinding) {
                textViewName.text = item.name
                textViewPrice.text = item.price
                textViewDescription.text = item.description

                root.setOnClickListener {
                    onItemClick(item)
                }
            }
        }

        private val SkuDetails.jsonObject get() = JSONObject(originalJson)

        private val SkuDetails.name: String get() = try {
            jsonObject.getString("name")
        } catch (e: JSONException) {
            Timber.e(e)
            title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewBinding = SkuDetailsItemBinding.inflate(inflater, parent, false)

        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback: DiffUtil.ItemCallback<SkuDetails>() {
        override fun areItemsTheSame(oldItem: SkuDetails, newItem: SkuDetails): Boolean {
            return oldItem.sku == newItem.sku
        }

        override fun areContentsTheSame(oldItem: SkuDetails, newItem: SkuDetails): Boolean {
            return oldItem == newItem
        }
    }
}