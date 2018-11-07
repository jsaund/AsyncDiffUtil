package com.experiments.jagsaund.asyncdiffutil

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: ItemAdapter

    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = ItemAdapter()
        val recyclerView = findViewById<RecyclerView>(R.id.list)
        recyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()

        job = launch {
            val repository = Repository()
            repository.data(2, TimeUnit.SECONDS).consumeEach { adapter.update(it) }
        }
    }

    override fun onStop() {
        super.onStop()
        job?.cancel()
    }

    class ItemAdapter : RecyclerView.Adapter<ItemViewHolder>() {
        private val asyncDiffUtil: AsyncDiffUtil<Item> = AsyncDiffUtil(this, object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item) = oldItem.isSame(newItem)

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean = oldItem.isContentSame(newItem)
        })

        override fun onCreateViewHolder(parent: ViewGroup, position: Int): ItemViewHolder {
            return ItemViewHolder.create(parent)
        }

        override fun getItemCount(): Int = asyncDiffUtil.current().size

        override fun onBindViewHolder(viewHolder: ItemViewHolder, position: Int) {
            if (position != RecyclerView.NO_POSITION) {
                val item = asyncDiffUtil.current()[position]
                viewHolder.bind(item)
            }
        }

        fun update(items: List<Item>) {
            asyncDiffUtil.update(items)
        }
    }

    class ItemViewHolder(private val view: TextView) : RecyclerView.ViewHolder(view) {
        companion object {
            fun create(parent: ViewGroup): ItemViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.item, parent, false)
                return ItemViewHolder(view.findViewById(R.id.item))
            }
        }

        fun bind(item: Item) {
            view.text = item.label
            view.setBackgroundColor(item.color)
        }
    }
}
