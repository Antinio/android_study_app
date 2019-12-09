package ag.shramko.home

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import io.realm.RealmList
import io.realm.RealmObject

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)

        if (savedInstanceState == null) {
            val bundle = Bundle()
            bundle.putString("param", "value")
            val fragment = MainFragment()
            fragment.arguments = bundle
            supportFragmentManager.beginTransaction().replace(R.id.fragment_place, fragment)
                .commitAllowingStateLoss()
        }
    }

    fun showArticle(url: String) {

        val bundle = Bundle()
        bundle.putString("url", url)
        val fragment = SecondFragment()
        fragment.arguments = bundle

        val frame2 = findViewById<View>(R.id.fragment_place2)
        if (frame2 != null) {
            frame2.visibility = View.VISIBLE
            supportFragmentManager.beginTransaction().replace(R.id.fragment_place2, fragment)
                .commitAllowingStateLoss()
        } else {
            supportFragmentManager.beginTransaction().add(R.id.fragment_place, fragment)
                .addToBackStack("main")
                .commitAllowingStateLoss()
        }
    }
}

class FeedAPI(
    val items: ArrayList<FeedItemAPI>
)

class FeedItemAPI(
    val title: String,
    val link: String,
    val thumbnail: String,
    val description: String,
    val guid: String
)

open class Feed(
    var items: RealmList<FeedItem> = RealmList<FeedItem>()
) : RealmObject()

open class FeedItem(
    var title: String = "",
    var link: String = "",
    var thumbnail: String = "",
    var description: String = "",
    var guid: String = ""
) : RealmObject()

class Adapter(val items: ArrayList<FeedItemAPI>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val inflater = LayoutInflater.from(parent!!.context)

        val view = convertView ?: inflater.inflate(R.layout.list_item, parent, false)
        val vTitle = view.findViewById<TextView>(R.id.item_title)

        val item = getItem(position) as FeedItemAPI

        vTitle.text = item.title

        return view
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }

}

class RecAdapter(val items: RealmList<FeedItem>) : RecyclerView.Adapter<RecHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecHolder {
        val inflater = LayoutInflater.from(parent!!.context)

        val view = inflater.inflate(R.layout.list_item, parent, false)

        return RecHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecHolder, position: Int) {
        val item = items[position]!!

        holder.bind(item)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

}

class RecHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(item: FeedItem) {
        val vTitle = itemView.findViewById<TextView>(R.id.item_title)
        val vDesc = itemView.findViewById<TextView>(R.id.item_desc)
        val vThumb = itemView.findViewById<ImageView>(R.id.item_thumb)
        vTitle.text = item.title
        vDesc.text = Html.fromHtml(item.description)

        if (item.thumbnail.isEmpty()) {
            vThumb
        } else {
            Picasso.with(vThumb.context).load(item.thumbnail).into(vThumb)
        }

        itemView.setOnClickListener {
            //            val i = Intent(Intent.ACTION_VIEW)
//            i.data = Uri.parse(item.link)
//            vThumb.context.startActivity(i)
            (vThumb.context as MainActivity).showArticle(item.link)
        }

    }
}