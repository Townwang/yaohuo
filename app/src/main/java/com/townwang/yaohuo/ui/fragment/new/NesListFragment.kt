package com.townwang.yaohuo.ui.fragment.new

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.ui.activity.ActivityAbout
import com.townwang.yaohuo.ui.activity.ActivityTheme
import com.townwang.yaohuo.ui.activity.ActivityWebView
import com.townwang.yaohuo.ui.fragment.pub.PubListFragment
import kotlinx.android.synthetic.main.fragment_list_new.*


class NesListFragment : Fragment() {
    lateinit var request: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        request = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            requireActivity().work {
                reload(config(THEME_KEY))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.beginTransaction()
            .replace(R.id.navHost, PubListFragment().apply {
                arguments = Bundle().also {
                    it.putInt(LIST_CLASS_ID_KEY, 0)
                    it.putString(LIST_BBS_NAME_KEY, "最新")
                }
            }
            ).commitNow()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.settings_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                requireActivity().onBackPressed()
                true
            }
            R.id.toolbar_r_img -> {
                startAnimator(item.subMenu.findItem(R.id.toolbar_r_setting).icon)
                startAnimator(item.subMenu.findItem(R.id.toolbar_r_theme).icon)
                startAnimator(item.subMenu.findItem(R.id.toolbar_r_about).icon)
                true
            }
            R.id.toolbar_r_setting -> {
                Snackbar.make(requireView(), "正在开发...", Snackbar.LENGTH_SHORT).show()
                true
            }
            R.id.toolbar_r_theme -> {
                request.launch(Intent(context, ActivityTheme::class.java))
                true
            }
            R.id.toolbar_r_about -> {
                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(), navHost, "share name"
                ).toBundle()
                ActivityCompat.startActivity(
                    requireContext(), Intent(
                        requireContext(), ActivityAbout::class.java
                    ), bundle
                )
                true
            }
            R.id.toolbar_r_msg -> {
                ActivityCompat.startActivity(
                    requireContext(), Intent(
                        requireContext(), ActivityWebView::class.java
                    ).apply {
                        putExtra(WEB_VIEW_URL_KEY, "https://yaohuo.me/bbs/messagelist.aspx")
                        putExtra(WEB_VIEW_URL_TITLE, "消息")
                    }, null
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}