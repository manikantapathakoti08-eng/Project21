// updated quiz.service.spec.ts
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
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch quiz by ID and map options', () => {
    const backendMock = [
      {
        id: 1,
        question: 'What is 2+2?',
        option1: '3',
        option2: '4',
        option3: '5',
        option4: '6'
      }
    ];

    service.getQuizById(3).subscribe(data => {
      expect(data.length).toBe(1);
      expect(data[0].questionTitle).toBe('What is 2+2?');
      expect(data[0].options[1]).toBe('4');
      expect(data[0].selected).toBe(-1);
    });

    const req = httpMock.expectOne(`${service['baseUrl']}/get/8`);
    expect(req.request.method).toBe('GET');
    req.flush(backendMock);
  });

  it('should submit quiz responses with expected payload', () => {
    const responses: QuizResponse[] = [{ questionId: 1, responseIndex: 2 }];

    const mockResult: QuizResult = {
      quizTitle: 'Basics of C',
      totalQuestions: 1,
      correctCount: 1,
      incorrectCount: 0,
      feedbackList: []
    };

    service.submitQuiz(3, responses).subscribe(result => {
      expect(result.quizTitle).toBe('Basics of C');
      expect(result.totalQuestions).toBe(1);
      expect(result.correctCount).toBe(1);
      expect(result.incorrectCount).toBe(0);
      expect(result.feedbackList.length).toBe(0);
    });

    const req = httpMock.expectOne(`${service['baseUrl']}/submit/8`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(responses);
    req.flush(mockResult);
  });
});