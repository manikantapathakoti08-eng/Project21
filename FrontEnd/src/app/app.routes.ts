import { Routes } from '@angular/router';
import { App as QuizComponent } from './quiz/quiz.component';
import { ResultsComponent } from './quiz/results/results.component';

export const routes: Routes = [
  { path: '', component: QuizComponent },         
  { path: 'result', component: ResultsComponent }, 
 
  { path: ':id', component: QuizComponent },
  { path: '**', redirectTo: '' }                  
];
