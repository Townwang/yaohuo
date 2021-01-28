package com.townwang.yaohuo.ui.fragment.bbs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.LIST_ACTION_KEY
import com.townwang.yaohuo.common.LIST_BBS_NAME_KEY
import com.townwang.yaohuo.common.LIST_CLASS_ID_KEY
import com.townwang.yaohuo.common.onClickListener
import com.townwang.yaohuo.ui.activity.ActivityList
import kotlinx.android.synthetic.main.fragment_bbs.*

class BBSFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bbs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resourceSharing.onClickListener {
            startActivity(Intent(
                requireContext(), ActivityList::class.java
            ).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                putExtra(LIST_CLASS_ID_KEY, 201)
                putExtra(LIST_BBS_NAME_KEY, getString(R.string.bbs_res_share))
                putExtra(LIST_ACTION_KEY, BuildConfig.YH_BBS_ACTION_CLASS)
            }
            )
        }

        integratedTechnology.onClickListener {
            startActivity(Intent(
                requireContext(), ActivityList::class.java
            ).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                putExtra(LIST_CLASS_ID_KEY, 197)
                putExtra(LIST_BBS_NAME_KEY, getString(R.string.bbs_integrated_technology))
                putExtra(LIST_ACTION_KEY, BuildConfig.YH_BBS_ACTION_CLASS)
            }
            )
        }
        mlTalkOver.onClickListener {
            startActivity(Intent(
                requireContext(), ActivityList::class.java
            ).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                putExtra(LIST_CLASS_ID_KEY, 203)
                putExtra(LIST_BBS_NAME_KEY, getString(R.string.bbs_ml_talk_over))
                putExtra(LIST_ACTION_KEY, BuildConfig.YH_BBS_ACTION_CLASS)
            }
            )
        }
        reward.onClickListener {
            startActivity(Intent(
                requireContext(), ActivityList::class.java
            ).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                putExtra(LIST_CLASS_ID_KEY, 204)
                putExtra(LIST_BBS_NAME_KEY, getString(R.string.bbs_reward))
                putExtra(LIST_ACTION_KEY, BuildConfig.YH_BBS_ACTION_CLASS)
            }
            )
        }
        teahouse.onClickListener {
            startActivity(Intent(
                requireContext(), ActivityList::class.java
            ).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                putExtra(LIST_CLASS_ID_KEY, 177)
                putExtra(LIST_BBS_NAME_KEY, getString(R.string.bbs_tea_house))
                putExtra(LIST_ACTION_KEY, BuildConfig.YH_BBS_ACTION_CLASS)
            }
            )
        }
        rewardQuestionAndAnswer.onClickListener {
            startActivity(Intent(
                requireContext(), ActivityList::class.java
            ).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                putExtra(LIST_CLASS_ID_KEY, 213)
                putExtra(LIST_BBS_NAME_KEY, getString(R.string.bbs_quest_answer))
                putExtra(LIST_ACTION_KEY, BuildConfig.YH_BBS_ACTION_CLASS)
            }
            )
        }
        texturedPhoto.onClickListener {
            startActivity(Intent(
                requireContext(), ActivityList::class.java
            ).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                putExtra(LIST_CLASS_ID_KEY, 240)
                putExtra(LIST_BBS_NAME_KEY, getString(R.string.bbs_textured_photo))
                putExtra(LIST_ACTION_KEY, BuildConfig.YH_BBS_ACTION_CLASS)
            }
            )
        }
        stationService.onClickListener {
            startActivity(Intent(
                requireContext(), ActivityList::class.java
            ).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                putExtra(LIST_CLASS_ID_KEY, 199)
                putExtra(LIST_BBS_NAME_KEY, getString(R.string.bbs_stationService))
                putExtra(LIST_ACTION_KEY, BuildConfig.YH_BBS_ACTION_CLASS)
            }
            )
        }
        complaint.onClickListener {
            startActivity(Intent(
                requireContext(), ActivityList::class.java
            ).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                putExtra(LIST_CLASS_ID_KEY, 198)
                putExtra(LIST_BBS_NAME_KEY, getString(R.string.bbs_complaint))
                putExtra(LIST_ACTION_KEY, BuildConfig.YH_BBS_ACTION_CLASS)
            }
            )
        }
        announcement.onClickListener {
            startActivity(Intent(
                requireContext(), ActivityList::class.java
            ).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                putExtra(LIST_CLASS_ID_KEY, 288)
                putExtra(LIST_BBS_NAME_KEY, getString(R.string.bbs_announcement))
                putExtra(LIST_ACTION_KEY, BuildConfig.YH_BBS_ACTION_CLASS)
            }
            )
        }
    }


}