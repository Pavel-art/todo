package com.example.todo.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.databinding.FragmentTodolistBinding
import com.example.todo.domain.entity.TodoModel


class TodoListFragment : Fragment() {

    private var _binding: FragmentTodolistBinding? = null
    private val binding: FragmentTodolistBinding
        get() = _binding ?: throw RuntimeException("FragmentTodolistBinding == null")

    lateinit var todoAdapter: TodoListAdapter
    lateinit var todoViewModel: TodoViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodolistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        todoViewModel = ViewModelProvider(this)[TodoViewModel::class.java]
        todoViewModel.todoList.observe(viewLifecycleOwner) {
            todoAdapter.submitList(it)
        }
        binding.floatBtn.setOnClickListener {
            launchToAddTodoItemFragment()
        }
    }


    private fun setupAdapter() {
        todoAdapter = TodoListAdapter()
        binding.rvList.adapter = todoAdapter
        setupSwipeListener()
        checkBoxListener()
    }

    private fun checkBoxListener() {
        todoAdapter.checkBoxListener = {
            todoViewModel.completedTodo(it)
        }
    }

    private fun setupSwipeListener() {
        val callback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val todo = todoAdapter.currentList[viewHolder.adapterPosition]
                todoViewModel.removeTodo(todo)
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rvList)
    }


    private fun launchToAddTodoItemFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in, R.anim.fade_out)
            .addToBackStack(null)
            .replace(R.id.main_layout, AddTodoItemFragment.instance())
            .commit()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}