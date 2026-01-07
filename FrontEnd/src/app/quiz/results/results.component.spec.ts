import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { ResultsComponent } from './results.component';

describe('ResultsComponent', () => {
  let component: ResultsComponent;
  let fixture: ComponentFixture<ResultsComponent>;

  // Mock Router with getCurrentNavigation returning state
  const mockRouter = {
    getCurrentNavigation: () => ({
      extras: {
        state: {
          quizTitle: 'Sample Quiz',
          totalQuestions: 2,
          correctCount: 1,
          incorrectCount: 1,
          feedbackList: [
            {
              questionText: 'What is 2+2?',
              userResponse: '3',
              correctAnswer: '4',
              isCorrect: false,
              explanation: '2+2 is always 4'
            }
          ]
        }
      }
    }),
    navigate: jasmine.createSpy('navigate')
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ResultsComponent],
      providers: [{ provide: Router, useValue: mockRouter }]
    }).compileComponents();

    fixture = TestBed.createComponent(ResultsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load quiz result from router state', () => {
    expect(component.quizTitle).toBe('Sample Quiz');
    expect(component.totalQuestions).toBe(2);
    expect(component.correctCount).toBe(1);
    expect(component.incorrectCount).toBe(1);
    expect(component.scorePercent).toBe(50);
    expect(component.questions.length).toBe(1);
    expect(component.questions[0].question).toBe('What is 2+2?');
    expect(component.questions[0].answer).toBe('4');
    expect(component.questions[0].isCorrect).toBeFalse();
  });

  it('should mark as passed if score >= 70%', () => {
    expect(component.passed).toBeTrue();
  });
});