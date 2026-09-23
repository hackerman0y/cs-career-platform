import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthStore } from '../../core/auth/auth.store';

@Component({
  selector: 'app-auth-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './auth-page.component.html',
  styleUrl: './auth-page.component.css'
})
export class AuthPageComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly authStore = inject(AuthStore);

  readonly mode = this.route.snapshot.data['mode'] === 'register' ? 'register' : 'login';
  readonly form = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['']
  });
  errorMessage = '';
  isSubmitting = false;

  submit(): void {
    const passwordsMatch = this.form.controls.password.value === this.form.controls.confirmPassword.value;
    if (this.form.invalid || (this.mode === 'register' && !passwordsMatch)) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';
    const { email, password } = this.form.getRawValue();
    const request = this.mode === 'register' ? this.authStore.register(email, password) : this.authStore.login(email, password);
    request.subscribe({
      next: () => void this.router.navigateByUrl('/profile'),
      error: (error: HttpErrorResponse) => {
        this.isSubmitting = false;
        this.errorMessage = error.status === 0 ? 'The API is not running yet. Start the Spring Boot backend, then try again.' : error.error?.message ?? 'We could not complete that request.';
      }
    });
  }
}

