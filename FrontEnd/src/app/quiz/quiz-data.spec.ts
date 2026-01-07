import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { QuizService, QuestionWrapper, QuizResponse, QuizResult } from './quiz.service';

describe('QuizService', () => {
  let service: QuizService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [QuizService]
    });

    service = TestBed.inject(QuizService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // Ensure no outstanding requests
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch quiz by ID', () => {
    const mockQuiz = [
      {
        id: 1,
        questionTitle: 'What is 2+2?',
        option1: '3',
        option2: '4',
        option3: '5',
        option4: '6'
      }
    ];

    service.getQuizById(3).subscribe(data => {
      expect(data.length).toBe(1);
      expect(data[0].questionTitle).toBe('What is 2+2?');
      // Map options for assertion
      const options = [data[0].option1, data[0].option2, data[0].option3, data[0].option4];
      expect(options[1]).toBe('4');
    });

    const req = httpMock.expectOne(`${service['baseUrl']}/get/6`);
    expect(req.request.method).toBe('GET');
    req.flush(mockQuiz);
  });

  it('should submit quiz responses', () => {
    const responses: QuizResponse[] = [{ id: 1, responseIndex: 2 }];

    const mockResult: QuizResult = {
      quizTitle: 'Basics of C',
      totalQuestions: 1,
      correctCount: 1,
      incorrectCount: 0,
      feedbackList: []
    };

    service.submitQuiz(6, responses).subscribe(result => {
      expect(result.quizTitle).toBe('Basics of C');
      expect(result.totalQuestions).toBe(1);
      expect(result.correctCount).toBe(1);
      expect(result.incorrectCount).toBe(0);
      expect(result.feedbackList.length).toBe(0);
    });

    const req = httpMock.expectOne(`${service['baseUrl']}/submit/6`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(responses);
    req.flush(mockResult);
  });
});