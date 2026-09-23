import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CareerPath } from '../../core/profile/student-profile.model';
import { StudentProfileStore } from '../../core/profile/student-profile.store';

const apiUrl = 'http://localhost:8080/api';

type Choice = { value: string; label: string };
type Question = { id: string; prompt: string; choices: Choice[] };
type Recommendation = { name: string; description: string; score: number };
type Result = { recommendations: Recommendation[] };

@Component({
  selector: 'app-assessment-page',
  imports: [RouterLink],
  templateUrl: './assessment-page.component.html',
  styleUrl: './assessment-page.component.css'
})
export class AssessmentPageComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly profileStore = inject(StudentProfileStore);
  private readonly router = inject(Router);

  questions: Question[] = [];
  answers: Record<string, string> = {};
  recommendations: Recommendation[] = [];
  errorMessage = '';
  isLoading = true;
  isSubmitting = false;

  ngOnInit(): void {
    this.http.get<Question[]>(`${apiUrl}/assessment/questions`).subscribe({
      next: (questions) => {
        this.questions = questions;
        this.isLoading = false;
        this.http.get<Result>(`${apiUrl}/assessment/result`).subscribe({
          next: result => this.recommendations = result.recommendations,
          error: () => { /* A new student has no saved result yet. */ }
        });
      },
      error: (error: HttpErrorResponse) => {
        this.isLoading = false;
        this.errorMessage = error.status === 401 || error.status === 403
          ? 'Create an account or sign in before taking the assessment.'
          : 'The assessment is unavailable right now. Make sure the backend is running.';
      }
    });
  }

  retake(): void {
    this.recommendations = [];
    this.answers = {};
    this.errorMessage = '';
  }

  selectAnswer(questionId: string, answer: string): void {
    this.answers[questionId] = answer;
  }

  submit(): void {
    if (this.questions.some((question) => !this.answers[question.id])) {
      this.errorMessage = 'Choose one answer for every question before continuing.';
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';
    const answers = this.questions.map((question) => ({ questionId: question.id, answer: this.answers[question.id] }));
    this.http.post<Result>(`${apiUrl}/assessment/submit`, { answers }).subscribe({
      next: (result) => { this.recommendations = result.recommendations; this.isSubmitting = false; },
      error: () => { this.errorMessage = 'We could not save your assessment. Please try again.'; this.isSubmitting = false; }
    });
  }

  useCareerPath(recommendation: Recommendation): void {
    this.profileStore.update({ ...this.profileStore.profile(), targetCareer: recommendation.name as CareerPath }).subscribe({
      next: () => void this.router.navigateByUrl('/dashboard'),
      error: () => this.errorMessage = 'We could not update your career path. Please try again.'
    });
  }
}
