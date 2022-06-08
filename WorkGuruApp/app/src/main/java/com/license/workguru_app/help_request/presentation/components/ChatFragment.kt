package com.license.workguru_app.help_request.presentation.components

import android.annotation.SuppressLint

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.license.workguru_app.R
import com.license.workguru_app.databinding.FragmentChatBinding
import com.license.workguru_app.di.SharedViewModel
import com.license.workguru_app.help_request.data.remote.DTO.GetMessagesByUserIdResponse
import com.license.workguru_app.help_request.data.remote.DTO.Message
import com.license.workguru_app.help_request.data.repository.HelpRequestRepository
import com.license.workguru_app.help_request.domain.use_cases.get_messages_related_to_a_user.ListMessagesRelatedToAUser
import com.license.workguru_app.help_request.domain.use_cases.get_messages_related_to_a_user.ListMessagesRelatedToAUserFactory
import com.license.workguru_app.help_request.domain.use_cases.send_message.SendMessageViewModel
import com.license.workguru_app.help_request.domain.use_cases.send_message.SendMessageViewModelFactory
import com.license.workguru_app.help_request.presentation.adapters.MessageAdapter
import com.license.workguru_app.utils.Constants
import com.license.workguru_app.utils.ProfileUtil
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.*
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import com.pusher.client.util.ConnectionFactory
import com.pusher.client.util.HttpAuthorizer
import java.lang.Exception


