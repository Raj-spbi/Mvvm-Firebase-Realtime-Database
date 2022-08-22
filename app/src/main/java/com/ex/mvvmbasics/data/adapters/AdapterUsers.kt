package com.ex.mvvmbasics.data.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ex.mvvmbasics.RegisterRequest
import com.ex.mvvmbasics.databinding.NoteItemBinding

class AdapterUsers(/*private val onNoteClicked: (RegisterRequest) -> Unit*/) :
    ListAdapter<RegisterRequest, AdapterUsers.NoteViewHolder>(ComparatorDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        note?.let {
            holder.bind(it)
        }
    }

    inner class NoteViewHolder(private val binding: NoteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(note: RegisterRequest) {
            binding.title.text = note.username
            binding.desc.text = note.email
            binding.root.setOnClickListener {
//                onNoteClicked(note)
            }
        }

    }

    class ComparatorDiffUtil : DiffUtil.ItemCallback<RegisterRequest>() {
        override fun areItemsTheSame(oldItem: RegisterRequest, newItem: RegisterRequest): Boolean {
            return oldItem.firebaseUserId == newItem.firebaseUserId
        }

        override fun areContentsTheSame(
            oldItem: RegisterRequest,
            newItem: RegisterRequest
        ): Boolean {
            return oldItem == newItem
        }
    }
}