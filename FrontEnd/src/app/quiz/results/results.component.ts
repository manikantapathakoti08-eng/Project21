import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

interface QuestionFeedback {
  questionText: string;
  userResponse: string;
  correctAnswer: string;
  isCorrect: boolean;
  explanation?: string; 
}

interface QuizResult {
  quizTitle: string;
  totalQuestions: number;
  correctCount: number;
  incorrectCount: number;
  feedbackList: QuestionFeedback[];
}

interface QuestionView {
  question: string;
  selected: string;
  answer: string;
  explanation: string;
  isCorrect: boolean;
}

@Component({
  selector: 'app-results',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './results.component.html',
  styleUrls: ['./results.component.css']
})
export class ResultsComponent implements OnInit {
  quizTitle = '';
  totalQuestions = 0;
  correctCount = 0;
  incorrectCount = 0;
  scorePercent = 0;

  questions: QuestionView[] = [];

  constructor(private router: Router) {}

  ngOnInit(): void {
    const nav = this.router.getCurrentNavigation();
    const state = nav?.extras?.state as QuizResult | undefined;

    if (state?.feedbackList) {
      this.quizTitle = state.quizTitle;
      this.totalQuestions = state.totalQuestions;
      this.correctCount = state.correctCount;
      this.incorrectCount = state.incorrectCount;
      this.scorePercent = this.totalQuestions > 0
        ? Math.round((this.correctCount / this.totalQuestions) * 100)
        : 0;

      this.questions = state.feedbackList.map(f => ({
        question: f.questionText,
        selected: f.userResponse || 'No answer',
        answer: f.correctAnswer,
        explanation: f.explanation ?? 'No explanation provided',
        isCorrect: f.isCorrect
      }));
    } else {
      // If no state is passed, redirect back to quiz
      this.router.navigate(['/quiz']);
    }
  }

  get passed(): boolean {
    return this.scorePercent >= 70; // Example threshold
  }
}