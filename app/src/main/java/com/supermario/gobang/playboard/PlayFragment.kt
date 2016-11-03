package com.supermario.gobang.playboard

import android.content.Context
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.supermario.gobang.R
import com.supermario.gobang.data.Chess
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [PlayFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [PlayFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlayFragment : Fragment(), PlayView{

    override fun updateScores(scoreArrayB: Array<Array<IntArray>>, scoreArrayW: Array<Array<IntArray>>, show: Boolean) {
        chessBoard?.updateScore(scoreArrayB, scoreArrayW, show)
    }

    lateinit var presenterP: PlayPresenter

    override fun setPresenter(presenter: PlayPresenter) {
        presenterP = presenter
    }

    override fun initView() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateCurrentChess(chessState: Stack<Chess>) {
        chessBoard?.updateChessStore(chessState)

    }

    var toast: Toast? = null
    override fun showGameResult(winner: Int) {
        chessBoard?.post {
            if (toast != null) {
                toast?.cancel()
                toast = null
            }
            val winnerName: String = if(winner == Chess.COLOR.BLACK) "BLACK" else "WHITE"
            toast = Toast.makeText(activity, resources.getString(R.string.game_result) + winnerName, Toast.LENGTH_SHORT)
            toast?.show()
        }
    }

    override fun prepareToStart() {
        chessBoard?.resetData()
    }

    override fun freezeToStop() {

    }

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: OnFragmentInteractionListener? = null

    private var chessBoard: ChessBoard? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
            mParam2 = arguments.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val fragmentView = inflater!!.inflate(R.layout.fragment_play, container, false)
        chessBoard = fragmentView.findViewById(R.id.chess_board) as ChessBoard?
        chessBoard?.touchCallback = object : ChessBoard.TouchCallback {
            override fun askToPutChessAt(x: Int, y: Int) {
                presenterP.putChess(x, y, true)
            }
        }

        return fragmentView
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context as OnFragmentInteractionListener?
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.

         * @param param1 Parameter 1.
         * *
         * @param param2 Parameter 2.
         * *
         * @return A new instance of fragment PlayFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): PlayFragment {
            val fragment = PlayFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
