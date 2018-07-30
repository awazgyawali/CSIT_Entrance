package np.com.aawaz.csitentrance.fragments.navigation_fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_leaderboard_main.*
import np.com.aawaz.csitentrance.R


class Leaderboard : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        scoreboardViewPager.adapter = ScoreboardPager(childFragmentManager)
        tabLayoutScorebaord.setupWithViewPager(scoreboardViewPager)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_leaderboard_main, container, false)
    }

    class ScoreboardPager(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return if (position == 0)
                ScoreLeaderboard()
            else VotesLeaderboard()
        }

        override fun getCount() = 2

        override fun getPageTitle(position: Int): CharSequence? {
            val titles = arrayOf("Score", "Upvote")
            return titles[position]
        }
    }
}
