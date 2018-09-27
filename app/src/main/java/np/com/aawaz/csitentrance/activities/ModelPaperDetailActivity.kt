package np.com.aawaz.csitentrance.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_model_paper_detail.*
import np.com.aawaz.csitentrance.R
import np.com.aawaz.csitentrance.objects.EventSender
import np.com.aawaz.csitentrance.objects.SPHandler

class ModelPaperDetailActivity : AppCompatActivity() {


    val codes = arrayOf(SPHandler.YEAR2069, SPHandler.YEAR2070, SPHandler.YEAR2071, SPHandler.YEAR2072, SPHandler.YEAR2073, SPHandler.YEAR2074,SPHandler.YEAR2075, SPHandler.MODEL1, SPHandler.MODEL2, SPHandler.MODEL3, SPHandler.MODEL4,
            SPHandler.MODEL5, SPHandler.MODEL6, SPHandler.MODEL7, SPHandler.MODEL8, SPHandler.MODEL9, SPHandler.MODEL10, SPHandler.SAGARMATHA, SPHandler.MODEL12, SPHandler.MODEL13, SPHandler.MODEL14, SPHandler.MODEL15, SPHandler.MODEL16, SPHandler.ACHS, SPHandler.MODEL17)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_model_paper_detail)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        }
        setSupportActionBar(model_detail_toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        val college = intent.getStringExtra("college")
        if (college == "samriddhi") {

            EventSender().logEvent("samriddhi_model_paper")
            title = "Samriddhi Model Paper"
            model_banner.setImageResource(R.drawable.samriddhi_full)
            model_name.text = "Samriddhi Model Paper"
            model_address.text = "Lokanthali, Bhaktapur"

            play_quiz.setOnClickListener {
                EventSender().logEvent("played_samriddhi_model")
                openQuizQuestion(12)
            }

            full_question.setOnClickListener {
                EventSender().logEvent("viewed_samriddhi_full")
                openFullQuestion(12)
            }
            call.setOnClickListener {
                startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "01-6636700", null)))
            }
            website.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://samriddhicollege.edu.np")))
            }
            maps.setOnClickListener {
                val gmmIntentUri = Uri.parse("geo:0,0?q=" + "Samriddhi College")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.`package` = "com.google.android.apps.maps"
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent)
                } else {
                    Toast.makeText(this, "Google maps doesn't exist..", Toast.LENGTH_SHORT).show()
                }
            }

        } else if (college == "sagarmatha") {

            EventSender().logEvent("sagarmatha_model_paper")
            title = "Sagarmatha Model Paper"
            model_banner.setImageResource(R.drawable.sagarmatha_banner)
            model_name.text = "Sagarmatha Model Paper"
            model_address.text = "Sanepa, Lalitpur"

            play_quiz.setOnClickListener {
                EventSender().logEvent("played_sagarmatha_model")
                openQuizQuestion(16)
            }

            full_question.setOnClickListener {
                EventSender().logEvent("viewed_sagarmatha_full")
                openFullQuestion(16)
            }
            call.setOnClickListener {
                startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "01-5527274", null)))
            }
            website.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://scst.edu.np/")))
            }
            maps.setOnClickListener {
                val gmmIntentUri = Uri.parse("geo:0,0?q=" + "Sagarmatha College Science And Technology")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.`package` = "com.google.android.apps.maps"
                if (mapIntent.resolveActivity(packageManager) != null) {
                    startActivity(mapIntent)
                } else {
                    Toast.makeText(this, "Google maps doesn't exist..", Toast.LENGTH_SHORT).show()
                }
            }

        } else if (college == "cab") {

            EventSender().logEvent("cab_model_paper")
            title = "CAB Model Paper"
            model_banner.setImageResource(R.drawable.cab_banner)
            model_name.text = "CAB Model Paper"
            model_address.text = "Gangahiti, Chabahil"

            play_quiz.setOnClickListener {
                EventSender().logEvent("played_cab_model")
                openQuizQuestion(20)
            }

            full_question.setOnClickListener {
                EventSender().logEvent("viewed_cab_full")
                openFullQuestion(20)
            }
            call.setOnClickListener {
                startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "01-4476119", null)))
            }
            website.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://cab.edu.np/")))
            }
            maps.setOnClickListener {
                val gmmIntentUri = Uri.parse("geo:0,0?q=" + "College of Applied Business")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.`package` = "com.google.android.apps.maps"
                if (mapIntent.resolveActivity(packageManager) != null) {
                    startActivity(mapIntent)
                } else {
                    Toast.makeText(this, "Google maps doesn't exist..", Toast.LENGTH_SHORT).show()
                }
            }

        } else if (college == "achs") {

            EventSender().logEvent("achs_model_paper")
            title = "ACHS Model Paper"
            model_banner.setImageResource(R.drawable.splash_achs)
            model_name.text = "ACHS Model Paper"
            model_address.text = "Putalisadak Kathmandu"

            play_quiz.setOnClickListener {
                EventSender().logEvent("played_achs_model")
                openQuizQuestion(22)
            }

            full_question.setOnClickListener {
                EventSender().logEvent("viewed_achs_full")
                openFullQuestion(22)
            }
            call.setOnClickListener {
                startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "01-4476119", null)))
            }
            website.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://achsnepal.edu.np/")))
            }
            maps.setOnClickListener {
                val gmmIntentUri = Uri.parse("geo:0,0?q=" + title)
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.`package` = "com.google.android.apps.maps"
                if (mapIntent.resolveActivity(packageManager) != null) {
                    startActivity(mapIntent)
                } else {
                    Toast.makeText(this, "Google maps doesn't exist..", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }


    fun openFullQuestion(position: Int) {
        val intent = Intent(this, FullQuestionActivity::class.java)
        intent.putExtra("code", codes[position])
        intent.putExtra("position", position + 1)

        startActivity(intent)
    }

    fun openQuizQuestion(position: Int) {
        val intent = Intent(this, YearQuizActivity::class.java)
        intent.putExtra("code", codes[position])
        intent.putExtra("position", position)

        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}
