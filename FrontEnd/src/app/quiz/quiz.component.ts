import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { QuizService, QuestionWrapper, QuizResponse, QuizResult } from './quiz.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  providers: [QuizService],
  templateUrl: './quiz.component.html',
})
export class App implements OnInit {
  // default fallback id (keeps your previous behaviour)
  quizId = 6;
  // whether the id was explicitly provided in the URL (path or query)
  idProvided = false;
  questions: QuestionWrapper[] = [];
  result?: QuizResult;
  isLoading = false;
  errorMessage = '';
  // bound to the small "load by id" input when no id is present in URL
  idInput = '';

  constructor(private quizService: QuizService, private route: ActivatedRoute) {}

  ngOnInit(): void {
    // Read quiz id from route param (e.g. https://localhost:4200/6) first,
    // then fallback to query param (e.g. https://localhost:4200/?id=6),
    // finally use the default `quizId` declared above.
    const pathParam = this.route.snapshot.paramMap.get('id');
    if (pathParam) {
      const parsed = Number(pathParam);
      if (!Number.isNaN(parsed) && parsed > 0) {
        this.quizId = parsed;
        this.idProvided = true;
        console.log('[DEBUG] quizId from path param =', this.quizId);
      } else {
        console.warn('[DEBUG] invalid id path param, using fallback', pathParam);
      }
    } else {
      // If no path param, try the query string: https://localhost:4200/?id=8
      const idParam = this.route.snapshot.queryParamMap.get('id');
      if (idParam) {
        const parsed = Number(idParam);
        if (!Number.isNaN(parsed) && parsed > 0) {
          this.quizId = parsed;
          this.idProvided = true;
          console.log('[DEBUG] quizId from query param =', this.quizId);
        } else {
          console.warn('[DEBUG] invalid id query param, using fallback', idParam);
        }
      } else {
        console.log('[DEBUG] no id provided in URL, using default quizId =', this.quizId);
      }
    }

    this.loadQuiz();
  }

  loadByInput(): void {
    this.errorMessage = '';
    const parsed = Number(this.idInput);
    if (this.idInput && !Number.isNaN(parsed) && parsed > 0) {
      this.quizId = parsed;
      this.idProvided = true;
      console.log('[DEBUG] loading quiz from manual input id=', this.quizId);
      this.loadQuiz();
    } else {
      this.errorMessage = 'Please enter a valid numeric quiz id (e.g. 8)';
    }
  }

  loadQuiz(): void {
    this.result = undefined;
    this.errorMessage = '';
    this.isLoading = true;

    console.log(`[DEBUG] requesting quiz id=${this.quizId}`);
    this.quizService.getQuizById(this.quizId).subscribe({
      next: (data: QuestionWrapper[]) => {
        console.log('[DEBUG] getQuizById response:', data);
        // Defensive: map unknown shapes into the expected QuestionWrapper
        this.questions = (data || []).map(q => ({
          id: q.id,
          questionTitle: q.questionTitle ?? (q as any).question ?? '',
          options: Array.isArray(q.options) ? q.options : (q as any).options ?? [],
          selected: typeof q.selected === 'number' ? q.selected : -1
        }));
        this.isLoading = false;

        if (!this.questions || this.questions.length === 0) {
          this.errorMessage = this.idProvided
            ? `No questions returned for quiz id ${this.quizId}. If you just created the quiz, confirm you used the same id (${this.quizId}).`
            : `No quiz id provided in the URL. Visit /:id (e.g. /6) or append ?id=6 to the URL to load a quiz.`;
          console.warn('[DEBUG] no questions returned from backend for quizId=', this.quizId);
        } else {
          // success
          this.errorMessage = '';
        }
      },
      error: err => {
        this.errorMessage = `Failed to load quiz id ${this.quizId}. Check backend is running and CORS/HTTPS are configured. See console for details.`;
        console.error('[DEBUG] getQuizById error:', err);
        this.isLoading = false;
      }
    });
  }

  get submitDisabled(): boolean {
    // Disables the button if any question has not been answered
    return this.questions.some(q => q.selected === -1);
  }

  submitQuiz(): void {
    const responses: QuizResponse[] = this.questions.map(q => ({
      questionId: q.id,
      responseIndex: q.selected === -1 ? null : q.selected
    }));

    console.log('[DEBUG] submitting responses:', responses);
    this.quizService.submitQuiz(this.quizId, responses).subscribe({
      next: res => {
        console.log('[DEBUG] submit response:', res);
        this.result = res;
        this.questions = []; // Hide quiz after submission
      },
      error: err => {
        console.error('Error submitting quiz:', err);
        this.errorMessage = 'Failed to submit quiz';
      }
    });
  }
}