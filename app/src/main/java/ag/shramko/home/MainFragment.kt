package ag.shramko.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmList

class MainFragment : Fragment() {

    lateinit var vRecView: RecyclerView
    var request: Disposable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val param = arguments!!.getString("param")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater!!.inflate(R.layout.activity_main, container, false)

        vRecView = view.findViewById<RecyclerView>(R.id.act1_recView)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val o =
            createRequest("https://api.rss2json.com/v1/api.json?rss_url=http%3A%2F%2Ffeeds.twit.tv%2Fbrickhouse.xml")
                .map { Gson().fromJson(it, FeedAPI::class.java) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

        request = o.subscribe({

            val feed = Feed(
                it.items.mapTo(
                    RealmList<FeedItem>(),
                    { feed ->
                        FeedItem(
                            feed.title,
                            feed.link,
                            feed.thumbnail,
                            feed.description,
                            feed.guid
                        )
                    })
            )

            Realm.getDefaultInstance().executeTransaction { realm ->

                val oldList = realm.where(Feed::class.java).findAll()
                if (oldList.size > 0)
                    for (item in oldList)
                        item.deleteFromRealm()

                realm.copyToRealm(feed)

            }
            showRecView()
            //            showLinerLayout(it.items)
        }, {
            Log.e("test", "", it)
            showRecView()
        })
    }

    fun showRecView() {

        Realm.getDefaultInstance().executeTransaction { realm ->
            if (!isVisible)
                return@executeTransaction
            val feed = realm.where(Feed::class.java).findAll()
            if (feed.size > 0) {
                vRecView.adapter = RecAdapter(feed[0]!!.items)
                vRecView.layoutManager = LinearLayoutManager(activity)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        request?.dispose()
    }


}