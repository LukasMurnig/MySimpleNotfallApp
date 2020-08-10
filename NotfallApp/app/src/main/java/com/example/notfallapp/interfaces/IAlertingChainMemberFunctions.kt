package com.example.notfallapp.interfaces

import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.notfallapp.adapter.AlertingChainListAdapter
import com.example.notfallapp.bll.AlertingChainMember
import com.example.notfallapp.menubar.contact.ContactActivity
import com.example.notfallapp.server.ServerAlertingChain

interface IAlertingChainMemberFunctions {
    companion object{
        private val LOG_TAG: String? = IAlertingChainMemberFunctions::class.simpleName
        private var adapter: AlertingChainListAdapter? = null

        fun setAdapter(adapter: AlertingChainListAdapter){
            this.adapter = adapter
        }
    }

    fun activateAlertingChainMember(alertingChainMember: AlertingChainMember, itemView: View){
        Log.i(LOG_TAG, "AlertingChainMember change active clicked ")

        if(ContactActivity.alertingChain == null){
            return
        }

        val alertingChainMembers = ContactActivity.alertingChain!!.helpers

        //alertingChainMember.active = !alertingChainMember.active

        for(i in alertingChainMembers!!.indices){
            if(alertingChainMembers[i].helperId == alertingChainMember.helperId){
                alertingChainMembers[i].active = !alertingChainMembers[i].active
            }

        }

        //ContactActivity.alertingChain!!.helpers!!.

        ServerAlertingChain().updateAlertingChainMembers(ContactActivity.alertingChain!!.helpers!!)
        /*adapter!!.alertingChain = adapter!!.alertingChain
        adapter!!.notifyDataSetChanged()*/
    }

    fun updateAlertingChainMember(alertingChainMember: AlertingChainMember, itemView: View){
        Toast.makeText(itemView.context, "Gerade deaktiviert, da nicht sicher ob es gebraucht wird", Toast.LENGTH_LONG).show()
        /*val intent = Intent(itemView.context, UpdateContactActivity::class.java)
        intent.putExtra("id", alertingChainMember.helperId)
        Log.i(LOG_TAG, "AlertingChainMember updated clicked")
        itemView.context.startActivity(intent)*/
    }

    fun deleteAlertingChainMember(alertingChainMember: AlertingChainMember, itemView: View){
        Toast.makeText(itemView.context, "Gerade deaktiviert, da nicht sicher ob es gebraucht wird", Toast.LENGTH_LONG).show()
        /*val alertingChain = ContactActivity.alertingChain
        var idx = 0
        var member: AlertingChainMember? = null
        Log.i(LOG_TAG, "AlertingChainMember delete clicked")

        // TODO im ServerResponse schauen, ob mit 0 oder 1 gestartet wird
        if(alertingChain != null){
            if(alertingChainMember.rank != 2){
                if(idx < alertingChain.helpers!!.size - 1 && member == null){
                    if(alertingChain.helpers!![idx].rank == alertingChainMember.rank + 1){
                        member = alertingChain.helpers!![idx]
                    }
                    idx++
                }
                if(member != null){
                    member.rank = member.rank - 1
                    alertingChain.helpers!![idx-1] = member
                }

                if(alertingChainMember.rank != 1){
                    idx=0
                    member = null
                    if(idx < alertingChain.helpers!!.size - 1 && member == null){
                        if(alertingChain.helpers!![idx].rank == alertingChainMember.rank + 2){
                            member = alertingChain.helpers!![idx]
                        }
                        idx++
                    }
                    if(member != null){
                        member.rank = member.rank - 1
                        alertingChain.helpers!![idx-1] = member
                    }
                }
            }

            alertingChain.helpers!!.forEach {acm ->
                if(acm.helperId == alertingChainMember.helperId){
                    alertingChain.helpers!!.drop(alertingChain.helpers!!.indexOf(acm))
                }
            }
        }

        //TODO alertingChain Updaten

         */
    }

    /*fun switchContact(clickedContact: Contact, itemView: View, upClicked: Boolean){
        class SwitchContact : AsyncTask<Unit, Unit, List<Contact>>() {

            override fun doInBackground(vararg p0: Unit?): List<Contact>? {
                val appDb: EmergencyAppDatabase = EmergencyAppDatabase.getInstance(itemView.context)

                // get Contact, which will be also switch
                val res = if(upClicked){
                    appDb.contactDao().getContactByPriority((clickedContact.priority-1))
                }else{
                    appDb.contactDao().getContactByPriority((clickedContact.priority+1))
                }

                return run {
                    if(res != null){
                        res.priority = clickedContact.priority.also { clickedContact.priority = res.priority }
                        appDb.contactDao().updateContact(res)
                        appDb.contactDao().updateContact(clickedContact)
                        Log.i(LOG_TAG, "contact switch priority with other contact")
                    }
                    appDb.contactDao().getAllContact()
                }
            }

            override fun onPostExecute(result: List<Contact>?) {
                if(result != null){
                    // update Contacts adapter
                    if(adapter != null){
                        adapter?.contacts = result
                        adapter?.notifyDataSetChanged()
                    }
                }
            }
        }

        val gd = SwitchContact()
        gd.execute()
    }*/
}