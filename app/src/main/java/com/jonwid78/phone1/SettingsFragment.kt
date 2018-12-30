package com.jonwid78.phone1


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_settings.*


/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var settingsRecyclerViewAdapter: RecyclerView.Adapter<SettingsViewHolder>? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = settingsRecyclerView
        settingsRecyclerViewAdapter = SettingsRecyclerViewAdapter(context)

        recyclerView!!.adapter = settingsRecyclerViewAdapter
        val layoutManager = LinearLayoutManager(context)
        recyclerView!!.layoutManager = layoutManager

        languageSelectBtn.setOnClickListener {
            val dialog = LanguageFragment()
            dialog.show(fragmentManager, null)
        }


    }


    class SettingsRecyclerViewAdapter(val context: Context): RecyclerView.Adapter<SettingsViewHolder>() {

        private val settings = Settings(context).retrieveSettings()

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SettingsViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.setting_two_choice_item,parent,false)
            return SettingsViewHolder(view)
        }

        override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
            holder.settingTitle.text = settings[position].title
            holder.settingDescription.text = settings[position].description
            holder.settingSwitch.isChecked = settings[position].isActive
            holder.settingSwitch.setOnCheckedChangeListener({ _, isChecked ->
                settings[position].isActive = isChecked
            })
        }

        override fun getItemCount(): Int {
            return settings.size
        }
    }

}// Required empty public constructor

class SettingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val settingTitle: TextView = itemView.findViewById(R.id.twoChoiceSettingTitleTextView)
    val settingDescription: TextView = itemView.findViewById(R.id.twoChoiceSettingDescriptionTextView)
    val settingSwitch: Switch = itemView.findViewById(R.id.settingSwitch)
}