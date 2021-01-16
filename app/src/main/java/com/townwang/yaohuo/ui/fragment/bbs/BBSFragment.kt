package com.townwang.yaohuo.ui.fragment.bbs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.ui.activity.ActivityList
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.fragment_bbs.*

class BBSFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bbs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).work {
            supportActionBar.work {
                title = getString(R.string.home_bbs)
                setDisplayHomeAsUpEnabled(false)
            }
            setTitleCenter(toolbar)
        }
        startAnim()
        resourceSharing.onClickListener {
            val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(), resourceSharing, "share name"
            ).toBundle()
            ActivityCompat.startActivity(
                requireContext(), Intent(
                    requireContext(), ActivityList::class.java
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    putExtra(LIST_CLASS_ID_KEY, 201)
                    putExtra(LIST_BBS_NAME_KEY, getString(R.string.bbs_res_share))
                    putExtra(LIST_ACTION_KEY, BuildConfig.YH_BBS_ACTION_CLASS)
                }, bundle
            )
        }

        integratedTechnology.onClickListener {
            val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(), resourceSharing, "share name"
            ).toBundle()
            ActivityCompat.startActivity(
                requireContext(), Intent(
                    requireContext(), ActivityList::class.java
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    putExtra(LIST_CLASS_ID_KEY, 197)
                    putExtra(LIST_BBS_NAME_KEY,  getString(R.string.bbs_integrated_technology))
                    putExtra(LIST_ACTION_KEY, BuildConfig.YH_BBS_ACTION_CLASS)
                }, bundle
            )
        }
        mlTalkOver.onClickListener {
            val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(), resourceSharing, "share name"
            ).toBundle()
            ActivityCompat.startActivity(
                requireContext(), Intent(
                    requireContext(), ActivityList::class.java
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    putExtra(LIST_CLASS_ID_KEY, 203)
                    putExtra(LIST_BBS_NAME_KEY,  getString(R.string.bbs_ml_talk_over))
                    putExtra(LIST_ACTION_KEY, BuildConfig.YH_BBS_ACTION_CLASS)
                }, bundle
            )
        }
        reward.onClickListener {
            val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(), resourceSharing, "share name"
            ).toBundle()
            ActivityCompat.startActivity(
                requireContext(), Intent(
                    requireContext(), ActivityList::class.java
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    putExtra(LIST_CLASS_ID_KEY, 204)
                    putExtra(LIST_BBS_NAME_KEY,  getString(R.string.bbs_reward))
                    putExtra(LIST_ACTION_KEY, BuildConfig.YH_BBS_ACTION_CLASS)
                }, bundle
            )
        }
        teahouse.onClickListener {
            val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(), resourceSharing, "share name"
            ).toBundle()
            ActivityCompat.startActivity(
                requireContext(), Intent(
                    requireContext(), ActivityList::class.java
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    putExtra(LIST_CLASS_ID_KEY, 177)
                    putExtra(LIST_BBS_NAME_KEY,  getString(R.string.bbs_tea_house))
                    putExtra(LIST_ACTION_KEY, BuildConfig.YH_BBS_ACTION_CLASS)
                }, bundle
            )
        }
        rewardQuestionAndAnswer.onClickListener {
            val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(), resourceSharing, "share name"
            ).toBundle()
            ActivityCompat.startActivity(
                requireContext(), Intent(
                    requireContext(), ActivityList::class.java
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    putExtra(LIST_CLASS_ID_KEY, 213)
                    putExtra(LIST_BBS_NAME_KEY,  getString(R.string.bbs_quest_answer))
                    putExtra(LIST_ACTION_KEY, BuildConfig.YH_BBS_ACTION_CLASS)
                }, bundle
            )
        }
        texturedPhoto.onClickListener {
            val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(), resourceSharing, "share name"
            ).toBundle()
            ActivityCompat.startActivity(
                requireContext(), Intent(
                    requireContext(), ActivityList::class.java
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    putExtra(LIST_CLASS_ID_KEY, 240)
                    putExtra(LIST_BBS_NAME_KEY, getString(R.string.bbs_textured_photo))
                    putExtra(LIST_ACTION_KEY, BuildConfig.YH_BBS_ACTION_CLASS)
                }, bundle
            )
        }
    }


    private fun startAnim() {
        startAnimator(resourceSharing_img.drawable)
        startAnimator(integratedTechnology_img.drawable)
        startAnimator(mlTalkOver_img.drawable)
        startAnimator(reward_img.drawable)
        startAnimator(teahouse_img.drawable)
        startAnimator(rewardQuestionAndAnswer_img.drawable)
        startAnimator(texturedPhoto_img.drawable)
    }


}