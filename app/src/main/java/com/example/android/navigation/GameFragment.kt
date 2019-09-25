/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.navigation

import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.android.navigation.databinding.FragmentGameBinding

class GameFragment : Fragment() {

    data class Question(
            val text: String,
            val answers: List<String>
            , val correctAnswerIndex: Int)

    // The first answer is the correct one.  We randomize the answers before showing the text.
    // All questions must have four answers.  We'd want these to contain references to string
    // resources so we could internationalize. (or better yet, not define the questions in code...)
    private val questions: MutableList<Question> = mutableListOf(
            Question(text = "What is Android Jetpack?",
                    answers = listOf("all of these", "tools", "documentation", "libraries"), correctAnswerIndex = 0),
            Question(text = "Base class for Layout?",
                    answers = listOf("ViewGroup", "ViewSet", "ViewCollection", "ViewRoot"), correctAnswerIndex = 0),
            Question(text = "Layout for complex Screens?",
                    answers = listOf("ConstraintLayout", "GridLayout", "LinearLayout", "FrameLayout"), correctAnswerIndex = 0),
            Question(text = "Pushing structured data into a Layout?",
                    answers = listOf("Data Binding", "Data Pushing", "Set Text", "OnClick"), correctAnswerIndex = 0),
            Question(text = "Inflate layout in fragments?",
                    answers = listOf("onCreateView", "onActivityCreated", "onCreateLayout", "onInflateLayout"), correctAnswerIndex = 0),
            Question(text = "Build system for Android?",
                    answers = listOf("Gradle", "Graddle", "Grodle", "Groyle"), correctAnswerIndex = 0),
            Question(text = "Android vector format?",
                    answers = listOf("VectorDrawable", "AndroidVectorDrawable", "DrawableVector", "AndroidVector"), correctAnswerIndex = 0),
            Question(text = "Android Navigation Component?",
                    answers = listOf("NavController", "NavCentral", "NavMaster", "NavSwitcher"), correctAnswerIndex = 0),
            Question(text = "Registers app with launcher?",
                    answers = listOf("intent-filter", "app-registry", "launcher-registry", "app-launcher"), correctAnswerIndex = 0),
            Question(text = "Mark a layout for Data Binding?",
                    answers = listOf("<layout>", "<binding>", "<data-binding>", "<dbinding>"), correctAnswerIndex = 0)
    )

    lateinit var currentQuestion: Question
    lateinit var answers: MutableList<String>
    private var questionIndex = 0
    private val numQuestions = Math.min((questions.size + 1) / 2, 3)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentGameBinding>(
                inflater, R.layout.fragment_game, container, false)

        // Shuffles the questions and sets the question index to the first question.
        randomizeQuestions()

        // Bind this fragment class to the layout
        binding.game = this

        // Set the onClickListener for the submitButton
        binding.submitButton.setOnClickListener { view: View ->
            val checkedId = binding.questionRadioGroup.checkedRadioButtonId
            // Do nothing if nothing is checked (id == -1)
            if (-1 != checkedId) {
                var answerIndex = 0
                when (checkedId) {
                    binding.firstAnswerRadioButton.id -> answerIndex = 0
                    binding.secondAnswerRadioButton.id -> answerIndex = 1
                    binding.thirdAnswerRadioButton.id -> answerIndex = 2
                }
                // The first answer in the original question is always the correct one, so if our
                // answer matches, we have the correct answer.
                if (answerIndex == currentQuestion.correctAnswerIndex) {
                    questionIndex++
                    // Advance to the next question
                    if (questionIndex < numQuestions) {
                        currentQuestion = questions[questionIndex]
                        setQuestion()
                        binding.invalidateAll()
                    } else {
                        // We've won!  Navigate to the gameWonFragment.
                        view.findNavController().navigate(GameFragmentDirections.actionGameFragmentToGameWonFragment(numQuestions, questionIndex))
                    }
                } else {
                    // Game over! A wrong answer sends us to the gameOverFragment.
                    view.findNavController().navigate(GameFragmentDirections.actionGameFragmentToGameOverFragment())
                }
            }
        }
        return binding.root
    }

    // randomize the questions and set the first question
    private fun randomizeQuestions() {
        questions.shuffle()
        questionIndex = 0
        setQuestion()
    }

    // Sets the question and randomizes the answers.  This only changes the data, not the UI.
    // Calling invalidateAll on the FragmentGameBinding updates the data.
    private fun setQuestion() {
        currentQuestion = questions[questionIndex]
        // randomize the answers into a copy of the array
        answers = currentQuestion.answers.toMutableList()
        // and shuffle them
        answers.shuffle()
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_android_trivia_question, questionIndex + 1, numQuestions)
    }
}
