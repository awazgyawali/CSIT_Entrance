package np.com.aawaz.csitentrance.fragments.navigation_fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_result.*
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import np.com.aawaz.csitentrance.R
import np.com.aawaz.csitentrance.interfaces.ResponseListener
import np.com.aawaz.csitentrance.objects.SPHandler
import np.com.aawaz.csitentrance.services.NetworkRequester
import org.json.JSONException
import org.json.JSONObject

class EntranceResult : Fragment() {

    private lateinit var rollNo: AppCompatEditText
    private lateinit var viewSwitcher: ViewSwitcher
    private lateinit var resultButton: RelativeLayout
    private lateinit var konfettiView: KonfettiView
    private lateinit var resultText: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var imm: InputMethodManager

    private fun workForViewingResult() {


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rollNo = view.findViewById(R.id.resultRollNo)
        viewSwitcher = view.findViewById(R.id.resultViewSwitcher)
        resultButton = view.findViewById(R.id.submitButton)
        resultText = view.findViewById(R.id.resultViewText)
        konfettiView = view.findViewById(R.id.konfettiView)
        progressBar = view.findViewById(R.id.progressResult)


        submitButton.setOnClickListener {
            val roll = rollNo.text.toString()
            val rollInt = roll.toInt()
            konfettiView.build()
                    .addColors(Color.RED, Color.BLUE, Color.MAGENTA)
                    .setDirection(0.0, 359.0)
                    .setSpeed(1f, 5f)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(2000L)
                    .addShapes(Shape.RECT, Shape.CIRCLE)
                    .addSizes(Size(12))
                    .setPosition(-50f, konfettiView.width + 50f, -50f, -50f)
                    .streamFor(rollInt*2, 5000L)

            imm.hideSoftInputFromWindow(view.windowToken, 0)
            if (roll.isNotEmpty()) {
                progressBar.visibility = View.VISIBLE
                NetworkRequester(getString(R.string.result_link) + roll, object : ResponseListener {
                    override fun onSuccess(res: String) {
                        try {
                            val response = JSONObject(res)
                            if (response.getBoolean("success")) {
                                resultText.visibility = View.VISIBLE
                                resultText.text = Html.fromHtml(response.getJSONObject("data").getString("result"))
                                konfettiView.build()
                                        .addColors(Color.RED, Color.BLUE, Color.MAGENTA)
                                        .setDirection(0.0, 359.0)
                                        .setSpeed(1f, 5f)
                                        .setFadeOutEnabled(true)
                                        .setTimeToLive(2000L)
                                        .addShapes(Shape.RECT, Shape.CIRCLE)
                                        .addSizes(Size(12))
                                        .setPosition(-50f, konfettiView.width + 50f, -50f, -50f)
                                        .streamFor(300, 5000L)
                            } else {
                                MaterialDialog.Builder(context!!).title("Oops").content("Sorry, Your roll number is not in the list.").show()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            Toast.makeText(context, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show()
                        }
                        progressBar.visibility = View.GONE
                    }

                    override fun onFailure() {
                        progressBar.visibility = View.GONE
                        Toast.makeText(context, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show()
                    }
                })
            }

        }

        rollNo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (SPHandler.getInstance().isResultPublished)
            handlePublished()
        FirebaseDatabase.getInstance().reference.child("result_published").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.getValue(Boolean::class.javaPrimitiveType!!)!!) {
                    handlePublished()
                    SPHandler.getInstance().setResultPublished()
                } else {
                    if (viewSwitcher.displayedChild == 1)
                        viewSwitcher.showPrevious()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun handlePublished() {
        rollNo.requestFocus()
        if (viewSwitcher.displayedChild == 0)
            viewSwitcher.showNext()
        workForViewingResult()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_result, container, false)
    }
}
