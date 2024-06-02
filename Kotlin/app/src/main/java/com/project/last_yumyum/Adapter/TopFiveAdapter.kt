import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.last_yumyum.Dataclass.MainPage
import com.project.last_yumyum.R
import com.project.last_yumyum.page.recipe_detail


class TopFiveAdapter(val context: Context, val list: MutableList<MainPage>) :
    RecyclerView.Adapter<TopFiveAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.rv_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(list[position])

        // 아이템 클릭 리스너 설정
        holder.itemView.setOnClickListener {
            val intent = Intent(context, recipe_detail::class.java)
            // 선택된 레시피의 rcpId를 인텐트에 추가하여 com.project.last_yumyum.page.recipe_detail 액티비티로 전달
            intent.putExtra("RECIPE_ID", list[position].rcpId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: MainPage) {
            val rvText = itemView.findViewById<TextView>(R.id.rvTextArea)
            val rvImg = itemView.findViewById<ImageView>(R.id.rvImageArea)

            rvText.text = item.rcpName
            Glide.with(context)
                .load(item.rcpImageUrl)
                .into(rvImg)
        }
    }
}
