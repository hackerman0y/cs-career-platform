import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CareerPath, StudentProfile } from '../../core/profile/student-profile.model';
import { StudentProfileStore } from '../../core/profile/student-profile.store';

@Component({
  selector: 'app-profile-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './profile-page.component.html',
  styleUrl: './profile-page.component.css'
})
export class ProfilePageComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly profileStore = inject(StudentProfileStore);
  private readonly router = inject(Router);

  readonly careerPaths: { value: CareerPath; description: string }[] = [
    { value: 'Frontend Developer', description: 'Build clear, responsive experiences for the web.' },
    { value: 'Backend Developer', description: 'Design APIs, databases, and the systems behind products.' },
    { value: 'Mobile Developer', description: 'Create useful experiences for Android and iOS.' },
    { value: 'Data Analyst', description: 'Use data to find patterns and support better decisions.' },
    { value: 'AI / Machine Learning', description: 'Build intelligent products with data, models, and evaluation.' },
    { value: 'Cybersecurity', description: 'Protect systems, networks, and users through practical security work.' },
    { value: 'DevOps / Cloud', description: 'Automate delivery and build dependable cloud infrastructure.' },
    { value: 'Full Stack Developer', description: 'Build complete products from interface to deployment.' }
  ];

  readonly profileForm = this.formBuilder.nonNullable.group({
    fullName: [this.profileStore.profile().fullName, [Validators.required, Validators.maxLength(80)]],
    university: [this.profileStore.profile().university, Validators.maxLength(120)],
    academicYear: [this.profileStore.profile().academicYear, Validators.required],
    githubUrl: [this.profileStore.profile().githubUrl],
    linkedinUrl: [this.profileStore.profile().linkedinUrl],
    targetCareer: [this.profileStore.profile().targetCareer, Validators.required]
  });

  saveError = '';

  ngOnInit(): void {
    this.profileStore.load().subscribe({
      next: (profile) => this.profileForm.patchValue(profile)
    });
  }

  saveProfile(): void {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }

    this.saveError = '';
    this.profileStore.update(this.profileForm.getRawValue() as StudentProfile).subscribe({
      next: () => void this.router.navigateByUrl('/dashboard'),
      error: () => this.saveError = 'We could not save your profile. Please try again.'
    });
  }
}
