package com.project.last_yumyum.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.last_yumyum.R
import com.project.last_yumyum.Dataclass.RecipeStep

class RecipeStepAdapter(private val steps: List<RecipeStep>) :
    RecyclerView.Adapter<RecipeStepAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stepNumberTextView: TextView = itemView.findViewById(R.id.stepNumberTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val stepImageView: ImageView = itemView.findViewById(R.id.stepImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_step, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val step = steps[position]
        holder.stepNumberTextView.text = "Step ${step.stepNumber}"
        holder.descriptionTextView.text = step.description
        Glide.with(holder.itemView.context)
            .load(step.stepUrl)
            .into(holder.stepImageView)
    }

    override fun getItemCount(): Int {
        return steps.size
    }
}