class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MessageAdapter


    lateinit var sendMessageViewModel: SendMessageViewModel
    lateinit var listMessagesRelatedToAUser: ListMessagesRelatedToAUser
    val itemList:MutableLiveData<MutableList<Message>> = MutableLiveData<MutableList<Message>>()
    val sharedViewModel:SharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChatBinding.inflate(inflater, container, false)

        initialize()
        loadEarlierMessages()
        return binding.root
    }

    private fun loadEarlierMessages() {
        itemList.observe(viewLifecycleOwner){
            itemList.value?.forEach {
                adapter.addMessage(it)
            }
        }
    }

    private fun initialize() {
        binding.messageList.layoutManager = LinearLayoutManager(requireActivity())
        (binding.messageList.layoutManager as LinearLayoutManager).setOrientation(LinearLayoutManager.VERTICAL);
        (binding.messageList.layoutManager as LinearLayoutManager).setStackFromEnd(true);
        (binding.messageList.layoutManager as LinearLayoutManager).setSmoothScrollbarEnabled(true);
        (binding.messageList.layoutManager as LinearLayoutManager).setReverseLayout(true);
        adapter = MessageAdapter(requireActivity(),sharedViewModel)
        binding.messageList.adapter = adapter
        binding.btnSend.setOnClickListener {
            if(binding.messageTextInput.text!!.isNotEmpty()) {
                lifecycleScope.launch {
                    if(sendMessageViewModel.sendMessage(binding.messageTextInput.text.toString(),
                        sharedViewModel.messageColleagueUserId.value!!
                    )){

                        binding.messageList.scrollToPosition(0);
                        resetInput()
                    }
                }
            } else {
                Toast.makeText(requireActivity(),getString(R.string.htRequiredField), Toast.LENGTH_SHORT).show()
            }
        }

        setupPusher()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = SendMessageViewModelFactory(requireActivity(), HelpRequestRepository())
        sendMessageViewModel = ViewModelProvider(this, factory).get(SendMessageViewModel::class.java)

        val factoryList = ListMessagesRelatedToAUserFactory(requireActivity(), HelpRequestRepository())
        listMessagesRelatedToAUser = ViewModelProvider(this, factoryList).get(ListMessagesRelatedToAUser::class.java)
        lifecycleScope.launch {
            if (listMessagesRelatedToAUser.listMessagesRelatedToAUser( sharedViewModel.messageColleagueUserId.value!!,0)){
                itemList.value = listMessagesRelatedToAUser.messageList.value as MutableList<Message>
                binding.messageList.scrollToPosition(0);
            }
        }
    }

    private fun resetInput() {
        // Clean text box
        binding.messageTextInput.text?.clear()
        
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupPusher() {

        val options = PusherOptions()
        val authorizer = HttpAuthorizer(Constants.BASE_URL + Constants.PUSHER_AUTHORIZER_ENDPOINT)
        options.setAuthorizer(authorizer)
        options.setCluster(Constants.PUSHER_CLUSTER);
        val pusher = Pusher(Constants.PUSHER_API_KEY,options)
//        authorizer.authorize("message."+ProfileUtil.getIntPref(this.requireActivity(), "USER_ID").toString(), pusher.connection.socketId)

        pusher?.connect(object : ConnectionEventListener {

            override fun onConnectionStateChange(change: ConnectionStateChange) {
                println(
                    "State changed to " + change.currentState +
                            " from " + change.previousState
                )
            }

            override fun onError(message: String?, code: String?, e: Exception?) {
                if (e!=null){
                    println("There was a problem connecting! "+e.message)
                }
                if (code!=null){
                    println("There was a problem connecting! "+code)
                }
                if (message!=null){
                    println("There was a problem connecting! "+message)
                }


            }
        }, ConnectionState.ALL)
//        val privateChannel = pusher.subscribe("message."+ProfileUtil.getIntPref(this.requireActivity(), "USER_ID").toString())

        val channel: Channel = pusher.subscribe("message."+ProfileUtil.getIntPref(this.requireActivity(), "USER_ID").toString(), object : ChannelEventListener {
            override fun onSubscriptionSucceeded(channelName: String) {
                Log.d("HELP_REQUEST", "Subscribed!")
            }

            override fun onError(message: String?, e: Exception) {
                Log.d("HELP_REQUEST", message.toString())
            }
            override fun onEvent(event: PusherEvent) {

                this@ChatFragment.activity?.runOnUiThread {
                    val jsonObject = JSONObject(event!!.data)
                    Log.d("HELP_REQUEST", jsonObject.toString())
                    val messageObject = jsonObject["message"] as JSONObject
                    val message = Message(
                        messageObject["created_at"].toString(),
                        messageObject["id"].toString().toInt(),
                        messageObject["text"].toString(),
                        messageObject["receiver_id"].toString().toInt(),
                        messageObject["sender_id"].toString().toInt()
                    )

                    requireActivity().runOnUiThread {
                        adapter.addMessage(message)
                        // scroll the RecyclerView to the last added element
                        binding.messageList.scrollToPosition(0);
                    }
                }
            }
        },"App\\Events\\MessagePool" )
        // Disconnect from the service
        pusher.disconnect();

        // Reconnect, with all channel subscriptions and event bindings automatically recreated
        pusher.connect();
        // The state change listener is notified when the connection has been re-established,
        // the subscription to "my-channel" and binding on "my-event" still exist.

        val ownChannel: Channel = pusher.subscribe("message."+sharedViewModel.messageColleagueUserId.value, object : ChannelEventListener {
            override fun onSubscriptionSucceeded(channelName: String) {
                Log.d("HELP_REQUEST", "Subscribed!")
            }

            override fun onError(message: String?, e: Exception) {
                Log.d("HELP_REQUEST", message.toString())
            }
            override fun onEvent(event: PusherEvent) {

                this@ChatFragment.activity?.runOnUiThread {
                    val jsonObject = JSONObject(event!!.data)
                    Log.d("HELP_REQUEST", jsonObject.toString())
                    val messageObject = jsonObject["message"] as JSONObject
                    val message = Message(
                        messageObject["created_at"].toString(),
                        messageObject["id"].toString().toInt(),
                        messageObject["text"].toString(),
                        messageObject["receiver_id"].toString().toInt(),
                        messageObject["sender_id"].toString().toInt()
                    )

                    requireActivity().runOnUiThread {
                        adapter.addMessage(message)
                        // scroll the RecyclerView to the last added element
                        binding.messageList.scrollToPosition(0);
                    }
                }
            }
        },"App\\Events\\MessagePool" )
        // Disconnect from the service
        pusher.disconnect();

        // Reconnect, with all channel subscriptions and event bindings automatically recreated
        pusher.connect();
        // The state change listener is notified when the connection has been re-established,
        // the subscription to "my-channel" and binding on "my-event" still exist.
    }
    fun getMapAuthorizationHeaders(channel_name:String, socket_id:String): HashMap<String, String>? {
        return try {
            val authHeader: HashMap<String, String> = HashMap()
            authHeader["channel_name"] = channel_name
            authHeader["socket_id"] = socket_id
            authHeader
        } catch (e: Exception) {
            null
        }
    }
}