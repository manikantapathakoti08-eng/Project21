import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';

// Frontend QuestionWrapper shape - produced by service.getQuizById mapping
export interface QuestionWrapper {
  id: number;
  questionTitle: string; // mapped from backend 'question'
  options: string[];
  selected: number; // -1 if none selected, otherwise 0–3
}

// Payload sent to backend on submit
export interface QuizResponse {
  questionId: number;
  responseIndex: number | null;
}

// Backend QuizResult and QuestionFeedback shape
export interface QuestionFeedback {
  questionText: string;
  userResponse: string;
  isCorrect: boolean;
  correctAnswer: string;
  explanation?: string;
}

export interface QuizResult {
  quizTitle: string;
  totalQuestions: number;
  correctCount: number;
  incorrectCount: number;
  skippedCount?: number;
  scorePercentage?: number;
  passed?: boolean;
  feedbackList: QuestionFeedback[];
}

@Injectable({
  providedIn: 'root'
})
export class QuizService {
  // Use environment-configured base URL (HTTPS)
  private baseUrl = environment.apiBaseUrl; // e.g. https://localhost:8443/quiz

  constructor(private http: HttpClient) {}

  // GET /quiz/get/{id} — map backend fields to frontend shape
  getQuizById(id: number): Observable<QuestionWrapper[]> {
    return this.http.get<any[]>(`${this.baseUrl}/get/${id}`).pipe(
      map(items => (items || []).map(item => {
        const options = [
          item.option1 ?? (item.options && item.options[0]),
          item.option2 ?? (item.options && item.options[1]),
          item.option3 ?? (item.options && item.options[2]),
          item.option4 ?? (item.options && item.options[3])
        ].filter(opt => opt !== undefined && opt !== null) as string[];

        return {
          id: item.id,
          questionTitle: item.question ?? item.questionTitle ?? '',
          options,
          selected: -1
        } as QuestionWrapper;
      }))
    );
  }

  // POST /quiz/submit/{id}
  submitQuiz(id: number, responses: QuizResponse[]): Observable<QuizResult> {
    // If you use cookie-based sessions (server sets cookies), enable withCredentials: true
    // return this.http.post<QuizResult>(`${this.baseUrl}/submit/${id}`, responses, { withCredentials: true });

    // If you use token-based Authorization header, you can omit withCredentials
    return this.http.post<QuizResult>(`${this.baseUrl}/submit/${id}`, responses);
  }
}